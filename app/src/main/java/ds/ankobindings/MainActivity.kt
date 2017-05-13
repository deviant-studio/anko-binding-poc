package ds.ankobindings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity(), View {

    val viewModel = MainViewModel(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainLayout(viewModel).setContentView(this)

    }

    override fun showAlert(text: String) {
        alert {
            title = "Congrats!"
            message = text
            okButton { it.dismiss() }
        }.show()
    }
}
