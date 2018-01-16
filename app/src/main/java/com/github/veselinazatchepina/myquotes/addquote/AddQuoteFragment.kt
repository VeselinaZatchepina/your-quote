package com.github.veselinazatchepina.myquotes.addquote

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.veselinazatchepina.myquotes.R
import com.github.veselinazatchepina.myquotes.enums.QuoteProperties
import com.github.veselinazatchepina.myquotes.enums.QuoteType
import icepick.Icepick
import icepick.State
import kotlinx.android.synthetic.main.add_quote_book_part.*
import kotlinx.android.synthetic.main.add_quote_book_part.view.*
import kotlinx.android.synthetic.main.add_quote_main_part.*
import kotlinx.android.synthetic.main.add_quote_other.*
import kotlinx.android.synthetic.main.add_quote_other.view.*
import kotlinx.android.synthetic.main.dialog_add_category.view.*
import org.jetbrains.anko.margin
import org.jetbrains.anko.support.v4.toast


class AddQuoteFragment : Fragment(), AddQuoteContract.View {

    private var addQuotePresenter: AddQuoteContract.Presenter? = null
    private lateinit var rootView: View
    private lateinit var quoteType: String
    @JvmField
    @State
    internal var authorFieldIds: ArrayList<Int> = ArrayList<Int>()
    private var quoteCategoriesList: List<String>? = null
    private lateinit var categorySpinnerAdapter: ArrayAdapter<String>
    private lateinit var quoteCategory: String
    @JvmField
    @State
    internal var chosenQuoteCategoryPosition: Int = -1
    @JvmField
    @State
    internal var chosenNewCategory: String? = null

