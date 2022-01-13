package com.ferelin.core.ui.params

import android.os.Parcelable
import com.ferelin.core.domain.entity.CompanyId
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartParams(
  val companyId: CompanyId,
  val companyTicker: String
) : Parcelable