/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorders;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyleXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFills;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFonts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;

public interface CTStylesheet
extends XmlObject {
    public static final DocumentFactory<CTStylesheet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstylesheet4257type");
    public static final SchemaType type = Factory.getType();

    public CTNumFmts getNumFmts();

    public boolean isSetNumFmts();

    public void setNumFmts(CTNumFmts var1);

    public CTNumFmts addNewNumFmts();

    public void unsetNumFmts();

    public CTFonts getFonts();

    public boolean isSetFonts();

    public void setFonts(CTFonts var1);

    public CTFonts addNewFonts();

    public void unsetFonts();

    public CTFills getFills();

    public boolean isSetFills();

    public void setFills(CTFills var1);

    public CTFills addNewFills();

    public void unsetFills();

    public CTBorders getBorders();

    public boolean isSetBorders();

    public void setBorders(CTBorders var1);

    public CTBorders addNewBorders();

    public void unsetBorders();

    public CTCellStyleXfs getCellStyleXfs();

    public boolean isSetCellStyleXfs();

    public void setCellStyleXfs(CTCellStyleXfs var1);

    public CTCellStyleXfs addNewCellStyleXfs();

    public void unsetCellStyleXfs();

    public CTCellXfs getCellXfs();

    public boolean isSetCellXfs();

    public void setCellXfs(CTCellXfs var1);

    public CTCellXfs addNewCellXfs();

    public void unsetCellXfs();

    public CTCellStyles getCellStyles();

    public boolean isSetCellStyles();

    public void setCellStyles(CTCellStyles var1);

    public CTCellStyles addNewCellStyles();

    public void unsetCellStyles();

    public CTDxfs getDxfs();

    public boolean isSetDxfs();

    public void setDxfs(CTDxfs var1);

    public CTDxfs addNewDxfs();

    public void unsetDxfs();

    public CTTableStyles getTableStyles();

    public boolean isSetTableStyles();

    public void setTableStyles(CTTableStyles var1);

    public CTTableStyles addNewTableStyles();

    public void unsetTableStyles();

    public CTColors getColors();

    public boolean isSetColors();

    public void setColors(CTColors var1);

    public CTColors addNewColors();

    public void unsetColors();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

