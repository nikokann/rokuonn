
package com.example.ru_1218.myrecoder

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.IOException
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

    private var player: MediaPlayer? = null


    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }





    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val record = findViewById<Button>(R.id.redord) //録音オブジェクト取得
        val stop = findViewById<Button>(R.id.stop) //録音停止オブジェクト取得
        val playback = findViewById<Button>(R.id.playback) //再生オブジェクト取得

        val listener = RecordButton() //レコードボタンリスナのインスタンス生成

        record.setOnClickListener(listener) //レコードボタンリスナの設定
        stop.setOnClickListener(listener)
        playback.setOnClickListener(listener)

    }

    //クリックイベントの設定
    private inner class RecordButton : View.OnClickListener {
        override fun onClick(v: View?) {
            Log.i(LOG_TAG, "クリック成功")
            Log.i(LOG_TAG, fileName)

            if(v != null){
                when(v.id){
                    //録音開始ボタン
                    R.id.redord -> {
                        onRecord(true)
                        Log.i(LOG_TAG, "録音開始")
                    }
                    //録音停止ボタン
                    R.id.stop -> {
                        onRecord(false)
                        Log.i(LOG_TAG, "録音終了")
                    }

                    R.id.playback -> {
                        onPlay(true)
                        Log.i(LOG_TAG, "再生中")
                    }
                }
            }
        }
    }
}