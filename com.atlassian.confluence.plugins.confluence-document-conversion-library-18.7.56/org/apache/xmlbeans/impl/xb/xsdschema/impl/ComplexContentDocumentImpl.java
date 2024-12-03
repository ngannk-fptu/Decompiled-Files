/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class ComplexContentDocumentImpl
extends XmlComplexContentImpl
implements ComplexContentDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "complexContent")};

    public ComplexContentDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ComplexContentDocument.ComplexContent getComplexContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setComplexContent(ComplexContentDocument.ComplexContent complexContent) {
        this.generatedSetterHelperImpl(complexContent, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ComplexContentDocument.ComplexContent addNewComplexContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class ComplexContentImpl
    extends AnnotatedImpl
    implements ComplexContentDocument.ComplexContent {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "restriction"), new QName("http://www.w3.org/2001/XMLSchema", "extension"), new QName("", "mixed")};

        public ComplexContentImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ComplexRestrictionType getRestriction() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ComplexRestrictionType target = null;
                target = (ComplexRestrictionType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
                return target == null ? null : target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetRestriction() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
            }
        }

        @Override
        public void setRestriction(ComplexRestrictionType restriction) {
            this.generatedSetterHelperImpl(restriction, PROPERTY_QNAME[0], 0, (short)1);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ComplexRestrictionType addNewRestriction() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ComplexRestrictionType target = null;
                target = (ComplexRestrictionType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetRestriction() {
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
        public ExtensionType getExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ExtensionType target = null;
                target = (ExtensionType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
                return target == null ? null : target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
            }
        }

        @Override
        public void setExtension(ExtensionType extension) {
            this.generatedSetterHelperImpl(extension, PROPERTY_QNAME[1], 0, (short)1);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ExtensionType addNewExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ExtensionType target = null;
                target = (ExtensionType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetExtension() {
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
        public boolean getMixed() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target == null ? false : target.getBooleanValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlBoolean xgetMixed() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlBoolean target = null;
                target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetMixed() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setMixed(boolean mixed) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.setBooleanValue(mixed);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetMixed(XmlBoolean mixed) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlBoolean target = null;
                target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.set(mixed);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetMixed() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[2]);
            }
        }
    }
}

