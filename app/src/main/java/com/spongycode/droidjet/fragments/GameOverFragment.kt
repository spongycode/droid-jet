package com.spongycode.droidjet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.spongycode.droidjet.R
import com.spongycode.droidjet.utils.ScoreManager
import kotlin.math.max

class GameOverFragment : Fragment() {
    interface RestartListener {
        fun onRestartClicked()
    }

    private var scoreManager: ScoreManager? = null
    private var score: Float = 0f
    var restartListener: RestartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_over, container, false)
        val scoreTextView = view.findViewById<TextView>(R.id.score)
        scoreTextView.text = "${score.toInt()}"

        val restartButton = view.findViewById<Button>(R.id.restart)
        restartButton.setOnClickListener {
            restartListener?.onRestartClicked()
        }

        scoreManager = ScoreManager(requireContext())
        val highScore = max(score.toInt(), scoreManager!!.score)
        scoreManager!!.score = highScore

        val highScoreTextView = view.findViewById<TextView>(R.id.high_score)
        highScoreTextView.text = "$highScore"

        val exitButton: Button = view.findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = arguments?.getFloat("score", 0f) ?: 0f
    }

    companion object {
        fun newInstance(): GameOverFragment {
            return GameOverFragment()
        }
    }
}
