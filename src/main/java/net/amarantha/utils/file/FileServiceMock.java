package net.amarantha.utils.file;

import java.util.HashMap;
import java.util.Map;

public class FileServiceMock implements FileService {

    private Map<String, String> fileContents = new HashMap<>();

    @Override
    public String readFromFile(String filename) {
        return fileContents.get(filename);
    }

    @Override
    public boolean writeToFile(String filename, String content) {
        fileContents.put(filename, content);
        return true;
    }
}
