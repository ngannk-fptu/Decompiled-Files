/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;

public interface CTLayout
extends XmlObject {
    public static final DocumentFactory<CTLayout> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlayout3192type");
    public static final SchemaType type = Factory.getType();

    public CTManualLayout getManualLayout();

    public boolean isSetManualLayout();

    public void setManualLayout(CTManualLayout var1);

    public CTManualLayout addNewManualLayout();

    public void unsetManualLayout();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

