package com.mrlb.business
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrlb.business.databinding.ActivityPinBinding
import com.mrlb.business.utils.SecurityUtils
class PinActivity:AppCompatActivity(){
 override fun onCreate(s:Bundle?){
  super.onCreate(s); val b=ActivityPinBinding.inflate(layoutInflater);setContentView(b.root)
  val p=getSharedPreferences("auth",0); val saved=p.getString("pin_hash",null)
  b.title.text=if(saved==null)"Set PIN" else "Enter PIN"
  b.save.setOnClickListener{
   val pin=b.pin.text.toString()
   if(pin.length<4){Toast.makeText(this,"PIN must be 4+ digits",Toast.LENGTH_SHORT).show();return@setOnClickListener}
   val hash=SecurityUtils.hash(pin)
   if(saved==null){p.edit().putString("pin_hash",hash).putBoolean("pin_enabled",true).putBoolean("unlocked",true).apply();finish()}
   else if(hash==saved){p.edit().putBoolean("unlocked",true).apply();finish()}
   else Toast.makeText(this,"Wrong PIN",Toast.LENGTH_SHORT).show()
  }
 }
 override fun onPause(){super.onPause()}
}
