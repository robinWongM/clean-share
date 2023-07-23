package tech.rwong.android.cleanshare

import ShareViewModel
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import tech.rwong.android.cleanshare.ui.theme.CleanShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain an instance of the ViewModel
        val viewModel = ShareViewModel()

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.resolveCleanLink(it)
                }
            }
        }

        setContent {
            CleanShareTheme {
                Greeting(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(viewModel: ShareViewModel) {
    val resolvedLink = viewModel.resolvedLink.collectAsState()

    ShareDialog(
        title = "Android",
        message = resolvedLink.value ?: run {
            "Loading..."
        },
        icon = { resolvedLink.value ?.run {
            Icon(Icons.Filled.Done, contentDescription = null)
        } ?: run {
            CircularProgressIndicator()
        } }
    )
}

@Composable
fun ShareDialog(title: String, message: String, icon: @Composable () -> Unit) {
    val activity = (LocalContext.current as? Activity)

    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .wrapContentHeight(),
        icon = icon,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
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
            CopyToWeChat(message)
        },
    )
}

@Composable
private fun CopyToWeChat(
    text: String,
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val packageManager = context.packageManager

    TextButton(
        onClick = {
            val clip: ClipData = ClipData.newPlainText("simple text", text)
            clipboard.setPrimaryClip(clip)

            val packageName =
                "com.tencent.mm" // Replace this with the package name of the other app
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            if (intent != null) {
                // The other app is installed, so start it
                startActivity(context, intent, null)
            } else {
                // The other app is not installed, you can show a message or handle the situation as needed
            }

            (context as? Activity)?.finish()
        }
    ) {
        Text("Copy to WeChat")
    }
}