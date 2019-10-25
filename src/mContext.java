import interfaces.Id;
import java.util.HashMap;
import java.util.Map;

public class mContext {
    public static Map<Id,myBlockManager> myBlockManagerMap;
    public static Map<Id,myFileManager> myFileManagerMap;
    public static Map<Id,Id> fileEmpytMap;
    static {
        myBlockManagerMap = new HashMap<>();
        myBlockManagerMap.put(new StringId("bm-01"),new myBlockManager("./path/to/bm-01/"));
        myBlockManagerMap.put(new StringId("bm-02"),new myBlockManager("./path/to/bm-02/"));
        myBlockManagerMap.put(new StringId("bm-03"),new myBlockManager("./path/to/bm-03/"));

        myFileManagerMap = new HashMap<>();
        myFileManagerMap.put(new StringId("fm-01"),new myFileManager("./path/to/fm-01/"));
        myFileManagerMap.put(new StringId("fm-02"),new myFileManager("./path/to/fm-02/"));

        fileEmpytMap = new HashMap<>();
        fileEmpytMap.put(new StringId("FILE_EMPTY"),new StringId("FILE_EMPTY"));
    }
}
