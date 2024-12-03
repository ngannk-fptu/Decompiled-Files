/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import org.jfree.ui.SortableTableModel;
import org.jfree.util.ResourceBundleWrapper;

public class SystemPropertiesTableModel
extends SortableTableModel {
    private List properties = new ArrayList();
    private String nameColumnLabel;
    private String valueColumnLabel;

    public SystemPropertiesTableModel() {
        try {
            Properties p = System.getProperties();
            Iterator<Object> iterator = p.keySet().iterator();
            while (iterator.hasNext()) {
                String name = (String)iterator.next();
                String value = System.getProperty(name);
                SystemProperty sp = new SystemProperty(name, value);
                this.properties.add(sp);
            }
        }
        catch (SecurityException se) {
            // empty catch block
        }
        Collections.sort(this.properties, new SystemPropertyComparator(true));
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("system-properties-table.column.name");
        this.valueColumnLabel = resources.getString("system-properties-table.column.value");
    }

    public boolean isSortable(int column) {
        return column == 0;
    }

    public int getRowCount() {
        return this.properties.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return this.nameColumnLabel;
        }
        return this.valueColumnLabel;
    }

    public Object getValueAt(int row, int column) {
        SystemProperty sp = (SystemProperty)this.properties.get(row);
        if (column == 0) {
            return sp.getName();
        }
        if (column == 1) {
            return sp.getValue();
        }
        return null;
    }

    public void sortByColumn(int column, boolean ascending) {
        if (this.isSortable(column)) {
            super.sortByColumn(column, ascending);
            Collections.sort(this.properties, new SystemPropertyComparator(ascending));
        }
    }

    protected static class SystemPropertyComparator
    implements Comparator {
        private boolean ascending;

        public SystemPropertyComparator(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(Object o1, Object o2) {
            if (o1 instanceof SystemProperty && o2 instanceof SystemProperty) {
                SystemProperty sp1 = (SystemProperty)o1;
                SystemProperty sp2 = (SystemProperty)o2;
                if (this.ascending) {
                    return sp1.getName().compareTo(sp2.getName());
                }
                return sp2.getName().compareTo(sp1.getName());
            }
            return 0;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SystemPropertyComparator)) {
                return false;
            }
            SystemPropertyComparator systemPropertyComparator = (SystemPropertyComparator)o;
            return this.ascending == systemPropertyComparator.ascending;
        }

        public int hashCode() {
            return this.ascending ? 1 : 0;
        }
    }

    protected static class SystemProperty {
        private String name;
        private String value;

        public SystemProperty(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }
}

