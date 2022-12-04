package com.vlatrof.androidrxjavademo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.vlatrof.androidrxjavademo.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogs.movementMethod = ScrollingMovementMethod()
        binding.btnStartComputation.setOnClickListener { onStartComputation() }
        binding.btnJustClick.setOnClickListener { onButtonJustClick() }
        binding.btnClearLogs.setOnClickListener { onClearLogs() }
    }

    private fun onStartComputation() {
        writeToLog("Background computation: started")

        val disposable = Observable.create { emitter ->
            for (i in 0 until 10) {
                Thread.sleep(1000)
                emitter.onNext(i)
            }
            emitter.onComplete()
        } // configuration chain:
            .map { it * it }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onNextValue -> writeToLog("Background computation: $onNextValue") },
                {},
                { writeToLog("Background computation: completed") }
            )
    }

    private fun onButtonJustClick() {
        writeToLog("Main thread: button clicked")
    }

    private fun onClearLogs() {
        binding.tvLogs.text = ""
    }

    private fun writeToLog(message: String) {
        binding.tvLogs.let {
            if (it.text.isNotEmpty()) it.append("\n")
            it.append(message)
        }
    }
}
