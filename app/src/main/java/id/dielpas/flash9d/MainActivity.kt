import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Flash9DTheme {
                Surface(color = MaterialTheme.colors.background) {
                    FlashlightControl()
                }
            }
        }
    }
}

@Composable
fun FlashlightControl() {
    val context = LocalContext.current
    val cameraManager = context.getSystemService<CameraManager>()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                toggleFlashlight(cameraManager)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Toggle Flashlight")
        }
    }
}

fun toggleFlashlight(cameraManager: CameraManager?) {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        try {
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            cameraId?.let { id ->
                val torchMode = cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                if (torchMode == true) {
                    val state = cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.FLASH_STATE)
                    when (state) {
                        CameraCharacteristics.FLASH_STATE_OFF -> {
                            cameraManager.setTorchMode(id, true)
                        }
                        CameraCharacteristics.FLASH_STATE_ON -> {
                            cameraManager.setTorchMode(id, false)
                        }
                        else -> {
                            // Torch state is unknown
                        }
                    }
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    } else {
        // Camera permission not granted
        ActivityCompat.requestPermissions(
            context as MainActivity,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_CAMERA
        )
    }
}

const val PERMISSION_CAMERA = 1001
