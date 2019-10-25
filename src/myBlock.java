import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class myBlock implements Block {
    private Id indexId;
    private BlockManager blockManager;
    private String dataPath;
    private String metaPath;

    public myBlock(Id indexId,BlockManager blockManager,String dataPath,String metaPath){
        this.indexId = indexId;
        this.blockManager = blockManager;
        this.dataPath = dataPath;
        this.metaPath = metaPath;
    }

    public Id getIndexId(){
        return indexId;
    };
    public BlockManager getBlockManager(){
        return blockManager;
    };
    public byte[] read(){
        int blkId = Integer.parseInt(((StringId)indexId).getId());
        int bufIndex =blkId %(Buffer.BUFFER_LINES);
        //在cache里找

        ArrayList<ArrayList<BufferBlk>> debug = Buffer.cache;

        for(int i = 0; i < Buffer.cache.get(bufIndex).size(); i++){
            //！注意这里没有讨论块忙的情况!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if(Integer.parseInt(Buffer.cache.get(bufIndex).get(i).getBufBlkId().getId()) == blkId
                && !Buffer.cache.get(bufIndex).get(i).isBusy()
                && Buffer.cache.get(bufIndex).get(i).getBufBlkManager().equals(blockManager.getName())){
//                Buffer.cache.get(bufIndex).get(i).setBusy(true);
                return Buffer.cache.get(bufIndex).get(i).getData();
            }
        }
        //没有被放入cache缓冲区
//        BufferBlk tmpBlk = Buffer.freeHead.getNextFreeBufBlk();
        BufferBlk newBlk = Buffer.findFreeBlk();
        if(newBlk == null){
            //空闲缓冲区已空？？？？？？？？？？？？？？？？？？？？？？？暂时认为不可能
            throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);

        }else {
            Buffer.delayWrite();

            //找到新的缓冲区tmp
            //把tmp从空闲缓冲区链表取下
//            BufferBlk tmpPre = tmpBlk.getPreFreeBufBlk();
//            BufferBlk tmpNext = tmpBlk.getNextFreeBufBlk();
//            if(tmpPre != null && tmpNext != null){
//                tmpPre.setNextFreeBufBlk(tmpNext);
//                tmpNext.setPreFreeBufBlk(tmpPre);
//            }else if(tmpPre != null && tmpNext == null){
//                tmpPre.setNextFreeBufBlk(null);
//            }
//            Buffer.cache.get(bufIndex).add(tmpBlk);
//            tmpBlk.setBusy(true);
            Buffer.deleteFromCache(newBlk);
            Buffer.makeBusy(newBlk,bufIndex);


            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(!dataFile.exists() || !metaFile.exists()){
                Buffer.makeFree(newBlk);
                throw new ErrorCode(ErrorCode.NO_SUCH_BLOCK);
            }
            byte[] data;
            String checkSum;
            try {
                BufferedReader br = new BufferedReader(new FileReader(metaFile));
                String tmp = br.readLine();
                long blockSize = Long.parseLong(tmp.split(":")[1]);
                tmp = br.readLine();
                checkSum = tmp.split(":")[1];

                data = new byte[(int)blockSize];
                new FileInputStream(dataFile).read(data);

            }catch (IOException e){
                Buffer.makeFree(newBlk);
                throw new ErrorCode(ErrorCode.IO_EXCEPTION);
            }
            boolean check;
            try{
                check = MD5Util.checkPassword(data,checkSum);
            }catch (ErrorCode errorCode){
                Buffer.makeFree(newBlk);
                throw errorCode;
            }
            if(check) {
                newBlk.setData(data);
                newBlk.setBufBlkId((StringId)indexId);
                newBlk.setBufBlkManager((StringId) blockManager.getName());
                Buffer.makeFree(newBlk);
                return data;
            } else {
                Buffer.makeFree(newBlk);
                throw new ErrorCode(ErrorCode.CHECKSUM_CHECK_FAILED);
            }
        }

    };
    public int blockSize(){
        java.io.File metaFile = new java.io.File(metaPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(metaFile));
            String tmp = br.readLine();
            long blockSize = Long.parseLong(tmp.split(":")[1]);
            return (int)blockSize;
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    };
}
