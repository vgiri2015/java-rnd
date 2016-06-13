package com.giri.hadoop.imageprocessing;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


//public class HiBReadandProcess extends AbstractImageBundle {
    public class HiBReadandProcess  {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Configuration configuration = new Configuration();
        configuration.addResource("/etc/hadoop/core-site.xml");
        FileSystem hdfs = FileSystem.get(new URI("hdfs://localhost:8020"), configuration);
        FileStatus[] fileStatus = hdfs.listStatus(new Path("hdfs://localhost:8020/user/giri"));
        Path[] paths = FileUtil.stat2Paths(fileStatus);
        System.out.println("***** Contents of the Directory *****");
        for(Path path : paths)
        {
            System.out.println(path);
        }
    }

//    /* Constructor1 for the Class HiBReadandProcess */
//    public HiBReadandProcess(Path file_path, Configuration conf) {
//        super(file_path, conf);
//    }
//
//    /* Constructor2 for the Class HiBReadandProcess */
//    public HiBReadandProcess(Path file_path,Configuration conf,short replication) {
//        super(file_path,conf);
//    }
//
//    /* Constructor3 for the Class HiBReadandProcess */
//    public HiBReadandProcess(Path file_path, Configuration conf, long blockSize) {
//        super(file_path, conf);
//    }
//
//    /* Constructor4 for the Class HiBReadandProcess */
//    public HiBReadandProcess(Path file_path, Configuration conf, short replication, long blockSize) {
//        super(file_path, conf);
//    }
//
//    /*Implementation Methods for the Class HiBReadandProcess from AbstractImageBundle Interface*/
//    @Override
//    protected void openForWrite() throws IOException {
//
//    }
//
//    @Override
//    protected void openForRead() throws IOException {
//
//    }
//
//    @Override
//    public void addImage(InputStream inputStream, ImageType imageType) throws IOException {
//
//    }
//
//    @Override
//    public long getImageCount() {
//        return 0;
//    }
//
//    @Override
//    protected boolean prepareNext() {
//        return false;
//    }
//
//    @Override
//    protected ImageHeader readHeader() throws IOException {
//        return null;
//    }
//
//    @Override
//    protected FloatImage readImage() throws IOException {
//        return null;
//    }
//
//    @Override
//    public void close() throws IOException {
//    }
}
