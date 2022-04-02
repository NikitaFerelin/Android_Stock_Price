/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.data_local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.data_local.entities.CryptoDBO.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CryptoDBO(
    @PrimaryKey
    @ColumnInfo(name = ID_COLUMN)
    val id: Int,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "logo_url")
    val logoUrl: String
) {
    companion object {
        const val TABLE_NAME = "crypto"
        const val ID_COLUMN = "crypto_id"
    }
}