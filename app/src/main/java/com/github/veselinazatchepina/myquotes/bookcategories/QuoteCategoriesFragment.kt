package com.github.veselinazatchepina.myquotes.bookcategories


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.veselinazatchepina.myquotes.R
import com.github.veselinazatchepina.myquotes.abstracts.AdapterImpl
import com.github.veselinazatchepina.myquotes.allquote.AllQuotesActivity
import com.github.veselinazatchepina.myquotes.data.local.entity.QuoteCategory
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.quote_categories_recycler_view_item.view.*


class QuoteCategoriesFragment : Fragment(), QuoteCategoriesContract.View {

    lateinit var bookCategoriesPresenter: QuoteCategoriesContract.Presenter
    lateinit var quoteCategoriesAdapter: AdapterImpl<QuoteCategory>
    lateinit var rootView: View
    private lateinit var quoteType: String

    companion object {
        private const val QUOTE_TYPE_BUNDLE = "quote_type_bundle"

        fun createInstance(quoteType: String): QuoteCategoriesFragment {
            val bundle = Bundle()
            bundle.putString(QUOTE_TYPE_BUNDLE, quoteType)
            val fragment = QuoteCategoriesFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quoteType = arguments?.getString(QUOTE_TYPE_BUNDLE) ?: ""
        bookCategoriesPresenter.getQuoteCategoriesList(quoteType)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        setCurrentCategoryTitleIsGone(rootView)
        return rootView
    }

    override fun showQuoteCategoriesList(quoteCategories: List<QuoteCategory>) {
        quoteCategoriesAdapter = AdapterImpl(quoteCategories, R.layout.quote_categories_recycler_view_item, {
            rootView.item_category_name.text = it.categoryName
            rootView.item_quote_count.text = it.quoteCount.toString()
        }, {
            startActivity(AllQuotesActivity.newIntent(activity!!.applicationContext,
                    this.categoryName,
                    quoteType))
        })
        rootView.recycler_view.adapter = quoteCategoriesAdapter
        rootView.recycler_view.layoutManager = LinearLayoutManager(activity)
    }

    override fun setPresenter(presenter: QuoteCategoriesContract.Presenter) {
        bookCategoriesPresenter = presenter
    }

    private fun setCurrentCategoryTitleIsGone(rootView: View) {
        rootView.current_category.visibility = View.GONE
    }
}