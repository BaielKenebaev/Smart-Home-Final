package com.example.smarthomefinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smarthomefinal.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.smarthomefinal.databinding.ActivityManageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
class ManageActivity : AppCompatActivity() {
    private lateinit var request: Request
    private lateinit var binding: ActivityManageBinding
    private lateinit var pref: SharedPreferences
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("MyPref", MODE_PRIVATE)
        onClickSaveIp()
        getIp()
        binding.apply {
            bLed1.setOnClickListener(onClickListener())
            bLed2.setOnClickListener(onClickListener())
            bLed3.setOnClickListener(onClickListener())
        }

        binding.btnSignOut.setOnClickListener{
            signOut()
            startActivity(Intent(this,MainActivity::class.java))

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sync) post("temperature")
        return true
    }

    private fun onClickListener(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                R.id.bLed1 -> { post("led1") }
                R.id.bLed2 -> { post("led2") }
                R.id.bLed3 -> { post("led3") }
            }
        }
    }

    private fun getIp() = with(binding){
        val ip = pref.getString("ip", "")
        if(ip != null){
            if(ip.isEmpty()) edIp.setText(ip)
        }
    }

    private fun onClickSaveIp() = with(binding){
        bSave.setOnClickListener {
            if(edIp.text.isNotEmpty())saveIp(edIp.text.toString())
        }
    }

    private fun saveIp(ip: String){
        val editor = pref.edit()
        editor.putString("ip", ip)
        editor.apply()
    }

    private fun post(post: String){
        Thread{

            request = Request.Builder().url("http://${binding.edIp.text}/$post").build()
            try {
                var response = client.newCall(request).execute()
                if(response.isSuccessful){
                    val resultText = response.body()?.string()
                    runOnUiThread {
//                        val temp = resultText
//                        val t1:String=temp.toString()
//                        val t2= t1.subSequence(0,5)
//
//
//
//                        val t3=t1.subSequence(5,10)
//                        binding.tvTemp.text =t2.padEnd(6, 'C')
//                        binding.tvHumidity.text = t3.padEnd(6, '%')

                    }

                }

            } catch (i: IOException) {

            }

        }.start()
    }

    private fun signOut(){
        var auth: FirebaseAuth
        Firebase.auth.signOut()
    }

}




