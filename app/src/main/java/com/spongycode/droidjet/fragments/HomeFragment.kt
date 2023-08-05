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

class HomeFragment : Fragment() {
    private var scoreManager: ScoreManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val playButton: Button = view.findViewById(R.id.playButton)
        val exitButton: Button = view.findViewById(R.id.exitButton)

        playButton.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, GameFragment.newInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        exitButton.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreManager = ScoreManager(requireContext())
        val retrievedScore = scoreManager!!.score
        val tv = requireActivity().findViewById<TextView>(R.id.tv_high_score)
        tv.text = retrievedScore.toString()
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
