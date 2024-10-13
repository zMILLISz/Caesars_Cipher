import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CaesarCipher {
    // Метод шифрования текста
    public static String encrypt(String text, int key) {
        StringBuilder encrypted = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                // Понимаем, какой алфавит использовать
                if (ch >= 'a' && ch <= 'z') { // английские буквы
                    encrypted.append((char) ((ch - 'a' + key) % 26 + 'a'));
                } else if (ch >= 'A' && ch <= 'Z') { // заглавные английские буквы
                    encrypted.append((char) ((ch - 'A' + key) % 26 + 'A'));
                }
            } else {
                encrypted.append(ch); // Добавляем символ без изменений
            }
        }
        return encrypted.toString();
    }

    // Метод расшифрования текста
    public static String decrypt(String text, int key) {
        return encrypt(text, 26 - key); // Правильное расшифрование
    }

    // Чтение из файла
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    // Запись в файл
    public static void writeFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes());
    }

    // Валидация ключа
    public static boolean isValidKey(int key) {
        return key >= 1 && key <= 25; // Проверяем, что ключ находится в пределах от 1 до 25
    }

    // Обработка входных данных
    public static void processInput(int mode, String filePath, int key) {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            System.out.println("Файл не существует. Пожалуйста, проверьте путь.");
            return;
        }

        String content;
        try {
            content = readFile(path);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        String result;
        String newFilePath;

        if (mode == 1) {
            result = encrypt(content, key);
            newFilePath = "encrypted_" + path.getFileName().toString();
        } else if (mode == 2) {
            result = decrypt(content, key);
            newFilePath = "decrypted_" + path.getFileName().toString();
        } else {
            System.out.println("Неверный режим. Ожидался 1 (шифрование) или 2 (расшифрование).");
            return;
        }

        // Записываем в новый файл в ту же директорию
        Path outputPath = path.getParent().resolve(newFilePath);
        try {
            writeFile(outputPath, result);
            System.out.println("Результат сохранён в файл: " + outputPath);
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    // Метод расшифровки с перебором всех ключей (brute force)
    public static void bruteForceDecrypt(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("Файл не существует. Пожалуйста, проверьте путь.");
            return;
        }

        String content;
        try {
            content = readFile(path);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        for (int key = 1; key <= 25; key++) {
            String decrypted = decrypt(content, key);
            // Выводим расшифрованный текст с указанием ключа и длины текста
            System.out.println("Ключ " + key + ": " + decrypted + " (" + decrypted.length() + " символов)");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим: 1 - Шифрование, 2 - Расшифрование с ключом, 3 - Brute Force");
        int mode = scanner.nextInt();

        System.out.print("Введите путь к файлу: ");
        scanner.nextLine(); // Очистка буфера
        String filePath = scanner.nextLine();

        // Обработка ввода только для режима расшифрования с ключом
        if (mode == 2 || mode == 1) {
            System.out.print("Введите ключ (1-25): ");
            int key = scanner.nextInt();
            if (!isValidKey(key)) {
                System.out.println("Недопустимый ключ. Ключ должен быть в диапазоне от 1 до 25.");
            } else {
                processInput(mode, filePath, key);
            }
        } else if (mode == 3) {
            bruteForceDecrypt(filePath);
        } else {
            System.out.println("Некорректный режим.");
        }
        scanner.close();
    }
}
