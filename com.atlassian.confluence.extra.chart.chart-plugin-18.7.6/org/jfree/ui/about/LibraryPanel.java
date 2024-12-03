/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jfree.base.Library;
import org.jfree.ui.about.LibraryTableModel;
import org.jfree.ui.about.ProjectInfo;

public class LibraryPanel
extends JPanel {
    private JTable table;
    private LibraryTableModel model;

    public LibraryPanel(List libraries) {
        this.setLayout(new BorderLayout());
        this.model = new LibraryTableModel(libraries);
        this.table = new JTable(this.model);
        this.add(new JScrollPane(this.table));
    }

    public LibraryPanel(ProjectInfo projectInfo) {
        this(LibraryPanel.getLibraries(projectInfo));
    }

    private static List getLibraries(ProjectInfo info) {
        if (info == null) {
            return new ArrayList();
        }
        ArrayList libs = new ArrayList();
        LibraryPanel.collectLibraries(info, libs);
        return libs;
    }

    private static void collectLibraries(ProjectInfo info, List list) {
        Library lib;
        int i;
        Library[] libs = info.getLibraries();
        for (i = 0; i < libs.length; ++i) {
            lib = libs[i];
            if (list.contains(lib)) continue;
            list.add(lib);
            if (!(lib instanceof ProjectInfo)) continue;
            LibraryPanel.collectLibraries((ProjectInfo)lib, list);
        }
        libs = info.getOptionalLibraries();
        for (i = 0; i < libs.length; ++i) {
            lib = libs[i];
            if (list.contains(lib)) continue;
            list.add(lib);
            if (!(lib instanceof ProjectInfo)) continue;
            LibraryPanel.collectLibraries((ProjectInfo)lib, list);
        }
    }

    public LibraryTableModel getModel() {
        return this.model;
    }

    protected JTable getTable() {
        return this.table;
    }
}

