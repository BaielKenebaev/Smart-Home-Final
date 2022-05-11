package com.example.smarthomefinal

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.smarthomefinal.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding:ActivitySignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }
    }

    fun signUpUser(){
        if (binding.gmailEditTextSignUp.text.toString().isEmpty()) with(binding){
            gmailEditTextSignUp.error = "Please enter email"
            gmailEditTextSignUp.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.gmailEditTextSignUp.text.toString()).matches()) with(binding){
            gmailEditTextSignUp.error = "Please enter valid email"
            gmailEditTextSignUp.requestFocus()
            return
        }

        if(binding.passwordEditTextSignUp.text.toString().isEmpty()) with(binding){
            passwordEditTextSignUp.error = "Please enter  password"
            passwordEditTextSignUp.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(binding.gmailEditTextSignUp.text.toString(), binding.passwordEditTextSignUp.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                        }



                } else {
                    // If sign in fails, display a message to the user.But if user exist
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }

    }


}