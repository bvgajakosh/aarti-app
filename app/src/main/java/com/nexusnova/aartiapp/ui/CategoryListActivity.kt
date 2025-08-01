package com.nexusnova.aartiapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexusnova.aartiapp.R
import com.nexusnova.aartiapp.data.local.AppDatabase
import com.nexusnova.aartiapp.repository.CategoryRepository
import com.nexusnova.aartiapp.repository.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryListActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        recycler = findViewById(R.id.recyclerCategories)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val dao = AppDatabase.getInstance(this).categoryDao()
        val repo = CategoryRepository(dao)

        lifecycleScope.launch {
            repo.getCategories().collectLatest { res ->
                when (res) {
                    is Resource.Loading -> {
                        // you could show a spinner here
                    }

                    is Resource.Success -> {
                        recycler.adapter = CategoryAdapter(res.data) { cat ->
                            startActivity(
                                Intent(this@CategoryListActivity, AartiListActivity::class.java)
                                    .putExtra("categoryId", cat.id)
                            )
                        }
                    }

                    is Resource.Error -> {
                        Log.e("CategoryList", res.message)
                        Toast.makeText(this@CategoryListActivity, res.message, Toast.LENGTH_LONG)
                            .show()

                        recycler.adapter = CategoryAdapter(res.data ?: emptyList()) { cat ->
                            startActivity(
                                Intent(this@CategoryListActivity, AartiListActivity::class.java)
                                    .putExtra("categoryId", cat.id)
                            )
                        }
                    }
                }
            }
        }
    }
}
