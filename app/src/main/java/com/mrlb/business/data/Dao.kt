package com.mrlb.business.data
import androidx.room.*
import com.mrlb.business.model.*
import kotlinx.coroutines.flow.Flow

@Dao interface CustomerDao {
 @Query("SELECT * FROM customers WHERE name LIKE '%'||:q||'%' OR phone LIKE '%'||:q||'%' OR business LIKE '%'||:q||'%' ORDER BY name") fun search(q:String):Flow<List<Customer>>
 @Query("SELECT * FROM customers ORDER BY name") fun all():Flow<List<Customer>>
 @Query("SELECT * FROM customers WHERE id=:id") suspend fun get(id:Long):Customer?
 @Insert suspend fun insert(c:Customer):Long
 @Update suspend fun update(c:Customer)
 @Delete suspend fun delete(c:Customer)
}
@Dao interface OrderDao {
 @Query("SELECT * FROM orders ORDER BY date DESC") fun all():Flow<List<Order>>
 @Query("SELECT COALESCE(SUM(total),0) FROM orders") fun sales():Flow<Double>
 @Query("SELECT COALESCE(SUM(total-paid),0) FROM orders") fun due():Flow<Double>
 @Insert suspend fun insert(o:Order):Long
 @Update suspend fun update(o:Order)
 @Delete suspend fun delete(o:Order)
 @Insert suspend fun insertItems(items:List<OrderItem>)
 @Query("SELECT * FROM order_items WHERE orderId=:id") suspend fun items(id:Long):List<OrderItem>
}
@Dao interface PaymentDao {
 @Query("SELECT * FROM payments ORDER BY date DESC") fun all():Flow<List<Payment>>
 @Query("SELECT COALESCE(SUM(amount),0) FROM payments") fun collected():Flow<Double>
 @Insert suspend fun insert(p:Payment):Long
}
@Dao interface ExpenseDao {
 @Query("SELECT * FROM expenses ORDER BY date DESC") fun all():Flow<List<Expense>>
 @Query("SELECT COALESCE(SUM(amount),0) FROM expenses") fun total():Flow<Double>
 @Insert suspend fun insert(e:Expense):Long
 @Update suspend fun update(e:Expense)
 @Delete suspend fun delete(e:Expense)
}
