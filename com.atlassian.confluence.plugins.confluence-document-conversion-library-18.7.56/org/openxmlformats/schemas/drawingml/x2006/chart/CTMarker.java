/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerSize;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTMarker
extends XmlObject {
    public static final DocumentFactory<CTMarker> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmarkera682type");
    public static final SchemaType type = Factory.getType();

    public CTMarkerStyle getSymbol();

    public boolean isSetSymbol();

    public void setSymbol(CTMarkerStyle var1);

    public CTMarkerStyle addNewSymbol();

    public void unsetSymbol();

    public CTMarkerSize getSize();

    public boolean isSetSize();

    public void setSize(CTMarkerSize var1);

    public CTMarkerSize addNewSize();

    public void unsetSize();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

