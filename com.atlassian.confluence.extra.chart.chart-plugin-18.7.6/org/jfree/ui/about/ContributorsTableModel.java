/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.jfree.ui.about.Contributor;
import org.jfree.util.ResourceBundleWrapper;

public class ContributorsTableModel
extends AbstractTableModel {
    private List contributors;
    private String nameColumnLabel;
    private String contactColumnLabel;

    public ContributorsTableModel(List contributors) {
        this.contributors = contributors;
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("contributors-table.column.name");
        this.contactColumnLabel = resources.getString("contributors-table.column.contact");
    }

    public int getRowCount() {
        return this.contributors.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        String result = null;
        switch (column) {
            case 0: {
                result = this.nameColumnLabel;
                break;
            }
            case 1: {
                result = this.contactColumnLabel;
            }
        }
        return result;
    }

    public Object getValueAt(int row, int column) {
        String result = null;
        Contributor contributor = (Contributor)this.contributors.get(row);
        if (column == 0) {
            result = contributor.getName();
        } else if (column == 1) {
            result = contributor.getEmail();
        }
        return result;
    }
}

