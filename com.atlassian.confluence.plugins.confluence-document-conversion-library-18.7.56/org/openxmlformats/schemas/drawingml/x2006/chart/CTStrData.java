/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTStrData
extends XmlObject {
    public static final DocumentFactory<CTStrData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstrdatad58btype");
    public static final SchemaType type = Factory.getType();

    public CTUnsignedInt getPtCount();

    public boolean isSetPtCount();

    public void setPtCount(CTUnsignedInt var1);

    public CTUnsignedInt addNewPtCount();

    public void unsetPtCount();

    public List<CTStrVal> getPtList();

    public CTStrVal[] getPtArray();

    public CTStrVal getPtArray(int var1);

    public int sizeOfPtArray();

    public void setPtArray(CTStrVal[] var1);

    public void setPtArray(int var1, CTStrVal var2);

    public CTStrVal insertNewPt(int var1);

    public CTStrVal addNewPt();

    public void removePt(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

