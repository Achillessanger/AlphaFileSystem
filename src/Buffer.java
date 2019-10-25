import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Buffer {
    public static final int BUFFER_LINES = 4;
    public static final ArrayList<ArrayList<BufferBlk>> cache;
    public static final BufferBlk freeHead;
    private static ArrayList<BufferBlk> delyBufBlks;
    static {
        freeHead = new BufferBlk();
        BufferBlk tmp = freeHead;
        cache = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            ArrayList<BufferBlk> al = new ArrayList<>();
            cache.add(al);
            for(int j = 0; j < 4; j++){
                BufferBlk bufferBlk = new BufferBlk();
                bufferBlk.setBusy(false);
                bufferBlk.setBufBlkId(new StringId("-1"));
                bufferBlk.setPreFreeBufBlk(tmp);
                tmp.setNextFreeBufBlk(bufferBlk);
                tmp = bufferBlk;
            }
        }
        delyBufBlks = new ArrayList<>();

    }
    public static void makeBusy(BufferBlk tmpBlk,int bufIndex){
        BufferBlk tmpPre = tmpBlk.getPreFreeBufBlk();
        BufferBlk tmpNext = tmpBlk.getNextFreeBufBlk();
        if(tmpPre != null && tmpNext != null){
            tmpPre.setNextFreeBufBlk(tmpNext);
            tmpNext.setPreFreeBufBlk(tmpPre);
        }else if(tmpPre != null && tmpNext == null){
            tmpPre.setNextFreeBufBlk(null);
        }
        cache.get(bufIndex).add(tmpBlk);
        tmpBlk.setBusy(true);
    }
    public static void makeFree(BufferBlk tmpBlk){
        BufferBlk tmp = freeHead.getNextFreeBufBlk();
        while (tmp.getNextFreeBufBlk() != null){
            tmp = tmp.getNextFreeBufBlk();
        }
        tmpBlk.setBusy(false);
        tmp.setNextFreeBufBlk(tmpBlk);
        tmpBlk.setPreFreeBufBlk(tmp);
        tmpBlk.setNextFreeBufBlk(null);
    }
    public static void deleteFromCache(BufferBlk tmpBlk){
        int blkId = Integer.parseInt((tmpBlk.getBufBlkId()).getId());
        int oldBufIndex = blkId %(BUFFER_LINES);
        if(blkId != -1){
            for(BufferBlk blk : cache.get(oldBufIndex)){
                if(blk.getBufBlkId().getId().equals(blkId+""))
                    cache.get(oldBufIndex).remove(blk);
            }
        }
    }
    public static BufferBlk findFreeBlk(){
        BufferBlk tmpBlk = freeHead.getNextFreeBufBlk();
        BufferBlk newBlk = null;
        while (tmpBlk != null){
            if(tmpBlk.isDelay()){
                delyBufBlks.add(tmpBlk);
            }else {
                newBlk = tmpBlk;
                break;
            }
            tmpBlk = tmpBlk.getNextFreeBufBlk();
        }
        return newBlk;
    }


    private static void delayWriteFin(BufferBlk blk){
        blk.setDelay(false);
        for(BufferBlk bufferBlk : delyBufBlks){
            if(bufferBlk.getBufBlkId().getId().equals(blk.getBufBlkId().getId()))
                delyBufBlks.remove(bufferBlk);
        }
    }
    public static void delayWrite(){
        if(delyBufBlks.size() == 0)
            return;
        try {
            for(BufferBlk blk : delyBufBlks){
                writeBlk2file(blk);

            }
        }catch (ErrorCode errorCode){
            throw errorCode;
        }

    }
    public static void writeBlk2file(BufferBlk blk){
        StringId debugid = blk.getBufBlkManager();
        String dataPath = mContext.myBlockManagerMap.get(blk.getBufBlkManager()).getPath() + blk.getBufBlkId().getId() + ".data";
        String metaPath = mContext.myBlockManagerMap.get(blk.getBufBlkManager()).getPath() + blk.getBufBlkId().getId() + ".meta";
        try {
            int bufIndex = Integer.parseInt(blk.getBufBlkId().getId()) % (Buffer.BUFFER_LINES);
            Buffer.makeBusy(blk, bufIndex);

            FileOutputStream os_data = new FileOutputStream(dataPath,false);
//            BufferedInputStream is_data = new BufferedInputStream(new ByteArrayInputStream(blk.getData()));
            os_data.write(blk.getData());
            os_data.flush();
//            is_data.close();
            os_data.close();

            FileOutputStream out_meta = new FileOutputStream(metaPath,false);
            StringBuffer sb_meta = new StringBuffer();
            sb_meta.append("size:"+blk.getData().length+"\n");
            String md5 = MD5Util.getMD5String(blk.getData());
            sb_meta.append("checksum:"+md5+"\n");
            out_meta.write(sb_meta.toString().getBytes("utf-8"));

            //delay块直写成功，清除
            Buffer.delayWriteFin(blk);

            Buffer.makeFree(blk);

        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }

    }
    public static BufferBlk findBufBlk(int blkId){
        int bufIndex = blkId % BUFFER_LINES;
        //在cache里找
        for(int i = 0; i < cache.get(bufIndex).size(); i++){
            //！注意这里没有讨论块忙的情况!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if(Integer.parseInt(cache.get(bufIndex).get(i).getBufBlkId().getId()) == blkId
                    && !cache.get(bufIndex).get(i).isBusy()){
//                Buffer.cache.get(bufIndex).get(i).setBusy(true);
                return cache.get(bufIndex).get(i);
            }
        }
        return null;
    }
}
