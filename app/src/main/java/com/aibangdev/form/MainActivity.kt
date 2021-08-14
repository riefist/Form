package com.aibangdev.form

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aibangdev.form.ui.theme.FormTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val formState = mutableListOf<FormTextFieldState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = "Pengisian Syarat Layanan")
                                }
                            )
                        }
                    ) {
                        val jsonString = Utils.getJsonDataFromAsset(this, "form.json")
                        jsonString?.let { json ->
                            val obj = JSONObject(json)
                            val title = obj.getString("title")
                            val description = obj.getString("description")

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it)
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .weight(1F)
                                ) {
                                    FormHeader(title, description)

                                    Spacer(modifier = Modifier.height(24.dp))

                                    FormContent(obj)

                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                ButtonKirim()

                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ButtonKirim() {
        Button(
            onClick = {
                Toast.makeText(
                    this@MainActivity,
                    formState.find { it.key == "nama_saksi_1" }?.text,
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Kirim")
        }
    }

    @Composable
    private fun FormContent(obj: JSONObject) {
        val forms = obj.getJSONArray("formfields")

        for (i in 0 until forms.length()) {
            val form = forms.getJSONObject(i)
            val label = form.getString("label")
            val key = form.getString("key")
            val comment = form.getString("comment")
            val commentText = if (comment.equals("null")) null else comment

            val textFieldState = FormTextFieldState(key = key)
            formState.add(textFieldState)
            val state = remember { textFieldState }

            FormTextField(
                label = label,
                value = state.text,
                onValueChange = { state.text = it },
                comment = commentText
            )
        }
    }

    @Composable
    private fun FormHeader(title: String, description: String) {
        Text(text = title)
        Text(text = description)
    }

}

class FormTextFieldState(
    var key: String
) {
    var text by mutableStateOf("")
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    comment: String? = null
) {
    Column() {
        Text(text = label)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
        )

        comment?.let {
            Text(
                text = it,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

}
