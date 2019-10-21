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
