package com.cheney.statuslayout

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by wangshuwen on 2018/9/13.
 */


class StateLayout : FrameLayout {

    private val TAG = "StateLayout"

    enum class ViewState {
        EMPTY,
        ERROR,
        LOADING,
        NORMAL
    }

    private lateinit var emptyLayer: View
    private lateinit var errorLayer: View
    private lateinit var loadingLayer: View
    private var currentState = ViewState.NORMAL
    private var oldContent: View? = null
    private var rootView: ViewGroup? = null
    private var oldContentIndex: Int = -1
    var errorClickListener: ((View) -> Unit)? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    fun init(target: Any?) {
        if (null == target) {
            return
        }
        //目标布局 的父布局
        when (target) {
            is Activity -> rootView = target.findViewById(android.R.id.content)
            is Fragment -> rootView = (target.view)?.parent as ViewGroup
            is View -> rootView = target.parent as ViewGroup
        }
        //记录目标布局的index
        rootView?.let {
            if (target is View) {
                for (i in 0 until it.childCount) {
                    if (it.getChildAt(i) == target) {
                        this@StateLayout.oldContent = target
                        oldContentIndex = i
                        break
                    }
                }
            } else {
                oldContentIndex = 0
                this@StateLayout.oldContent = it.getChildAt(0)
            }
            if (oldContentIndex == -1) {
                return
            }
            it.removeViewAt(oldContentIndex)
            it.addView(this@StateLayout, oldContentIndex, this@StateLayout.oldContent!!.layoutParams)
            buildDefaultView()
            changeStatus(ViewState.NORMAL)
        }
    }


    private fun buildDefaultView() {
        emptyLayer = View.inflate(context, R.layout.status_layout_empty, null)
        addView(emptyLayer)

        errorLayer = View.inflate(context, R.layout.status_layout_error, null)
        addView(errorLayer)

        errorLayer.setOnClickListener {
            errorClickListener?.invoke(it)
        }

        loadingLayer = View.inflate(context, R.layout.status_layout_loading, null)
        addView(loadingLayer)

        oldContent?.let {
            addView(it)
        }

    }

    private fun changeStatus(state: ViewState) {
        this@StateLayout.currentState = state
        Log.i(TAG, "changeState is called  current state =$state")
        emptyLayer.visibility = if (this@StateLayout.currentState != ViewState.EMPTY) View.GONE else View.VISIBLE
        errorLayer.visibility = if (this@StateLayout.currentState != ViewState.ERROR) View.GONE else View.VISIBLE
        loadingLayer.visibility = if (this@StateLayout.currentState != ViewState.LOADING) View.GONE else View.VISIBLE
        oldContent?.visibility = if (this@StateLayout.currentState != ViewState.NORMAL) View.GONE else View.VISIBLE

    }

    fun showLoadingView() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeStatus(ViewState.LOADING)
        } else {
            loadingLayer.post {
                changeStatus(ViewState.LOADING)
            }
        }
    }


    fun showErrorView() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeStatus(ViewState.ERROR)
        } else {
            errorLayer.post {
                changeStatus(ViewState.ERROR)
            }
        }
    }


    fun showEmptyView() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeStatus(ViewState.EMPTY)
        } else {
            errorLayer.post {
                changeStatus(ViewState.EMPTY)
            }
        }
    }

    fun hideState() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeStatus(ViewState.NORMAL)
        } else {
            oldContent?.post {
                changeStatus(ViewState.NORMAL)
            }
        }
    }

    class Builder(context: Context) {

        private var stateLayout: StateLayout = StateLayout(context)

        fun init(target: Any): Builder {
            stateLayout.init(target)
            return this
        }

        fun errorView(errorView: View): Builder {
            stateLayout.errorLayer = errorView
            return this
        }

        fun emptyView(emptyView: View): Builder {
            stateLayout.emptyLayer = emptyView
            return this
        }

        fun loadingView(loadingView: View): Builder {
            stateLayout.loadingLayer = loadingView
            return this
        }

        fun errorClickListener(errorListener: (View) -> Unit): Builder {
            stateLayout.errorClickListener = errorListener
            return this
        }

        fun build(): StateLayout {
            return stateLayout
        }
    }


}