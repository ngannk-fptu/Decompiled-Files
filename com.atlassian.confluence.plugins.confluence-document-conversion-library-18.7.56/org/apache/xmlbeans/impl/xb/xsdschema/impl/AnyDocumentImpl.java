/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.math.BigInteger;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.WildcardImpl;

public class AnyDocumentImpl
extends XmlComplexContentImpl
implements AnyDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "any")};

    public AnyDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyDocument.Any getAny() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyDocument.Any target = null;
            target = (AnyDocument.Any)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAny(AnyDocument.Any any) {
        this.generatedSetterHelperImpl(any, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyDocument.Any addNewAny() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyDocument.Any target = null;
            target = (AnyDocument.Any)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class AnyImpl
    extends WildcardImpl
    implements AnyDocument.Any {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "minOccurs"), new QName("", "maxOccurs")};

        public AnyImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BigInteger getMinOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[0]));
                }
                return target == null ? null : target.getBigIntegerValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlNonNegativeInteger xgetMinOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlNonNegativeInteger target = null;
                target = (XmlNonNegativeInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (XmlNonNegativeInteger)this.get_default_attribute_value(PROPERTY_QNAME[0]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetMinOccurs() {
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
        public void setMinOccurs(BigInteger minOccurs) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.setBigIntegerValue(minOccurs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetMinOccurs(XmlNonNegativeInteger minOccurs) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlNonNegativeInteger target = null;
                target = (XmlNonNegativeInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (XmlNonNegativeInteger)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.set(minOccurs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetMinOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[0]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object getMaxOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[1]));
                }
                return target == null ? null : target.getObjectValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AllNNI xgetMaxOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AllNNI target = null;
                target = (AllNNI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (AllNNI)this.get_default_attribute_value(PROPERTY_QNAME[1]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetMaxOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setMaxOccurs(Object maxOccurs) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.setObjectValue(maxOccurs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetMaxOccurs(AllNNI maxOccurs) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AllNNI target = null;
                target = (AllNNI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (AllNNI)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.set(maxOccurs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetMaxOccurs() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }
    }
}

