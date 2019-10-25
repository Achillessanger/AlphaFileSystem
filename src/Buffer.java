import java.util.ArrayList;

public class Buffer {
    public static final int BUFFER_LINES = 4;
    public static final ArrayList<ArrayList<BufferBlk>> cache;
    public static final BufferBlk freeHead;
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
    public static  BufferBlk findFreeBlk(){
        BufferBlk tmpBlk = freeHead.getNextFreeBufBlk();
        BufferBlk newBlk = null;
        while (tmpBlk != null){
            if(tmpBlk.isDelay()){
                //因为block数据不会更改，所以没有写回功能

            }else {
                newBlk = tmpBlk;
                break;
            }
            tmpBlk = tmpBlk.getNextFreeBufBlk();
        }
        return newBlk;
    }
}
