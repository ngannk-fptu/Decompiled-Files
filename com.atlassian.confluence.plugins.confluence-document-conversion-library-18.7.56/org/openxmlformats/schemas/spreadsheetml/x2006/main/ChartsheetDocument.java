/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;

public interface ChartsheetDocument
extends XmlObject {
    public static final DocumentFactory<ChartsheetDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "chartsheet99dedoctype");
    public static final SchemaType type = Factory.getType();

    public CTChartsheet getChartsheet();

    public void setChartsheet(CTChartsheet var1);

    public CTChartsheet addNewChartsheet();
}

