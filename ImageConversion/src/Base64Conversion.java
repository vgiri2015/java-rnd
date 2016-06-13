import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by gfp2ram on 8/1/2015.
 */

public class Base64Conversion {
    public static void main (String args[]) throws IOException {
        //Convert an Image to Base64 String
        BufferedImage img = ImageIO.read(new File("C:\\RnD\\Workspace\\java\\ImageConversion\\images\\harbour.jpg"));
        String imgstr;
        imgstr = encodeToString(img, "jpg");
        System.out.println(imgstr);

        //Convert a Base64 String to Image
        BufferedImage newImg;
        newImg = decodeToImage(imgstr);
        ImageIO.write(newImg, "jpg", new File("C:\\RnD\\Workspace\\java\\ImageConversion\\images\\decodedharbour.jpg"));
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
