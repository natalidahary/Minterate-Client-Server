package com.example.myapplication.signupActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignupIdactivityBinding
import com.example.myapplication.serverOperations.RetrofitInterface
import com.example.myapplication.serverOperations.RetrofitManager
import com.example.myapplication.userPreferences.SoundManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SignupIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupIdactivityBinding

    private var retrofit: Retrofit
    private var retrofitInterface: RetrofitInterface

    init {
        retrofit = RetrofitManager.getRetrofit()
        retrofitInterface = RetrofitManager.getRetrofitInterface()
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var signUpIDHeader: AppCompatTextView
    private lateinit var exitButton: MaterialButton
    private lateinit var uploadID: MaterialButton
    private lateinit var capturePhotoID: MaterialButton
    private lateinit var progressBarID: ProgressBar
    private lateinit var soundManager: SoundManager

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mobile: String
    //personal information
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var dob: String
    private lateinit var id: String
    private lateinit var address: String
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var filePath: File
    private var fileUrl: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var captureImageLauncher: ActivityResultLauncher<Intent>

    private var uploadSuccess = false
    private var uploadMessage = ""

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val TAG = "SignupIDActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupIdactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        findViews()
        progressBarID.visibility = View.INVISIBLE

        extractIntentExtras()

        soundManager = SoundManager(this)
        registerActivityResultLaunchers()

        uploadID.setOnClickListener {
            // Handle upload ID button click
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        capturePhotoID.setOnClickListener {
            soundManager.playClickSound()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                // Handle capture photo button click
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureImageLauncher.launch(intent)
            }
        }

        binding.signupIDBTNExitButton.setOnClickListener {
            soundManager.playClickSound()
            startActivity(Intent(this@SignupIDActivity, SignupThirdActivity::class.java))
            finish()
        }
    }

    private fun navigateToNextPage(fileUrl: String?){
        soundManager = SoundManager(this@SignupIDActivity)
        soundManager.playClickSound()
        val intent = Intent(this, SignupFourthActivity::class.java)
        intent.putExtra("email" , email)
        intent.putExtra("password" , password)
        intent.putExtra("mobile" , mobile)
        intent.putExtra("firstName" , firstName)
        intent.putExtra("lastName" , lastName)
        intent.putExtra("dob" , dob)
        intent.putExtra("id" , id)
        intent.putExtra("address" , address)
        intent.putExtra("city" , city)
        intent.putExtra("state" , state)
        intent.putExtra("fileUrl", fileUrl)
        startActivity(intent)
    }
    private fun extractIntentExtras() {
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        mobile = intent.getStringExtra("mobile").toString()
        firstName = intent.getStringExtra("firstName").toString()
        lastName = intent.getStringExtra("lastName").toString()
        dob = intent.getStringExtra("dob").toString()
        id = intent.getStringExtra("id").toString()
        address = intent.getStringExtra("address").toString()
        city = intent.getStringExtra("city").toString()
        state = intent.getStringExtra("state").toString()
    }
    private fun findViews() {
        signUpIDHeader = findViewById(R.id.signup_ID_TVW_signUpIDHeader)
        exitButton = findViewById(R.id.signup_ID_BTN_exitButton)
        uploadID = findViewById(R.id.signup_ID_BTN_uploadID)
        capturePhotoID = findViewById(R.id.signup_ID_BTN_capturePhotoID)
        progressBarID = findViewById(R.id.signup_ID_progressBar)
    }

    private fun registerActivityResultLaunchers() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri = result.data?.data
                    imageUri?.let {
                        val imageStream = contentResolver.openInputStream(it)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        uploadImage(selectedImage)
                    } ?: run {
                        Log.e(TAG, "Image URI is null")
                        Toast.makeText(this, "Failed to get selected file", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting selected file", e)
                    Toast.makeText(this, "Error getting selected file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(TAG, "Result not OK for image selection")
                Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        captureImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        uploadImage(it)
                    } ?: run {
                        Log.e(TAG, "Bitmap is null")
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting captured file", e)
                    Toast.makeText(this, "Error getting captured file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(TAG, "Result not OK for image capture")
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadImage(bitmap: Bitmap) {
        progressBarID.visibility = View.VISIBLE

        val uniqueFileName = "$id-identity"
        filePath = convertBitmapToFile(bitmap, uniqueFileName)

        val requestFile = filePath.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", filePath.name, requestFile)

        // Add the email to the request body
        val email = intent.getStringExtra("email").toString()
        Log.d(TAG, "Email to be sent with image: $email")
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())

        if (email.isNullOrEmpty()) {
            progressBarID.visibility = View.INVISIBLE
            Toast.makeText(this, "User email is not available.", Toast.LENGTH_SHORT).show()
            return
        }

        retrofitInterface.uploadIDImage(body, emailBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressBarID.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    // Handle successful response
                    val result = response.body()
                    val fileUrl = result?.get("fileUrl")?.takeIf { it.isJsonPrimitive }?.asString
                    Log.d(TAG, "UploadResult: $result, fileUrl: $fileUrl")

                    // Check if the labelName is 'id'
                    if (result?.get("labelName")?.asString == "id") {
                        uploadSuccess = true
                        uploadMessage = "Image recognized as ID"
                        Toast.makeText(this@SignupIDActivity,"ID verified" , Toast.LENGTH_SHORT).show()
                        // Save the fileUrl for further use, e.g., pass it to the next activity
                        navigateToNextPage(fileUrl)
                    } else {
                        uploadSuccess = false
                        uploadMessage = "Image is not recognized as ID"
                        Toast.makeText(this@SignupIDActivity,"ID is not verified" , Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d(TAG, "Upload failed with response: ${response.errorBody()?.string()}")
                    uploadSuccess = false
                    uploadMessage = "Failed to upload image"
                    Toast.makeText(this@SignupIDActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressBarID.visibility = View.INVISIBLE
                // Handle failure
                Log.d(TAG, "Upload failed with error: ${t.message}")
                uploadSuccess = false
                uploadMessage = "Error: ${t.message}"
                Toast.makeText(this@SignupIDActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(cacheDir, fileName)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return file
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, launch the camera intent
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    captureImageLauncher.launch(intent)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}