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

package com.ferelin.data_local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ferelin.data_local.entities.*

@Database(
    entities = [
        CompanyDBO::class,
        StockPriceDBO::class,
        NewsDBO::class,
        PastPriceDBO::class,
        ProfileDBO::class,
        SearchRequestDBO::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompaniesDao

    abstract fun stockPriceDao(): StockPriceDao

    abstract fun newsDao(): NewsDao

    abstract fun pastPriceDao(): PastPriceDao

    abstract fun profileDao(): ProfileDao

    abstract fun searchRequestsDao() : SearchRequestsDao

    companion object {
        const val DB_NAME = "stock.price.db"
    }
}