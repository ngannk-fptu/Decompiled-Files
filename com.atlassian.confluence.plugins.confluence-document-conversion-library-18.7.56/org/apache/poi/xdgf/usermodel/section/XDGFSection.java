/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import org.apache.poi.xdgf.usermodel.section.XDGFSectionTypes;

public abstract class XDGFSection {
    protected SectionType _section;
    protected XDGFSheet _containingSheet;
    protected Map<String, XDGFCell> _cells = new HashMap<String, XDGFCell>();

    public static XDGFSection load(SectionType section, XDGFSheet containingSheet) {
        return XDGFSectionTypes.load(section, containingSheet);
    }

    protected XDGFSection(SectionType section, XDGFSheet containingSheet) {
        this._section = section;
        this._containingSheet = containingSheet;
        for (CellType cell : section.getCellArray()) {
            this._cells.put(cell.getN(), new XDGFCell(cell));
        }
    }

    @Internal
    public SectionType getXmlObject() {
        return this._section;
    }

    public String toString() {
        return "<Section type=" + this._section.getN() + " from " + this._containingSheet + ">";
    }

    public abstract void setupMaster(XDGFSection var1);
}

