import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import sun.security.util.BitArray;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by gfp2ram on 7/31/2015.
 */
public class ImageConversion {
    public static void main(String args[]) throws Exception {
        PlaywithImages();
    }

    private static void PlaywithImages() throws IOException {
        //Convert Image to Binary Code - Option 1 URL based
            BufferedImage image = ImageIO.read(new URL("https://www.edrawsoft.com/images/examples/TIFF-2.png"));
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ImageIO.write(image, "png", b);
            byte[] pngByteArray = b.toByteArray();
            StringBuilder sb = new StringBuilder();
            for (byte by : pngByteArray)
                sb.append(Integer.toBinaryString(by & 0xFF));
            System.out.println("Binary Code of the Image"+sb.toString());
            b.flush();


        //Convert BinaryCode to Image



        //Convert Image to ByteArray
        File file = new File("C:\\RnD\\Workspace\\java\\ImageConversion\\images\\TIFF-2.png");
        FileInputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                //Writes to this byte array output stream
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        byte[] bytecode = bos.toByteArray();
        System.out.println("Byte Array of the Image"+bytecode);

        //Convert ByteArray to the Original Image
        ByteArrayInputStream bis = new ByteArrayInputStream(bytecode);
        Iterator<?> readers = ImageIO.getImageReadersByFormatName("png");
        //ImageIO is a class containing static methods for locating ImageReaders
        //and ImageWriters, and performing simple encoding and decoding.
        ImageReader reader = (ImageReader) readers.next();
        Object source = bis;
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Image image1 = reader.read(0, param);
        //got an image file
        BufferedImage bufferedImage = new BufferedImage(image1.getWidth(null), image1.getHeight(null), BufferedImage.TYPE_INT_RGB);
        //bufferedImage is the RenderedImage to be written
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image1, null, null);
        File imageFile = new File("C:\\RnD\\Workspace\\java\\ImageConversion\\images\\ByteArraytoImage.jpg");
        ImageIO.write(bufferedImage, "jpg", imageFile);
        System.out.println(imageFile.getPath());
        return;
    }
}
