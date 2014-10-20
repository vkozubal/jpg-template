package nl.lincsafe.bsc;

import nl.lincsafe.bsc.configuration.AbstractConfig;
import nl.lincsafe.bsc.configuration.Config;
import nl.lincsafe.bsc.model.Bill;
import nl.lincsafe.bsc.model.BillCounterObject;
import nl.lincsafe.bsc.model.CustomerDetails;
import nl.lincsafe.bsc.printing.PrinterSerialPort;
import nl.lincsafe.bsc.printing.ReportGenerator;
import nl.lincsafe.bsc.printing.ReportImageWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.*;

public class MainTest extends BaseTest {
    public static final String JPG_TEMPLATE_PATH = "/unnamed.jpg";

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
        Config config = Util.getConfig("/configuration.xml");

        BillCounterObject billCounter = new BillCounterObject(1);
        billCounter.setBills(getBills());
        BufferedImage image = Util.getBufferedImage(JPG_TEMPLATE_PATH);
        ReportGenerator.populateTable(image, billCounter, config);

        File w = writeToFile(image, "test1-unnamed.bmp");
        Assert.assertTrue(w.exists());

        // comment this line to see result
        w.deleteOnExit();
    }

    @Test
    public void writeDataMap() throws IOException, JAXBException, URISyntaxException {
        BufferedImage image = Util.getBufferedImage(JPG_TEMPLATE_PATH);
        ReportImageWriter.populateFromMap(image, getDataMap(), Util.getConfig("/configuration.xml"));
        File w = writeToFile(image, "test-unnamed.bmp");
        Assert.assertTrue(w.exists());

        // comment this line to see result
        w.deleteOnExit();
    }

    @Test
    public void writeReportTest() throws IOException {
        BufferedImage filledReport = ReportGenerator.fillReport(getBillCounter(), getCustomerDetails());
        File w = writeToFile(filledReport, "final-result.bmp");
        Assert.assertTrue(w.exists());

        // comment this line to see result
        w.deleteOnExit();
    }

    /**
     * You need a loopback on specified ports to test PrinterSerialPort and root privileges
     * see README file
     */
    @Test
    public void loopbackTest() throws IOException, JAXBException, URISyntaxException {

        try (PrinterSerialPort loopBackIn = new PrinterSerialPort()) {
            // Open serial port
            loopBackIn.connect("/dev/ttyS90");
            // Register the serial event handler
            byte[] bytesData = getDataForTransfer();

            InputStream inStream = loopBackIn.getInputStream();

            PrinterSerialPort.SerialEventHandler serialEventHandler =
                    new PrinterSerialPort.SerialEventHandler(inStream, bytesData.length);
            loopBackIn.addDataAvailableEventHandler(serialEventHandler);

            try (PrinterSerialPort loopBackOut = new PrinterSerialPort()) {
                // Open serial port
                loopBackOut.connect("/dev/ttyS91");
                // Send the testing string
                OutputStream outStream =
                        loopBackOut.getOutputStream();
                outStream.write(bytesData);
            }
            waitAndCheckResult(bytesData, serialEventHandler);
        }
    }

    private void waitAndCheckResult(byte[] expectedData, PrinterSerialPort.SerialEventHandler serialEventHandler) {
        // Wait until all the data is received
        long elapsedTime, startTime = System.currentTimeMillis();
        // Timeout = 1s
        final int TIMEOUT_VALUE = 10000;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        } while ((elapsedTime < TIMEOUT_VALUE) &&
                (!serialEventHandler.isBufferFull()));

        // Check the data if not TIMEOUT
        Assert.assertTrue(elapsedTime < TIMEOUT_VALUE, "Test failed due to timeout");
        Assert.assertEquals(expectedData, serialEventHandler.getReadBuffer());
    }

    private byte[] getDataForTransfer() throws URISyntaxException, JAXBException, IOException {
        BufferedImage image = Util.getBufferedImage(JPG_TEMPLATE_PATH);
        ReportImageWriter.populateFromMap(image, getDataMap(), Util.getConfig("/configuration.xml"));
        return ReportGenerator.imageToByArray(image);
    }

//    *********************** Test Data

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

    private static List<Bill> getBills() {
        int[] billValueArray = {5, 10, 20, 50, 100, 200, 500};
        int[] billAmountArray = {555555, 101010, 202020, 505050, 100100, 200200, 500500};

        List<Bill> result = new ArrayList<>();
        for (int i = 0; i < billValueArray.length; i++) {
            Bill bill = new Bill(billValueArray[i], "");
            for (int j = 0; j < billAmountArray[i]; j++) {
                bill.AddBill();
            }
            result.add(bill);
        }
        return result;
    }

    private BillCounterObject getBillCounter() {
        BillCounterObject billCounter = new BillCounterObject(124235);
        billCounter.setBills(getBills());
        return billCounter;
    }

    private CustomerDetails getCustomerDetails() {
        CustomerDetails details = new CustomerDetails();
        details.setCaseNumber(971909723L);
        details.setDate(Calendar.getInstance());
        details.setClientName("Client Name");
        details.setAgent("Agent");
        details.setClientNumber(984245L);
        details.setNom("Nom");
        return details;
    }
}
