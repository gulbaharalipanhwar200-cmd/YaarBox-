package com.example.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object YaarBoxSoundService {
  private const val TAG = "YaarBoxSoundService"
  private val scope = CoroutineScope(Dispatchers.Default)
  private var ringJob: Job? = null
  private var currentRingTrack: AudioTrack? = null
  private var soundEnabled = true

  fun setSoundEnabled(enabled: Boolean) {
    soundEnabled = enabled
  }

  fun isSoundEnabled(): Boolean = soundEnabled

  /**
   * App open melodic chime (C5 -> E5 -> G5)
   */
  fun playOpenSound() {
    if (!soundEnabled) return
    scope.launch {
      try {
        playMultiTone(
          frequencies = listOf(523.25, 659.25, 783.99),
          durationMs = 120,
          pauseMs = 25
        )
      } catch (_: CancellationException) {
        // Normal cancellation
      } catch (e: Exception) {
        Log.e(TAG, "Failed playing open sound", e)
        fallbackTone(ToneGenerator.TONE_PROP_ACK)
      }
    }
  }

  /**
   * Message sent/received bubble pop sound
   */
  fun playPopSound() {
    if (!soundEnabled) return
    scope.launch {
      try {
        playPopWaveform()
      } catch (_: CancellationException) {
        // Normal cancellation
      } catch (e: Exception) {
        Log.e(TAG, "Failed playing pop sound", e)
        fallbackTone(ToneGenerator.TONE_PROP_BEEP)
      }
    }
  }

  /**
   * Incoming or outgoing call ringing loop
   */
  fun startCallRingtone() {
    if (!soundEnabled) return
    stopCallRingtone()
    ringJob = scope.launch {
      try {
        while (isActive) {
          playDualTone(freq1 = 440.0, freq2 = 480.0, durationMs = 1200)
          delay(1800)
        }
      } catch (e: Throwable) {
        if (e is CancellationException || e.javaClass.name.contains("Cancellation")) {
          // Normal cancellation when call is accepted/declined/ended
        } else {
          Log.e(TAG, "Ring error", e)
          fallbackTone(ToneGenerator.TONE_SUP_RINGTONE)
        }
      } finally {
        stopActiveTrack()
      }
    }
  }

  fun stopCallRingtone() {
    ringJob?.cancel()
    ringJob = null
    stopActiveTrack()
  }

  private fun stopActiveTrack() {
    try {
      currentRingTrack?.pause()
      currentRingTrack?.flush()
      currentRingTrack?.stop()
      currentRingTrack?.release()
    } catch (_: Exception) {
    } finally {
      currentRingTrack = null
    }
  }

  /**
   * Call disconnected tone
   */
  fun playEndCallSound() {
    if (!soundEnabled) return
    stopCallRingtone()
    scope.launch {
      try {
        playMultiTone(
          frequencies = listOf(440.0, 330.0),
          durationMs = 160,
          pauseMs = 40
        )
      } catch (_: CancellationException) {
        // Normal cancellation
      } catch (e: Exception) {
        fallbackTone(ToneGenerator.TONE_PROP_PROMPT)
      }
    }
  }

  private fun fallbackTone(toneType: Int) {
    try {
      val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
      toneGen.startTone(toneType, 200)
      scope.launch {
        try {
          delay(250)
          toneGen.release()
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {}
  }

  private suspend fun playPopWaveform() {
    val sampleRate = 44100
    val durationMs = 90
    val totalSamples = (sampleRate * durationMs / 1000)
    val buffer = ShortArray(totalSamples)

    for (i in 0 until totalSamples) {
      val t = i.toDouble() / sampleRate
      val progress = i.toDouble() / totalSamples
      val freq = 880.0 - (progress * 660.0)
      val envelope = (1.0 - progress) * (1.0 - progress)
      val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.75
      buffer[i] = sample.toInt().toShort()
    }

    playShortArray(buffer, sampleRate, isRingtone = false)
  }

  private suspend fun playDualTone(freq1: Double, freq2: Double, durationMs: Int) {
    val sampleRate = 44100
    val totalSamples = (sampleRate * durationMs / 1000)
    val buffer = ShortArray(totalSamples)

    for (i in 0 until totalSamples) {
      val t = i.toDouble() / sampleRate
      val fade = when {
        i < 2000 -> i.toDouble() / 2000
        i > totalSamples - 2000 -> (totalSamples - i).toDouble() / 2000
        else -> 1.0
      }
      val s1 = sin(2.0 * Math.PI * freq1 * t)
      val s2 = sin(2.0 * Math.PI * freq2 * t)
      val sample = ((s1 + s2) * 0.5) * fade * Short.MAX_VALUE * 0.6
      buffer[i] = sample.toInt().toShort()
    }

    playShortArray(buffer, sampleRate, isRingtone = true)
  }

  private suspend fun playMultiTone(frequencies: List<Double>, durationMs: Int, pauseMs: Int) {
    val sampleRate = 44100
    frequencies.forEach { freq ->
      val totalSamples = (sampleRate * durationMs / 1000)
      val buffer = ShortArray(totalSamples)
      for (i in 0 until totalSamples) {
        val t = i.toDouble() / sampleRate
        val fade = when {
          i < 500 -> i.toDouble() / 500
          i > totalSamples - 500 -> (totalSamples - i).toDouble() / 500
          else -> 1.0
        }
        val sample = sin(2.0 * Math.PI * freq * t) * fade * Short.MAX_VALUE * 0.65
        buffer[i] = sample.toInt().toShort()
      }
      playShortArray(buffer, sampleRate, isRingtone = false)
      delay(pauseMs.toLong())
    }
  }

  private suspend fun playShortArray(buffer: ShortArray, sampleRate: Int, isRingtone: Boolean) {
    var audioTrack: AudioTrack? = null
    try {
      val minSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      val bufferSize = maxOf(buffer.size * 2, minSize)
      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      if (isRingtone) {
        currentRingTrack = audioTrack
      }

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()

      val playTimeMs = (buffer.size * 1000L / sampleRate) + 20L
      delay(playTimeMs)
    } catch (e: CancellationException) {
      throw e
    } catch (_: Exception) {
    } finally {
      try {
        audioTrack?.pause()
        audioTrack?.flush()
        audioTrack?.stop()
        audioTrack?.release()
      } catch (_: Exception) {}
      if (isRingtone && currentRingTrack == audioTrack) {
        currentRingTrack = null
      }
    }
  }
}
