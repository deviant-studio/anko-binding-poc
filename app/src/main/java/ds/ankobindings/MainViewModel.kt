package ds.ankobindings

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

// todo validation
class MainViewModel(val view: View) : ViewModel {

    var login: CharSequence by binding("")
    var password: CharSequence by binding("")
    var isRefreshing: Boolean by binding(false)

    fun onCreate() {
    }

    fun onSignIn() = launch(UI) {
        isRefreshing = true
        try {
            delay(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isRefreshing = false
            view.showAlert("Logged in with creds $login/$password")
        }

    }

    fun onSignUp() {
        view.showAlert("todo")
    }



}
