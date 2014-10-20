package nl.lincsafe.bsc;

import nl.lincsafe.bsc.configuration.AbstractConfig;
import nl.lincsafe.bsc.configuration.Config;
import org.apache.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;


public class Util {
    static Logger logger = Logger.getLogger(Util.class);

    public static BufferedImage getBufferedImage(String fileName) {
        BufferedImage image = null;
        try {
            URL url = Util.class.getResource(fileName);
            File y = new File(url.toURI());
            image = ImageIO.read(y);
        } catch (IOException | URISyntaxException e) {
            logger.error("Can't read image!", e);
        }
        return image;
    }

    public static Config getConfig(String fileName) {
        try {
            File file = new File(Util.class.getResource(fileName).toURI());
            String xmlConfig = new Scanner(file).useDelimiter("\\Z").next();
            return AbstractConfig.unmarshalFromString(xmlConfig);

        } catch (FileNotFoundException | JAXBException | URISyntaxException e) {
            logger.error("Can't read config file");
        }
        return null;
    }
}
