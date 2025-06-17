package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import java.io.InputStream;
import java.io.OutputStream;

public interface SymmetricBlockCipher {

    void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException, CipherException;

    void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException;
}
