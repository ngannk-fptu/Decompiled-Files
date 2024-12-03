/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;

public interface CTStrRef
extends XmlObject {
    public static final DocumentFactory<CTStrRef> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstrref5d1atype");
    public static final SchemaType type = Factory.getType();

    public String getF();

    public XmlString xgetF();

    public void setF(String var1);

    public void xsetF(XmlString var1);

    public CTStrData getStrCache();

    public boolean isSetStrCache();

    public void setStrCache(CTStrData var1);

    public CTStrData addNewStrCache();

    public void unsetStrCache();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

