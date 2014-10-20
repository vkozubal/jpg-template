package nl.lincsafe.bsc;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class BaseTest {

    protected static void writeImageToFile(BufferedImage image, javax.imageio.ImageWriter writer) throws IOException {
        ImageWriteParam iwp;
        iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        // specify that bitmap is not compressed
        iwp.setCompressionType(Constants.BI_RGB);
        iwp.setCompressionQuality(1);
        IIOImage image1 = new IIOImage(image, null, null);
        writer.write(null, image1, iwp);
        writer.dispose();
    }

    protected static File writeToFile(BufferedImage image, String fileName) throws IOException {
        File w = new File(fileName);
        javax.imageio.ImageWriter writer = getImageWriter(w);
        writeImageToFile(image, writer);
        return w;
    }

    protected static javax.imageio.ImageWriter getImageWriter(File w) throws IOException {
        FileImageOutputStream stream = new FileImageOutputStream(w);
        Iterator iterator = ImageIO.getImageWritersByFormatName("bmp");
        javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter) iterator.next();
        writer.setOutput(stream);
        return writer;
    }
}
