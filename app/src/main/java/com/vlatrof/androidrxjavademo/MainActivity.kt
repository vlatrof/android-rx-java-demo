package com.vlatrof.androidrxjavademo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.vlatrof.androidrxjavademo.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogs.movementMethod = ScrollingMovementMethod()
        binding.btnClearLogs.setOnClickListener { clearLogs() }
        binding.btnJustClick.setOnClickListener { writeToLog("Main thread: button clicked") }
        binding.btnStartComputationObservable.setOnClickListener { startComputationObservable() }
        binding.btnStartComputationSingle.setOnClickListener { startComputationSingle() }
    }

    private fun startComputationSingle() {
        writeToLog("Main thread: Computation single started")
        val singleObservable = Single.create<Int> { singleEmitter ->
            Thread.sleep(1000)
            singleEmitter.onSuccess(1)
        }

        val singleDisposable = singleObservable
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ successValue ->
                writeToLog("Main thread: Computation single completed: $successValue")
            }, {})
    }

    private fun startComputationObservable() {
        writeToLog("Main thread: Computation observable started")
        val observable = Observable.create { emitter ->
            for (i in 0 until 5) {
                Thread.sleep(1000)
                emitter.onNext(i)
            }
            emitter.onComplete()
        }

        val disposable = observable
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onNextValue -> writeToLog("BG thread: computation observable: $onNextValue") },
                {},
                { writeToLog("Main thread: Computation observable completed") }
            )
    }

    private fun clearLogs() {
        binding.tvLogs.text = ""
    }

    private fun writeToLog(message: String) {
        // to TextView
        val nextLine = if (binding.tvLogs.text.isEmpty()) "" else "\n"
        binding.tvLogs.append(nextLine + message)
    }
}
