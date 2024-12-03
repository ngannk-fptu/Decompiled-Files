/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLogBase;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOrientation;

public interface CTScaling
extends XmlObject {
    public static final DocumentFactory<CTScaling> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctscaling1dfftype");
    public static final SchemaType type = Factory.getType();

    public CTLogBase getLogBase();

    public boolean isSetLogBase();

    public void setLogBase(CTLogBase var1);

    public CTLogBase addNewLogBase();

    public void unsetLogBase();

    public CTOrientation getOrientation();

    public boolean isSetOrientation();

    public void setOrientation(CTOrientation var1);

    public CTOrientation addNewOrientation();

    public void unsetOrientation();

    public CTDouble getMax();

    public boolean isSetMax();

    public void setMax(CTDouble var1);

    public CTDouble addNewMax();

    public void unsetMax();

    public CTDouble getMin();

    public boolean isSetMin();

    public void setMin(CTDouble var1);

    public CTDouble addNewMin();

    public void unsetMin();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

