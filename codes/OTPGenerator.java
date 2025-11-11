import java.util.Random;

public class OTPGenerator {
    public static String generateOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return otp.toString();
    }

    public static void main(String[] args) {
        System.out.println("Generated OTP: " + generateOTP(6));
    }
}
