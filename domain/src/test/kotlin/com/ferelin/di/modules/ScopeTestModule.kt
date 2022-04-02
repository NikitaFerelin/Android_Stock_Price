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

package com.ferelin.di.modules

import com.ferelin.TestDispatchersProvider
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
class ScopeTestModule {

    @Provides
    fun provideTestDispatcher() : DispatchersProvider {
        return TestDispatchersProvider()
    }

    @Provides
    @Singleton
    @Named(NAMED_EXTERNAL_SCOPE)
    fun provideExternalScope(dispatchersProvider: DispatchersProvider): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatchersProvider.Main)
    }
}