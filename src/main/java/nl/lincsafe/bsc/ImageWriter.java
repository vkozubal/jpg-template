package nl.lincsafe.bsc;

import nl.lincsafe.bsc.configuration.Config;
import nl.lincsafe.bsc.objects.Bill;
import nl.lincsafe.bsc.objects.BillCounterObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageWriter {

    /**
     * Inserts data from {@code dataList} into table column
     *
     * @param image    image to write into
     * @param dataList String values to be drawn
     * @param config   configuration that contains labels' coordinates
     */
    public static void populateTableColumn(BufferedImage image, Map<String, List<String>> dataList, Config config) {
        int cellHeight = config.getTableCellHeight();

        for (Map.Entry<String, List<String>> entry : dataList.entrySet()) {
            Config.Column column = config.getColumn(entry.getKey());
            if (column != null) {
                for (int i = 0, current = column.getLocation().getY(); i < config.getRowCount(); i++) {
                    write(image, entry.getValue().get(i), column.getLocation().getX(), current);
                    current += cellHeight;
                }
            }
        }
    }

    /**
     * Inserts {@code dataMap } by key into image
     *
     * @param image   image to write into
     * @param dataMap map with constant as a key {@link nl.lincsafe.bsc.Constants.Labels} and data as a value
     * @param config  configuration
     */
    public static void populateFromMap(BufferedImage image, Map<String, String> dataMap, Config config) {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            Config.Point location = config.getPosition(entry.getKey());
            if (location != null) {
                write(image, entry.getValue(), location.getX(), location.getY());
            }
        }
    }

    /**
     * @param image image to write into
     * @param value the string to be drawn.
     * @param x     the <i>x</i> coordinate.
     * @param y     the <i>y</i> coordinate.
     */
    private static void write(BufferedImage image, String value, int x, int y) {
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(14f));
        g.drawString(value, x, y);
        g.translate(712, 0);
        g.drawString(value, x, y);
        g.dispose();
        System.out.println("write " + value + " to (" + x + ", " + y + ")");
    }
}
