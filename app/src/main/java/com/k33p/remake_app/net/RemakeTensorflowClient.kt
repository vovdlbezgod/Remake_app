package com.k33p.remake_app.net

import android.content.ContentValues.TAG
import android.util.Log
import com.k33p.remaketensorflowservingclient.helpers.RemakeTensorflowClientConfig
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit


interface  RemakeTensorflowClient{
    fun getPhotoSegmentation(photo: File): String?
    fun getPhotoImpainting(photo: File, mask: File): String?
}

// TODO rewrite this staff
// Hardcoded for TensorflowServing Retrofit Client
class TensorflowServingRemakeClient(val config: RemakeTensorflowClientConfig)

    : RemakeTensorflowClient {
    // TODO refactor this hack after server refactoring (adding cache for each user)
    override fun getPhotoImpainting(photo: File, mask: File): String? {
        try {
            val reqBody: RequestBody = buildInpRequestBody(photo, mask)
            val request = buildRequest(config.serverEndpoint, config.serverInpaintingPath,
                    reqBody)

            val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

            client.connectTimeoutMillis()
            val response = client.newCall(request).execute()

            Log.i("Response", "uploadImage:" + response.body()!!.source().toString())

            return response.body()!!.string()

        } catch (e: Exception) {
            Log.e(TAG, "Error: " + e.localizedMessage)
            return e.localizedMessage
        }


    }

    override fun getPhotoSegmentation(photo: File): String? {
        try {

            val reqBody: RequestBody = buildSegRequestBody(photo)

            val request = buildRequest(config.serverEndpoint, config.serverSegmentationPath,
                                      reqBody)
            //println(bodyToString(request))
            val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

            client.connectTimeoutMillis()
            val response = client.newCall(request).execute()

            Log.i("Response", "uploadImage:"+ response.body()!!.source().toString())

            return response.body()!!.string()

        } catch (e : Exception) {
                Log.e(TAG, "Error: " + e.localizedMessage)
                return e.localizedMessage
        }

    }


    private fun buildRequest(serverEndpoint : String, serverPath: String,
                             req: RequestBody) =
            Request.Builder()
            .url("$serverEndpoint$serverPath")
            .post(req)
            .build()

    private fun buildSegRequestBody(photo: File) = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(config.userIdKey, config.userId)
            .addFormDataPart(config.imageNameKey, config.imageName,
                    RequestBody.create(MediaType.parse("image/jpg"), photo))
            .build()


    // TODO delete this hack after server refactoring (adding cache for each user)
    private fun buildInpRequestBody(photo: File, mask: File) = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(config.userIdKey, config.userId)
            .addFormDataPart(config.imageNameKey, config.imageName,
                    RequestBody.create(MediaType.parse("image/jpg"), photo))
            .addFormDataPart(config.maskNameKey, config.maskName,
                    RequestBody.create(MediaType.parse("image/jpg"), mask))
            .build()

}

