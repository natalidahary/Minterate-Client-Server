# Minterate: Client-Server Lending Platform
<img width="256" alt="Logo" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/fe15dfdb-71e4-45d1-a253-16455594c2fb">

## About

Minterate is a client-server lending platform designed to facilitate lending transactions between borrowers and lenders. It employs a client-server architecture to handle data storage, processing, and communication between users.

## Technologies Used

- **Client-side:** Android Studio, Kotlin, CurrencyLayer API
- **Server-side:** Node.js, Express.js, Firebase Firestore
- **Communication:** RESTful APIs
- **Authentication:** JSON Web Tokens (JWT), Firebase Authentication

  
## Server
<img width="252" alt="Screenshot 2024-07-01 at 20 54 12" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/516247e7-6964-423f-b169-c4e01baae07e">
<img width="252" alt="Screenshot 2024-07-01 at 20 54 34" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/a9215cfa-21ab-4753-89f7-bcd828f20b73">
<img width="252" alt="Screenshot 2024-07-01 at 20 54 52" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/8763f695-4596-4e99-a8e7-43f6f58c5e63">

## Features

### User Registration and Loan Interaction:
- Users can register on the platform and interact as both borrowers and lenders.
- Lenders can lend money by choosing an amount, period, expiration date, and interest rate.
- Borrowers can filter loans by amount and period to find suitable loan options.

### Loan Listings and Matching:
- Loan listings are displayed on the client interface.
- Matching filteration facilitate the pairing of borrowers with suitable loans based on lenders criteria and preferences.

### Transparent Loan Terms and Interest Rates:
- The platform provides transparent loan terms, including interest rates, repayment schedules, and borrower credit assessments.
- Lenders have access to comprehensive loan information to make informed lending decisions.

### Automated Payment Processing and Loan Servicing:
- The server automates payment processing, including loan disbursements, borrower repayments, and lender returns.
- Loan servicing functions, such as account management, payment reminders, and collections, are handled centrally by the server.

## Installation

To set up Minterate on your local machine, follow these steps:

Instructions for Use:
- **Requirements to Install:** Visual Studio Code for server, and Android Studio for the client.

Pre-Running Steps:
1. Open the Minterate server folder, paste inside the it the .env file : inside the env file - change the SERVICE_ACCOUNT_PATH to the path where my-application-d346d-firebase-adminsdk-ci5a0-f162b5830c.json is located.
2. Open the terminal and run ifconfig to get the IP address.
3. In Android Studio, import MyApplication, then navigate to serverOperations folder -> config.kt, and change the IP address to the extracted one (keep the same port).

Run the Server:
- Via Visual Studio Code terminal, run node index.js.
- Hit enter, and you will see the server running.

Run the Client:
1. Click "build" on Android Studio.
2. If there's an issue with the SDK, click on "sync project with Gradle file" on the top right side of the toolbar.
3. Click "play" and enjoy.

## Screenshots
<img width="1210" alt="Screenshot 2024-07-01 at 20 49 28" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/71877987-fb1a-4a04-bdef-f3bbaefab559">
<img width="1210" alt="Screenshot 2024-07-01 at 20 49 35" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/4c53e8ba-794b-4b70-ab86-eb4eccd808c3">
<img width="1218" alt="Screenshot 2024-07-01 at 20 49 47" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/8e7f87f3-f764-4c94-bec0-8515dadfbe5e">
<img width="1224" alt="Screenshot 2024-07-01 at 20 49 54" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/31318515-663a-4e7d-8c99-2a8988def1d7">
<img width="1201" alt="Screenshot 2024-07-01 at 20 50 04" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/ec8b741f-58dd-472b-af66-26e6e5c9e978">
<img width="1216" alt="Screenshot 2024-07-01 at 20 50 10" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/ee023759-145e-41e6-86b9-e1fea4e28f60">
<img width="1220" alt="Screenshot 2024-07-01 at 20 50 16" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/066b7b85-c46d-45bd-8cbb-e18cec99ec25">
<img width="1201" alt="Screenshot 2024-07-01 at 20 50 37" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/c4b0391e-407d-4866-a50e-8c1e0cca5d12">
<img width="1200" alt="Screenshot 2024-07-01 at 20 50 47" src="https://github.com/ShalevShar/Minterate-Client/assets/127881894/81478bd5-a9dd-4f8b-9799-374f94dba1fa">




## Still on development stage.
.
.
.
.
.

