package net.amarantha.utils.file;

public interface FileService {
    boolean exists(String filename);

    String readFromFile(String filename);

    boolean writeToFile(String filename, String content);
}
