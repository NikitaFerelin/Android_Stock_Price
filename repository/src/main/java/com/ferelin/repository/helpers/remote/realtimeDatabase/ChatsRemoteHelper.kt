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

package com.ferelin.repository.helpers.remote.realtimeDatabase

import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.Flow

interface ChatsRemoteHelper {

    fun cacheChatToRealtimeDb(currentUserNumber: String,chat: AdaptiveChat)

    suspend fun getUserChatsFromRealtimeDb(
        userNumber: String
    ): Flow<RepositoryResponse<AdaptiveChat>>
}