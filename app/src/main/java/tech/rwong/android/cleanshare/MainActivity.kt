package tech.rwong.android.cleanshare

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.rwong.android.cleanshare.ui.theme.CleanShareTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var text = "Android"
        var title = "Title"

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    text = it
                    title = "Cleaned Share"
                }
            }
        }

        val url = Regex("https?://.*").find(text)?.value
        if (url != null) {
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

                    text = text.replace(url, cleanUrl.toString())

                    setContent {
                        CleanShareTheme {
                            Greeting(title, text)
                        }
                    }
                }
            })
        }

        setContent {
            CleanShareTheme {
                Greeting(title, text)
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = Color.Transparent
//                ) {
//
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(title: String, message: String, modifier: Modifier = Modifier) {
    val activity = (LocalContext.current as? Activity)
    val clipboard = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
        title = {
            Text(text = "$title")
        },
        text = {
            Text(
                "$message"
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    activity?.finish()
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val clip: ClipData = ClipData.newPlainText("simple text", message)
                    clipboard.setPrimaryClip(clip)
                    activity?.finish()
                }
            ) {
                Text("Copy")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanShareTheme {
        Greeting("Title", "Android")
    }
}