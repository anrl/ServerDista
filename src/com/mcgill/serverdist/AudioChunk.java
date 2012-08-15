package com.mcgill.serverdist;

public class AudioChunk {
	public short data[];
	protected int sampleRate = 44100;
	protected int channelsNumber = 1;
	protected int nextInsertPos = 0;
	
	public AudioChunk(final int numSamples, final int sampleRate, final int channelsNumber) {
		this.data = new short[numSamples*channelsNumber];
		this.sampleRate = sampleRate;
		this.channelsNumber = channelsNumber;
	}
	
	public void appendSineWave(int samples, final float frequency[], final int amp) {
		double temp[] = new double[frequency.length];
		final int maxAmplitude = amp / frequency.length;
		
		// verification to make sure the array doesn't go out of bounds
		if (nextInsertPos + samples*channelsNumber > data.length) {
			return;
		}
		
		// if amplitude is 0, just fill the samples with 0, without calculating sines
		if (amp == 0) {
			for (int i = 0; i < samples*channelsNumber; i++)
				this.data[nextInsertPos + i] = 0;
			nextInsertPos += samples*channelsNumber;
			return;
		}
		
		// calculate the angular frequency for each frequency
		for (int i = 0; i < temp.length; i++) {
			temp[i] = (Math.PI * 2 * frequency[i]) / sampleRate;
		}

		// fill the data with the sin waves
		int insertPos = nextInsertPos;
		for (int i = 0; i < samples; i++) {
			double value = 0;
			for (int k = 0; k < frequency.length; k++)
				value += (maxAmplitude * Math.sin(temp[k] * i));
			
			for (int j = 0; j < channelsNumber; j++, insertPos++) {
				this.data[insertPos] = (short) value;
			}
		}
		
		nextInsertPos = insertPos;
	}
}
