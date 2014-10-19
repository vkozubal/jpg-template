package nl.lincsafe.bsc;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class Util {
    public static final String BI_RGB = "BI_RGB";

    public static BufferedImage getBufferedImage(String fileName) {
        BufferedImage image = null;
        try {
            URL url = Util.class.getResource(fileName);
            File y = new File(url.toURI());
            image = ImageIO.read(y);
        } catch (IOException | URISyntaxException e) {
            // TODO handle exception
            e.printStackTrace();
        }
        return image;
    }

    public static void writeImageToFile(BufferedImage image, ImageWriter writer) throws IOException {
        ImageWriteParam iwp;
        iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        // specify that bitmap is not compressed
        iwp.setCompressionType(BI_RGB);
        iwp.setCompressionQuality(1);
        IIOImage image1 = new IIOImage(image, null, null);
        writer.write(null, image1, iwp);
        writer.dispose();
    }
}
