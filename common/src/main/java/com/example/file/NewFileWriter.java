package com.example.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Обертка для удобной записи в файл.
 */

public class NewFileWriter {
    /**
     * Записывает строки в файл по одной.
     * Использует BufferOutputStream для помещения строки в буфер целиком и записи за раз.
     * @param line
     * @param fileName
     * @throws IOException
     */
    public void writeLineToFile(String line, String fileName) throws IOException {
            byte[] byteLine = line.getBytes(StandardCharsets.UTF_8);
            try (
                    FileOutputStream fos = new FileOutputStream(fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, byteLine.length);
            ) {

                bos.write(byteLine);
                bos.flush();
            }

    }
}
