/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;

public interface CTDxf
extends XmlObject {
    public static final DocumentFactory<CTDxf> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdxfa3b1type");
    public static final SchemaType type = Factory.getType();

    public CTFont getFont();

    public boolean isSetFont();

    public void setFont(CTFont var1);

    public CTFont addNewFont();

    public void unsetFont();

    public CTNumFmt getNumFmt();

    public boolean isSetNumFmt();

    public void setNumFmt(CTNumFmt var1);

    public CTNumFmt addNewNumFmt();

    public void unsetNumFmt();

    public CTFill getFill();

    public boolean isSetFill();

    public void setFill(CTFill var1);

    public CTFill addNewFill();

    public void unsetFill();

    public CTCellAlignment getAlignment();

    public boolean isSetAlignment();

    public void setAlignment(CTCellAlignment var1);

    public CTCellAlignment addNewAlignment();

    public void unsetAlignment();

    public CTBorder getBorder();

    public boolean isSetBorder();

    public void setBorder(CTBorder var1);

    public CTBorder addNewBorder();

    public void unsetBorder();

    public CTCellProtection getProtection();

    public boolean isSetProtection();

    public void setProtection(CTCellProtection var1);

    public CTCellProtection addNewProtection();

    public void unsetProtection();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

