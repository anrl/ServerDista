package com.mcgill.serverdist;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaySound implements Runnable {
	final static int sampleRate = 44100;
	final static int channels = 2;
	final static int channelConfig = ((channels == 2) ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO);
	private int maxVolume = 0; 

	//final static float freq[] = {8957.8125f, 9646.875f, 10335.9375f}; //13, 14, 15
	final static float freq[] = {16537.5f}; //13, 14, 15
	final int numSamples = 44100;
	
	AudioTrack audioTrack;
    AudioChunk chunk;
    AudioManager manager;

	public PlaySound(Context cont) {
		chunk = new AudioChunk(numSamples, sampleRate, channels);

		chunk.appendSineWave(5000, freq, 32767);
		chunk.appendSineWave(10000, freq, 0);
		chunk.appendSineWave(5000, freq, 32767);
        
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, chunk.data.length, AudioTrack.MODE_STATIC);
		audioTrack.write(chunk.data, 0, chunk.data.length);

		manager = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = manager.getStreamMaxVolume(audioTrack.getStreamType());
	}
	
	public void setVolume(int percentage) {
		manager.setStreamVolume(audioTrack.getStreamType(), (int) ((percentage*this.maxVolume)/100), 0);
	}
	
	public void run() {
		if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			audioTrack.play();
			audioTrack.stop();
			audioTrack.reloadStaticData();
		}
	}
}
