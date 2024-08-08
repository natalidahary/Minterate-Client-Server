const FormData = require('form-data');
const fs = require('fs');
const multer = require('multer');
const express = require('express');

class IdImageProcess {
    constructor(bucket, db) {
        this.clientId = 'ffu5t0j9qm4fstmbbe9xvabut052oqto';
        this.clientSecret = 'v32jkteapznfs54s6xgxsb643zbtvhl0sfkoq2qh0z8vnw1nwcvqavvkwygkhkf7';
        this.tokenEndpoint = 'https://www.nyckel.com/connect/token';
        this.classifyEndpoint = 'https://www.nyckel.com/v1/functions/w5cgfw9fb5d19die/invoke';
        this.accessToken = null;
        this.upload = multer({ dest: 'uploads/' });

        this.bucket = bucket;
        this.db = db;
    }

    async fetchAccessToken() {
        console.log('Fetching access token...');
        try {
            const fetch = (await import('node-fetch')).default;
            const response = await fetch(this.tokenEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `client_id=${this.clientId}&client_secret=${this.clientSecret}&grant_type=client_credentials`
            });
            const data = await response.json();
            if (response.ok) {
                this.accessToken = data.access_token;
                console.log('Access token fetched successfully.');
            } else {
                throw new Error(`Failed to fetch access token: ${data.error_description}`);
            }
        } catch (error) {
            console.error('Error fetching access token:', error.message);
            throw error;
        }
    }

    async classifyImage(fileBytes, fileName) {
        console.log(`Classifying image: ${fileName}...`);
        try {
            const fetch = (await import('node-fetch')).default;
            if (!this.accessToken) {
                console.log('No access token found, fetching a new one...');
                await this.fetchAccessToken();
            }

            const form = new FormData();
            form.append('data', fileBytes, {
                filename: fileName,
                contentType: 'image/jpeg' // Default content type
            });

            console.log('Sending image to classify endpoint...');
            const response = await fetch(this.classifyEndpoint, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.accessToken}`,
                    ...form.getHeaders()
                },
                body: form
            });

            const data = await response.json();
            if (response.ok) {
                console.log('Image classified successfully.');
                return data;
            } else {
                console.error('Error response from classify endpoint:', data);
                throw new Error(`Failed to classify image: ${data.error}`);
            }
        } catch (error) {
            console.error('Error classifying image:', error.message);
            throw error;
        }
    }

    // async uploadImageToFirebase(filePath, fileName, userEmail) {
    //     console.log(`Uploading ${fileName} to Firebase Storage...`);
    //     try {
    //         const [file] = await this.bucket.upload(filePath, {
    //             destination: `images/${fileName}`,
    //             resumable: false,
    //             metadata: {
    //                 contentType: 'image/jpeg' // Update the content type as needed
    //             }
    //         });

    //         // Make the file publicly accessible
    //         await file.makePublic();

    //         const fileUrl = file.publicUrl();
    //         console.log(`Image ${fileName} uploaded to Firebase Storage successfully at URL: ${fileUrl}`);

    //         // Ensure userEmail is valid
    //         if (!userEmail || typeof userEmail !== 'string' || userEmail.trim() === '') {
    //             throw new Error('Invalid user email');
    //         }

    //         // Update the user document with the image URL
    //         const userRef = this.db.collection('users').doc(userEmail);
    //         const userDoc = await userRef.get();

    //         if (!userDoc.exists) {
    //             // Create a new document if it does not exist
    //             await userRef.set({
    //                 idImageUrl: fileUrl // Add the idImageUrl field to the new document
    //             });
    //             console.log('User document created and updated with image URL');
    //         } else {
    //             // Update the existing document
    //             await userRef.update({
    //                 idImageUrl: fileUrl // Add or update the field in the user's document
    //             });
    //             console.log('User document updated with image URL');
    //         }

    //         // Return the file URL
    //         return fileUrl;

    //     } catch (error) {
    //         console.error('Error uploading image to Firebase Storage:', error.message);
    //         throw error;
    //     }
    // }

    async uploadImageToFirebaseStorage(filePath, fileName) {
        console.log(`Uploading ${fileName} to Firebase Storage...`);
        try {
            const [file] = await this.bucket.upload(filePath, {
                destination: `images/${fileName}`,
                resumable: false,
                metadata: {
                    contentType: 'image/jpeg' // Update the content type as needed
                }
            });

            // Make the file publicly accessible
            await file.makePublic();

            const fileUrl = file.publicUrl();
            console.log(`Image ${fileName} uploaded to Firebase Storage successfully at URL: ${fileUrl}`);

            return fileUrl;
        } catch (error) {
            console.error('Error uploading image to Firebase Storage:', error.message);
            throw error;
        }
    }

    async updateUserDocumentWithImageUrl(userEmail, fileUrl) {
        console.log(`Updating Firestore document for user: ${userEmail} with image URL: ${fileUrl}`);
        try {
            // Ensure userEmail is valid
            if (!userEmail || typeof userEmail !== 'string' || userEmail.trim() === '') {
                throw new Error('Invalid user email');
            }

            // Update the user document with the image URL
            const userRef = this.db.collection('users').doc(userEmail);
            const userDoc = await userRef.get();

            if (!userDoc.exists) {
                // Create a new document if it does not exist
                await userRef.set({
                    idImageUrl: fileUrl // Add the idImageUrl field to the new document
                });
                console.log('User document created and updated with image URL');
            } else {
                // Update the existing document
                await userRef.update({
                    idImageUrl: fileUrl // Add or update the field in the user's document
                });
                console.log('User document updated with image URL');
            }
        } catch (error) {
            console.error('Error updating Firestore document with image URL:', error.message);
            throw error;
        }
    }

    setupRoutes(app) {
        app.post('/check-image-id', this.upload.single('image'), async (req, res) => {
            console.log('Received request to /check-image-id');
            try {
                const filePath = req.file.path;
                const fileName = req.file.originalname;
                const userEmail = req.body.email; // Assume the user's email is sent in the request body
                console.log(`Processing file: ${fileName} at path: ${filePath} for user: ${userEmail}`);

                if (!userEmail || typeof userEmail !== 'string' || userEmail.trim() === '') {
                    throw new Error('Invalid or missing user email');
                }

                const fileBytes = fs.readFileSync(filePath);
                console.log('File read successfully.');

                const result = await this.classifyImage(fileBytes, fileName);
                console.log('Classification result:', result);

                // Only upload to Firebase if the labelName is 'id'
                let fileUrl = null;
                if (result.labelName === 'id') {
                    fileUrl = await this.uploadImageToFirebaseStorage(filePath, fileName);
                } else {
                    console.log('Image not recognized as ID, not uploading to Firebase Storage.');
                }

                res.json({ ...result, fileUrl });
            } catch (error) {
                console.error('Error in /check-image-id route:', error.message);
                res.status(500).json({ error: error.message });
            }
        });
    }
}


module.exports = IdImageProcess;
