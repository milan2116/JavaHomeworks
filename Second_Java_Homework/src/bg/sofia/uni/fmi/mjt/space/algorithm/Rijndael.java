package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {
    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            throw new CipherException("Encryption failed", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            throw new CipherException("Decryption failed", e);
        }
    }
}
