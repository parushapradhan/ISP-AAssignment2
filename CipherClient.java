import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;

public class CipherClient
{
    public static void main(String[] args) throws Exception 
    {
        String plainText = "The quick brown fox jumps over the lazy dog.";
        String serverIP = "127.0.0.1";
        int serverPort = 7999;
        Socket socketConnection = new Socket(serverIP, serverPort);
        
        // Step 1: Generate a DES key.
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        SecretKey secretKey = generator.generateKey();
        
        // Step 2: Store the key in a file.
        try (FileOutputStream fileStream = new FileOutputStream("deskey.dat")) {
            fileStream.write(secretKey.getEncoded());
        }
        
        // Step 3: Use the key to encrypt the message and send it to the server.
        Cipher encryptor = Cipher.getInstance("DES/ECB/PKCS5Padding");
        encryptor.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = encryptor.doFinal(plainText.getBytes("UTF-8"));
        
        // Step 4: Open a socket connection to the server and send the encrypted message.
        DataOutputStream outputStream = new DataOutputStream(socketConnection.getOutputStream());
        
        // Optionally, send the length of the encrypted message first.
        outputStream.writeInt(cipherText.length);
        outputStream.write(cipherText);
        outputStream.flush();
    }
}
