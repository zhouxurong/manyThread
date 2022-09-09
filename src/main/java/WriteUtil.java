import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WriteUtil {
    private int len;
    private byte[] bytes;

    public WriteUtil(int len, byte[] bytes) {
        this.len = len;
        this.bytes = bytes;
    }

    /*
    读取文件
     */
    public void ReadBigFile(String fileName){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            //使用InputStream读取文件的长度
            len = inputStream.available();
            //构建文件长度大小的byte数组
            bytes = new byte[len];
            //将文件中的内容读取到byte数组中
            inputStream.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    将文件分块
     */
    public List<String> WriteBlockFile(int threadNumber, String outBlockFolderName){
        int blockLen = len / (threadNumber - 1);
        int lastLen = len % (threadNumber - 1);
        //创建线程数为threadNumber的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        List<String> pathList = new ArrayList<>();
        for(int i = 0;i < threadNumber;i++){
            String outFileName_ = outBlockFolderName + "/outBlock_" + (i+1) + ".txt";
            pathList.add(outFileName_);
            byte[] outBytes;
            /*
            对最后一块线程进行单独处理
             */
            if(i == threadNumber - 1){
                outBytes = new byte[lastLen];
                //使用System的arraycopy方法将byte数组中起始位置为blockLen*i，长度为lastLen的内容
                // 写到outbyte数组起始位置为0的地方
                System.arraycopy(bytes,blockLen*i,outBytes,0,lastLen);
            }else {
                outBytes = new byte[blockLen];
                //使用System的arraycopy方法将byte数组中起始位置为blockLen*i，长度为blockLen的内容
                // 写到outbyte数组起始位置为0的地方
                System.arraycopy(bytes,blockLen*i,outBytes,0,blockLen);
            }
            //利用线程池提交task，将输出文件名和输出byte数组大小传入WriterBlockTask中
            executorService.submit(new WriterBlockTask(outFileName_,outBytes));
        }
        //关闭线程
        executorService.shutdown();
        //System.out.println(blockLen+":"+lastLen);
        return pathList;
    }

    /*
    大文件合并
     */
    public void mergeBigFile(List<String> pathList, String outBlockFolderName, String outPath)
            throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(outPath, "rw");
        try {
            long startIndex = 0L;
            for (String path : pathList) {
                randomAccessFile.seek(startIndex);
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(path);
                    int available = fileInputStream.available();
                    byte[] bytes = new byte[available];
                    fileInputStream.read(bytes);
                    //写入
                    randomAccessFile.write(bytes);
                    startIndex += available;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                }
            }
        } finally {
            File file = new File(outBlockFolderName);
            for(File fi:file.listFiles()){
                fi.delete();
            }
            file.delete();
            randomAccessFile.close();
        }
    }
}
