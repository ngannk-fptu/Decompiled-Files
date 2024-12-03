/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdownload.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemasDocument;

public class DownloadedSchemasDocumentImpl
extends XmlComplexContentImpl
implements DownloadedSchemasDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "downloaded-schemas")};

    public DownloadedSchemasDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DownloadedSchemasDocument.DownloadedSchemas getDownloadedSchemas() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DownloadedSchemasDocument.DownloadedSchemas target = null;
            target = (DownloadedSchemasDocument.DownloadedSchemas)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDownloadedSchemas(DownloadedSchemasDocument.DownloadedSchemas downloadedSchemas) {
        this.generatedSetterHelperImpl(downloadedSchemas, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DownloadedSchemasDocument.DownloadedSchemas addNewDownloadedSchemas() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DownloadedSchemasDocument.DownloadedSchemas target = null;
            target = (DownloadedSchemasDocument.DownloadedSchemas)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class DownloadedSchemasImpl
    extends XmlComplexContentImpl
    implements DownloadedSchemasDocument.DownloadedSchemas {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.bea.com/2003/01/xmlbean/xsdownload", "entry"), new QName("", "defaultDirectory")};

        public DownloadedSchemasImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<DownloadedSchemaEntry> getEntryList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<DownloadedSchemaEntry>(this::getEntryArray, this::setEntryArray, this::insertNewEntry, this::removeEntry, this::sizeOfEntryArray);
            }
        }

        @Override
        public DownloadedSchemaEntry[] getEntryArray() {
            return (DownloadedSchemaEntry[])this.getXmlObjectArray(PROPERTY_QNAME[0], new DownloadedSchemaEntry[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DownloadedSchemaEntry getEntryArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int sizeOfEntryArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setEntryArray(DownloadedSchemaEntry[] entryArray) {
            this.check_orphaned();
            this.arraySetterHelper(entryArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setEntryArray(int i, DownloadedSchemaEntry entry) {
            this.generatedSetterHelperImpl(entry, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DownloadedSchemaEntry insertNewEntry(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DownloadedSchemaEntry addNewEntry() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DownloadedSchemaEntry target = null;
                target = (DownloadedSchemaEntry)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeEntry(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[0], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getDefaultDirectory() {
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
        public XmlToken xgetDefaultDirectory() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetDefaultDirectory() {
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
        public void setDefaultDirectory(String defaultDirectory) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.setStringValue(defaultDirectory);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetDefaultDirectory(XmlToken defaultDirectory) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                if (target == null) {
                    target = (XmlToken)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                }
                target.set(defaultDirectory);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetDefaultDirectory() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }
    }
}

