/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import java.util.Vector;

public class TabbedPane {
    protected String tabAlign = null;
    protected Vector content = null;
    protected int selectedIndex = 0;

    public TabbedPane(int defaultIndex) {
        this.selectedIndex = defaultIndex;
    }

    public void setContent(Vector content) {
        this.content = content;
    }

    public Vector getContent() {
        return this.content;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void setTabAlign(String tabAlign) {
        this.tabAlign = tabAlign;
    }

    public String getTabAlign() {
        return this.tabAlign;
    }
}

