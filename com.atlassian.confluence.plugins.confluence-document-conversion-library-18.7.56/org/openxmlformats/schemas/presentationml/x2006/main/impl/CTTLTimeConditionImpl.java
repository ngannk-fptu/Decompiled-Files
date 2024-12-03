/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerRuntimeNode
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerTimeNodeID
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTriggerEvent
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeCondition;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerRuntimeNode;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerTimeNodeID;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTime;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTriggerEvent;

public class CTTLTimeConditionImpl
extends XmlComplexContentImpl
implements CTTLTimeCondition {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "tgtEl"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "tn"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "rtn"), new QName("", "evt"), new QName("", "delay")};

    public CTTLTimeConditionImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeTargetElement getTgtEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeTargetElement target = null;
            target = (CTTLTimeTargetElement)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTgtEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setTgtEl(CTTLTimeTargetElement tgtEl) {
        this.generatedSetterHelperImpl(tgtEl, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeTargetElement addNewTgtEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeTargetElement target = null;
            target = (CTTLTimeTargetElement)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTgtEl() {
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
    public CTTLTriggerTimeNodeID getTn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTriggerTimeNodeID target = null;
            target = (CTTLTriggerTimeNodeID)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setTn(CTTLTriggerTimeNodeID tn) {
        this.generatedSetterHelperImpl((XmlObject)tn, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTriggerTimeNodeID addNewTn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTriggerTimeNodeID target = null;
            target = (CTTLTriggerTimeNodeID)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTn() {
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
    public CTTLTriggerRuntimeNode getRtn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTriggerRuntimeNode target = null;
            target = (CTTLTriggerRuntimeNode)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRtn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setRtn(CTTLTriggerRuntimeNode rtn) {
        this.generatedSetterHelperImpl((XmlObject)rtn, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTriggerRuntimeNode addNewRtn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTriggerRuntimeNode target = null;
            target = (CTTLTriggerRuntimeNode)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRtn() {
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
    public STTLTriggerEvent.Enum getEvt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target == null ? null : (STTLTriggerEvent.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTriggerEvent xgetEvt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTriggerEvent target = null;
            target = (STTLTriggerEvent)this.get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEvt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[3]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setEvt(STTLTriggerEvent.Enum evt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.setEnumValue(evt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetEvt(STTLTriggerEvent evt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTriggerEvent target = null;
            target = (STTLTriggerEvent)this.get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (STTLTriggerEvent)this.get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set((XmlObject)evt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEvt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[3]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getDelay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTLTime xgetDelay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDelay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[4]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDelay(Object delay) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.setObjectValue(delay);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDelay(STTLTime delay) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTLTime target = null;
            target = (STTLTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (STTLTime)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.set(delay);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDelay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[4]);
        }
    }
}

