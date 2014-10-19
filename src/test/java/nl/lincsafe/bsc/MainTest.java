package nl.lincsafe.bsc;

import nl.lincsafe.bsc.configuration.AbstractConfig;
import nl.lincsafe.bsc.configuration.Config;
import nl.lincsafe.bsc.objects.Bill;
import nl.lincsafe.bsc.objects.BillCounterObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class MainTest {

    @Test
    public void marshalingTest() throws JAXBException, IOException {
        Config expectedConf = new Config();
        expectedConf.addTableColumn(new Config.Column(358, 133, Constants.ColumnNames.NUMBER_COLUMN));
        expectedConf.addTableColumn(new Config.Column(520, 133, Constants.ColumnNames.TOTAL_COLUMN));
        expectedConf.setTableRowCount(7);
        expectedConf.setTableCellHeight(35);

        Map<String, Config.Point> columns = new HashMap<>();
        columns.put(Constants.Labels.TOTAL_VALUE, new Config.Point(416, 426));
        expectedConf.setLabels(columns);
        String result = expectedConf.marshalToString();
        Config actualConfig = AbstractConfig.unmarshalFromString(result);

        Assert.assertEquals(actualConfig.getRowCount(), actualConfig.getRowCount());
        Assert.assertEquals(actualConfig.getLabels().get(Constants.Labels.TOTAL_VALUE).getX(), 416);
        Assert.assertEquals(actualConfig.getColumn(Constants.ColumnNames.NUMBER_COLUMN).getLocation().getX(), 358);
    }

    @Test
    public void writeTable() throws IOException, JAXBException, URISyntaxException {
        Config config = getConfig("/configuration.xml");

        BillCounterObject billCounter = new BillCounterObject(1);
        billCounter.setBills(getBills());
        BufferedImage image = Util.getBufferedImage(Constants.JPG_TEMPLATE);
        populateTable(image, billCounter, config);

        File w = new File("test1-unnamed.bmp");
        System.out.println(w.getAbsolutePath());
        FileImageOutputStream stream = new FileImageOutputStream(w);

        Iterator iterator = ImageIO.getImageWritersByFormatName("bmp");
        javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter) iterator.next();
        writer.setOutput(stream);
        Util.writeImageToFile(image, writer);
        Assert.assertTrue(w.exists());
        w.deleteOnExit();
    }

    @Test
    public void writeDataMap() throws IOException, JAXBException, URISyntaxException {
        BufferedImage image = Util.getBufferedImage(Constants.JPG_TEMPLATE);
        ImageWriter.populateFromMap(image, getDataMap(), getConfig("/configuration.xml"));

        File w = new File("test-unnamed.bmp");
        FileImageOutputStream stream = new FileImageOutputStream(w);

        Iterator iterator = ImageIO.getImageWritersByFormatName("bmp");
        javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter) iterator.next();
        writer.setOutput(stream);
        Util.writeImageToFile(image, writer);
        Assert.assertTrue(w.exists());
        w.deleteOnExit();
    }

    private static Map<String, String> getDataMap() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(Constants.Labels.TOTAL_VALUE, "333380775");
        dataMap.put(Constants.Labels.CASE_NUMBER, "09713598-1534578-2345");
        dataMap.put(Constants.Labels.DATE, "03.09.2011");
        dataMap.put(Constants.Labels.TIME, "13:56");
        dataMap.put(Constants.Labels.CLIENT, "Client Name");
        dataMap.put(Constants.Labels.AGENT, "Agent");
        dataMap.put(Constants.Labels.CLIENT_NUMBER, "984245");
        dataMap.put(Constants.Labels.NOM, "Nom");
        return dataMap;
    }


    private void populateTable(BufferedImage image, BillCounterObject billCounter, Config config) {
        List<String> numberColumnData = new ArrayList<>();
        List<String> totalColumnData = new ArrayList<>();

        for (Bill bill : billCounter.getBillsList()) {
            numberColumnData.add(String.valueOf(bill.getBillAmount()));
            totalColumnData.add(String.valueOf(bill.getTotalValue()));
        }
        Map<String, List<String>> columnMap = new HashMap<>();
        columnMap.put(Constants.ColumnNames.NUMBER_COLUMN, numberColumnData);
        columnMap.put(Constants.ColumnNames.TOTAL_COLUMN, totalColumnData);

        ImageWriter.populateTableColumn(image, columnMap, config);
    }


    private Config getConfig(String fileName) throws URISyntaxException, FileNotFoundException, JAXBException {
        File file = new File(getClass().getResource(fileName).toURI());
        String xmlConfig = new Scanner(file).useDelimiter("\\Z").next();
        System.out.println(xmlConfig);
        return AbstractConfig.unmarshalFromString(xmlConfig);
    }

    public static List<Bill> getBills() {
        int[] billValueArray = {5, 10, 20, 50, 100, 200, 500};
        int[] billAmountArray = {555555, 101010, 202020, 505050, 100100, 200200, 500500};

        List<Bill> result = new ArrayList<Bill>();
        for (int i = 0; i < billValueArray.length; i++) {
            Bill bill = new Bill(billValueArray[i], "");
            for (int j = 0; j < billAmountArray[i]; j++) {
                bill.AddBill();
            }
            result.add(bill);
        }
        return result;
    }
}
