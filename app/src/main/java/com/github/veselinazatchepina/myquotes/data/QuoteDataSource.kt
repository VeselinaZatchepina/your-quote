package com.github.veselinazatchepina.myquotes.data

import com.github.veselinazatchepina.myquotes.data.local.entity.BookAuthor
import com.github.veselinazatchepina.myquotes.data.local.entity.BookReleaseYear
import com.github.veselinazatchepina.myquotes.data.local.entity.Quote
import com.github.veselinazatchepina.myquotes.data.local.model.AllQuoteData
import com.github.veselinazatchepina.myquotes.data.local.model.QuoteCategoryModel
import com.github.veselinazatchepina.myquotes.enums.QuoteProperties
import io.reactivex.Flowable


interface QuoteDataSource {

    fun getQuoteCategories(quoteType: String): Flowable<List<QuoteCategoryModel>>

    fun saveQuoteData(mapOfQuoteProperties: HashMap<QuoteProperties, String>,
                      authors: List<String>): Long

    fun getAllQuotes(): Flowable<List<Quote>>

    fun getQuotesByType(quoteType: String): Flowable<List<Quote>>

    fun getQuotesByTypeAndCategory(quoteType: String, quoteCategory: String): Flowable<List<Quote>>

    fun getAllQuoteData(): Flowable<List<AllQuoteData>>

    fun getAllQuoteDataByQuoteType(quoteType: String): Flowable<List<AllQuoteData>>

    fun getAllQuoteDataByQuoteTypeAndQuoteCategory(quoteType: String, quoteCategory: String): Flowable<List<AllQuoteData>>

    fun getQuotesByQuoteTextIfContains(quoteText: String): Flowable<List<Quote>>

    fun getQuotesByTypeAndTextIfContains(quoteType: String, text: String): Flowable<List<Quote>>

    fun getQuotesByTypeAndCategoryAndTextIfContains(quoteType: String,
                                                    quoteCategory: String,
                                                    text: String): Flowable<List<Quote>>

    fun getBookAuthorsByIds(ids: List<Long>): Flowable<List<BookAuthor>>

    fun getBookReleaseYearsByIds(ids: List<Long>): Flowable<List<BookReleaseYear>>

    fun deleteQuote(qId: Long)

    fun deleteQuoteCategory(quoteType: String, quoteCategory: String): Int

    fun updateQuote(quoteId: Long,
                    mapOfQuoteProperties: HashMap<QuoteProperties, String>,
                    authors: List<String>)

    fun getCoincideQuotesByInputText(inputText: String): Flowable<List<Quote>>
}