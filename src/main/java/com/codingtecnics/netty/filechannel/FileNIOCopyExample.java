package com.codingtecnics.netty.filechannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;



public class FileNIOCopyExample {

    /**
     * 演示程序的入口函数
     *
     * @param args
     */
    public static void main(String[] args) {
    	String srcPath = "/Users/zhangxiao/tem/src/mysql-20200502-124506.sql";
        String destPath = "/Users/zhangxiao/tem/dest/mysql-20200502-124506.sql";
        //演示复制资源文件
        oioCopyFile(srcPath,destPath);
       
        nioCopyFile(srcPath,destPath);
    }


    /**
     * OIO复制两个资源目录下的文件
     */
   
    
    public static void oioCopyFile(String srcPath, String destPath) {

        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        try {
            //如果目标文件不存在，则新建
            if (!destFile.exists()) {
                destFile.createNewFile();
            }


            long startTime = System.currentTimeMillis();

            FileInputStream fis = null;
            FileOutputStream fos = null;
            
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                byte[] buff = new byte[1024*1024];
                int len = -1;
                //从输入通道读取到buf
                while ((len = fis.read(buff)) != -1) {
                    
                	fos.write(buff);
                    
                }
                
                fos.flush();

                
            } finally {
                
                closeQuietly(fos);
              
                closeQuietly(fis);
            }
            long endTime = System.currentTimeMillis();
           System.out.println("OIO 复制毫秒数：" + (endTime - startTime));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * NIO复制文件
     *
     * @param srcPath
     * @param destPath
     */
    public static void nioCopyFile(String srcPath, String destPath) {

        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        try {
            //如果目标文件不存在，则新建
            if (!destFile.exists()) {
                destFile.createNewFile();
            }


            long startTime = System.currentTimeMillis();

            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                inChannel = fis.getChannel();
                outChannel = fos.getChannel();
                int length = -1;
                ByteBuffer buf = ByteBuffer.allocate(1024*1024);
                //从输入通道读取到buf
                while ((length = inChannel.read(buf)) != -1) {

                    //翻转buf,变成读模式
                    buf.flip();
                    int outlength = 0;
                    //将buf写入到输出的通道
                    while ((outlength = outChannel.write(buf)) != 0) {
                        System.out.println("写入字节数：" + outlength);
                    }
                    //清除buf,变成写入模式
                    buf.clear();
                }

                //强制刷新磁盘
                outChannel.force(true);
            } finally {
                closeQuietly(outChannel);
                closeQuietly(fos);
                closeQuietly(inChannel);
                closeQuietly(fis);
            }
            long endTime = System.currentTimeMillis();
           System.out.println("NIO 复制毫秒数：" + (endTime - startTime));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void closeQuietly(java.io.Closeable o)
    {
        if (null == o) return;
        try
        {
            o.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}