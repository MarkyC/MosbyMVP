package com.jshvarts.mosbymvp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.hannesdorfmann.mosby3.mvp.viewstate.MvpViewStateActivity

class MainActivity : MvpViewStateActivity<GreetingContract.View, GreetingContract.Presenter, GreetingViewState>(), GreetingContract.View {

    @BindView(R.id.greeting_textview)
    lateinit var greetingTextView: TextView

    @BindView(R.id.loading_indicator)
    lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)
    }

    override fun onNewViewStateInstance() {
        // no-op
    }

    override fun createViewState() = GreetingViewState()

    override fun createPresenter() = GreetingPresenter()

    @OnClick(R.id.hello_greeting_button)
    override fun onGreetingButtonClicked() {
        presenter.loadGreeting()
    }

    override fun showGreeting(greetingText: String) {
        viewState.setData(greetingText)
        greetingTextView.visibility = View.VISIBLE
        greetingTextView.text = greetingText
    }

    override fun showLoading() {
        viewState.setShowLoading()
        greetingTextView.visibility = View.GONE
        loadingIndicator.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingIndicator.visibility = View.GONE
    }

    override fun showError() {
        viewState.setShowError()
        Toast.makeText(applicationContext, getString(R.string.greeting_loading_error), Toast.LENGTH_LONG).show()
    }
}
