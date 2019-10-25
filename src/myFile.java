import com.sun.javafx.collections.MappingChange;
import interfaces.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myFile implements File {
    private String path;
    private long fileSize;
    private long blockSize;
    ArrayList<Map<Id,Id>> LogicBlockList;
    ArrayList<Block> usingBlocks = new ArrayList<>();
    private FileManager fileManager;
    private Id fileId;
    private long cursor = 0;
    public myFile(Id id, FileManager fileManager, long fileSize, long blockSize, ArrayList<Map<Id,Id>> list,String path){
        this.fileId = id;
        this.fileManager = fileManager;
        this.fileSize = fileSize;
        this.blockSize = blockSize;
        this.LogicBlockList = list;
        this.path = path;
    }

    public Id getFileId(){
        return fileId;
    };
    public FileManager getFileManager(){
        return fileManager;
    };

    public byte[] read(int length){
        if(length < 0)
            throw new ErrorCode(ErrorCode.READ_LENGTH_ERROR);
        if(length == 0)
            return "".getBytes();
        if(length > fileSize)
            length = (int)fileSize;
        if(cursor < 0 || (cursor >= fileSize && fileSize != 0) || (fileSize == 0 && cursor != 0))
            throw new ErrorCode(ErrorCode.CURSOR_ERROR);

        long indexBegin = cursor/blockSize;
        long indexEnd = (cursor+length > fileSize)?fileSize/blockSize:(cursor+length)/blockSize ;
        byte[] retBytes = new byte[length];
        int retBytesIndex = 0;

        try{
            byte[] bytes;
            int i = (int)indexBegin;
            bytes = chooseDuplication(LogicBlockList.get(i));
            for(int j = (int)(cursor%blockSize); j < bytes.length; j++){
                retBytes[retBytesIndex] = bytes[j];
                retBytesIndex++;
                if(retBytesIndex == length)
                    break;
            }
            i++;
            for(; i < indexEnd; i++){
                bytes = chooseDuplication(LogicBlockList.get(i));
                for(int j = 0; j < bytes.length; j++){
                    retBytes[retBytesIndex] = bytes[j];
                    retBytesIndex++;
                }
            }

            bytes = chooseDuplication(LogicBlockList.get((int)indexEnd));
            for(int j = 0; j < bytes.length & retBytesIndex < length; j++){
                retBytes[retBytesIndex] = bytes[j];
                retBytesIndex++;
                if(retBytesIndex == length)
                    break;
            }

            cursor += length;
            return retBytes;
        }catch (ErrorCode errorCode){
            throw errorCode;
        }
    };
    public void write(byte[] b){
        if(cursor < 0 || (cursor > fileSize && fileSize != 0) || (fileSize == 0 && cursor != 0))
            throw new ErrorCode(ErrorCode.CURSOR_ERROR);

        int indexBegin = (int)(cursor/blockSize);
        int indexEnd = (int)((cursor+b.length)/blockSize);
        int writeIndex = 0;
        byte[] newBytes = new byte[(int)blockSize];
        Map<Id,Id> map = null;
        try {
            map = LogicBlockList.get(indexBegin);
        }catch (IndexOutOfBoundsException e){
            map = null;
        }
        byte[] oldBytes = chooseDuplication(map);
        int s = LogicBlockList.size();
        for(int i = indexBegin; i < s; i++){
            LogicBlockList.remove(indexBegin);
        }
        if(indexBegin != indexEnd){
            int i = 0;
            for(; i < cursor%blockSize;i++){//??????????????????????
                newBytes[i] = oldBytes[i];
            }
            for(;i < blockSize;i++){
                newBytes[i] = b[writeIndex];
                writeIndex++;
            }
            LogicBlockList.add(writeDuplication(newBytes));

            for(i = indexBegin+1; i < indexEnd; i++){
                newBytes = new byte[(int)blockSize];
                for(int j = 0; j < blockSize; j++){
                    newBytes[j] = b[writeIndex];
                    writeIndex++;
                }
                LogicBlockList.add(writeDuplication(newBytes));
            }
            newBytes = new byte[b.length - writeIndex];
            for(int j = 0; j < newBytes.length; j++){//////////////////////
                newBytes[j] = b[writeIndex];
                writeIndex++;
            }
            LogicBlockList.add(writeDuplication(newBytes));
        }else {
            newBytes = new byte[(int)(cursor%blockSize) + b.length];
            int i = 0;
            for(; i < cursor%blockSize;i++){//??????????????????????
                newBytes[i] = oldBytes[i];
            }
            for(;i < newBytes.length;i++){
                newBytes[i] = b[writeIndex];
                writeIndex++;
            }
            LogicBlockList.add(writeDuplication(newBytes));
        }




        //更新file.meta
        long newFileSize = cursor+b.length;
        try {
            updateFileMeta(newFileSize);
        }catch (ErrorCode errorCode){
            throw errorCode;
        }
        //先newfilesize后再set filesize是为了一致性
        cursor += b.length;

    }

    public long move(long offset, int where){
        switch (where){
            case MOVE_CURR:
                cursor += offset;
                break;
            case MOVE_HEAD:
                cursor = offset;
                break;
            case MOVE_TAIL:
                cursor = fileSize - offset;  ///////////////////////
                break;
        }
        return 0;
    }
    public void close(){/////////////////////我觉得不需要这个
        for(Block block:usingBlocks){
            int blkId = Integer.parseInt(((StringId) block.getIndexId()).getId());
            BufferBlk bufferBlk = Buffer.findBufBlk(blkId);
            if(bufferBlk.isDelay()){
                //写回
                Buffer.writeBlk2file(bufferBlk);
                bufferBlk.setDelay(false);
            }
        }
        usingBlocks.clear();
    }
    public long size(){
        return fileSize;
    }
    public void setSize(long newSize){/////////////////要加try
        if(newSize < 0)
            throw new ErrorCode(ErrorCode.NEWSIZE_ERROR);
        long oldSize = this.fileSize;

        if(newSize > oldSize){
            int oldBlockEndIndex = (int)(oldSize/blockSize);
            int newBlockEndIndex = (int)(newSize/blockSize);

            byte[] oldBlockEnd = chooseDuplication(LogicBlockList.get(oldBlockEndIndex));
            int s = LogicBlockList.size();
            for(int i = oldBlockEndIndex; i < s; i++){
                LogicBlockList.remove(oldBlockEndIndex);
            }
            int index = 0;
            byte[] newBlock;
            if(newBlockEndIndex == oldBlockEndIndex){
                newBlock = new byte[(int)(newSize%blockSize)];
            }else {
                newBlock = new byte[(int)blockSize];
            }

            for(int i = 0; i < oldBlockEnd.length; i++){
                newBlock[index] = oldBlockEnd[i];
                index++;
            }
            int debug = 0;
            for(;index < blockSize; index++){
                newBlock[index] = 0x00;
                if(index == newBlock.length - 1)
                    break;
            }
            int debug2 = 0;
            LogicBlockList.add(writeDuplication(newBlock));
            for(int i = oldBlockEndIndex+1; i < newBlockEndIndex; i++){
                LogicBlockList.add(writeDuplication(new byte[(int)blockSize]));
            }
            int debug3 = 0;
            if(oldBlockEndIndex != newBlockEndIndex)
                LogicBlockList.add(writeDuplication(new byte[(int)(newSize%blockSize)]));

            updateFileMeta(newSize);
        }else if(newSize < oldSize){
            int newBlockEndIndex = (int)(newSize/blockSize); //newIndex oldIndex
            byte[] newBlockEnd = chooseDuplication(LogicBlockList.get(newBlockEndIndex));
            int s = LogicBlockList.size();
            for(int i = newBlockEndIndex; i < s; i++){
                LogicBlockList.remove(newBlockEndIndex);
            }

            byte[] newBlock = new byte[(int)(newSize%blockSize)];
            for(int i = 0; i < newBlock.length; i++){
                newBlock[i] = newBlockEnd[i];
            }
            LogicBlockList.add(writeDuplication(newBlock));
            updateFileMeta(newSize);
        }

    }

    private byte[] chooseDuplication(Map<Id,Id> map){
        byte[] blockData = null;
        if(map == null){
            blockData = new byte[(int)blockSize];
        }else {
            for(Map.Entry<Id,Id> entry : map.entrySet()){
                if((entry.getKey()).equals(new StringId("FILE_EMPTY"))){ //处理文件空洞,newEmpytBlock不应该用在bm里因为文件空洞的时候是不存在block里的
                    blockData = new byte[(int)blockSize];
                    break;
                }else {
                    BlockManager blockManager = mContext.myBlockManagerMap.get(entry.getKey());
                    try {
                        Block block = blockManager.getBlock(entry.getValue());
                        blockData = block.read();
//                        usingBlocks.add(entry.getValue());
                        break;
                    }catch (ErrorCode errorCode){
                        throw errorCode;
                    }
                }
            }
        }

        if(blockData == null)
            throw new ErrorCode(ErrorCode.MEMORY_ERROR);
        else
            return blockData;
    }

    /**
     * bm amount should not larger than 9
     * @param b
     * @return
     */
    private Map<Id,Id> writeDuplication(byte[] b){
        Map<Id,Id> ret = new HashMap<>();
        boolean isEmpty1 = true;
        for(byte b1 : b){
            if(b1 != 0x00)
                isEmpty1 = false;
        }
        if(isEmpty1){
            return mContext.fileEmpytMap;
        }else {

            int bm_number = mContext.myBlockManagerMap.size();
            int i = (int) (Math.random() * bm_number) + 1;//1,2,3
            StringId stringId1 = new StringId("bm-0" + i);
            Block block1 = writeBlock(stringId1, b);

            int j = i;
            while (j == i) {
                j = (int) (Math.random() * bm_number) + 1;
            }
            StringId stringId2 = new StringId("bm-0" + j);
            Block block2 = writeBlock(stringId2, b);

            ret.put(stringId1, block1.getIndexId());
            ret.put(stringId2, block2.getIndexId());
            return ret;
        }
    }
    private Block writeBlock(StringId id,byte[] b){
        BlockManager blockManager = mContext.myBlockManagerMap.get(id);
        Block newBlock = blockManager.newBlock(b);
        usingBlocks.add(newBlock);
        return newBlock;
    }
    private void updateFileMeta(long newFileSize){
        java.io.File changeMetaFile = new java.io.File(path);
        if(!changeMetaFile.exists())
            throw new ErrorCode(ErrorCode.MEMORY_ERROR);
        try {
            FileOutputStream out = new FileOutputStream(changeMetaFile,false);
            StringBuffer sb = new StringBuffer();
            sb.append("size:"+newFileSize+"\n");
            sb.append("block size:"+blockSize+"\n");
            sb.append("logic block:\n");
            for(int k = 0; k < LogicBlockList.size(); k++){
                sb.append(k+":");
                if(LogicBlockList.get(k).equals(mContext.fileEmpytMap)){

                }else {
                    for(Map.Entry<Id,Id> entry : LogicBlockList.get(k).entrySet()){
                        sb.append("[\""+((StringId)entry.getKey()).getId()+"\","+((StringId)entry.getValue()).getId()+"] ");
                    }
                }

                sb.append("\n");
            }
            out.write(sb.toString().getBytes("utf-8"));
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
        this.fileSize = newFileSize;
    }
}
