package com.mrlb.business
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.databinding.ActivityMainBinding
import com.mrlb.business.utils.ExportUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
class MainActivity:AppCompatActivity(){
 private lateinit var b:ActivityMainBinding
 private val db by lazy{AppDatabase.get(this)}
 private val money=NumberFormat.getCurrencyInstance(Locale("en","IN"))
 override fun onCreate(s:Bundle?){super.onCreate(s);
  if (android.os.Build.VERSION.SDK_INT >= 33 &&
      checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 9001)
  }b=ActivityMainBinding.inflate(layoutInflater);setContentView(b.root)
  lifecycleScope.launch{combine(db.orders().sales(),db.payments().collected(),db.orders().due(),db.expenses().total()){a,c,d,e->listOf(a,c,d,e)}.collect{a->
   b.sales.text="Sales  "+money.format(a[0]);b.collection.text="Collected  "+money.format(a[1]);b.due.text="Due  "+money.format(a[2]);b.expense.text="Expense  "+money.format(a[3]);b.profit.text="Estimated Profit  "+money.format(a[0]-a[3])}}
  b.customers.setOnClickListener{startActivity(Intent(this,CustomersActivity::class.java))}
  b.orders.setOnClickListener{startActivity(Intent(this,OrdersActivity::class.java))}
  b.payments.setOnClickListener{startActivity(Intent(this,PaymentsActivity::class.java))}
  b.expenses.setOnClickListener{startActivity(Intent(this,ExpensesActivity::class.java))}
  b.settings.setOnClickListener{startActivity(Intent(this,SettingsActivity::class.java))}
 }
}
