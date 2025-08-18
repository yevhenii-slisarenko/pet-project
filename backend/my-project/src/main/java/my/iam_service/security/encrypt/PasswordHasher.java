package my.iam_service.security.encrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Генерируем хеши паролей ОДИН раз
        String super_admin_password = encoder.encode("Test11111");
        String admin_password = encoder.encode("Test22222");
        String user_password = encoder.encode("Test33333");

        // Выводим в консоль
        System.out.println("Hashed first_password: " + super_admin_password);
        System.out.println("Hashed second_password: " + admin_password);
        System.out.println("Hashed third_password: " + user_password);
    }
}