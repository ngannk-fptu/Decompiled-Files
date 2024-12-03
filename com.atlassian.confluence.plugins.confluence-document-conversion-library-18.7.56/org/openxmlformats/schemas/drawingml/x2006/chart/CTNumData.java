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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTNumData
extends XmlObject {
    public static final DocumentFactory<CTNumData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumdata4f16type");
    public static final SchemaType type = Factory.getType();

    public String getFormatCode();

    public STXstring xgetFormatCode();

    public boolean isSetFormatCode();

    public void setFormatCode(String var1);

    public void xsetFormatCode(STXstring var1);

    public void unsetFormatCode();

    public CTUnsignedInt getPtCount();

    public boolean isSetPtCount();

    public void setPtCount(CTUnsignedInt var1);

    public CTUnsignedInt addNewPtCount();

    public void unsetPtCount();

    public List<CTNumVal> getPtList();

    public CTNumVal[] getPtArray();

    public CTNumVal getPtArray(int var1);

    public int sizeOfPtArray();

    public void setPtArray(CTNumVal[] var1);

    public void setPtArray(int var1, CTNumVal var2);

    public CTNumVal insertNewPt(int var1);

    public CTNumVal addNewPt();

    public void removePt(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

