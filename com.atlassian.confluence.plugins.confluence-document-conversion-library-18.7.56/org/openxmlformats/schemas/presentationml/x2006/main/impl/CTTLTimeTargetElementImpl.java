/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLShapeTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeTargetElement;

public class CTTLTimeTargetElementImpl
extends XmlComplexContentImpl
implements CTTLTimeTargetElement {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldTgt"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sndTgt"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "spTgt"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "inkTgt")};

    public CTTLTimeTargetElementImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getSldTgt() {
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
    public boolean isSetSldTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setSldTgt(CTEmpty sldTgt) {
        this.generatedSetterHelperImpl((XmlObject)sldTgt, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewSldTgt() {
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
    public void unsetSldTgt() {
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
    public CTEmbeddedWAVAudioFile getSndTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmbeddedWAVAudioFile target = null;
            target = (CTEmbeddedWAVAudioFile)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSndTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setSndTgt(CTEmbeddedWAVAudioFile sndTgt) {
        this.generatedSetterHelperImpl((XmlObject)sndTgt, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmbeddedWAVAudioFile addNewSndTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmbeddedWAVAudioFile target = null;
            target = (CTEmbeddedWAVAudioFile)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSndTgt() {
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
    public CTTLShapeTargetElement getSpTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLShapeTargetElement target = null;
            target = (CTTLShapeTargetElement)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setSpTgt(CTTLShapeTargetElement spTgt) {
        this.generatedSetterHelperImpl(spTgt, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLShapeTargetElement addNewSpTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLShapeTargetElement target = null;
            target = (CTTLShapeTargetElement)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpTgt() {
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
    public CTTLSubShapeId getInkTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSubShapeId target = null;
            target = (CTTLSubShapeId)this.get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInkTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setInkTgt(CTTLSubShapeId inkTgt) {
        this.generatedSetterHelperImpl((XmlObject)inkTgt, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLSubShapeId addNewInkTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLSubShapeId target = null;
            target = (CTTLSubShapeId)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInkTgt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}

