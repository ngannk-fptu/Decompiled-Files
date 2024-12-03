/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDepthPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPerspective;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotX;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotY;

public interface CTView3D
extends XmlObject {
    public static final DocumentFactory<CTView3D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctview3daf66type");
    public static final SchemaType type = Factory.getType();

    public CTRotX getRotX();

    public boolean isSetRotX();

    public void setRotX(CTRotX var1);

    public CTRotX addNewRotX();

    public void unsetRotX();

    public CTHPercent getHPercent();

    public boolean isSetHPercent();

    public void setHPercent(CTHPercent var1);

    public CTHPercent addNewHPercent();

    public void unsetHPercent();

    public CTRotY getRotY();

    public boolean isSetRotY();

    public void setRotY(CTRotY var1);

    public CTRotY addNewRotY();

    public void unsetRotY();

    public CTDepthPercent getDepthPercent();

    public boolean isSetDepthPercent();

    public void setDepthPercent(CTDepthPercent var1);

    public CTDepthPercent addNewDepthPercent();

    public void unsetDepthPercent();

    public CTBoolean getRAngAx();

    public boolean isSetRAngAx();

    public void setRAngAx(CTBoolean var1);

    public CTBoolean addNewRAngAx();

    public void unsetRAngAx();

    public CTPerspective getPerspective();

    public boolean isSetPerspective();

    public void setPerspective(CTPerspective var1);

    public CTPerspective addNewPerspective();

    public void unsetPerspective();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

