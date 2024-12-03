/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommonTimeNodeData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeTargetElement;

public interface CTTLCommonMediaNodeData
extends XmlObject {
    public static final DocumentFactory<CTTLCommonMediaNodeData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttlcommonmedianodedatab6c2type");
    public static final SchemaType type = Factory.getType();

    public CTTLCommonTimeNodeData getCTn();

    public void setCTn(CTTLCommonTimeNodeData var1);

    public CTTLCommonTimeNodeData addNewCTn();

    public CTTLTimeTargetElement getTgtEl();

    public void setTgtEl(CTTLTimeTargetElement var1);

    public CTTLTimeTargetElement addNewTgtEl();

    public Object getVol();

    public STPositiveFixedPercentage xgetVol();

    public boolean isSetVol();

    public void setVol(Object var1);

    public void xsetVol(STPositiveFixedPercentage var1);

    public void unsetVol();

    public boolean getMute();

    public XmlBoolean xgetMute();

    public boolean isSetMute();

    public void setMute(boolean var1);

    public void xsetMute(XmlBoolean var1);

    public void unsetMute();

    public long getNumSld();

    public XmlUnsignedInt xgetNumSld();

    public boolean isSetNumSld();

    public void setNumSld(long var1);

    public void xsetNumSld(XmlUnsignedInt var1);

    public void unsetNumSld();

    public boolean getShowWhenStopped();

    public XmlBoolean xgetShowWhenStopped();

    public boolean isSetShowWhenStopped();

    public void setShowWhenStopped(boolean var1);

    public void xsetShowWhenStopped(XmlBoolean var1);

    public void unsetShowWhenStopped();
}

