package com.spongycode.droidjet.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spongycode.droidjet.R
import com.spongycode.droidjet.views.GameView

class GameFragment : Fragment() {
    private lateinit var gameView: GameView
    private val handler = Handler()
    private val gameOverRunnable = Runnable {
        showGameOverFragment()
    }
    private var gameOverCallback: GameOverCallback? = null

    interface GameOverCallback {
        fun onGameOver(score: Float)
    }

    companion object {
        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is GameOverCallback) {
            gameOverCallback = activity as GameOverCallback
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        gameView = view.findViewById(R.id.gameView)
        return view
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
        gameView.setOnGameOverListener { gameOver ->
            if (gameOver) {
                handler.postDelayed(
                    gameOverRunnable,
                    1500
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameOverRunnable)
        gameView.pause()
    }

    private fun showGameOverFragment() {
        gameOverCallback?.onGameOver(GameView.getScore())
        gameView.setGameOver()
        gameView.restartGame()
    }
}
