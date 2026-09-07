package com.mrlb.business
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.model.*
import com.mrlb.business.databinding.ActivityListBinding
import kotlinx.coroutines.launch
class OrdersActivity:AppCompatActivity(){
 private lateinit var b:ActivityListBinding;private val db by lazy{AppDatabase.get(this)}
 override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityListBinding.inflate(layoutInflater);setContentView(b.root);b.title.text="Orders";b.add.text="+ New Order";b.add.setOnClickListener{dialog()};lifecycleScope.launch{db.orders().all().collect{list->b.list.removeAllViews();list.forEach{o->val v=TextView(this@OrdersActivity);v.text="#ORD-${o.id}   Total ₹${o.total}\nPaid ₹${o.paid}   Due ₹${(o.total-o.paid).coerceAtLeast(0.0)}\n${o.status}";v.textSize=17f;v.setPadding(16,18,16,18);b.list.addView(v)}}}}
 private fun dialog(){val v=LinearLayout(this);v.orientation=LinearLayout.VERTICAL;val f=mutableListOf<EditText>();fun e(h:String){val x=EditText(this);x.hint=h;v.addView(x);f.add(x)};e("Customer ID");e("Product");e("Quantity");e("Rate");e("Paid now");e("Due date (optional YYYY-MM-DD)")
  AlertDialog.Builder(this).setTitle("New Order").setView(v).setPositiveButton("Save"){_,_->lifecycleScope.launch{val q=f[2].text.toString().toDoubleOrNull()?:0.0;val r=f[3].text.toString().toDoubleOrNull()?:0.0;val total=q*r;val paid=f[4].text.toString().toDoubleOrNull()?:0.0;val id=db.orders().insert(Order(customerId=f[0].text.toString().toLongOrNull()?:0,total=total,paid=paid));db.orders().insertItems(listOf(OrderItem(orderId=id,product=f[1].text.toString(),quantity=q,rate=r)))}}.setNegativeButton("Cancel",null).show()}
}
