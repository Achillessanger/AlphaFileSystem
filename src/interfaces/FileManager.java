package interfaces;

import interfaces.File;

public interface FileManager {
    File getFile(Id fileId);
    File newFile(Id fileID);
}
