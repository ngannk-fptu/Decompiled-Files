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
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAnimationElementChoice;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLOleChartTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLShapeTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTextTargetElement;

public class CTTLShapeTargetElementImpl
extends XmlComplexContentImpl
implements CTTLShapeTargetElement {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bg"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "subSp"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "oleChartEl"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txEl"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "graphicEl"), new QName("", "spid")};

    public CTTLShapeTargetElementImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getBg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setBg(CTEmpty bg) {
        this.generatedSetterHelperImpl((XmlObject)bg, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewBg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBg() {
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
    public CTTLSubShapeId getSubSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSubShapeId target = null;
            target = (CTTLSubShapeId)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSubSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setSubSp(CTTLSubShapeId subSp) {
        this.generatedSetterHelperImpl((XmlObject)subSp, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLSubShapeId addNewSubSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSubShapeId target = null;
            target = (CTTLSubShapeId)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSubSp() {
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
    public CTTLOleChartTargetElement getOleChartEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLOleChartTargetElement target = null;
            target = (CTTLOleChartTargetElement)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOleChartEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setOleChartEl(CTTLOleChartTargetElement oleChartEl) {
        this.generatedSetterHelperImpl((XmlObject)oleChartEl, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLOleChartTargetElement addNewOleChartEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLOleChartTargetElement target = null;
            target = (CTTLOleChartTargetElement)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOleChartEl() {
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
    public CTTLTextTargetElement getTxEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTextTargetElement target = null;
            target = (CTTLTextTargetElement)this.get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTxEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setTxEl(CTTLTextTargetElement txEl) {
        this.generatedSetterHelperImpl((XmlObject)txEl, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTextTargetElement addNewTxEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTextTargetElement target = null;
            target = (CTTLTextTargetElement)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTxEl() {
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
    public CTAnimationElementChoice getGraphicEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnimationElementChoice target = null;
            target = (CTAnimationElementChoice)this.get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGraphicEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setGraphicEl(CTAnimationElementChoice graphicEl) {
        this.generatedSetterHelperImpl((XmlObject)graphicEl, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnimationElementChoice addNewGraphicEl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnimationElementChoice target = null;
            target = (CTAnimationElementChoice)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGraphicEl() {
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
    public long getSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target == null ? 0L : target.getLongValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDrawingElementId xgetSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDrawingElementId target = null;
            target = (STDrawingElementId)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSpid(long spid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.setLongValue(spid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSpid(STDrawingElementId spid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDrawingElementId target = null;
            target = (STDrawingElementId)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (STDrawingElementId)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.set(spid);
        }
    }
}

