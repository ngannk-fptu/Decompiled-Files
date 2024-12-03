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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutMode;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutTarget;

public interface CTManualLayout
extends XmlObject {
    public static final DocumentFactory<CTManualLayout> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmanuallayout872ctype");
    public static final SchemaType type = Factory.getType();

    public CTLayoutTarget getLayoutTarget();

    public boolean isSetLayoutTarget();

    public void setLayoutTarget(CTLayoutTarget var1);

    public CTLayoutTarget addNewLayoutTarget();

    public void unsetLayoutTarget();

    public CTLayoutMode getXMode();

    public boolean isSetXMode();

    public void setXMode(CTLayoutMode var1);

    public CTLayoutMode addNewXMode();

    public void unsetXMode();

    public CTLayoutMode getYMode();

    public boolean isSetYMode();

    public void setYMode(CTLayoutMode var1);

    public CTLayoutMode addNewYMode();

    public void unsetYMode();

    public CTLayoutMode getWMode();

    public boolean isSetWMode();

    public void setWMode(CTLayoutMode var1);

    public CTLayoutMode addNewWMode();

    public void unsetWMode();

    public CTLayoutMode getHMode();

    public boolean isSetHMode();

    public void setHMode(CTLayoutMode var1);

    public CTLayoutMode addNewHMode();

    public void unsetHMode();

    public CTDouble getX();

    public boolean isSetX();

    public void setX(CTDouble var1);

    public CTDouble addNewX();

    public void unsetX();

    public CTDouble getY();

    public boolean isSetY();

    public void setY(CTDouble var1);

    public CTDouble addNewY();

    public void unsetY();

    public CTDouble getW();

    public boolean isSetW();

    public void setW(CTDouble var1);

    public CTDouble addNewW();

    public void unsetW();

    public CTDouble getH();

    public boolean isSetH();

    public void setH(CTDouble var1);

    public CTDouble addNewH();

    public void unsetH();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

