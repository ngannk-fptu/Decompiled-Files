/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTCustSplit
extends XmlObject {
    public static final DocumentFactory<CTCustSplit> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustsplit93bftype");
    public static final SchemaType type = Factory.getType();

    public List<CTUnsignedInt> getSecondPiePtList();

    public CTUnsignedInt[] getSecondPiePtArray();

    public CTUnsignedInt getSecondPiePtArray(int var1);

    public int sizeOfSecondPiePtArray();

    public void setSecondPiePtArray(CTUnsignedInt[] var1);

    public void setSecondPiePtArray(int var1, CTUnsignedInt var2);

    public CTUnsignedInt insertNewSecondPiePt(int var1);

    public CTUnsignedInt addNewSecondPiePt();

    public void removeSecondPiePt(int var1);
}

