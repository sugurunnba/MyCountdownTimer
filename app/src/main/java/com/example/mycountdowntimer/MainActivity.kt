package com.example.mycountdowntimer

import android.content.IntentSender
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long):
    CountDownTimer(millisInFuture, countDownInterval) {
//        カウントダウン中か停止中かを表すフラグ
        var isRunning = false

        override fun onTick(millisUntilFinished: Long){
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            binding.timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish(){
            binding.timerText.text = "0:00"
//            カウントが0になった時、soundResIdに入っているサウンドが流れる
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.timerText.text = "3:00"
        var timer = MyCountDownTimer(3 * 60 * 1000, 100)
        binding.playStop.setOnClickListener{
            timer.isRunning = when (timer.isRunning){
                true -> {
                    timer.cancel()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    false
                }
                false -> {
                    timer.start()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_stop_24
                    )
                    true
                }
            }
        }

//        スピナーの設定
        binding.spinner.onItemSelectedListener =
//            クリックされた時、無名インナークラスを使ってonItemSecectedListenerにどのデータを渡すかを指定
            object : AdapterView.OnItemSelectedListener{
//                スピナーで項目が選択された場合に、このメソッドが呼ばれる。メソッドにて、どのデータを呼ぶかを指定する。
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ){
                    timer.cancel()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
//                    as : キャストを行う
//                    → 対象オブジェクト as キャストしたい型名
//                    as? : 型が一致すればキャストを行い、一致しなければnullを返す
//                    → 対象オブジェクト as? キャストしたい型名
                    val spinner = parent as? Spinner
//                    selectedImtem() : 選択中の項目、またはnullを取得する
                    val item = spinner?.selectedItem as? String
                    item?.let{
//                        ここでのitはitem
                        if(it.isNotEmpty()) binding.timerText.text = it
                        val times = it.split(":")
                        val min = times[0].toLong()
                        val sec = times[1].toLong()
                        timer = MyCountDownTimer((min * 60 + sec) * 1000, 100)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?){

                }
            }
    }

    override fun onResume(){
        super.onResume()
        soundPool =
            SoundPool.Builder().run{
                val audioAttributes = AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_ALARM)
//                    build() : AudioAttributesインスタンスの生成
                    build()
                }
                setMaxStreams(1)
                setAudioAttributes(audioAttributes)
//                build() : SoundPoolオブジェクトを生成
                build()
            }
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause(){
        super.onPause()
        soundPool.release()
    }
}