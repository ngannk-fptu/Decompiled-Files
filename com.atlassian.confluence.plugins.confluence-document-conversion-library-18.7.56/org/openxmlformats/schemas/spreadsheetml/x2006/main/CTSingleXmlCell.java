/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlCellPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;

public interface CTSingleXmlCell
extends XmlObject {
    public static final DocumentFactory<CTSingleXmlCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsinglexmlcell7790type");
    public static final SchemaType type = Factory.getType();

    public CTXmlCellPr getXmlCellPr();

    public void setXmlCellPr(CTXmlCellPr var1);

    public CTXmlCellPr addNewXmlCellPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public XmlUnsignedInt xgetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public String getR();

    public STCellRef xgetR();

    public void setR(String var1);

    public void xsetR(STCellRef var1);

    public long getConnectionId();

    public XmlUnsignedInt xgetConnectionId();

    public void setConnectionId(long var1);

    public void xsetConnectionId(XmlUnsignedInt var1);
}

