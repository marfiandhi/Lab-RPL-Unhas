package divascion.marfiandhi.labrplunhas.service

import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.config.Config
import divascion.marfiandhi.labrplunhas.view.login.LoginActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var config: Config

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if(p0?.data!=null) {
            config = Config()
            sendNotification(p0)
        }
    }
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        config.title = data["title"]
        config.content = data["content"]
        config.imageUrl = data["imageUrl"]
        val intent = Intent(this, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val builder = NotificationCompat.Builder(this, "LAUNCH_APP")
            .setSmallIcon(R.drawable.ic_school_black_24dp)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(19204214, builder.build())
        }
        Log.e("title", config.title)
        Log.e("body", config.content)
        Log.e("title hard", remoteMessage.notification?.title)
        Log.e("body hard", remoteMessage.notification?.body)
    }
}
