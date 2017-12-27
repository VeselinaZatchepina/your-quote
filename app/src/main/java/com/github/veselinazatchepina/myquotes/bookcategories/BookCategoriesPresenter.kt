package com.github.veselinazatchepina.myquotes.bookcategories

import com.github.veselinazatchepina.myquotes.data.QuoteDataSource
import com.github.veselinazatchepina.myquotes.data.local.entity.BookCategory
import com.github.veselinazatchepina.myquotes.data.local.entity.Quote
import com.github.veselinazatchepina.myquotes.data.local.pojo.BookCategoriesAndQuoteType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber


class BookCategoriesPresenter(val quoteDataSource: QuoteDataSource,
                              val bookCategoriesView: BookCategoriesContract.View) : BookCategoriesContract.Presenter {

    private var compositeDisposable: CompositeDisposable

    init {
        bookCategoriesView.setPresenter(this)
        compositeDisposable = CompositeDisposable()
    }

    override fun getBookCategoriesList() {
        compositeDisposable.add(quoteDataSource.getBookCategories("").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<BookCategoriesAndQuoteType>>() {
                    override fun onNext(list: List<BookCategoriesAndQuoteType>?) {
                       // if (list != null) {
                        val list2 = arrayListOf<BookCategory>(BookCategory("first1", 1),
                                BookCategory("second2", 2),
                                BookCategory("third3", 3))
                            bookCategoriesView.showBookCategoriesList(list2)
                        //}
                    }

                    override fun onComplete() {

                    }

                    override fun onError(t: Throwable?) {

                    }

                }))
    }

    override fun getQuotesByCategory(categoryName: String) : List<Quote> {
        return arrayListOf()
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}