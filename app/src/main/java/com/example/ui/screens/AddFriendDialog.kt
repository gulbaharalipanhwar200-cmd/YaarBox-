package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun AddFriendDialog(
  onDismiss: () -> Unit,
  onAddFriend: (name: String, phone: String, status: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var status by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = YaarDarkCard,
    title = {
      Text(
        text = "Add New Yaar (نیا دوست بنائیں)",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = YaarTextPrimary,
          fontSize = 18.sp
        )
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Friend's Name") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_friend_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = YaarGreen,
            unfocusedBorderColor = YaarDarkBorder,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number / Username") },
          placeholder = { Text("+92 300 1234567") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_friend_phone_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = YaarGreen,
            unfocusedBorderColor = YaarDarkBorder,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = status,
          onValueChange = { status = it },
          label = { Text("Status / Bio") },
          placeholder = { Text("Online • Ready to chat") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_friend_status_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = YaarGreen,
            unfocusedBorderColor = YaarDarkBorder,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary
          )
        )
      }
    },
    confirmButton = {
      ElevatedButton(
        onClick = {
          if (name.isNotBlank()) {
            onAddFriend(name, phone, status)
          }
        },
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = YaarGreen,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_add_friend_button")
      ) {
        Text("Add Yaar", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_add_friend_button")
      ) {
        Text("Cancel", color = YaarTextSecondary)
      }
    }
  )
}
