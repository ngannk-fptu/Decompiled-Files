/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBlipBullet;

public class CTTextBlipBulletImpl
extends XmlComplexContentImpl
implements CTTextBlipBullet {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blip")};

    public CTTextBlipBulletImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlip getBlip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlip target = null;
            target = (CTBlip)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setBlip(CTBlip blip) {
        this.generatedSetterHelperImpl(blip, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlip addNewBlip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlip target = null;
            target = (CTBlip)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}

