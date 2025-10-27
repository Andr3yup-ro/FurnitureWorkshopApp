package com.predandrei.atelier.data.db

import androidx.room.TypeConverter
import com.predandrei.atelier.data.model.InventoryCategory
import com.predandrei.atelier.data.model.PaymentMethod
import com.predandrei.atelier.data.model.ProjectStatus
import com.predandrei.atelier.data.model.TransactionType

class EnumConverters {
    @TypeConverter
    fun toProjectStatus(v: String?): ProjectStatus? = v?.let { ProjectStatus.valueOf(it) }

    @TypeConverter
    fun fromProjectStatus(s: ProjectStatus?): String? = s?.name

    @TypeConverter
    fun toInventoryCategory(v: String?): InventoryCategory? = v?.let { InventoryCategory.valueOf(it) }

    @TypeConverter
    fun fromInventoryCategory(s: InventoryCategory?): String? = s?.name

    @TypeConverter
    fun toTransactionType(v: String?): TransactionType? = v?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromTransactionType(s: TransactionType?): String? = s?.name

    @TypeConverter
    fun toPaymentMethod(v: String?): PaymentMethod? = v?.let { PaymentMethod.valueOf(it) }

    @TypeConverter
    fun fromPaymentMethod(s: PaymentMethod?): String? = s?.name
}
