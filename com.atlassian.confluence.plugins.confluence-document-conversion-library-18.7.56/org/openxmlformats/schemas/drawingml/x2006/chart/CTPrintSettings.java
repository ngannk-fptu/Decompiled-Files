/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHeaderFooter;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageSetup;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRelId;

public interface CTPrintSettings
extends XmlObject {
    public static final DocumentFactory<CTPrintSettings> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctprintsettings61b6type");
    public static final SchemaType type = Factory.getType();

    public CTHeaderFooter getHeaderFooter();

    public boolean isSetHeaderFooter();

    public void setHeaderFooter(CTHeaderFooter var1);

    public CTHeaderFooter addNewHeaderFooter();

    public void unsetHeaderFooter();

    public CTPageMargins getPageMargins();

    public boolean isSetPageMargins();

    public void setPageMargins(CTPageMargins var1);

    public CTPageMargins addNewPageMargins();

    public void unsetPageMargins();

    public CTPageSetup getPageSetup();

    public boolean isSetPageSetup();

    public void setPageSetup(CTPageSetup var1);

    public CTPageSetup addNewPageSetup();

    public void unsetPageSetup();

    public CTRelId getLegacyDrawingHF();

    public boolean isSetLegacyDrawingHF();

    public void setLegacyDrawingHF(CTRelId var1);

    public CTRelId addNewLegacyDrawingHF();

    public void unsetLegacyDrawingHF();
}

