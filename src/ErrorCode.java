import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {
    public static final int IO_EXCEPTION = 1;
    public static final int CHECKSUM_CHECK_FAILED = 2;
    public static final int FILE_ALREADY_EXISTED = 3;
    public static final int NO_SUCH_FILE = 4;
    public static final int READ_LENGTH_ERROR = 5;
    public static final int BLOCK_MEMORY_ERROR = 6;
    public static final int INITFILE_ERROR = 7;
    public static final int MD5_INIT_FAILED = 8;
    public static final int NO_SUCH_BLOCK = 9;
    public static final int MEMORY_ERROR = 10;
    public static final int CURSOR_ERROR = 11;
    public static final int OPEN_TOO_MANY_FILES = 12;
    public static final int NEWSIZE_ERROR = 13;
    public static final int NO_OPERATING_FILE = 14;
    public static final int COMMAND_PARAMETER_ERROE = 15;
    public static final int NO_THIS_CURSOR_TYPE = 16;
    public static final int INVALID_COMMAND = 17;
    private static final Map<Integer,String> ErrorCodeMap = new HashMap<>();
    static {
        ErrorCodeMap.put(IO_EXCEPTION,"IO exception");
        ErrorCodeMap.put(CHECKSUM_CHECK_FAILED,"block checksum check failed");
        ErrorCodeMap.put(FILE_ALREADY_EXISTED,"file already existed");
        ErrorCodeMap.put(NO_SUCH_FILE,"file not found");
        ErrorCodeMap.put(READ_LENGTH_ERROR,"read() parameter error");
        ErrorCodeMap.put(BLOCK_MEMORY_ERROR,"block memory error");
        ErrorCodeMap.put(INITFILE_ERROR,"initial file error");
        ErrorCodeMap.put(MD5_INIT_FAILED,"MD5 initiate error");
        ErrorCodeMap.put(NO_SUCH_BLOCK,"Block not found");
        ErrorCodeMap.put(MEMORY_ERROR,"some blocks error");
        ErrorCodeMap.put(CURSOR_ERROR,"file cursor error");
        ErrorCodeMap.put(OPEN_TOO_MANY_FILES,"open too many files,close some");
        ErrorCodeMap.put(NEWSIZE_ERROR,"invalid newsize");
        ErrorCodeMap.put(NO_OPERATING_FILE,"you should cat one file first");
        ErrorCodeMap.put(COMMAND_PARAMETER_ERROE,"invalid parameter(s)");
        ErrorCodeMap.put(NO_THIS_CURSOR_TYPE,"invalid cursor move strategy(cur,head,tail)");
        ErrorCodeMap.put(INVALID_COMMAND,"invalid command(cat,hex,write,copy,q)");
    }

    public static String getErrorText(int errorCode){
        return ErrorCodeMap.getOrDefault(errorCode,"invalid");
    }
    private int errorCode;
    public ErrorCode(int errorCode){
        super(String.format("error code '%d' \"%s\"", errorCode,getErrorText(errorCode)));
        this.errorCode = errorCode;
    }
    public int getErrorCode(){
        return errorCode;
    }

}
