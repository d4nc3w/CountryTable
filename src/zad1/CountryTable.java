package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Vector;

public class CountryTable {

    private String fileName;

    public CountryTable(String fileName) {
        this.fileName = fileName;
    }

    public JTable create() throws Exception {
        Object[][] data = readData();

        //Col names
        Object[] columnNames = Arrays.copyOf(data[0], data[0].length, String[].class);

        //Remove the head
        data = Arrays.copyOfRange(data, 1, data.length);

        for (Object[] row : data) {
            convertData(row);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Integer.class;
                } else if (columnIndex == 3) {
                    return ImageIcon.class;
                } else {
                    return Object.class;
                }
            }
        };

        JTable table = new JTable(model);

        table.setRowHeight(50);

        table.getColumnModel().getColumn(2).setCellRenderer(new PopulationCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        return table;
    }

    private void convertData(Object[] row) {
        // if the row is the header row
        if (!row[0].equals("Name")) {
            String populationString = row[2].toString();
            try {
                int population = Integer.parseInt(populationString);
                row[2] = population;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String flagPath = row[3].toString();
            ImageIcon flagIcon = new ImageIcon(flagPath);
            row[3] = flagIcon;
        }
    }

    private Object[][] readData() throws Exception {
        Vector<Object[]> data = new Vector<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowArray = line.split("\\t");
                if (rowArray.length >= 3) {
                    Object[] row = Arrays.copyOf(rowArray, rowArray.length, Object[].class);
                    data.add(row);
                } else {
                    System.err.println("Skipping invalid row: " + Arrays.toString(rowArray));
                }
            }
        }

        return data.toArray(new Object[0][]);
    }

    private static class PopulationCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Integer && (Integer) value > 20000000) {
                cellComponent.setForeground(Color.RED);
            } else {
                cellComponent.setForeground(table.getForeground());
            }

            return cellComponent;
        }
    }

    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) value;
                label.setIcon(icon);
                label.setText("");
            } else {
                label.setIcon(null);
                label.setText((String) value);
            }

            return label;
        }
    }
}