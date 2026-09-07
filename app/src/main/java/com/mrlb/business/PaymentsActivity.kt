package com.mrlb.business
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.model.Payment
import com.mrlb.business.databinding.ActivityListBinding
import kotlinx.coroutines.launch
class PaymentsActivity:AppCompatActivity(){
 private lateinit var b:ActivityListBinding;private val db by lazy{AppDatabase.get(this)}
 override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityListBinding.inflate(layoutInflater);setContentView(b.root);b.title.text="Payments / Due";b.add.text="+ Add Payment";b.add.setOnClickListener{dialog()};lifecycleScope.launch{db.payments().all().collect{list->b.list.removeAllViews();list.forEach{p->val v=TextView(this@PaymentsActivity);v.text="Customer #${p.customerId}   ₹${p.amount}\n${p.method}";v.textSize=17f;v.setPadding(16,18,16,18);b.list.addView(v)}}}}
 private fun dialog(){val v=LinearLayout(this);v.orientation=LinearLayout.VERTICAL;val f=mutableListOf<EditText>();fun e(h:String){val x=EditText(this);x.hint=h;v.addView(x);f.add(x)};e("Customer ID");e("Order ID (optional)");e("Amount");e("Cash / UPI / Bank");AlertDialog.Builder(this).setTitle("Add Payment").setView(v).setPositiveButton("Save"){_,_->lifecycleScope.launch{db.payments().insert(Payment(customerId=f[0].text.toString().toLongOrNull()?:0,orderId=f[1].text.toString().toLongOrNull(),amount=f[2].text.toString().toDoubleOrNull()?:0.0,method=f[3].text.toString().ifBlank{"Cash"}))}}.setNegativeButton("Cancel",null).show()}
}
