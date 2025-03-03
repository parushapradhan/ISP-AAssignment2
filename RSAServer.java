import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.Cipher;

public class RSAServer {
    public static void main(String[] args) throws Exception {
        final int SERVER_PORT = 8000;
        
        // Generate RSA key pair for the server.
        KeyPair rsaKeyPair = generateRSAKeyPair();
        PublicKey serverPubKey = rsaKeyPair.getPublic();
        PrivateKey serverPrivKey = rsaKeyPair.getPrivate();
        
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server listening on port " + SERVER_PORT);
            
            try (Socket connection = serverSocket.accept();
                 ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream())) {
                 
                System.out.println("Client connected.");
                
                // Exchange public keys.
                outputStream.writeObject(serverPubKey);
                outputStream.flush();
                PublicKey clientPubKey = (PublicKey) inputStream.readObject();
                
                // Receive the encrypted message and the signature.
                byte[] encryptedData = (byte[]) inputStream.readObject();
                byte[] signatureData = (byte[]) inputStream.readObject();
                
                // Decrypt the message using the server's private key.
                byte[] decryptedBytes = decryptData(encryptedData, serverPrivKey);
                String decryptedMessage = new String(decryptedBytes, "UTF-8");
                
                // Verify the signature using the client's public key.
                boolean signatureValid = verifySignature(decryptedBytes, signatureData, clientPubKey);
                
                System.out.println("Decrypted Message: " + decryptedMessage);
                System.out.println("Signature Verified: " + signatureValid);
            }
        }
    }
    
    private static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
    
    private static byte[] decryptData(byte[] cipherData, PrivateKey privateKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return rsaCipher.doFinal(cipherData);
    }
    
    private static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}
