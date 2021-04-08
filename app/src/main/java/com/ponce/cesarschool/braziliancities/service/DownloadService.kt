package com.ponce.cesarschool.braziliancities.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ponce.cesarschool.braziliancities.R
import com.ponce.cesarschool.braziliancities.ui.activity.MainActivity
import com.ponce.cesarschool.braziliancities.util.extension.unzip
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class DownloadService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("WrongConstant", "LongLogTag")
    override fun onHandleWork(intent: Intent) {
        sendNotification("Download Iniciado.", "Baixando arquivos")

        val urlsList = intent.getStringArrayListExtra("urls")
        if (urlsList == null) {
            Log.e(TAG, "onHandleWork: Invalid urls")
            return
        }

        urlsList.forEach {
            val url = URL(it)
            val fileName = File(url.path).name;
            val file = File(filesDir, fileName);
            if(!file.exists()){
                Log.d(TAG, "Baixando ${fileName} ..")
                try {
                    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    urlConnection.setRequestMethod("GET")
                    urlConnection.setDoOutput(false)
                    urlConnection.connect()

                    val inputStream  = urlConnection.inputStream
                    val fileOutput = FileOutputStream(file)

                    val buffer = ByteArray(1024)
                    var bufferLength = 0

                    while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                        fileOutput.write(buffer, 0, bufferLength)
                    }
                    fileOutput.close()
                }catch (e: MalformedURLException) {
                    e.printStackTrace();
                } catch (e: IOException) {
                    e.printStackTrace();
                }

                if(file.extension=="zip"){
                    if(file.unzip()) file.delete()
                }
            }else{
                Log.d(TAG, "Arquivo ${fileName} já existe, pulando ..")
            }
        }

        sendNotification("Download Finalizado", "Arquivos baixados.")

        if(MainActivity.running){
            val intent = Intent(this, MainActivity::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String, text: String){
        createNotificationChannel();

        val intent = Intent(this, MainActivity::class.java);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var builder = NotificationCompat.Builder(this, "download")
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        builder.setContentTitle(title)
            .setContentText(text)

        with(NotificationManagerCompat.from(this)) {
            notify(10, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.getString(R.string.channel_name)
            val descriptionText = this.getString(R.string.channel_description)
            val channel = NotificationChannel(
                "download",
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(MainActivity.TAG, "Stop Service!");
    }

    companion object {
        const val INTENT_SERVICE_ID = 1004
        const val TAG = "DownloadService"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, DownloadService::class.java, INTENT_SERVICE_ID, intent)
        }

        fun isRunning(context: Context): Boolean {
            val scheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            var hasBeenScheduled = false
            for (jobInfo in scheduler.allPendingJobs) {
                if (jobInfo.id == INTENT_SERVICE_ID) {
                    hasBeenScheduled = true
                    break
                }
            }
            return hasBeenScheduled
        }
    }
}