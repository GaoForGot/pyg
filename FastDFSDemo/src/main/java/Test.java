import org.csource.fastdfs.*;

public class Test {
    public static void main(String[] args) throws Exception {
        ClientGlobal.init("C:\\Users\\xiaoz\\IdeaProjects\\pinyougou\\FastDFSDemo\\src\\main\\resources\\fdfs_client.conf");
        TrackerClient client = new TrackerClient();
        TrackerServer conn = client.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(conn, storageServer);
        String[] url = storageClient.upload_file("C:\\Users\\xiaoz\\Desktop\\hanghaijia.jpg", "jpg", null);
        for (String s : url) {
            System.out.println(s);
        }
    }
}
