package com.mrlb.business.utils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class ReminderReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  ReminderUtils.show(context,intent.getIntExtra("id",0),intent.getStringExtra("title")?:"Due Reminder",intent.getStringExtra("text")?:"Payment due")
 }
}
