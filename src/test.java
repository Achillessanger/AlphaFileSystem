import interfaces.BlockManager;
import interfaces.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args){
        ArrayList<Map<Id,Integer>> LogicBlockList = new ArrayList<>();
        Map<Id,Integer> map = new HashMap<>();
        map.put(new StringId("bm-01"),1);
        map.put(new StringId("bm-02"),1);
        LogicBlockList.add(map);
//        System.out.println(map.toString());
        MyFileManager myFileManager = new MyFileManager("./path/to/fm-01/");
//        myFileManager.getFile(new StringId("a"));

        BlockManager blockManager = new myBlockManager("./path/to/bm-01/");
        byte[] a = "abcde".getBytes();
        byte[] b = new byte[]{
                0x12,
        };
        blockManager.newBlock(a);
    }
}
