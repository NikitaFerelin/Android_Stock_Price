package com.ferelin.features.about.about

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AboutScope

@AboutScope
@Component(dependencies = [AboutDeps::class])
interface AboutComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun aboutParams(aboutParams: AboutParams): Builder

    fun dependencies(deps: AboutDeps): Builder
    fun build(): AboutComponent
  }

  fun viewModelFactory(): AboutViewModelFactory
}

interface AboutDeps {
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val dispatchersProvider: DispatchersProvider
}

interface AboutDepsProvider {
  var deps: AboutDeps

  companion object : AboutDepsProvider by AboutDepsStore
}

object AboutDepsStore : AboutDepsProvider {
  override var deps: AboutDeps by Delegates.notNull()
}