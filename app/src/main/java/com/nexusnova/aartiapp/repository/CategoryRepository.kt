package com.nexusnova.aartiapp.repository

import com.nexusnova.aartiapp.data.local.CategoryDao
import com.nexusnova.aartiapp.data.local.CategoryEntity
import com.nexusnova.aartiapp.data.remote.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class CategoryRepository(
    private val dao: CategoryDao,
    private val firestore: FirebaseFirestore = FirestoreService.db
) {
    fun getCategories(): Flow<Resource<List<CategoryEntity>>> = flow {
        emit(Resource.Loading())
        val cached = dao.getAll().first()                    // off main thread
        emit(Resource.Success(cached))

        try {
            val snap = firestore.collection("aartiCategories").get().await()
            val remote = snap.documents.map { d ->
                CategoryEntity(
                    id       = d.id,
                    title    = d.getString("title") ?: "",
                    imageUrl = d.getString("imageURL") ?: ""
                )
            }
            dao.upsert(remote)                                 // off main thread
            emit(Resource.Success(dao.getAll().first()))       // off main thread
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Could not load categories: ${e.message}", cached))
        }
    }
        .flowOn(Dispatchers.IO) // ← ensure all of the above runs on IO
}
