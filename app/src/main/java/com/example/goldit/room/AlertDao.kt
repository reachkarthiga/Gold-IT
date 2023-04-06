package com.example.goldit.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.gms.tasks.Task

@Dao
interface AlertDao {

    @Query("Select * From alert_details where alertAchieved = 0 and userEmail = :user order by id desc " )
    fun getActiveAlerts(user: String) : LiveData<List<Alert>>

    @Query("Select * From alert_details where alertAchieved = 1 and userEmail =:user order by achievedDateTime desc")
    fun getNotifiedAlerts(user: String) : LiveData<List<Alert>>

    @Query("Select * from alert_details where id = :alertId and alertAchieved = 1 and userEmail =:user")
    fun getNotifiedAlertById(alertId: Long, user:String) : Alert

    @Query("Select count(*) from alert_details where userEmail = :user and alertAchieved = 0 ")
    fun getUserActiveAlertCount(user:String) :Int

    @Query("Update alert_details set achievedDateTime = 0, alertAchieved = 0 where id = :id")
    fun enableAlert(id:Long)

    @Query("Select * from alert_details where goldRate >= :price and goldType = :purity and alertAchieved = 0 and userEmail = :user")
    fun checkIfAlertAchieved(price:Int, purity :String, user:String) : List<Alert>

    @Insert
    suspend fun saveAlert(alert:Alert)

    @Query("Delete from alert_details where id = :alertId")
    suspend fun deleteAlert(alertId: Long)

    @Update
    suspend fun updateAlert(alert: Alert)

    @Query("Select * from alert_details where id = :alertId and userEmail =:user")
    fun getActiveAlertById(alertId:Long, user: String): Alert

}