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
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommonTimeNodeData;
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

public class CTTLCommonTimeNodeDataImpl
extends XmlComplexContentImpl
implements CTTLCommonTimeNodeData {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "stCondLst"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "endCondLst"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "endSync"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "iterate"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "childTnLst"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "subTnLst"), new QName("", "id"), new QName("", "presetID"), new QName("", "presetClass"), new QName("", "presetSubtype"), new QName("", "dur"), new QName("", "repeatCount"), new QName("", "repeatDur"), new QName("", "spd"), new QName("", "accel"), new QName("", "decel"), new QName("", "autoRev"), new QName("", "restart"), new QName("", "fill"), new QName("", "syncBehavior"), new QName("", "tmFilter"), new QName("", "evtFilter"), new QName("", "display"), new QName("", "masterRel"), new QName("", "bldLvl"), new QName("", "grpId"), new QName("", "afterEffect"), new QName("", "nodeType"), new QName("", "nodePh")};

    public CTTLCommonTimeNodeDataImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeConditionList getStCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeConditionList target = null;
            target = (CTTLTimeConditionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setStCondLst(CTTLTimeConditionList stCondLst) {
        this.generatedSetterHelperImpl(stCondLst, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeConditionList addNewStCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeConditionList target = null;
            target = (CTTLTimeConditionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeConditionList getEndCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeConditionList target = null;
            target = (CTTLTimeConditionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEndCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setEndCondLst(CTTLTimeConditionList endCondLst) {
        this.generatedSetterHelperImpl(endCondLst, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeConditionList addNewEndCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeConditionList target = null;
            target = (CTTLTimeConditionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEndCondLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeCondition getEndSync() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeCondition target = null;
            target = (CTTLTimeCondition)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEndSync() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setEndSync(CTTLTimeCondition endSync) {
        this.generatedSetterHelperImpl(endSync, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeCondition addNewEndSync() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeCondition target = null;
            target = (CTTLTimeCondition)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEndSync() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLIterateData getIterate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLIterateData target = null;
            target = (CTTLIterateData)this.get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetIterate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setIterate(CTTLIterateData iterate) {
        this.generatedSetterHelperImpl((XmlObject)iterate, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLIterateData addNewIterate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLIterateData target = null;
            target = (CTTLIterateData)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetIterate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeNodeList getChildTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeNodeList target = null;
            target = (CTTimeNodeList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetChildTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setChildTnLst(CTTimeNodeList childTnLst) {
        this.generatedSetterHelperImpl(childTnLst, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeNodeList addNewChildTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeNodeList target = null;
            target = (CTTimeNodeList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetChildTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeNodeList getSubTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeNodeList target = null;
            target = (CTTimeNodeList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSubTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setSubTnLst(CTTimeNodeList subTnLst) {
        this.generatedSetterHelperImpl(subTnLst, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeNodeList addNewSubTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeNodeList target = null;
            target = (CTTimeNodeList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSubTnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            return target == null ? 0L : target.getLongValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeID xgetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeID target = null;
            target = (STTLTimeNodeID)this.get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[6]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setId(long id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
            }
            target.setLongValue(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetId(STTLTimeNodeID id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeID target = null;
            target = (STTLTimeNodeID)this.get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (STTLTimeNodeID)this.get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.set((XmlObject)id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[6]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getPresetID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInt xgetPresetID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPresetID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[7]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPresetID(int presetID) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[7]));
            }
            target.setIntValue(presetID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPresetID(XmlInt presetID) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[7]));
            if (target == null) {
                target = (XmlInt)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[7]));
            }
            target.set(presetID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPresetID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[7]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodePresetClassType.Enum getPresetClass() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            return target == null ? null : (STTLTimeNodePresetClassType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodePresetClassType xgetPresetClass() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodePresetClassType target = null;
            target = (STTLTimeNodePresetClassType)this.get_store().find_attribute_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPresetClass() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[8]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPresetClass(STTLTimeNodePresetClassType.Enum presetClass) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[8]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[8]));
            }
            target.setEnumValue(presetClass);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPresetClass(STTLTimeNodePresetClassType presetClass) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodePresetClassType target = null;
            target = (STTLTimeNodePresetClassType)this.get_store().find_attribute_user(PROPERTY_QNAME[8]);
            if (target == null) {
                target = (STTLTimeNodePresetClassType)this.get_store().add_attribute_user(PROPERTY_QNAME[8]);
            }
            target.set((XmlObject)presetClass);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPresetClass() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[8]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getPresetSubtype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInt xgetPresetSubtype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPresetSubtype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[9]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPresetSubtype(int presetSubtype) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
            }
            target.setIntValue(presetSubtype);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPresetSubtype(XmlInt presetSubtype) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[9]));
            if (target == null) {
                target = (XmlInt)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[9]));
            }
            target.set(presetSubtype);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPresetSubtype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[9]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTime xgetDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[10]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDur(Object dur) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.setObjectValue(dur);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDur(STTLTime dur) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[10]));
            if (target == null) {
                target = (STTLTime)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[10]));
            }
            target.set(dur);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[10]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getRepeatCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[11]));
            }
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTime xgetRepeatCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (STTLTime)this.get_default_attribute_value(PROPERTY_QNAME[11]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRepeatCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[11]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRepeatCount(Object repeatCount) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.setObjectValue(repeatCount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRepeatCount(STTLTime repeatCount) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
            if (target == null) {
                target = (STTLTime)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
            }
            target.set(repeatCount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRepeatCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[11]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getRepeatDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTime xgetRepeatDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRepeatDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[12]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRepeatDur(Object repeatDur) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.setObjectValue(repeatDur);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRepeatDur(STTLTime repeatDur) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
            if (target == null) {
                target = (STTLTime)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
            }
            target.set(repeatDur);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRepeatDur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[12]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getSpd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[13]));
            }
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPercentage xgetSpd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPercentage target = null;
            target = (STPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (STPercentage)this.get_default_attribute_value(PROPERTY_QNAME[13]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[13]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSpd(Object spd) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
            }
            target.setObjectValue(spd);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSpd(STPercentage spd) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPercentage target = null;
            target = (STPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (STPercentage)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
            }
            target.set(spd);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[13]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getAccel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[14]));
            }
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveFixedPercentage xgetAccel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveFixedPercentage target = null;
            target = (STPositiveFixedPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
            if (target == null) {
                target = (STPositiveFixedPercentage)this.get_default_attribute_value(PROPERTY_QNAME[14]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAccel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[14]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccel(Object accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[14]));
            }
            target.setObjectValue(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccel(STPositiveFixedPercentage accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveFixedPercentage target = null;
            target = (STPositiveFixedPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
            if (target == null) {
                target = (STPositiveFixedPercentage)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[14]));
            }
            target.set(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAccel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[14]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getDecel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[15]));
            }
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveFixedPercentage xgetDecel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveFixedPercentage target = null;
            target = (STPositiveFixedPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
            if (target == null) {
                target = (STPositiveFixedPercentage)this.get_default_attribute_value(PROPERTY_QNAME[15]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDecel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[15]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDecel(Object decel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[15]));
            }
            target.setObjectValue(decel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDecel(STPositiveFixedPercentage decel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveFixedPercentage target = null;
            target = (STPositiveFixedPercentage)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
            if (target == null) {
                target = (STPositiveFixedPercentage)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[15]));
            }
            target.set(decel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDecel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[15]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getAutoRev() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[16]));
            }
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetAutoRev() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(PROPERTY_QNAME[16]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAutoRev() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[16]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoRev(boolean autoRev) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[16]));
            }
            target.setBooleanValue(autoRev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoRev(XmlBoolean autoRev) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[16]));
            }
            target.set(autoRev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAutoRev() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[16]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeRestartType.Enum getRestart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
            return target == null ? null : (STTLTimeNodeRestartType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeRestartType xgetRestart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeRestartType target = null;
            target = (STTLTimeNodeRestartType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRestart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[17]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRestart(STTLTimeNodeRestartType.Enum restart) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[17]));
            }
            target.setEnumValue(restart);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRestart(STTLTimeNodeRestartType restart) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeRestartType target = null;
            target = (STTLTimeNodeRestartType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
            if (target == null) {
                target = (STTLTimeNodeRestartType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[17]));
            }
            target.set(restart);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRestart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[17]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeFillType.Enum getFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
            return target == null ? null : (STTLTimeNodeFillType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeFillType xgetFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeFillType target = null;
            target = (STTLTimeNodeFillType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[18]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFill(STTLTimeNodeFillType.Enum fill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[18]));
            }
            target.setEnumValue(fill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFill(STTLTimeNodeFillType fill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeFillType target = null;
            target = (STTLTimeNodeFillType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
            if (target == null) {
                target = (STTLTimeNodeFillType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[18]));
            }
            target.set(fill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[18]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeSyncType.Enum getSyncBehavior() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[19]));
            return target == null ? null : (STTLTimeNodeSyncType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeSyncType xgetSyncBehavior() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeSyncType target = null;
            target = (STTLTimeNodeSyncType)this.get_store().find_attribute_user(PROPERTY_QNAME[19]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSyncBehavior() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[19]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSyncBehavior(STTLTimeNodeSyncType.Enum syncBehavior) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[19]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[19]));
            }
            target.setEnumValue(syncBehavior);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSyncBehavior(STTLTimeNodeSyncType syncBehavior) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeSyncType target = null;
            target = (STTLTimeNodeSyncType)this.get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (STTLTimeNodeSyncType)this.get_store().add_attribute_user(PROPERTY_QNAME[19]);
            }
            target.set((XmlObject)syncBehavior);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSyncBehavior() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[19]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getTmFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[20]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetTmFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTmFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[20]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTmFilter(String tmFilter) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[20]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[20]));
            }
            target.setStringValue(tmFilter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTmFilter(XmlString tmFilter) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[20]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[20]));
            }
            target.set(tmFilter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTmFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[20]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getEvtFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[21]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetEvtFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEvtFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[21]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setEvtFilter(String evtFilter) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[21]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[21]));
            }
            target.setStringValue(evtFilter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetEvtFilter(XmlString evtFilter) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[21]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[21]));
            }
            target.set(evtFilter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEvtFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[21]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[22]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisplay(boolean display) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[22]));
            }
            target.setBooleanValue(display);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDisplay(XmlBoolean display) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[22]));
            }
            target.set(display);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[22]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeMasterRelation.Enum getMasterRel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            return target == null ? null : (STTLTimeNodeMasterRelation.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeMasterRelation xgetMasterRel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeMasterRelation target = null;
            target = (STTLTimeNodeMasterRelation)this.get_store().find_attribute_user(PROPERTY_QNAME[23]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMasterRel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[23]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMasterRel(STTLTimeNodeMasterRelation.Enum masterRel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[23]));
            }
            target.setEnumValue(masterRel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMasterRel(STTLTimeNodeMasterRelation masterRel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeMasterRelation target = null;
            target = (STTLTimeNodeMasterRelation)this.get_store().find_attribute_user(PROPERTY_QNAME[23]);
            if (target == null) {
                target = (STTLTimeNodeMasterRelation)this.get_store().add_attribute_user(PROPERTY_QNAME[23]);
            }
            target.set((XmlObject)masterRel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMasterRel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[23]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getBldLvl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInt xgetBldLvl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBldLvl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[24]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBldLvl(int bldLvl) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[24]));
            }
            target.setIntValue(bldLvl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBldLvl(XmlInt bldLvl) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            if (target == null) {
                target = (XmlInt)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[24]));
            }
            target.set(bldLvl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBldLvl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[24]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getGrpId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            return target == null ? 0L : target.getLongValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlUnsignedInt xgetGrpId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlUnsignedInt target = null;
            target = (XmlUnsignedInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGrpId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[25]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setGrpId(long grpId) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[25]));
            }
            target.setLongValue(grpId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetGrpId(XmlUnsignedInt grpId) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlUnsignedInt target = null;
            target = (XmlUnsignedInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            if (target == null) {
                target = (XmlUnsignedInt)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[25]));
            }
            target.set(grpId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGrpId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[25]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getAfterEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetAfterEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAfterEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[26]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAfterEffect(boolean afterEffect) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[26]));
            }
            target.setBooleanValue(afterEffect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAfterEffect(XmlBoolean afterEffect) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[26]));
            }
            target.set(afterEffect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAfterEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[26]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeType.Enum getNodeType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            return target == null ? null : (STTLTimeNodeType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTimeNodeType xgetNodeType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeType target = null;
            target = (STTLTimeNodeType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNodeType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[27]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNodeType(STTLTimeNodeType.Enum nodeType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[27]));
            }
            target.setEnumValue(nodeType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNodeType(STTLTimeNodeType nodeType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTimeNodeType target = null;
            target = (STTLTimeNodeType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            if (target == null) {
                target = (STTLTimeNodeType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[27]));
            }
            target.set(nodeType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNodeType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[27]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getNodePh() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetNodePh() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNodePh() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[28]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNodePh(boolean nodePh) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.setBooleanValue(nodePh);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNodePh(XmlBoolean nodePh) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.set(nodePh);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNodePh() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[28]);
        }
    }
}

