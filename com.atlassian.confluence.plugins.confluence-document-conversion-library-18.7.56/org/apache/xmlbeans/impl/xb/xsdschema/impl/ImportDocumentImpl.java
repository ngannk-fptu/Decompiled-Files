/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnnotatedImpl;

public class ImportDocumentImpl
extends XmlComplexContentImpl
implements ImportDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "import")};

    public ImportDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImportDocument.Import getImport() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ImportDocument.Import target = null;
            target = (ImportDocument.Import)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setImport(ImportDocument.Import ximport) {
        this.generatedSetterHelperImpl(ximport, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImportDocument.Import addNewImport() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ImportDocument.Import target = null;
            target = (ImportDocument.Import)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class ImportImpl
    extends AnnotatedImpl
    implements ImportDocument.Import {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "namespace"), new QName("", "schemaLocation")};

        public ImportImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlAnyURI xgetNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetNamespace() {
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
        public void setNamespace(String namespace) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.setStringValue(namespace);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetNamespace(XmlAnyURI namespace) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                if (target == null) {
                    target = (XmlAnyURI)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                }
                target.set(namespace);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetNamespace() {
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
        public String getSchemaLocation() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlAnyURI xgetSchemaLocation() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetSchemaLocation() {
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
        public void setSchemaLocation(String schemaLocation) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.setStringValue(schemaLocation);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetSchemaLocation(XmlAnyURI schemaLocation) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (XmlAnyURI)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.set(schemaLocation);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetSchemaLocation() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }
    }
}

