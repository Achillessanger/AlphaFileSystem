import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.*;
import java.util.ArrayList;

public class myBlockManager implements BlockManager {
    private String path;
    private Id name;

    public String getPath() {
        return path;
    }
    public myBlockManager(String path,Id id){
        this.path = path;
        this.name = id;
    }

    @Override
    public Id getName() {
        return name;
    }

    @Override
    public Block getBlock(Id indexId){
        if(indexId instanceof StringId) {
            StringId sid = (StringId) indexId;
            String id = sid.getId();

//            BufferBlk blkIfCached = Buffer.findBufBlk(Integer.parseInt(id));
//            if(blkIfCached != null){
//
//            }

            String dataPath = path + id + ".data";
            String metaPath = path + id + ".meta";
//            java.io.File dataFile = new java.io.File(dataPath);
//            java.io.File metaFile = new java.io.File(metaPath);
//            if(!dataFile.exists()||!metaFile.exists()){
//                throw new ErrorCode(ErrorCode.BLOCK_MEMORY_ERROR);
//            }
            return new myBlock(indexId,this,dataPath,metaPath);
        }

        return null;
    }

    @Override
    public Block newBlock(byte[] b){
        String idPath = path + "../id.count";
        java.io.File file = new java.io.File(idPath);
        if(!file.exists()){
            throw new ErrorCode(ErrorCode.INITFILE_ERROR);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tmp;
            tmp = br.readLine();
            long newIndex = Long.parseLong(tmp);
            String dataPath = path + newIndex + ".data";
            String metaPath = path + newIndex + ".meta";


            int bufIndex = (int) newIndex % (Buffer.BUFFER_LINES);
            BufferBlk newBlk = Buffer.findFreeBlk();
            if (newBlk == null) {
                //空闲缓冲区已空？？？？？？？？？？？？？？？？？？？？？？？暂时认为不可能
                throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);

            } else {
                Buffer.delayWrite();
                Buffer.deleteFromCache(newBlk);
                newBlk.setData(b);
                newBlk.setDelay(true);
                newBlk.setBufBlkManager((StringId) name);
                StringId sid = new StringId(newIndex + "");
                newBlk.setBufBlkId(sid);
                Buffer.makeBusy(newBlk, bufIndex);
                Buffer.makeFree(newBlk);


                newIndex++;

                FileOutputStream out = new FileOutputStream(file, false);
                StringBuffer sb = new StringBuffer();
                sb.append(newIndex);
                out.write(sb.toString().getBytes("utf-8"));
                out.close();

                return new myBlock(sid, this, dataPath, metaPath);
            }
        } catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    }




//
//            java.io.File dataFile = new java.io.File(dataPath);
//            java.io.File metaFile = new java.io.File(metaPath);
//            if(dataFile.exists() || metaFile.exists()){
//                throw new ErrorCode(ErrorCode.INITFILE_ERROR);
//            }
//            Block newBlock = new myBlock(new StringId(tmp),this,dataPath,metaPath);
//            newIndex++;
//            FileOutputStream out = new FileOutputStream(file,false);
//            StringBuffer sb = new StringBuffer();
//            sb.append(newIndex);
//            out.write(sb.toString().getBytes("utf-8"));
//            out.close();
//            sb.delete(0,sb.length());
//
//
//            dataFile.createNewFile();
//            metaFile.createNewFile();
//
//
//            FileOutputStream os_data = new FileOutputStream(dataFile,false);
//            BufferedInputStream is_data = new BufferedInputStream(new ByteArrayInputStream(b));
//            os_data.write(b);
//            os_data.flush();
//            is_data.close();
//            os_data.close();
//
//
//            FileOutputStream out_meta = new FileOutputStream(metaFile,false);
//            sb.append("size:"+b.length+"\n");
//            String md5 = MD5Util.getMD5String(b);
//            sb.append("checksum:"+md5+"\n");
//            out_meta.write(sb.toString().getBytes("utf-8"));
//
//        }catch (IOException e){
//            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
//        }catch (ErrorCode errorCode){
//            throw errorCode;
//        }
//
//        return null;


}