    companion object {
        private const val QUOTE_TYPE_BUNDLE = "quote_type_bundle"

        fun createInstance(quoteType: String): AddQuoteFragment {
            val bundle = Bundle()
            bundle.putString(QUOTE_TYPE_BUNDLE, quoteType)
            val fragment = AddQuoteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quoteType = arguments?.getString(QUOTE_TYPE_BUNDLE) ?: resources.getString(QuoteType.BOOK_QUOTE.resource)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Icepick.restoreInstanceState(this, savedInstanceState)
        rootView = inflater.inflate(R.layout.fragment_add_quote, container, false)
        addQuotePresenter?.getQuoteCategoriesList(quoteType)
        defineLayoutFields()
        defineAddFieldsForAuthorDataBtn()
        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        chosenQuoteCategoryPosition = addCategorySpinner.selectedItemPosition
        Icepick.saveInstanceState(this, outState)
    }

    private fun defineLayoutFields() {
        if (quoteType == resources.getString(QuoteType.MY_QUOTE.resource)) {
            hideBookQuoteFields()
        }
        quoteCategory = getString(R.string.spinner_hint)
    }

    private fun defineAddFieldsForAuthorDataBtn() {
        rootView.addAuthorFieldsBtn.setOnClickListener {
            val hints = listOf(getString(R.string.hint_author_name),
                    getString(R.string.hint_author_surname),
                    getString(R.string.hint_author_second_name))

            for (i in 0 until hints.count()) {
                rootView.addAuthorFieldsLinearLayout.addView(createTextInputLayout(hints[i]))
            }
        }
    }

    private fun createTextInputLayout(hint: String): TextInputLayout {
        val newFieldInputLayout = TextInputLayout(activity)
        newFieldInputLayout.layoutParams = createLayoutParamsForInputLayout()
        newFieldInputLayout.addView(createEditText(hint))
        return newFieldInputLayout
    }

    private fun createLayoutParamsForInputLayout(): LinearLayout.LayoutParams {
        val layoutParamsInputLayout = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParamsInputLayout.margin = resources.getDimension(R.dimen.add_author_fields_margin).toInt()
        return layoutParamsInputLayout
    }

    private fun createEditText(hint: String): EditText {
        val newFieldEditText = EditText(activity)
        newFieldEditText.layoutParams = createLayoutParamsForEditText()
        newFieldEditText.hint = hint
        newFieldEditText.id = View.generateViewId()
        authorFieldIds.add(newFieldEditText.id)
        return newFieldEditText
    }

    private fun createLayoutParamsForEditText(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun hideBookQuoteFields() {
        rootView.addBookPartCard.visibility = View.GONE
        rootView.addPublishingOfficeInputLayout.visibility = View.GONE
        rootView.addYearInputLayout.visibility = View.GONE
        rootView.addPageNumberInputLayout.visibility = View.GONE
    }

    override fun setPresenter(presenter: AddQuoteContract.Presenter) {
        addQuotePresenter = presenter
    }

    override fun defineCategorySpinner(quoteCategories: List<String>) {
        if (isAdded) {
            quoteCategoriesList = quoteCategories
            defineSpinnerAdapter()
            setListenerToSpinner()

        }
    }

    override fun updateCategorySpinner(quoteCategories: List<String>) {
        if (isAdded) {
            categorySpinnerAdapter.clear()
            categorySpinnerAdapter.addAll(quoteCategories)
            chosenNewCategory = quoteCategories[0]
            setSpinnerToCorrectPositionWhenUpdate()
        }
    }

    private fun setSpinnerToCorrectPositionWhenUpdate() {
        if (chosenNewCategory != null) {
            addCategorySpinner.setSelection(categorySpinnerAdapter.getPosition(chosenNewCategory))
        } else {
            if (chosenQuoteCategoryPosition != -1) {
                addCategorySpinner.setSelection(chosenQuoteCategoryPosition)
            } else {
                addCategorySpinner.setSelection(0)
            }
        }
    }

    private fun defineSpinnerAdapter() {
        categorySpinnerAdapter = object : ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    v.findViewById<TextView>(android.R.id.text1).text = ""
                    v.findViewById<TextView>(android.R.id.text1).hint = getItem(count)
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }
        defineSpinnerAdapterWhenCategoryAdded()
        addCategorySpinner.adapter = categorySpinnerAdapter
        setSpinnerToCorrectPositionWhenCreate()
    }

    private fun defineSpinnerAdapterWhenCategoryAdded() {
        if (chosenNewCategory != null) {
            val quoteCategoriesListWithNewCategory = ArrayList(quoteCategoriesList)
            quoteCategoriesListWithNewCategory.add(0, chosenNewCategory)
            categorySpinnerAdapter.addAll(quoteCategoriesListWithNewCategory)
        } else {
            categorySpinnerAdapter.addAll(quoteCategoriesList)
        }
    }

    private fun setSpinnerToCorrectPositionWhenCreate() {
        if (chosenQuoteCategoryPosition == -1) {
            addCategorySpinner.setSelection(categorySpinnerAdapter.count)
        } else {
            addCategorySpinner.setSelection(chosenQuoteCategoryPosition)
        }
    }

    private fun setListenerToSpinner() {
        addCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == getString(R.string.spinner_add_category)) {
                    createAddCategoryDialog()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun createAddCategoryDialog() {
        val layoutInflater = LayoutInflater.from(activity)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_add_category_ok)) { dialogInterface: DialogInterface?, id: Int ->
                    val currentUserInput = dialogView.input_text.text.toString()
                    addQuotePresenter?.addQuoteCategory(currentUserInput)
                }
                .setNegativeButton(getString(R.string.dialog_add_category_cancel)) { dialogInterface: DialogInterface?, id: Int ->
                    dialogInterface?.cancel()
                    addCategorySpinner.setSelection(0)
                }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    fun createMapOfQuoteProperties() {
        if (isQuoteTextFieldNotEmpty() && isBookCategorySelected()) {
            val mapOfQuoteProperties = HashMap<QuoteProperties, String>()
            mapOfQuoteProperties[QuoteProperties.QUOTE_TEXT] = addQuoteText.text.toString()
            mapOfQuoteProperties[QuoteProperties.BOOK_NAME] = addBookName.text.toString()
            mapOfQuoteProperties[QuoteProperties.BOOK_CATEGORY_NAME] = addCategorySpinner.selectedItem.toString()
            mapOfQuoteProperties[QuoteProperties.PAGE_NUMBER] = addPageNumber.text.toString()
            mapOfQuoteProperties[QuoteProperties.YEAR_NUMBER] = addYear.text.toString()
            mapOfQuoteProperties[QuoteProperties.PUBLISHING_OFFICE_NAME] = addPublishingOfficeName.text.toString()
            mapOfQuoteProperties[QuoteProperties.QUOTE_CREATION_DATE] = ""
            mapOfQuoteProperties[QuoteProperties.QUOTE_TYPE] = quoteType
            mapOfQuoteProperties[QuoteProperties.QUOTE_COMMENTS] = addComments.text.toString()

            addQuotePresenter?.saveQuote(mapOfQuoteProperties, createAuthorsList())
            activity?.finish()
        }
        if (!isBookCategorySelected()) {
            toast("Choose quote category")
        }
    }

    private fun createAuthorsList(): List<String> {
        val list = ArrayList<String>()
        list.add(addAuthorFirstName.text.toString())
        list.add(addAuthorSurname.text.toString())
        list.add(addAuthorMiddleName.text.toString())
        authorFieldIds
                .map { rootView.findViewById<EditText>(it) }
                .mapTo(list) { it.text.toString() }
        return list
    }

    private fun isQuoteTextFieldNotEmpty(): Boolean {
        var isEmpty = true
        if (TextUtils.isEmpty(addQuoteText.text)) {
            addQuoteTextInputLayout.error = "Quote text mustn't be empty"
            isEmpty = false
        }
        return isEmpty
    }

    private fun isBookCategorySelected(): Boolean = addCategorySpinner.selectedItem.toString() != getString(R.string.spinner_hint)
}