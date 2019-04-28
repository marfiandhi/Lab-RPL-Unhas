package divascion.marfiandhi.labrplunhas.view.exam

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.Question
import kotlinx.android.synthetic.main.activity_exam.*
import org.jetbrains.anko.*

class ExamActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var subject: String
    private lateinit var chapter: String
    private lateinit var nim: String
    private lateinit var nilai: Nilai
    private var questionList: MutableList<Question> = mutableListOf()
    private var answerChoose: MutableList<String> = mutableListOf()
    private var questionNumber = 0
    private var score = 0
    private lateinit var name: String
    private var attempt: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)
        subject = intent.getStringExtra("subject")
        chapter = intent.getStringExtra("chapter")
        name = intent.getStringExtra("user")
        attempt = intent.getIntExtra("attempt", -1)
        nim = intent.getStringExtra("nim")
        nilai = intent.getParcelableExtra("nilai")

        mDatabase = FirebaseDatabase.getInstance().reference

        getQuestionList()

        btn_next_question.setOnClickListener {_ ->
            if(isAnswered()) {
                alert("Do you want to lock your current answer?\n\nYou can't change your answer, choose wisely.") {
                    yesButton {
                        questionNumber += 1
                        showQuestion(questionNumber)
                    }
                    noButton {
                        it.dismiss()
                    }
                }.show().setCancelable(false)
            } else {
                toast("You have to answer this question first before facing the next question.")
            }
        }

        val timer = object : CountDownTimer(120000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished/1000)/60
                val sMinutes = if(minutes<10) {
                    "0$minutes"
                } else {
                    minutes.toString()
                }
                val seconds = (millisUntilFinished/1000)%60
                val sSeconds = if(seconds<10) {
                    "0$seconds"
                } else {
                    seconds.toString()
                }
                txt_timer.text = "$sMinutes:$sSeconds"
            }

            override fun onFinish() {
                countScore()
                finish()
            }
        }

        btn_finish.setOnClickListener {_ ->
            if(isAnswered()) {
                alert("Do you want to lock your current answer?\n\nYou can't change your answer, choose wisely.") {
                    yesButton {
                        countScore()
                        timer.cancel()
                        finish()
                    }
                    noButton {
                        it.dismiss()
                    }
                }.show().setCancelable(false)
            } else {
                toast("You have to answer this question first before facing the next question.")
            }
        }

        button_confirm_exam.setOnClickListener {
            if(!questionList.isEmpty()) {
                button_confirm_exam.visibility = View.GONE
                restricted_view.visibility = View.VISIBLE
                timer.start()
            } else {
                toast("Please wait till the question loaded.")
            }
        }
    }

    override fun finish() {
        if(questionList.size>0) {
            startActivity(intentFor<ScoreActivity>(
                "right" to score,
                "totalQuestion" to questionList.size,
                "user" to name,
                "attempt" to attempt,
                "chapter" to chapter,
                "subject" to subject,
                "nim" to nim,
                "nilai" to nilai
            ))
            super.finish()
        } else {
            super.finish()
        }
    }

    private fun getQuestionList() {
        exam_progress_bar.visibility = View.VISIBLE
        btn_next_question.visibility = View.GONE

        val questionListListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toast(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()) {
                    snapshot.children.mapNotNullTo(questionList) {
                        it.getValue<Question>(Question::class.java)
                    }
                    questionList.shuffle()

                    exam_progress_bar.visibility = View.GONE
                    btn_next_question.visibility = View.VISIBLE
                    showQuestion(questionNumber)
                } else {
                    finish()
                    toast("There is no such task. Try again next time.")
                }
            }
        }
        mDatabase.child("respon/$subject/$chapter").addListenerForSingleValueEvent(questionListListener)
    }

    @SuppressLint("SetTextI18n")
    private fun showQuestion(number: Int) {
        isLastQuestion()
        question_number.text = "Soal ${number+1}"
        question_txt.text = questionList[number].question
        choice_a.text = questionList[number].choiceA
        choice_b.text = questionList[number].choiceB
        choice_c.text = questionList[number].choiceC
        choice_d.text = questionList[number].choiceD
        choice_e.text = questionList[number].choiceE
    }

    private fun isLastQuestion() {
        if(questionNumber == questionList.size-1) {
            btn_next_question.visibility = View.GONE
            btn_finish.visibility = View.VISIBLE
        }
    }

    private fun isAnswered(): Boolean {
        when {
            choice_a.isChecked -> answerChoose.add("${choice_a.text}")
            choice_b.isChecked -> answerChoose.add("${choice_b.text}")
            choice_c.isChecked -> answerChoose.add("${choice_c.text}")
            choice_d.isChecked -> answerChoose.add("${choice_d.text}")
            choice_e.isChecked -> answerChoose.add("${choice_e.text}")
            else -> return false
        }
        return true
    }

    private fun countScore() {
        score = 0
        for(index in questionList.indices) {
            try {
                if(questionList[index].answer == answerChoose[index]) {
                    score++
                }
            } catch(e: Exception) {
                Log.e("Choose index", e.toString())
            }
        }
    }

    override fun onBackPressed() {
        alert("Exiting will going to end this exam and your score will be taken even if you didn't finished yet.\n\nContinue?") {
            yesButton {
                countScore()
                it.dismiss()
                finish()
            }
            noButton {
                it.dismiss()
            }
        }.show()
    }
}
