import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.*;

public class myBlockManager implements BlockManager {
    private String path;

    public String getPath() {
        return path;
    }

    public Block getBlock(Id indexId){
        if(indexId instanceof StringId) {
            StringId sid = (StringId) indexId;
            String id = sid.getId();
            String dataPath = path + id + ".data";
            String metaPath = path + id + ".meta";
            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(!dataFile.exists()||!metaFile.exists()){
                throw new ErrorCode(ErrorCode.BLOCK_MEMORY_ERROR);
            }
            return new myBlock(indexId,this,dataPath,metaPath);
        }

        return null;
    };
    public Block newBlock(byte[] b){
        String idPath = path + "id.count";
        java.io.File file = new java.io.File(idPath);
        if(!file.exists()){
            throw new ErrorCode(ErrorCode.INITFILE_ERROR);
        }
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tmp = null;
            tmp = br.readLine();
            long newIndex = Long.parseLong(tmp);
            String dataPath = path + newIndex + ".data";
            String metaPath = path + newIndex + ".meta";
            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(dataFile.exists() || metaFile.exists()){
                throw new ErrorCode(ErrorCode.INITFILE_ERROR);
            }
            Block newBlock = new myBlock(new StringId(tmp),this,dataPath,metaPath);
            newIndex++;
            FileOutputStream out = new FileOutputStream(file,false);
            StringBuffer sb = new StringBuffer();
            sb.append(newIndex);
            out.write(sb.toString().getBytes("utf-8"));
            out.close();
            sb.delete(0,sb.length());


            dataFile.createNewFile();
            metaFile.createNewFile();


            FileOutputStream os_data = new FileOutputStream(dataFile,false);
            BufferedInputStream is_data = new BufferedInputStream(new ByteArrayInputStream(b));
            os_data.write(b);
            os_data.flush();
            is_data.close();
            os_data.close();


            FileOutputStream out_meta = new FileOutputStream(metaFile,false);
            sb.append("size:"+b.length+"\n");
            String md5 = MD5Util.getMD5String(b);
            sb.append("checksum:"+md5+"\n");
            out_meta.write(sb.toString().getBytes("utf-8"));

        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }catch (ErrorCode errorCode){
            throw errorCode;
        }

        return null;
    };
    public myBlockManager(String path){
        this.path = path;
    }
}
