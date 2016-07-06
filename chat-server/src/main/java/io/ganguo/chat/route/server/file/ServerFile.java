package io.ganguo.chat.route.server.file;

/**
 * Created by user on 2016/6/8.
 */
public class ServerFile extends FileData {

    private static final long serialVersionUID =  -8917310651102310680L;

    private String fileName;
    private long fileSize;

    public String getFileName(){
        return fileName;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public long getFileSize(){
        return fileSize;
    }

    public void setFileSize(long fileSize){
        this.fileSize = fileSize;

    }
}
