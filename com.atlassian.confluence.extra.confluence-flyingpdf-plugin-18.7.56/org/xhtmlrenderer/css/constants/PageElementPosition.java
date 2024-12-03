/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.util.HashMap;
import java.util.Map;

public class PageElementPosition {
    private static final Map ALL = new HashMap();
    private static int _maxAssigned = 0;
    public final int FS_ID;
    private final String _ident;
    public static final PageElementPosition START = PageElementPosition.addValue("start");
    public static final PageElementPosition FIRST = PageElementPosition.addValue("first");
    public static final PageElementPosition LAST = PageElementPosition.addValue("last");
    public static final PageElementPosition LAST_EXCEPT = PageElementPosition.addValue("last-except");

    private PageElementPosition(String ident) {
        this._ident = ident;
        this.FS_ID = _maxAssigned++;
    }

    private static final PageElementPosition addValue(String ident) {
        PageElementPosition val = new PageElementPosition(ident);
        ALL.put(ident, val);
        return val;
    }

    public String toString() {
        return this._ident;
    }

    public static PageElementPosition valueOf(String ident) {
        return (PageElementPosition)ALL.get(ident);
    }

    public int hashCode() {
        return this.FS_ID;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PageElementPosition)) {
            return false;
        }
        return this.FS_ID == ((PageElementPosition)o).FS_ID;
    }
}

