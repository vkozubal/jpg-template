package nl.lincsafe.bsc.printing;

import nl.lincsafe.bsc.Constants;
import nl.lincsafe.bsc.Util;
import nl.lincsafe.bsc.configuration.Config;
import nl.lincsafe.bsc.model.Bill;
import nl.lincsafe.bsc.model.BillCounterObject;
import nl.lincsafe.bsc.model.CustomerDetails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportGenerator {
    public static final String JPG_TEMPLATE_PATH = "/unnamed.jpg";
    public static final String CONFIG_PATH = "/configuration.xml";
    public static final String PORT_IDENTIFIER = "/dev/ttyS91";

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    public static BufferedImage fillReport(BillCounterObject billCounter, CustomerDetails details) {
        BufferedImage template = Util.getBufferedImage(JPG_TEMPLATE_PATH);
        Config config = Util.getConfig(CONFIG_PATH);

        Map<String, String> dataMap = getMap(details);
        dataMap.put(Constants.Labels.TOTAL_VALUE, String.valueOf(billCounter.getTotalValue()));

        ReportImageWriter.populateFromMap(template, dataMap, config);
        populateTable(template, billCounter, config);
        return template;
    }

    /**
     * send image for printing
     *
     * @param image report
     * @throws IOException failed attempt to send data for printing
     */
    public static void printReport(BufferedImage image) throws IOException {
        try (PrinterSerialPort serialPort = new PrinterSerialPort()) {
            // Open serial port
            serialPort.connect(PORT_IDENTIFIER);
            // Send the testing string
            serialPort.getOutputStream().write(imageToByArray(image));
        }
    }

    public static byte[] imageToByArray(BufferedImage image) throws IOException {
        byte[] imageInBytes;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, Constants.JPG, byteStream);
            byteStream.flush();
            imageInBytes = byteStream.toByteArray();
        }
        return imageInBytes;
    }

    /**
     * fills table in template image with data from {@code billCounter}
     */
    public static void populateTable(BufferedImage image, BillCounterObject billCounter, Config config) {
        List<String> numberColumnData = new ArrayList<>();
        List<String> totalColumnData = new ArrayList<>();

        for (Bill bill : billCounter.getBillsList()) {
            numberColumnData.add(String.valueOf(bill.getBillAmount()));
            totalColumnData.add(String.valueOf(bill.getTotalValue()));
        }

        Map<String, List<String>> columnMap = new HashMap<>();
        columnMap.put(Constants.ColumnNames.NUMBER_COLUMN, numberColumnData);
        columnMap.put(Constants.ColumnNames.TOTAL_COLUMN, totalColumnData);

        ReportImageWriter.populateTableColumn(image, columnMap, config);
    }

    private static Map<String, String> getMap(CustomerDetails details) {
        Calendar calendar = Calendar.getInstance();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(Constants.Labels.CASE_NUMBER, String.valueOf(details.getCaseNumber()));
        dataMap.put(Constants.Labels.DATE, DATE_FORMAT.format(calendar.getTime()));
        dataMap.put(Constants.Labels.TIME, TIME_FORMAT.format(calendar.getTime()));
        dataMap.put(Constants.Labels.CLIENT, details.getClientName());
        dataMap.put(Constants.Labels.AGENT, details.getAgent());
        dataMap.put(Constants.Labels.CLIENT_NUMBER, String.valueOf(details.getClientNumber()));
        dataMap.put(Constants.Labels.NOM, details.getNom());
        return dataMap;
    }
}
