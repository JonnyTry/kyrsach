package nk.rockabillyradio.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.wang.avi.AVLoadingIndicatorView
import io.github.nikhilbhutani.analyzer.DataAnalyzer
import nk.rockabillyradio.Const
import nk.rockabillyradio.Const.ACTION
import nk.rockabillyradio.R
import nk.rockabillyradio.audio.Player
import nk.rockabillyradio.connections.GetTrackInfo
import nk.rockabillyradio.notifications.NotificationService
import nk.rockabillyradio.notifications.NotificationService.Companion.context
import nk.rockabillyradio.views.CircularSeekBar
import nk.rockabillyradio.views.CircularSeekBar.OnCircularSeekBarChangeListener




@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {


   // var database = FirebaseDatabase.getInstance()
   // var myRef = database.getReference("message")
  //  val fab: View = findViewById(R.id.fab)



    private val PREFERENCE_FILE_KEY = "Preference"
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        activity = this
        sharedPref = getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
        try {
            app = this.packageManager.getApplicationInfo("nk.rockabillyradio", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            val toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT)
            toast.show()
            e.printStackTrace()
        }
        initialise()
        setCustomFont()
        startListenVolume()
        GetTrackInfo().execute()
        startRefreshing()
    }


    fun initialise() {
         background = findViewById(R.id.bckg)
        title_tv = findViewById(R.id.title_tv)
        volumeChanger = findViewById(R.id.circularSeekBar1)
        playing_animation = findViewById(R.id.playing_anim)
        playing_animation!!.visibility = View.GONE
        loading_animation = findViewById(R.id.load_animation)
        control_button = findViewById(R.id.control_button)
        control_button!!.setOnClickListener(controlButtonListener)

    }

    fun setCustomFont() {
        val tf = Typeface.createFromAsset(this.assets, "radio.ttf")
        title_tv!!.typeface = tf
    }

    fun startListenVolume() {
        Player.setVolume((100 - volumeChanger!!.progress) / 100f)
        volumeChanger!!.setOnSeekBarChangeListener(object : OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar, progress: Int, fromUser: Boolean) {
                Player.setVolume((100 - circularSeekBar.progress) / 100f)
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })
    }

    fun startPlayerService() {
        val serviceIntent = Intent(this@MainActivity, NotificationService::class.java)
        serviceIntent.action = ACTION.STARTFOREGROUND_ACTION
        startService(serviceIntent)
    }

    fun vibrate() {
        (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(Const.VIBRATE_TIME.toLong())
    }

    fun startRefreshing() {
        dataAnalyzer = DataAnalyzer(this)
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(Const.PHOTO_LOAD_REFRESH_TIME.toLong())
                        runOnUiThread { GetTrackInfo().execute() }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        t.start()
    }


    private var controlButtonListener = View.OnClickListener {
        if (controlIsActivated == false) {
            startPlayerService()
            control_button!!.setImageResource(R.drawable.pause)
            playing_animation!!.visibility = View.GONE
            loading_animation!!.visibility = View.VISIBLE
            control_button!!.visibility = View.GONE
            controlIsActivated = true
            vibrate()
        } else {
            Player.stop()
            control_button!!.setImageResource(R.drawable.play)
            playing_animation!!.visibility = View.GONE
            loading_animation!!.visibility = View.VISIBLE
            control_button!!.visibility = View.VISIBLE
            controlIsActivated = false
            vibrate()
        }
    }

    override fun onBackPressed() {
        Player.stop()
        super.onBackPressed()
    }



    companion object {
        var activity: Activity? = null
        var background: ImageView? = null
        var artist: String? = null
        var track: String? = null
        var album: String? = null
        var title_tv: TextView? = null
        var volumeChanger: CircularSeekBar? = null
        var playing_animation: AVLoadingIndicatorView? = null
        var loading_animation: AVLoadingIndicatorView? = null
        var control_button: ImageButton? = null
        var controlIsActivated = false
        var dataAnalyzer: DataAnalyzer? = null
        var app: ApplicationInfo? = null






    }
}