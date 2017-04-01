package net.amarantha.utils.file;

public interface FileService {
    String readFromFile(String filename);

    boolean writeToFile(String filename, String content);
}
