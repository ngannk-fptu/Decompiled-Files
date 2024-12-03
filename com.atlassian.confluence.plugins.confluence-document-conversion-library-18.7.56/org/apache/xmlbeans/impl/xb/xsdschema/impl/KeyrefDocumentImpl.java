/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.KeybaseImpl;

public class KeyrefDocumentImpl
extends XmlComplexContentImpl
implements KeyrefDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "keyref")};

    public KeyrefDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public KeyrefDocument.Keyref getKeyref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            KeyrefDocument.Keyref target = null;
            target = (KeyrefDocument.Keyref)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setKeyref(KeyrefDocument.Keyref keyref) {
        this.generatedSetterHelperImpl(keyref, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public KeyrefDocument.Keyref addNewKeyref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            KeyrefDocument.Keyref target = null;
            target = (KeyrefDocument.Keyref)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class KeyrefImpl
    extends KeybaseImpl
    implements KeyrefDocument.Keyref {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "refer")};

        public KeyrefImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public QName getRefer() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                return target == null ? null : target.getQNameValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlQName xgetRefer() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setRefer(QName refer) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.setQNameValue(refer);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetRefer(XmlQName refer) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (XmlQName)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.set(refer);
            }
        }
    }
}

