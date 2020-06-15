package nk.rockabillyradio.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import nk.rockabillyradio.R
import nk.rockabillyradio.activity.MainActivity

/**
 * Created by kolyas on 01.10.2016.
 */
object Player {
    var exoPlayer: SimpleExoPlayer? = null
    fun start(URL: String?, context: Context?) {
        if (exoPlayer != null) {
            exoPlayer!!.stop()
        }
        val URI = Uri.parse(URL)
        Log.e("URL", URL)
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(Util.getUserAgent(context, "nk.rockabillyradio"))
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(URI)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context)
        // Prepare the player with the SmoothStreaming media source.
        exoPlayer!!.prepare(mediaSource)
        exoPlayer!!.playWhenReady = true
        exoPlayer!!.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.e("playbackState", exoPlayer!!.playbackState.toString() + "")
                // This state if player is ready to work and loaded all data
                if (playbackState == 3) {
                    MainActivity.playing_animation!!.visibility = View.VISIBLE
                    MainActivity.loading_animation!!.visibility = View.GONE
                    MainActivity.control_button!!.visibility = View.VISIBLE
                    MainActivity.control_button!!.setImageResource(R.drawable.pause)
                }
            }
        })
    }

    fun stop() {
        if (exoPlayer != null) {
            exoPlayer!!.stop()
        }
    }

    fun setVolume(volume: Float) {
        if (exoPlayer != null) {
            exoPlayer!!.volume = volume
        }
    }
}