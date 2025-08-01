package com.nexusnova.aartiapp.repository

import com.nexusnova.aartiapp.data.local.AartiDao
import com.nexusnova.aartiapp.data.local.AartiEntity
import com.nexusnova.aartiapp.data.remote.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/** Simple wrapper to represent loading, success or error states */
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
}

class AartiRepository(
    private val dao: AartiDao,
    private val firestore: FirebaseFirestore = FirestoreService.db
) {
    /** Unfiltered fetch (you can still use this elsewhere) */
    fun getAartis(): Flow<Resource<List<AartiEntity>>> = flow {
        emit(Resource.Loading<List<AartiEntity>>())
        val cached = dao.getAll().first()
        emit(Resource.Success(cached))
        try {
            val snap = firestore.collection("aarti").get().await()
            val remote = snap.documents.map { d ->
                AartiEntity(
                    id          = d.id,
                    title       = d.getString("title")       ?: "",
                    description = d.getString("description") ?: "",
                    imageURL    = d.getString("imageURL")    ?: "",
                    mp3URL      = d.getString("mp3URL")      ?: "",
                    prime       = d.getString("prime")       ?: "",
                    categoryId  = d.getString("categoryId")  ?: ""
                )
            }
            dao.upsert(remote)
            emit(Resource.Success(dao.getAll().first()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Could not load Aartis: ${e.message}", cached))
        }
    }.flowOn(Dispatchers.IO)

    /** Fetch Aartis only for a given category (nested sub-collection) */
    fun getAartisByCategory(catId: String): Flow<Resource<List<AartiEntity>>> = flow {
        emit(Resource.Loading<List<AartiEntity>>())

        // 1) Emit cached items for this category
        val cached = dao.getByCategory(catId).first()
        emit(Resource.Success(cached))

        try {
            // 2) Fetch from Firestore nested path
            val snap = firestore
                .collection("aartiCategories")
                .document(catId)
                .collection("aartis")
                .get()
                .await()

            // 3) Map documents into entities—use the passed-in catId explicitly
            val remote = snap.documents.map { d ->
                AartiEntity(
                    id          = d.id,
                    title       = d.getString("title")       ?: "",
                    description = d.getString("description") ?: "",
                    imageURL    = d.getString("imageURL")    ?: "",
                    mp3URL      = d.getString("mp3URL")      ?: "",
                    prime       = d.getString("prime")       ?: "",
                    categoryId  = catId
                )
            }

            // 4) Cache into Room
            dao.upsert(remote)

            // 5) Emit freshly cached list
            val fresh = dao.getByCategory(catId).first()
            emit(Resource.Success(fresh))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Could not load Aartis: ${e.message}", cached))
        }
    }.flowOn(Dispatchers.IO)
}
