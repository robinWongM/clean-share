import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ShareViewModel : ViewModel() {
    private val client = OkHttpClient()

    private val _resolvedLink = MutableStateFlow<String?>(null)
    val resolvedLink: StateFlow<String?> = _resolvedLink

    fun resolveCleanLink(link: String) {
        Log.i("ShareViewModel", "resolving")

        val url = Regex("https?://.*").find(link)?.value ?: return

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "onFailure: ", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val bUrl = response.request.url
                // remove query params from url
                val cleanUrl = HttpUrl.Builder()
                    .scheme(bUrl.scheme)
                    .host(bUrl.host)
                    .port(bUrl.port)
                    .encodedPath(bUrl.encodedPath)
                    .build()

                Log.i("ShareViewModel", "resolved $cleanUrl")

                _resolvedLink.value = link.replace(url, cleanUrl.toString())
            }
        })
    }
}
