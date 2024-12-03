/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAnimationElementChoice
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLOleChartTargetElement
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTextTargetElement
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAnimationElementChoice;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLOleChartTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTextTargetElement;

public interface CTTLShapeTargetElement
extends XmlObject {
    public static final DocumentFactory<CTTLShapeTargetElement> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttlshapetargetelement2763type");
    public static final SchemaType type = Factory.getType();

    public CTEmpty getBg();

    public boolean isSetBg();

    public void setBg(CTEmpty var1);

    public CTEmpty addNewBg();

    public void unsetBg();

    public CTTLSubShapeId getSubSp();

    public boolean isSetSubSp();

    public void setSubSp(CTTLSubShapeId var1);

    public CTTLSubShapeId addNewSubSp();

    public void unsetSubSp();

    public CTTLOleChartTargetElement getOleChartEl();

    public boolean isSetOleChartEl();

    public void setOleChartEl(CTTLOleChartTargetElement var1);

    public CTTLOleChartTargetElement addNewOleChartEl();

    public void unsetOleChartEl();

    public CTTLTextTargetElement getTxEl();

    public boolean isSetTxEl();

    public void setTxEl(CTTLTextTargetElement var1);

    public CTTLTextTargetElement addNewTxEl();

    public void unsetTxEl();

    public CTAnimationElementChoice getGraphicEl();

    public boolean isSetGraphicEl();

    public void setGraphicEl(CTAnimationElementChoice var1);

    public CTAnimationElementChoice addNewGraphicEl();

    public void unsetGraphicEl();

    public long getSpid();

    public STDrawingElementId xgetSpid();

    public void setSpid(long var1);

    public void xsetSpid(STDrawingElementId var1);
}

