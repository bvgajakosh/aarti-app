package com.nexusnova.aartiapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexusnova.aartiapp.R
import com.nexusnova.aartiapp.adapter.AartiAdapter
import com.nexusnova.aartiapp.data.local.AppDatabase
import com.nexusnova.aartiapp.repository.AartiRepository
import com.nexusnova.aartiapp.repository.Resource
import com.nexusnova.aartiapp.data.remote.FirestoreService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AartiListActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aarti_list)

        recycler = findViewById(R.id.recyclerAartis)
        recycler.layoutManager = LinearLayoutManager(this)
        // Attach an empty adapter to prevent "no adapter" errors
        recycler.adapter = AartiAdapter(emptyList()) {}

        val catId = intent.getStringExtra("categoryId") ?: return
        val dao   = AppDatabase.getInstance(this).aartiDao()
        val repo  = AartiRepository(dao)

        // Optional debug: raw nested Firestore fetch
        FirestoreService.db
            .collection("aartiCategories")
            .document(catId)
            .collection("aartis")
            .get()
            .addOnSuccessListener { snap ->
                Log.d("AartiList", "RAW nested fetch: ${snap.size()} docs")
            }
            .addOnFailureListener { exc ->
                Log.e("AartiList", "RAW nested fetch failure", exc)
            }

        lifecycleScope.launch {
            repo.getAartisByCategory(catId).collectLatest { res ->
                Log.d("AartiList", "Repo emitted: $res")       // <-- add this line
                when (res) {
                    is Resource.Loading -> { /* show spinner if desired */ }
                    is Resource.Success -> {
                        Log.d("AartiList", "Success with ${res.data.size} items")  // <-- and this
                        recycler.adapter = AartiAdapter(res.data) { a ->
                            startActivity(
                                Intent(this@AartiListActivity, AartiDetailActivity::class.java)
                                    .putExtra("aartiId", a.id)
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e("AartiList", "Error fetching: ${res.message}")
                        recycler.adapter = AartiAdapter(res.data ?: emptyList()) { a ->
                            startActivity(
                                Intent(this@AartiListActivity, AartiDetailActivity::class.java)
                                    .putExtra("aartiId", a.id)
                            )
                        }
                    }
                }
            }
        }

    }
}
