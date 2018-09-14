package com.cheney.statuslayout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by wangshuwen on 2018/9/14.
 */

class MainActivity : AppCompatActivity() {

    private var handler = Handler(Looper.getMainLooper())

    private val mStatesLayout by lazy {
        StateLayout.Builder(this)
                .init(content)
                .errorClickListener {
                    this@MainActivity.loading(it)
                    handler.postDelayed({
                        this@MainActivity.normal(it)
                    }, 5000)
                }
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loading(view: View) {
        mStatesLayout.showLoadingView()
    }

    fun error(view: View) {
        mStatesLayout.showErrorView()
    }

    fun normal(view: View) {
        mStatesLayout.hideState()
    }

    fun empty(view: View) {
        mStatesLayout.showEmptyView()
    }

}
