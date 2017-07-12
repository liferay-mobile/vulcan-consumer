package com.liferay.vulcan.consumer.screens.views.row

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.liferay.vulcan.consumer.R
import com.liferay.vulcan.consumer.delegates.bindNonNull
import com.liferay.vulcan.consumer.delegates.converter
import com.liferay.vulcan.consumer.model.BlogPosting
import com.liferay.vulcan.consumer.model.Thing
import com.liferay.vulcan.consumer.screens.ThingScreenlet
import com.liferay.vulcan.consumer.screens.views.BaseView

class BlogPostingRowView(context: Context, attrs: AttributeSet) : BaseView(context, attrs) {
    val headline by bindNonNull<TextView>(R.id.headline)
    val creator by bindNonNull<ThingScreenlet>(R.id.creator_avatar)

    override var thing: Thing? by converter<BlogPosting> {
        headline.text = it.headline

        it.creator?.also {
            creator.load(it.id)
        }
    }
}