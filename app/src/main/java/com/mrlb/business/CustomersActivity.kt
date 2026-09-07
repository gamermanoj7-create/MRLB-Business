package com.mrlb.business
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.model.Customer
import com.mrlb.business.databinding.ActivityListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
class CustomersActivity:AppCompatActivity(){
 private lateinit var b:ActivityListBinding;private val db by lazy{AppDatabase.get(this)}
 override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityListBinding.inflate(layoutInflater);setContentView(b.root);b.title.text="Customers";b.add.text="+ Add Customer"
  b.search.setOnQueryTextListener(object:SearchView.OnQueryTextListener{override fun onQueryTextSubmit(q:String)=false;override fun onQueryTextChange(q:String):Boolean{load(q);return true}})
  b.add.setOnClickListener{dialog(null)};load("")
 }
 private fun load(q:String){lifecycleScope.launch{db.customers().search(q).collectLatest{list->b.list.removeAllViews();list.forEach{c->val x=TextView(this@CustomersActivity);x.text="${c.name}\n${c.business}\n${c.phone}";x.textSize=18f;x.setPadding(16,18,16,18);x.setOnClickListener{dialog(c)};b.list.addView(x)}}}}
 private fun dialog(c:Customer?){val v=LinearLayout(this);v.orientation=LinearLayout.VERTICAL;fun e(h:String,t:String=""){val x=EditText(this);x.hint=h;x.setText(t);v.addView(x);fields.add(x)};fields.clear();e("Name",c?.name?:"");e("Phone",c?.phone?:"");e("Shop / Business",c?.business?:"");e("Address",c?.address?:"")
  AlertDialog.Builder(this).setTitle(if(c==null)"Add Customer" else "Edit Customer").setView(v).setPositiveButton("Save"){_,_->val a=fields;val n=Customer(c?.id?:0,a[0].text.toString(),a[1].text.toString(),a[2].text.toString(),a[3].text.toString());lifecycleScope.launch{if(c==null)db.customers().insert(n)else db.customers().update(n)}}.apply{if(c!=null)setNeutralButton("Delete"){_,_->lifecycleScope.launch{db.customers().delete(c)}}}.setNegativeButton("Cancel",null).show()}
 private val fields=mutableListOf<EditText>()
}
