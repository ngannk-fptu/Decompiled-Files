/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLIterateData
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeID
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeMasterRelation
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodePresetClassType
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeSyncType
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLIterateData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeCondition;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeConditionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTimeNodeList;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTime;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeFillType;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeID;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeMasterRelation;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodePresetClassType;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeRestartType;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeSyncType;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeNodeType;

public interface CTTLCommonTimeNodeData
extends XmlObject {
    public static final DocumentFactory<CTTLCommonTimeNodeData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttlcommontimenodedatac8e9type");
    public static final SchemaType type = Factory.getType();

    public CTTLTimeConditionList getStCondLst();

    public boolean isSetStCondLst();

    public void setStCondLst(CTTLTimeConditionList var1);

    public CTTLTimeConditionList addNewStCondLst();

    public void unsetStCondLst();

    public CTTLTimeConditionList getEndCondLst();

    public boolean isSetEndCondLst();

    public void setEndCondLst(CTTLTimeConditionList var1);

    public CTTLTimeConditionList addNewEndCondLst();

    public void unsetEndCondLst();

    public CTTLTimeCondition getEndSync();

    public boolean isSetEndSync();

    public void setEndSync(CTTLTimeCondition var1);

    public CTTLTimeCondition addNewEndSync();

    public void unsetEndSync();

    public CTTLIterateData getIterate();

    public boolean isSetIterate();

    public void setIterate(CTTLIterateData var1);

    public CTTLIterateData addNewIterate();

    public void unsetIterate();

    public CTTimeNodeList getChildTnLst();

    public boolean isSetChildTnLst();

    public void setChildTnLst(CTTimeNodeList var1);

    public CTTimeNodeList addNewChildTnLst();

    public void unsetChildTnLst();

    public CTTimeNodeList getSubTnLst();

    public boolean isSetSubTnLst();

    public void setSubTnLst(CTTimeNodeList var1);

    public CTTimeNodeList addNewSubTnLst();

    public void unsetSubTnLst();

    public long getId();

    public STTLTimeNodeID xgetId();

    public boolean isSetId();

    public void setId(long var1);

    public void xsetId(STTLTimeNodeID var1);

    public void unsetId();

    public int getPresetID();

    public XmlInt xgetPresetID();

    public boolean isSetPresetID();

    public void setPresetID(int var1);

    public void xsetPresetID(XmlInt var1);

    public void unsetPresetID();

    public STTLTimeNodePresetClassType.Enum getPresetClass();

    public STTLTimeNodePresetClassType xgetPresetClass();

    public boolean isSetPresetClass();

    public void setPresetClass(STTLTimeNodePresetClassType.Enum var1);

    public void xsetPresetClass(STTLTimeNodePresetClassType var1);

    public void unsetPresetClass();

    public int getPresetSubtype();

    public XmlInt xgetPresetSubtype();

    public boolean isSetPresetSubtype();

    public void setPresetSubtype(int var1);

    public void xsetPresetSubtype(XmlInt var1);

    public void unsetPresetSubtype();

    public Object getDur();

    public STTLTime xgetDur();

    public boolean isSetDur();

    public void setDur(Object var1);

    public void xsetDur(STTLTime var1);

    public void unsetDur();

    public Object getRepeatCount();

    public STTLTime xgetRepeatCount();

    public boolean isSetRepeatCount();

    public void setRepeatCount(Object var1);

    public void xsetRepeatCount(STTLTime var1);

    public void unsetRepeatCount();

    public Object getRepeatDur();

    public STTLTime xgetRepeatDur();

    public boolean isSetRepeatDur();

    public void setRepeatDur(Object var1);

    public void xsetRepeatDur(STTLTime var1);

    public void unsetRepeatDur();

    public Object getSpd();

    public STPercentage xgetSpd();

    public boolean isSetSpd();

    public void setSpd(Object var1);

    public void xsetSpd(STPercentage var1);

    public void unsetSpd();

    public Object getAccel();

    public STPositiveFixedPercentage xgetAccel();

    public boolean isSetAccel();

    public void setAccel(Object var1);

    public void xsetAccel(STPositiveFixedPercentage var1);

    public void unsetAccel();

    public Object getDecel();

    public STPositiveFixedPercentage xgetDecel();

    public boolean isSetDecel();

    public void setDecel(Object var1);

    public void xsetDecel(STPositiveFixedPercentage var1);

    public void unsetDecel();

    public boolean getAutoRev();

    public XmlBoolean xgetAutoRev();

    public boolean isSetAutoRev();

    public void setAutoRev(boolean var1);

    public void xsetAutoRev(XmlBoolean var1);

    public void unsetAutoRev();

    public STTLTimeNodeRestartType.Enum getRestart();

    public STTLTimeNodeRestartType xgetRestart();

    public boolean isSetRestart();

    public void setRestart(STTLTimeNodeRestartType.Enum var1);

    public void xsetRestart(STTLTimeNodeRestartType var1);

    public void unsetRestart();

    public STTLTimeNodeFillType.Enum getFill();

    public STTLTimeNodeFillType xgetFill();

    public boolean isSetFill();

    public void setFill(STTLTimeNodeFillType.Enum var1);

    public void xsetFill(STTLTimeNodeFillType var1);

    public void unsetFill();

    public STTLTimeNodeSyncType.Enum getSyncBehavior();

    public STTLTimeNodeSyncType xgetSyncBehavior();

    public boolean isSetSyncBehavior();

    public void setSyncBehavior(STTLTimeNodeSyncType.Enum var1);

    public void xsetSyncBehavior(STTLTimeNodeSyncType var1);

    public void unsetSyncBehavior();

    public String getTmFilter();

    public XmlString xgetTmFilter();

    public boolean isSetTmFilter();

    public void setTmFilter(String var1);

    public void xsetTmFilter(XmlString var1);

    public void unsetTmFilter();

    public String getEvtFilter();

    public XmlString xgetEvtFilter();

    public boolean isSetEvtFilter();

    public void setEvtFilter(String var1);

    public void xsetEvtFilter(XmlString var1);

    public void unsetEvtFilter();

    public boolean getDisplay();

    public XmlBoolean xgetDisplay();

    public boolean isSetDisplay();

    public void setDisplay(boolean var1);

    public void xsetDisplay(XmlBoolean var1);

    public void unsetDisplay();

    public STTLTimeNodeMasterRelation.Enum getMasterRel();

    public STTLTimeNodeMasterRelation xgetMasterRel();

    public boolean isSetMasterRel();

    public void setMasterRel(STTLTimeNodeMasterRelation.Enum var1);

    public void xsetMasterRel(STTLTimeNodeMasterRelation var1);

    public void unsetMasterRel();

    public int getBldLvl();

    public XmlInt xgetBldLvl();

    public boolean isSetBldLvl();

    public void setBldLvl(int var1);

    public void xsetBldLvl(XmlInt var1);

    public void unsetBldLvl();

    public long getGrpId();

    public XmlUnsignedInt xgetGrpId();

    public boolean isSetGrpId();

    public void setGrpId(long var1);

    public void xsetGrpId(XmlUnsignedInt var1);

    public void unsetGrpId();

    public boolean getAfterEffect();

    public XmlBoolean xgetAfterEffect();

    public boolean isSetAfterEffect();

    public void setAfterEffect(boolean var1);

    public void xsetAfterEffect(XmlBoolean var1);

    public void unsetAfterEffect();

    public STTLTimeNodeType.Enum getNodeType();

    public STTLTimeNodeType xgetNodeType();

    public boolean isSetNodeType();

    public void setNodeType(STTLTimeNodeType.Enum var1);

    public void xsetNodeType(STTLTimeNodeType var1);

    public void unsetNodeType();

    public boolean getNodePh();

    public XmlBoolean xgetNodePh();

    public boolean isSetNodePh();

    public void setNodePh(boolean var1);

    public void xsetNodePh(XmlBoolean var1);

    public void unsetNodePh();
}

