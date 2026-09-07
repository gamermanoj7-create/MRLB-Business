package com.mrlb.business.utils
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mrlb.business.MainActivity

object ReminderUtils {
    private const val CHANNEL="due_reminders"
    fun schedule(context:Context,id:Int,whenMs:Long,title:String,text:String) {
        val nm=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(NotificationChannel(CHANNEL,"Due Reminders",NotificationManager.IMPORTANCE_DEFAULT))
        val alarm=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent=Intent(context,ReminderReceiver::class.java).apply{putExtra("title",title);putExtra("text",text);putExtra("id",id)}
        val receiver=PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if(Build.VERSION.SDK_INT>=31 && !alarm.canScheduleExactAlarms()) alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,receiver)
        else alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,receiver)
    }
    fun show(context:Context,id:Int,title:String,text:String) {
        if(Build.VERSION.SDK_INT>=33 && context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        val n=NotificationCompat.Builder(context,CHANNEL).setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(text).setAutoCancel(true).build()
        NotificationManagerCompat.from(context).notify(id,n)
    }
}
