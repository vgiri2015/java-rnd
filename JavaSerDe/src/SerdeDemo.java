/**
 * Created by gfp2ram on 9/8/2015.
 */
import java.io.*;

public class SerdeDemo
{
    public static void main(String [] args)
    {
        Employee e = new Employee();
        e.name = "Reyan Ali";
        e.address = "Phokka Kuan, Ambehta Peer";
        e.SSN = 11122333;
        e.number = 101;
        e.mailCheck();

        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("C:\\RnD\\Workspace\\java\\JavaSerDe\\testData\\employee.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(e);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in C:\\RnD\\Workspace\\java\\JavaSerDe\\testData\\employee.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }
}
