package com.yourcompany.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
public static void saveConversationHistory(String history) {
        try {
            File file = new File(System.getProperty("user.home") + "/Desktop/conversation_history.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); // Append mode
            writer.write(history);
            writer.newLine(); // Add a new line for separation
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
