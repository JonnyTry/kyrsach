package nk.rockabillyradio.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import nk.rockabillyradio.Const
import nk.rockabillyradio.Const.ACTION
import nk.rockabillyradio.R
import nk.rockabillyradio.activity.MainActivity
import nk.rockabillyradio.audio.Player

class NotificationService : Service() {
    var status: Notification? = null
    var isPause = true
    private fun showNotification(pos: Int) {
        val views = RemoteViews(packageName,
                R.layout.status_bar)
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = ACTION.MAIN_ACTION
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)
        val playIntent = Intent(this, NotificationService::class.java)
        playIntent.action = ACTION.PLAY_ACTION
        val pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0)
        val closeIntent = Intent(this, NotificationService::class.java)
        closeIntent.action = ACTION.STOPFOREGROUND_ACTION
        val pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0)
        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent)
        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent)
        if (pos == 0) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause_ntf)
        }
        if (pos == 1) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause_ntf)
            if (MainActivity.control_button != null) {
                MainActivity.control_button!!.setImageResource(R.drawable.play)
                MainActivity.playing_animation!!.visibility = View.GONE
                MainActivity.loading_animation!!.visibility = View.VISIBLE
                MainActivity.control_button!!.visibility = View.GONE
                MainActivity.controlIsActivated = true
            }
        }
        if (pos == 2) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.play_ntf)
            if (MainActivity.control_button != null) {
                MainActivity.control_button!!.setImageResource(R.drawable.play)
                MainActivity.playing_animation!!.visibility = View.GONE
                MainActivity.loading_animation!!.visibility = View.GONE
                MainActivity.control_button!!.visibility = View.VISIBLE
                MainActivity.controlIsActivated = false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "nk.rockabillyradio"
            val channelName = "Rockabilly Radio Service"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.RED
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            status = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.radio)
                    .setCustomContentView(views)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .build()
            startForeground(Const.FOREGROUND_SERVICE, status)
        } else {
            status = Notification.Builder(this).build()
            status!!.contentView = views
            status!!.flags = Notification.FLAG_ONGOING_EVENT
            status!!.icon = R.drawable.radio
            status!!.contentIntent = pendingIntent
            startForeground(Const.FOREGROUND_SERVICE, status)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        context = this
        if (intent.action == ACTION.STARTFOREGROUND_ACTION) {
            isPause = false
            showNotification(0)
            Player.start(Const.RADIO_PATH, this)
        } else if (intent.action == ACTION.PLAY_ACTION) {
            if (!isPause) {
                showNotification(2)
                Player.stop()
                isPause = true
            } else {
                showNotification(1)
                isPause = false
                Player.start(Const.RADIO_PATH, this)
            }
        } else if (intent.action ==
                ACTION.STOPFOREGROUND_ACTION) {
            if (MainActivity.control_button != null) {
                MainActivity.control_button!!.setImageResource(R.drawable.play)
                MainActivity.playing_animation!!.visibility = View.GONE
                MainActivity.loading_animation!!.visibility = View.GONE
                MainActivity.control_button!!.visibility = View.VISIBLE
                MainActivity.controlIsActivated = false
            }
            Player.stop()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        var context: Context? = null
    }
}