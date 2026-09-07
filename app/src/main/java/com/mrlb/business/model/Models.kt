package com.mrlb.business.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="customers")
data class Customer(@PrimaryKey(autoGenerate=true) val id:Long=0,val name:String,val phone:String="",val business:String="",val address:String="")

@Entity(tableName="orders")
data class Order(@PrimaryKey(autoGenerate=true) val id:Long=0,val customerId:Long,val date:Long=System.currentTimeMillis(),val total:Double,val paid:Double=0.0,val dueDate:Long=0,val status:String="Pending")

@Entity(tableName="order_items")
data class OrderItem(@PrimaryKey(autoGenerate=true) val id:Long=0,val orderId:Long,val product:String,val quantity:Double,val rate:Double)

@Entity(tableName="payments")
data class Payment(@PrimaryKey(autoGenerate=true) val id:Long=0,val customerId:Long,val orderId:Long?,val amount:Double,val method:String,val date:Long=System.currentTimeMillis())

@Entity(tableName="expenses")
data class Expense(@PrimaryKey(autoGenerate=true) val id:Long=0,val category:String,val amount:Double,val note:String="",val date:Long=System.currentTimeMillis())
