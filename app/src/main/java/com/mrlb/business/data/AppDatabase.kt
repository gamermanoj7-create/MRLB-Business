package com.mrlb.business.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mrlb.business.model.*
@Database(entities=[Customer::class,Order::class,OrderItem::class,Payment::class,Expense::class],version=1,exportSchema=false)
abstract class AppDatabase:RoomDatabase(){
 abstract fun customers():CustomerDao
 abstract fun orders():OrderDao
 abstract fun payments():PaymentDao
 abstract fun expenses():ExpenseDao
 companion object{
  @Volatile private var instance:AppDatabase?=null
  fun get(c:Context)=instance?:synchronized(this){instance?:Room.databaseBuilder(c.applicationContext,AppDatabase::class.java,"mrlb_business.db").build().also{instance=it}}
 }
}
