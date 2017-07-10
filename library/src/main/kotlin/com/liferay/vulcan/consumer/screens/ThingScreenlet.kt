package com.liferay.vulcan.consumer.screens

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.liferay.vulcan.consumer.R
import com.liferay.vulcan.consumer.extensions.inflate
import com.liferay.vulcan.consumer.fetch
import com.liferay.vulcan.consumer.model.Thing
import com.liferay.vulcan.consumer.screens.views.BaseView
import okhttp3.HttpUrl

open class BaseScreenlet @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var layout: View? = null
}

class ThingScreenlet @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    BaseScreenlet(context, attrs, defStyleAttr, defStyleRes) {

    var screenletEvents: ScreenletEvents? = null

    val layoutIds: Map<String, Map<ViewScenario, Int>> = mapOf(
        "BlogPosting" to BlogPosting.DefaultViews,
        "Collection" to Collection.DefaultViews,
        "Person" to Person.DefaultViews
    )

    val layoutId: Int

    var thing: Thing? = null
        set(value) {
            field = value
            viewModel?.thing = value
        }

    val viewModel: ViewModel? get() = layout as ViewModel

    fun load(thingId: String, onComplete: ((ThingScreenlet) -> Unit)? = null) {
        fetch(HttpUrl.parse(thingId)!!) {
            val layoutId = if (layoutId == 0)
                it.component1()?.type?.get(0)
                    ?.let { layoutIds[it] }
                    ?: R.layout.thing_default
            else layoutId

            layout = this.inflate(layoutId)

            addView(layout)

            (layout as? BaseView)?.screenlet = this

            it.failure { viewModel?.showError(it.message) }

            it.success { thing = it }

            onComplete?.invoke(this)
        }
    }

    init {
        val typedArray = attrs?.let { context.theme.obtainStyledAttributes(it, R.styleable.ThingScreenlet, 0, 0) }

        layoutId = typedArray?.getResourceId(R.styleable.ThingScreenlet_layoutId, 0) ?: 0
    }

    fun <T> onEventFor(action: Action<T>): T? = when (action) {
        is ClickAction -> screenletEvents?.onClickEvent(layout as BaseView, action.view, action.thing) as? T
        is CustomLayoutAction -> screenletEvents?.onGetCustomLayout(this, action.view, action.thing, action.scenario) as? T
    }
}

interface ViewModel {
    var thing: Thing?
    fun showError(message: String?)
}

}