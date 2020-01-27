import java.io.*;

public class ReadFile {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\apps\\virusmap\\public\\json\\provinces\\湖北.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
        String line = br.readLine();
        StringWriter sw = new StringWriter();
        while (line!=null){
            sw.append(line);
            line=br.readLine();
        }

        System.out.println(sw.toString());
    }


}
