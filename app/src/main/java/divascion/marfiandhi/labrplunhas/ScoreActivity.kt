package divascion.marfiandhi.labrplunhas

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_score.*
import org.jetbrains.anko.toast
import java.util.function.IntToDoubleFunction

class ScoreActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        val score = intent.getIntExtra("right", 0)
        val totalQuestion = intent.getIntExtra("totalQuestion", 0)
        val name = intent.getStringExtra("user")
        var attempt = intent.getIntExtra("attempt", -1)
        attempt += 1
        val chapter = intent.getStringExtra("chapter")

        val realScore: Int = ((score.toFloat()/totalQuestion.toFloat())*100f).toInt()

        when (chapter) {
            "bab1" -> toast("attempt 1 = $attempt")
            "bab2" -> toast("attempt 2 = $attempt")
            "bab3" -> toast("attempt 3 = $attempt")
            "bab4" -> toast("attempt 4 = $attempt")
            "bab5" -> toast("attempt 5 = $attempt")
            "bab6" -> toast("attempt 6 = $attempt")
            "bab7" -> toast("attempt 7 = $attempt")
            "bab8" -> toast("attempt 8 = $attempt")
        }

        txt_count_right.text = score.toString()
        txt_count_task.text = totalQuestion.toString()
        txt_score.text = realScore.toString()
        txt_your_score.text = "$name's Score"

        btn_home.setOnClickListener {
            finish()
        }
    }
}
