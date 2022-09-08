import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MultiThreadRW {
    public static void main(String[] args) {
        //输入文档路径
        String fileName = null;
        //输出Block文件夹
        String outBlockFolderName = null;
        //输出文件夹
        String outFolderName = null;
        //所需线程数
        int threadNumber = 0;
        //输出文件名
        String outFileName = null;
        //输出文件类型
        String outFileType = null;
        //String fileName = "D:\\bosc_work\\随便一个文档.txt";
        System.out.println("请输入读取路径：");
        Scanner sc = new Scanner(System.in);
        if(sc.hasNext()){
            fileName = sc.next();
        }
        int len = 0;
        byte[] bytes = null;
        //创建工具类对象
        WriteUtil writeUtil = new WriteUtil(len, bytes);

        /*
        读取大文件
         */
        long startRead = System.currentTimeMillis();
        writeUtil.ReadBigFile(fileName);
        long endRead = System.currentTimeMillis();
        System.out.println("读取文件花费了"+(endRead-startRead)+"毫秒");

        /*
        分块写文件
         */
        System.out.println("------------------------------------");
        System.out.println("请填写你的输出Block文件夹名：");
        if(sc.hasNext()){
            outBlockFolderName = sc.next();
            File file = new File(outBlockFolderName);
            if(!file.exists()){
                file.mkdir();
            }
        }
        //线程数量
        System.out.println("请输入你所需线程数：");
        if(sc.hasNext()){
            threadNumber = Integer.valueOf(sc.next());
        }
        long startWriteBlock = System.currentTimeMillis();
        List<String> pathList = writeUtil.WriteBlockFile(threadNumber, outBlockFolderName);
        long endWriteBlock = System.currentTimeMillis();
        System.out.println("写block文件花费了"+(endWriteBlock-startWriteBlock)+"毫秒");

        /*
        合并大文件
         */
        long startMerge = System.currentTimeMillis();
        try {
            //输出文件夹
            System.out.println("------------------------------------");
            System.out.println("请填写你的输出文件夹名：");
            if(sc.hasNext()){
                outFolderName = sc.next();
                File file = new File(outFolderName);
                if(!file.exists()){
                    file.mkdir();
                }
            }
            System.out.println("------------------------------------");
            System.out.println("请填写你的输出文件名");
            if(sc.hasNext()){
                outFileName = sc.next();
            }
            System.out.println("请填写你的输出文件类型");
            if(sc.hasNext()){
                outFileType = sc.next();
            }
            writeUtil.mergeBigFile(pathList,outBlockFolderName,outFolderName + "/" +outFileName + "." + outFileType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endMerge = System.currentTimeMillis();
        System.out.println("合并文件花费了"+(endMerge-startMerge)+"毫秒");
        /*File file1 = new File(fileName);
        File file2 = new File(outFolderName + "/" +outFileName + "." + outFileType);
        System.out.println(file1.length()+":"+file2.length());*/
    }
}
