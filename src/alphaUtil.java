import interfaces.Block;
import interfaces.BlockManager;
import interfaces.File;
import interfaces.FileManager;

import java.util.ArrayList;

public class alphaUtil {
    private static ArrayList<File> openedFile = new ArrayList<>();
    private static File operatingFile;
    public static void alphaCat(String fmName, String fileName){
        try {
            FileManager fm = mContext.myFileManagerMap.get(new StringId(fmName));
            File file = fm.getFile(new StringId(fileName));
            operatingFile = file;
            file.move(0,File.MOVE_HEAD);
            System.out.println(new String(file.read((int)file.size())));
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }
    public static void alphaHex(String bmName, int blockIndex){
        try {
            BlockManager bm = mContext.myBlockManagerMap.get(new StringId(bmName));
            Block block = bm.getBlock(new StringId(blockIndex+""));
            byte[] bytes = block.read();
            for(int i = 0; i < bytes.length;i++){
                System.out.print("0x"+Integer.toHexString(bytes[i])+" ");
                if(i%16 == 15)
                    System.out.print("\n");
            }
            System.out.print("\n");
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }
    public static void alphaWrite(int offset, String where, String content){
        try {
            int cursorWhere = 0;
            if(operatingFile == null)
                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
            if("cur".equals(where))
                cursorWhere = File.MOVE_CURR;
            else if("head".equals(where))
                cursorWhere = File.MOVE_HEAD;
            else if("tail".equals(where))
                cursorWhere = File.MOVE_TAIL;
            else
                throw new ErrorCode(ErrorCode.NO_THIS_CURSOR_TYPE);

            operatingFile.move(offset,cursorWhere);
            operatingFile.write(content.getBytes());
            if(!openedFile.contains(operatingFile))
                openedFile.add(operatingFile);
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }
    public static void alphaCopy(String fmNameOld, String fileNameOld, String fmNameNew, String fileNameNew){
        try{
            FileManager fmOld = mContext.myFileManagerMap.get(new StringId(fmNameOld));
            File fileOld = fmOld.getFile(new StringId(fileNameOld));
            FileManager fmNew = mContext.myFileManagerMap.get(new StringId(fmNameNew));
            File fileNew = fmNew.newFile(new StringId(fileNameNew));

            openedFile.add(fileNew);

            fileNew.write(fileOld.read((int)fileOld.size()));
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }

    public static void finish(){
        for(File f : openedFile){
            try {
                f.close();
            }catch (ErrorCode errorCode){
//                System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
                throw errorCode;
            }
        }
    }

    public static void alphaCreate(String fmName, String fileName){
        try {
            FileManager fm = mContext.myFileManagerMap.get(new StringId(fmName));
            File file = fm.newFile(new StringId(fileName));
            operatingFile = file;
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }

    public static void alphaSetSize(int newSize){
        try {
            if(operatingFile == null)
                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
            operatingFile.setSize(newSize);
        }catch (ErrorCode errorCode){
            throw errorCode;
        }

    }


}
