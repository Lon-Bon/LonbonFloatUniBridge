package com.lonbon.floatunibridging;

/**
 * *****************************************************************************
 * <p>
 * Copyright (C),2007-2016, LonBon Technologies Co. Ltd. All Rights Reserved.
 * <p>
 * *****************************************************************************
 *
 * @ProjectName: LBFloatUniDemo
 * @Package: com.lonbon.floatunibridging
 * @ClassName: JsonFile
 * @Author： neo
 * @Create: 2023/2/22
 * @Describe:
 */
public class JsonFile {

    private String path;
    private String fileName;
    private long fileSize;
    private boolean directory;
    private boolean hasChildFileList;

    public JsonFile(String path, String fileName, long fileSize, boolean directory, boolean hasChildFileList) {
        this.path = path;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.directory = directory;
        this.hasChildFileList = hasChildFileList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isHasChildFileList() {
        return hasChildFileList;
    }

    public void setHasChildFileList(boolean hasChildFileList) {
        this.hasChildFileList = hasChildFileList;
    }
}
