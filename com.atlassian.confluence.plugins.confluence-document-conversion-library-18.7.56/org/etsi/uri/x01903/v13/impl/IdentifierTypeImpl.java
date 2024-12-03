/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.QualifierType
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaUriHolderEx;
import org.etsi.uri.x01903.v13.IdentifierType;
import org.etsi.uri.x01903.v13.QualifierType;

public class IdentifierTypeImpl
extends JavaUriHolderEx
implements IdentifierType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "Qualifier")};

    public IdentifierTypeImpl(SchemaType sType) {
        super(sType, true);
    }

    protected IdentifierTypeImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QualifierType.Enum getQualifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : (QualifierType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QualifierType xgetQualifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            QualifierType target = null;
            target = (QualifierType)this.get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetQualifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setQualifier(QualifierType.Enum qualifier) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setEnumValue(qualifier);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetQualifier(QualifierType qualifier) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            QualifierType target = null;
            target = (QualifierType)this.get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (QualifierType)this.get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set((XmlObject)qualifier);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetQualifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }
}

