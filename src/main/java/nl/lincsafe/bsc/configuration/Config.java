package nl.lincsafe.bsc.configuration;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Config extends AbstractConfig {

    public Config() {
        table = new Table();
    }

    @XmlElement
    private Table table;

    // column name mapped to y cartesian coordinate
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, Point> labels;

    @XmlAccessorType(XmlAccessType.NONE)
    public static class Table {

        @XmlElement
        private List<Column> columns = new ArrayList<>();

        @XmlAttribute
        private Integer rowCount;

        @XmlAttribute
        private Integer cellHeight;
    }

    public static class Column {

        public Column() {
        }

        public Column(int x, int y, String name) {
            this.location = new Point(x,y);
            this.name = name;
        }

        @XmlElement(name = "location")
        private Point location;

        @XmlAttribute
        private String name;

        public Point getLocation() {
            return location;
        }
    }

    public static class MapEntry {
        @XmlAttribute
        private String key;

        @XmlAttribute
        private Integer x;

        @XmlAttribute
        private Integer y;
    }

    // cartesian coordinate
    @XmlAccessorType(XmlAccessType.NONE)
    public static final class Point {

        //        no-arg constructor for Jaxb unmarshaller
        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @XmlAttribute
        private int x;

        @XmlAttribute
        private int y;

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }
    }

    public static class PositionMapping {
        public PositionMapping() {
        }

        public List<MapEntry> entry = new ArrayList<>();
    }

    public static final class MapAdapter extends XmlAdapter<PositionMapping, Map<String, Point>> {

        @Override
        public Map<String, Point> unmarshal(PositionMapping v) throws Exception {
            Map<String, Point> map = new HashMap<>();
            for (MapEntry mapEntry : v.entry) {
                map.put(mapEntry.key, new Point(mapEntry.x, mapEntry.y));
            }
            return map;
        }

        @Override
        public PositionMapping marshal(Map<String, Point> v) throws Exception {
            PositionMapping mapping = new PositionMapping();
            for (Map.Entry<String, Point> entry : v.entrySet()) {
                MapEntry mapEntry = new MapEntry();
                mapEntry.key = entry.getKey();
                mapEntry.x = entry.getValue().x;
                mapEntry.y = entry.getValue().y;
                mapping.entry.add(mapEntry);
            }
            return mapping;
        }
    }

    public Point getPosition(String key) {
        return labels.get(key);
    }

    public void setLabels(Map<String, Point> labels) {
        this.labels = labels;
    }

    public void setTableRowCount(int rowCount) {
        this.table.rowCount = rowCount;
    }

    public int getRowCount() {
        return table.rowCount;
    }

    public void addTableColumn(Column column) {
        this.table.columns.add(column);
    }

    public List<Column> getColumns(){
        return table.columns;
    }

    public Column getColumn(String key){
        for(Column column: getColumns()){
            if (column.name.equals(key)){
                return column;
            }
        }
        return null;
    }

    public Integer getTableCellHeight() {
        return table.cellHeight;
    }

    public void setTableCellHeight(Integer cellHeight) {
        table.cellHeight = cellHeight;
    }

    public Map<String, Point> getLabels() {
        return labels;
    }
}
