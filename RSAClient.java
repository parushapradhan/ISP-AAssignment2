import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.Cipher;

public class RSAClient {
    public static void main(String[] args) throws Exception {
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 8000;
        
        // Generate RSA key pair for the client.
        KeyPair clientKeyPair = createRSAKeyPair();
        PublicKey clientPubKey = clientKeyPair.getPublic();
        PrivateKey clientPrivKey = clientKeyPair.getPrivate();
        
        try (Socket connection = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream outStream = new ObjectOutputStream(connection.getOutputStream());
             ObjectInputStream inStream = new ObjectInputStream(connection.getInputStream())) {
             
            System.out.println("Connected to server.");
            
            // Receive the server's public key.
            PublicKey serverPubKey = (PublicKey) inStream.readObject();
            
            // Send the client's public key to the server.
            outStream.writeObject(clientPubKey);
            outStream.flush();
            
            // Prepare the original message.
            String originalMessage = "Message!";
            byte[] messageData = originalMessage.getBytes("UTF-8");
            
            // Encrypt the message using the server's public key.
            byte[] encryptedData = encryptWithRSA(messageData, serverPubKey);
            
            // Sign the original message using the client's private key.
            byte[] signatureData = signData(messageData, clientPrivKey);
            
            // Send the encrypted message and signature to the server.
            outStream.writeObject(encryptedData);
            outStream.writeObject(signatureData);
            outStream.flush();
            
            System.out.println("Message and signature sent.");
        }
    }
    
    private static KeyPair createRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        return keyPairGen.generateKeyPair();
    }
    
    private static byte[] encryptWithRSA(byte[] data, PublicKey pubKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return rsaCipher.doFinal(data);
    }
    
    private static byte[] signData(byte[] data, PrivateKey privKey) throws Exception {
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privKey);
        signer.update(data);
        return signer.sign();
    }
}
