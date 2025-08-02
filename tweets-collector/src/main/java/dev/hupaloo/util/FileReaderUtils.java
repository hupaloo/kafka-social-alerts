package dev.hupaloo.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileReaderUtils {

    public static String readFileContent(String fileName) {
        try (InputStream inputStream = FileReaderUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Couldn't load %s file during initialisation, error: %s",
                    fileName,
                    e.getMessage()),
                    e);
        }
    }
}
