import interfaces.*;

import java.util.ArrayList;
import java.util.Map;

public class MyFile implements File {
    private long fileSize;
    private long blockSize;
    ArrayList<Map<Id,Id>> LogicBlockList = new ArrayList<>();
    private FileManager fileManager;
    private Id fileId;
    private long cursor = 0;
    public MyFile(Id id, FileManager fileManager, long fileSize, long blockSize,ArrayList<Map<Id,Id>> list){
        this.fileId = id;
        this.fileManager = fileManager;
        this.fileSize = fileSize;
        this.blockSize = blockSize;
        this.LogicBlockList = list;

    }

    public Id getFileId(){
        return fileId;
    };
    public FileManager getFileManager(){
        return fileManager;
    };

    public byte[] read(int length){
        if(length < 0)
            throw new ErrorCode(5);
        if(length == 0)
            return null;
        long indexBegin = cursor/blockSize;
        long indexEnd = (cursor+length > fileSize)?fileSize/blockSize:(cursor+length)/blockSize;
        for(long i = indexBegin; i <= indexEnd; i++){


        }
        return null;
    };
    public void write(byte[] b){

    };

    public long move(long offset, int where){
        return 0;
    };
    public void close(){

    };
    public long size(){
        return fileSize;
    };
    public void setSize(long newSize){
        this.fileSize = newSize;
    };

    private byte[] chooseDuplication(Map<Id,Id> map){
        String wd = "./path/to/";
        for(Map.Entry<Id,Id> entry : map.entrySet()){
            String path = wd + entry.getKey()+"/";
            BlockManager blockManager = new myBlockManager(path);
            try {
                Block block = blockManager.getBlock(entry.getValue());
            }catch (ErrorCode errorCode){
                if(errorCode.getErrorCode() == 6){

                }
            }


        }

        return null;

    }
}
