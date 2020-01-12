package com.androkrip.utils;

import sse.org.bouncycastle.crypto.BlockCipher;
import sse.org.bouncycastle.crypto.BufferedBlockCipher;
import sse.org.bouncycastle.crypto.CipherParameters;
import sse.org.bouncycastle.crypto.engines.TwofishEngine;
import sse.org.bouncycastle.crypto.modes.CBCBlockCipher;
import sse.org.bouncycastle.crypto.modes.EAXBlockCipher;
import sse.org.bouncycastle.crypto.paddings.BlockCipherPadding;
import sse.org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import sse.org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import sse.org.bouncycastle.crypto.params.AEADParameters;
import sse.org.bouncycastle.crypto.params.KeyParameter;
import sse.org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Cipher Provider with Bouncy Castle lightweight API
 * 
 * @author Unicus (unicus<atmark>paranoiaworks.com) for Paranoia Works
 */
public class CipherProvider {
	
	public static final int ALG_TWOFISH  = 0;

	
	public static BufferedBlockCipher getBufferedBlockCipher(boolean forEncryption, byte[] iv, byte[] key, int algorithmCode) //CBC
	{
		return getBufferedBlockCipher(forEncryption, iv, key, algorithmCode, true);
	}
	
	public static BufferedBlockCipher getBufferedBlockCipher(boolean forEncryption, byte[] iv, byte[] key, int algorithmCode, boolean withPadding) //CBC
	{
		BufferedBlockCipher cipher = null;
  	    KeyParameter keyParam = new KeyParameter(key);
  	    CipherParameters params = new ParametersWithIV(keyParam, iv);
  	    BlockCipherPadding padding = new ISO10126d2Padding();
  	    cipher = withPadding ? new PaddedBufferedBlockCipher(getBaseCBCCipher(algorithmCode), padding) : new BufferedBlockCipher(getBaseCBCCipher(algorithmCode));
		cipher.init(forEncryption, params);
		
		return cipher;
	}
	
	public static EAXBlockCipher getEAXCipher(boolean forEncryption, byte[] nonce, byte[] key, int algorithmCode)
	{
		EAXBlockCipher cipher = getBaseEAXCipher(algorithmCode);
		KeyParameter keyParam = new KeyParameter(key);
		int macSize = cipher.getBlockSize() * 8;
		if(macSize > 256) macSize = 256; // limit MAC size to 256
		CipherParameters params = new AEADParameters(keyParam, macSize, nonce);
		cipher.init(forEncryption, params);

		return cipher;
	}
	
	
	private static EAXBlockCipher getBaseEAXCipher(int algorithmCode)
	{
		EAXBlockCipher baseCipher = null;
		switch (algorithmCode)
        {        	
        	case 0: 
        	{
				baseCipher = new EAXBlockCipher(new TwofishEngine());
				break;
        	}
        	default: 
            	break;
        }
		
		return baseCipher;
	}	
	
	private static BlockCipher getBaseCBCCipher(int algorithmCode)
	{
		BlockCipher baseCipher = null;
		switch (algorithmCode)
        {        	
        	case 0: 
        	{
				baseCipher = new CBCBlockCipher(new TwofishEngine());
				break;
        	}
        	default: 
            	break;
        }
		
		return baseCipher;
	}
}
