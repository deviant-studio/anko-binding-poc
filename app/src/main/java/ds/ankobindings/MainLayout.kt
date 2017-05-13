package ds.ankobindings

import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk19.listeners.onClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class MainLayout(val viewModel: MainViewModel) : AnkoComponent<MainActivity> {

    constructor() : this(MainViewModel(DummyView))

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        coordinatorLayout {

            swipeRefreshLayout {
                isEnabled = false

                viewModel.bind(viewModel::isRefreshing, this::setRefreshing, this::isRefreshing)

                verticalLayout {
                    horizontalPadding = dip(72)
                    topPadding = dip(32)

                    textInputLayout {
                        editText {
                            hint = "Email"
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            bind(
                                viewModel { it::login },
                                this::setText,
                                this::getText
                            )
                        }
                    }
                    textInputLayout {
                        editText {
                            hint = "Password"
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            viewModel.bind(viewModel::password, this::setText, this::getText)
                        }
                    }
                    linearLayout {
                        val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.weight = 1f }

                        button("Sign In") {
                            onClick {
                                viewModel.onSignIn()
                            }
                            bind(viewModel { it::isRefreshing }, { isEnabled = !it })
                        }.lparams(lp)
                        button("Sign Up") {
                            onClick { viewModel.onSignUp() }
                            bind(viewModel { it::isRefreshing }, { isEnabled = !it })
                        }.lparams(lp)
                    }
                }
            }
        }
    }
}

object DummyView : ds.ankobindings.View {
    override fun showAlert(text: String) {
        throw UnsupportedOperationException("not implemented")
    }
}
