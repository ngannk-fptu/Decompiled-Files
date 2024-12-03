/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class SimpleContentDocumentImpl
extends XmlComplexContentImpl
implements SimpleContentDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "simpleContent")};

    public SimpleContentDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SimpleContentDocument.SimpleContent getSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSimpleContent(SimpleContentDocument.SimpleContent simpleContent) {
        this.generatedSetterHelperImpl(simpleContent, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SimpleContentDocument.SimpleContent addNewSimpleContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class SimpleContentImpl
    extends AnnotatedImpl
    implements SimpleContentDocument.SimpleContent {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "restriction"), new QName("http://www.w3.org/2001/XMLSchema", "extension")};

        public SimpleContentImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SimpleRestrictionType getRestriction() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleRestrictionType target = null;
                target = (SimpleRestrictionType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
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
        public void setRestriction(SimpleRestrictionType restriction) {
            this.generatedSetterHelperImpl(restriction, PROPERTY_QNAME[0], 0, (short)1);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SimpleRestrictionType addNewRestriction() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleRestrictionType target = null;
                target = (SimpleRestrictionType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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
        public SimpleExtensionType getExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleExtensionType target = null;
                target = (SimpleExtensionType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
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
        public void setExtension(SimpleExtensionType extension) {
            this.generatedSetterHelperImpl(extension, PROPERTY_QNAME[1], 0, (short)1);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SimpleExtensionType addNewExtension() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleExtensionType target = null;
                target = (SimpleExtensionType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
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
    }
}

