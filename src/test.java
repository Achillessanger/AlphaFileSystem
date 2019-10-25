import interfaces.BlockManager;
import interfaces.File;
import interfaces.FileManager;
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
//        Map<Id,myFileManager> debug = mContext.myFileManagerMap;
//        myFileManager myFileManager = debug.get(new StringId("fm-01"));
////        myFileManager.getFile(new StringId("a"));
//
//        BlockManager blockManager1 = mContext.myBlockManagerMap.get("bm-01");
//        BlockManager blockManager2 = new myBlockManager("./path/to/bm-02/");
//        BlockManager blockManager3 = new myBlockManager("./path/to/bm-03/");
//        byte[] a = "helloooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes();
//        byte[] b = "worldddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd".getBytes();
////        blockManager1.newBlock(a);
////        blockManager2.newBlock(a);
////        blockManager1.newBlock(b);
////        blockManager3.newBlock(b);
//        File file = myFileManager.getFile(new StringId("a"));
//        byte[] bytes = file.read(700);
//        System.out.println(new String(bytes));
        FileManager fm = mContext.myFileManagerMap.get(new StringId("fm-02"));
//        File f = fm.getFile(new StringId("name"));
//        byte[] w = "abcdefghijklmn".getBytes();
//        f.write(w);
//        f.write(w);
//        byte[] bytes = f.read(15);
//        System.out.println(new String(bytes)+"\n");
//        byte[] bytes2 = f.read(5);
//        System.out.println(new String(bytes2));
        ArrayList<ArrayList<BufferBlk>> arrayLists = Buffer.cache;
        int debug = 0;

        byte[] w2 = "aooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooa$$".getBytes();
        byte[] zero = new byte[514];
        File f2 = fm.getFile(new StringId("2_blks"));

        f2.write(w2);
        f2.move(0,File.MOVE_HEAD);
        f2.setSize(20);
        f2.setSize(511);
        f2.move(500,File.MOVE_HEAD);
        System.out.println(new String(f2.read(5)));
    }
}
