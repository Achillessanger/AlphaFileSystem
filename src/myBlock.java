import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

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
//        String path = blockManager.getPath();
        return null;
    };
    public int blockSize(){
        return 0;
    };
}
