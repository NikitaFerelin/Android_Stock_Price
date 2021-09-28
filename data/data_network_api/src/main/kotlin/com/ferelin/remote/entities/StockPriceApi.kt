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

package com.ferelin.remote.entities

import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Represents api that returns stock price
 * */
interface StockPriceApi {

    /**
     * Requests actual stock price
     *
     * @param symbol is a company symbol for which stock history is need
     * @param token is an api token required to access the server
     * @return server response as [StockPriceResponse] object
     * */
    @GET("quote")
    fun getStockPrice(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<StockPriceResponse>
}

data class StockPriceResponse(
    @Json(name = "o") val openPrice: Double,
    @Json(name = "h") val highPrice: Double,
    @Json(name = "l") val lowPrice: Double,
    @Json(name = "c") val currentPrice: Double,
    @Json(name = "pc") val previousClosePrice: Double
)