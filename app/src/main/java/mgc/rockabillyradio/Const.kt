package nk.rockabillyradio

/**
 * Created by kolyas on 29.09.2016.
 */
object Const {
    var LAST_FM_KEY = "3ccfc2669d19581d8ae2e40f5399042c"
    var RADIO_PATH = "http://lin3.ash.fast-serv.com:6026/stream_96"
    var TRACK_INFO_URL = "http://199.58.163.11/rr/now_playing.php"
    var PHOTO_LOAD_REFRESH_TIME = 20000
    var VIBRATE_TIME = 5
    var FOREGROUND_SERVICE = 101
    const val K: Long = 1024
    const val M = K * K
    const val G = M * K
    const val T = G * K

    interface ACTION {
        companion object {
            const val MAIN_ACTION = "mgc.tockabillyradio.action.main"
            const val PLAY_ACTION = "mgc.tockabillyradio.action.play"
            const val STARTFOREGROUND_ACTION = "mgc.tockabillyradio.action.startforeground"
            const val STOPFOREGROUND_ACTION = "mgc.tockabillyradio.action.stopforeground"
        }
    }
}