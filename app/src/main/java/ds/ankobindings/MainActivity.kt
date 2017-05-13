package ds.ankobindings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk19.listeners.onClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class MainActivity : AppCompatActivity(), View {

    val viewModel = MainViewModel(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            viewModel.bind(viewModel::login, this::setText, this::getText)
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
                            viewModel.bind(viewModel::isRefreshing, {
                                isEnabled = !it
                            })
                        }.lparams(lp)
                        button("Sign Up") {
                            onClick { viewModel.onSignUp() }
                            viewModel.bind(viewModel::isRefreshing, { isEnabled = !it })
                        }.lparams(lp)
                    }
                }
            }
        }
    }

    override fun showAlert(text: String) {
        alert {
            title = "Congrats!"
            message = text
            okButton { it.dismiss() }
        }.show()
    }
}
