package nk.rockabillyradio.connections

import android.os.AsyncTask
import nk.rockabillyradio.Const
import nk.rockabillyradio.activity.MainActivity
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset

class GetTrackInfo : AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val doc = Jsoup.connect(Const.TRACK_INFO_URL).get()
            val first_letter = doc.getElementsByClass("boxed").select("p")[1].toString().substring(29, doc.getElementsByClass("boxed").select("p")[1].toString().length)
            val parts = first_letter.split("<br> <strong>Track:</strong> ").toTypedArray()
            val first = parts[0]
            MainActivity.artist = first
            val second = parts[1]
            MainActivity.track = second.substring(0, second.length - 9)
            readJsonFromUrl("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + MainActivity.artist!!.replace(" ", "%20") + "&api_key=" + Const.LAST_FM_KEY + "&format=json".replace(" ", "%20"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)

    }

    companion object {
        @Throws(IOException::class)
        fun readJsonFromUrl(url: String?): String {
            val `is` = URL(url).openStream()
            return try {
                val rd = BufferedReader(
                        InputStreamReader(`is`, Charset.forName("UTF-8")))
                readAll(rd)
            } finally {
                `is`.close()
            }
        }

        @Throws(IOException::class)
        private fun readAll(rd: Reader): String {
            val sb = StringBuilder()
            var cp: Int
            while (rd.read().also { cp = it } != -1) {
                sb.append(cp.toChar())
            }
            var dataJsonObj: JSONObject
            //   try {
            MainActivity.album = ""
            return sb.toString()
        }

        fun isError(dataJsonObj: JSONObject): Boolean {
            return !dataJsonObj.toString().contains("{\"error\":6,\"message\":\"The artist you supplied could not be found\",\"links\":[]}")
        }
    }
}