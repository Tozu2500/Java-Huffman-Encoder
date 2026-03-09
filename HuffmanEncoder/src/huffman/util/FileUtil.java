package huffman.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtil {

    public static String readText(File f) throws IOException {
        return Files.readString(f.toPath(), StandardCharsets.UTF_8);
    }

    public static void writeText(File f, String text) throws IOException {
        Files.writeString(f.toPath(), text, StandardCharsets.UTF_8);
    }
}
