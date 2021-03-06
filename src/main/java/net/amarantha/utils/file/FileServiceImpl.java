package net.amarantha.utils.file;

import com.google.inject.Singleton;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class FileServiceImpl implements FileService {

    @Override
    public boolean exists(String filename) {
        return Files.exists(Paths.get(filename));
    }

    @Override
    public String readFromFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean writeToFile(String filename, String content) {
        try (FileWriter output = new FileWriter(filename) ) {
            output.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
