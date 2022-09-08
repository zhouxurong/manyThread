import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriterBlockTask implements Runnable{
    //目标路径
    String targetPath;
    //传进来的bytes数组
    byte[] bytes;

    public WriterBlockTask() {
    }

    public WriterBlockTask(String targetPath, byte[] bytes) {
        this.targetPath = targetPath;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetPath);
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
