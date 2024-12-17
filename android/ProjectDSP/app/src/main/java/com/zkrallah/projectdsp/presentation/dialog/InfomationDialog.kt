package com.zkrallah.projectdsp.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zkrallah.projectdsp.R

@Composable
fun InformationDialog(
    title: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val info = if (title == "DHT Sensor")
                    "DHT Sensor measures the current temperature and humidity."
                else
                    "Heart Rate Sensor measures the current heart rate."

                Text(
                    text = info,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier

                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Dismiss",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InformationDialogPreview() {
    InformationDialog(title = "DHT Sensor") {}
}
