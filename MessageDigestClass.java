import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestClass {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string: ");
        String input = scanner.nextLine();

        try {
            // Create MD5
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(input.getBytes());
            byte[] md5HashBytes = md5Digest.digest();
            String md5Hash = bytesToHex(md5HashBytes);
            System.out.println("MD5 Hash: " + md5Hash);

            // Create SHA
            MessageDigest shaDigest = MessageDigest.getInstance("SHA");
            shaDigest.update(input.getBytes());
            byte[] shaHashBytes = shaDigest.digest();
            String shaHash = bytesToHex(shaHashBytes);
            System.out.println("SHA Hash: " + shaHash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        scanner.close();
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
