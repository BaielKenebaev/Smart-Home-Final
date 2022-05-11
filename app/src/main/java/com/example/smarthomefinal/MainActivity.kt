package com.example.smarthomefinal

import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.smarthomefinal.ManageActivity
import com.example.smarthomefinal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding
    private var cancellationSignal: CancellationSignal? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication Success !")
                    startActivity(Intent(this@MainActivity,ManageActivity::class.java))
                }

            }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkBiometricSupport()

        //init a firebase
        auth = Firebase.auth

        binding.fingerScan.
        setOnClickListener{
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Title of prompt")
                .setSubtitle("Authentication is required")
                .setDescription("This app uses figerprint protection to keep data secure")
                .setNegativeButton("Cancell",this.mainExecutor,DialogInterface.OnClickListener{ dialog,which ->
                    notifyUser("Authenticaion cancelled")
                }).build()
            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)
        }

        binding.btnLogin.setOnClickListener{
            doLogin()
        }

        binding.btnSignUp.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

    }

    private fun doLogin() {
        if(binding.gmailEditText.text.toString().isEmpty()){
            binding.gmailEditText.error = "Please enter the gmail"
            binding.gmailEditText.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.gmailEditText.text.toString()).matches()){
            binding.gmailEditText.error = "Please enter valid email"
            binding.gmailEditText.requestFocus()
            return
        }

        if(binding.passwordEditText.text.toString().isEmpty()){
            binding.passwordEditText.error = "Please enter the gmail"
            binding.passwordEditText.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(binding.gmailEditText.text.toString(),binding.passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }

    }

    private fun notifyUser(message: String){
        Toast.makeText(this,"$message",Toast.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {

        val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint authentication has not been enabled settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        } else true


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?){
        if(currentUser != null){
            if(currentUser.isEmailVerified){
                startActivity(Intent(this,ManageActivity::class.java))
                finish()
            }else {
                    Toast.makeText(baseContext,"Please verify your email",Toast.LENGTH_SHORT).show()
            }
        }else  {
            Toast.makeText(this , "Login failed", Toast.LENGTH_SHORT).show()
        }
    }
}