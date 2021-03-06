package com.jshvarts.mosbymvp.searchrepos

import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.OnEditorAction
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.jshvarts.mosbymvp.GithubApp
import com.jshvarts.mosbymvp.R
import com.jshvarts.mosbymvp.domain.GithubRepo
import com.jshvarts.mosbymvp.mvp.BaseViewController
import com.jshvarts.mosbymvp.repodetail.RepoDetailViewController
import com.jshvarts.mosbymvp.viewext.initRecyclerView
import timber.log.Timber

class SearchViewController : BaseViewController<SearchContract.View, SearchContract.Presenter, SearchViewState>(), SearchContract.View {

    @BindView(R.id.username_edit_text)
    lateinit var usernameEditText: TextInputEditText

    @BindView(R.id.loading_indicator)
    lateinit var loadingIndicator: ProgressBar

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private val onClick: (GithubRepo) -> Unit = this::onRepoClicked

    private val recyclerViewAdapter = SearchAdapter(onClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view: View = super.onCreateView(inflater, container)
        recyclerView.initRecyclerView(LinearLayoutManager(view.context), recyclerViewAdapter)
        return view
    }

    override fun showLoading() {
        viewState.setShowLoading()
        recyclerView.visibility = View.GONE
        loadingIndicator.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    @OnEditorAction(R.id.username_edit_text)
    override fun onSearchAction(code: Int): Boolean {
        if (code == EditorInfo.IME_ACTION_DONE) {
            this.view?.hideKeyboard()
            presenter.searchRepos(usernameEditText.text.toString())
            usernameEditText.isCursorVisible = false
            return true
        }
        return false
    }

    override fun onSearchSuccess(repos: List<GithubRepo>) {
        viewState.setData(repos)
        recyclerView.visibility = View.VISIBLE
        recyclerViewAdapter.updateRepos(repos)
    }

    override fun onSearchError(throwable: Throwable) {
        viewState.setShowError()
        recyclerViewAdapter.updateRepos(emptyList())
        Timber.e(throwable)
        showMessage(R.string.error_loading_repos)
    }

    override fun onSearchEmptyResult() {
        showMessage(R.string.search_results_empty)
    }

    override fun createViewState() = SearchViewState()

    override fun createPresenter() = DaggerSearchComponent.builder()
            .appComponent(GithubApp.component)
            .searchModule(SearchModule())
            .build()
            .presenter()

    override fun onRepoClicked(repo: GithubRepo) {
        val repoDetailView = RepoDetailViewController().apply {
            args.putString(RepoDetailViewController.REPO_NAME, repo.name)
            args.putString(RepoDetailViewController.LOGIN, repo.owner.login)
        }
        router.pushController(RouterTransaction.with(repoDetailView)
                .pushChangeHandler(FadeChangeHandler())
                .popChangeHandler(FadeChangeHandler()))
    }

    override fun getLayoutId() = R.layout.search_repos

    override fun getToolbarTitleId() = R.string.search_repos_title
}