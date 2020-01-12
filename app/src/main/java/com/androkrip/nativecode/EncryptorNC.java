package com.androkrip.nativecode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.androkrip.utils.Helpers;

/**
 * Native Code Helper + Wrapper
 * 
 * @author Unicus (unicus<atmark>paranoiaworks.com) for Paranoia Works
 */
public class EncryptorNC {
	
	public static final int AC_TWOFISH_256 = 0;
	public static final int AC_THREEFISH_1024 = 7;
	
	private static boolean initOk = false;
	private static Map<Integer, Integer> tweakSizeMap = new HashMap<Integer, Integer>();
	
	static {
		try {			
			System.loadLibrary("pwncenc");
			initOk = true;
			tweakSizeMap.put(AC_THREEFISH_1024, 16);
		} catch (UnsatisfiedLinkError e) {
			// disable N.C.
		}
	}
	
	private native int encryptByteArrayNC(byte[] iv, byte[] key, byte[] data, int algorithmCode);
	private native int decryptByteArrayNC(byte[] iv, byte[] key, byte[] data, int algorithmCode);
	
	public boolean encryptByteArray(byte[] iv, byte[] key, byte[] data, int algorithmCode)
	{
		Integer tweakSize = tweakSizeMap.get(algorithmCode);
		byte[] keyCopy = getByteArrayCopy(key);
		if (tweakSize != null) keyCopy = Helpers.concat(keyCopy, new byte[tweakSize]);
		byte[] ivCopy = getByteArrayCopy(iv);
		
		int ok = encryptByteArrayNC(ivCopy, keyCopy, data, algorithmCode);
		return ok == 1 ? true : false;		
	}
	
	public boolean decryptByteArray(byte[] iv, byte[] key, byte[] data, int algorithmCode)
	{
		Integer tweakSize = tweakSizeMap.get(algorithmCode);
		byte[] keyCopy = getByteArrayCopy(key); 
		if (tweakSize != null) keyCopy = Helpers.concat(keyCopy, new byte[tweakSize]);
		byte[] ivCopy = getByteArrayCopy(iv);
		
		int ok = decryptByteArrayNC(ivCopy, keyCopy, data, algorithmCode);
		return ok == 1 ? true : false;		
	}
	
	public byte[] encryptByteArrayWithPadding(byte[] iv, byte[] key, byte[] data, int algorithmCode)
	{
		Integer tweakSize = tweakSizeMap.get(algorithmCode);
		byte[] keyCopy = getByteArrayCopy(key);
		if (tweakSize != null) keyCopy = Helpers.concat(keyCopy, new byte[tweakSize]);
        byte[] ivCopy = getByteArrayCopy(iv);
		
		byte[] padding = getPaddingBytes(iv.length, data.length);		
    	byte[] output = Helpers.concat(data, padding);
    	
		int ok = encryptByteArrayNC(ivCopy, keyCopy, output, algorithmCode);
		return ok == 1 ? output : null;		
	}
	
	public boolean checkCipher(int algorithmCode, int blockSize, int keySize)
	{	
		if(!initOk) return false;
		
		byte[] ivA = getPseudoRandomBytes(blockSize);
		byte[] ivB = getByteArrayCopy(ivA);
		byte[] keyA = getPseudoRandomBytes(keySize);
        byte[] keyB = getByteArrayCopy(keyA); 
		byte[] data = getPseudoRandomBytes(256);
		byte[] dataCopy = getByteArrayCopy(data); 

		boolean ok = false;
		ok = encryptByteArray(ivA, keyA, data, algorithmCode);
		if(!ok) return false;
		ok = decryptByteArray(ivB, keyB, data, algorithmCode);
		if(!ok) return false;
		ok = Arrays.equals(dataCopy, data);
		return ok;
	}
	
	protected static byte[] getPaddingBytes(int ivLength, int dataLength)
	{
		Random rand = new Random(System.currentTimeMillis());
		
		int paddingSize = ivLength - (dataLength % ivLength);  	
    	byte[] padding = new byte[paddingSize];
    	for (int i = 0; i < paddingSize - 1; ++i) padding[i] = (byte)(rand.nextInt());
    	padding[paddingSize - 1] = (byte)paddingSize;
    	
    	return padding;
	}
	
	private static byte[] getPseudoRandomBytes(int size)
	{
		byte[] output = new byte[size];
		Random rand = new Random(System.currentTimeMillis());
		for(int i = 0; i < size; ++i)
			output[i] = (byte)(rand.nextInt());
		return output;
	}
	
	private static byte[] getByteArrayCopy(byte[] data)
	{
		byte[] dataCopy = new byte[data.length];
		System.arraycopy(data, 0, dataCopy, 0, data.length);
		return dataCopy;
	}
}
