package edu.kamshanski.rubancfttask.model.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import java.math.BigDecimal

/**
 * Describe exchange for single foreign currency
 *
 * @property objId - required for objectBox
 * @property id
 * @property numCode
 * @property charCode - currency ISO code
 * @property nominal of foreign currency that is allowd to gain for [valute] amount of rubles
 * @property name - currency name in Russian
 * @property valute price in rubles
 * @property previos - [valute] of the previous day
 */
@Entity
class Rate(
    @Id
    @Expose(serialize = false, deserialize = false)
    var objId: Long,
    @SerializedName(value = "ID")
    var id: String = "",
    @SerializedName(value = "NumCode")
    var numCode: String = "",
    @Index
    @SerializedName(value = "CharCode")
    var charCode: String = "",
    @SerializedName(value = "Nominal")
    var nominal: Int = 0,
    @SerializedName(value = "Name")
    var name: String = "",
    @SerializedName(value = "Value")
    var valute: Double = 0.0,
    @SerializedName(value = "Previous")
    var previos: Double = 0.0
) {
    override fun toString(): String {
        return "Rate(id='$id', numCode='$numCode', charCode='$charCode', nominal=$nominal, name='$name', varute=$valute, previos=$previos)"
    }

    /** Coefficient to  transform Rubles to Foreign currency */
    val rubToForeignCoef : BigDecimal
        get() {
            return BigDecimal.valueOf(valute) / BigDecimal.valueOf(nominal.toLong())
        }
}