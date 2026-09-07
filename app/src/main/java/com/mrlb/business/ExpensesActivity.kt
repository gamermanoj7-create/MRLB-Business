package com.mrlb.business
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.model.Expense
import com.mrlb.business.databinding.ActivityListBinding
import kotlinx.coroutines.launch
class ExpensesActivity:AppCompatActivity(){
 private lateinit var b:ActivityListBinding;private val db by lazy{AppDatabase.get(this)}
 override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityListBinding.inflate(layoutInflater);setContentView(b.root);b.title.text="Expenses";b.add.text="+ Add Expense";b.add.setOnClickListener{dialog()};lifecycleScope.launch{db.expenses().all().collect{list->b.list.removeAllViews();list.forEach{e->val v=TextView(this@ExpensesActivity);v.text="${e.category}   ₹${e.amount}\n${e.note}";v.textSize=17f;v.setPadding(16,18,16,18);b.list.addView(v)}}}}
 private fun dialog(){val v=LinearLayout(this);v.orientation=LinearLayout.VERTICAL;val f=mutableListOf<EditText>();fun e(h:String){val x=EditText(this);x.hint=h;v.addView(x);f.add(x)};e("Category: Material / Labour / Printing / Transport / Other");e("Amount");e("Note");AlertDialog.Builder(this).setTitle("Add Expense").setView(v).setPositiveButton("Save"){_,_->lifecycleScope.launch{db.expenses().insert(Expense(category=f[0].text.toString(),amount=f[1].text.toString().toDoubleOrNull()?:0.0,note=f[2].text.toString()))}}.setNegativeButton("Cancel",null).show()}
}
