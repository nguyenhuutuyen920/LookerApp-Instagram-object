package com.example.lookerapp.Post

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.lookerapp.HomeActivity
import com.example.lookerapp.Models.Post
import com.example.lookerapp.Models.User
import com.example.lookerapp.databinding.ActivityPostBinding
import com.example.lookerapp.utils.POST
import com.example.lookerapp.utils.POST_FOLDER
import com.example.lookerapp.utils.USER_NODE
import com.example.lookerapp.utils.uploadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    private var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, POST_FOLDER) { url ->
                if (url !== null) {
                    binding.selectImage.setImageURI(uri)
                    this.imageUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }


        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document().get().addOnSuccessListener {


                var user=it.toObject<User>()
                val post: Post = Post(
                    postUrl = imageUrl!!,
                    caption = binding.caption.editText?.text.toString(),
                    uid = Firebase.auth.currentUser!!.uid,
                    time = System.currentTimeMillis().toString()
                )

                Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                        .set(post)
                        .addOnSuccessListener {
                            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                            finish()
                        }

                }
            }


        }
    }
}