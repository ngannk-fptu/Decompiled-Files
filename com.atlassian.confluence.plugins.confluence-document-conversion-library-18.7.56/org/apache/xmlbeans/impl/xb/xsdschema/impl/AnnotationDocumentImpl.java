/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.AppinfoDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.OpenAttrsImpl;

public class AnnotationDocumentImpl
extends XmlComplexContentImpl
implements AnnotationDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "annotation")};

    public AnnotationDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnnotationDocument.Annotation getAnnotation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnnotationDocument.Annotation target = null;
            target = (AnnotationDocument.Annotation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAnnotation(AnnotationDocument.Annotation annotation) {
        this.generatedSetterHelperImpl(annotation, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnnotationDocument.Annotation addNewAnnotation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnnotationDocument.Annotation target = null;
            target = (AnnotationDocument.Annotation)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class AnnotationImpl
    extends OpenAttrsImpl
    implements AnnotationDocument.Annotation {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "appinfo"), new QName("http://www.w3.org/2001/XMLSchema", "documentation"), new QName("", "id")};

        public AnnotationImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<AppinfoDocument.Appinfo> getAppinfoList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<AppinfoDocument.Appinfo>(this::getAppinfoArray, this::setAppinfoArray, this::insertNewAppinfo, this::removeAppinfo, this::sizeOfAppinfoArray);
            }
        }

        @Override
        public AppinfoDocument.Appinfo[] getAppinfoArray() {
            return (AppinfoDocument.Appinfo[])this.getXmlObjectArray(PROPERTY_QNAME[0], new AppinfoDocument.Appinfo[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AppinfoDocument.Appinfo getAppinfoArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
        public int sizeOfAppinfoArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setAppinfoArray(AppinfoDocument.Appinfo[] appinfoArray) {
            this.check_orphaned();
            this.arraySetterHelper(appinfoArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setAppinfoArray(int i, AppinfoDocument.Appinfo appinfo) {
            this.generatedSetterHelperImpl(appinfo, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AppinfoDocument.Appinfo insertNewAppinfo(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AppinfoDocument.Appinfo addNewAppinfo() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AppinfoDocument.Appinfo target = null;
                target = (AppinfoDocument.Appinfo)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeAppinfo(int i) {
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
        public List<DocumentationDocument.Documentation> getDocumentationList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<DocumentationDocument.Documentation>(this::getDocumentationArray, this::setDocumentationArray, this::insertNewDocumentation, this::removeDocumentation, this::sizeOfDocumentationArray);
            }
        }

        @Override
        public DocumentationDocument.Documentation[] getDocumentationArray() {
            return (DocumentationDocument.Documentation[])this.getXmlObjectArray(PROPERTY_QNAME[1], new DocumentationDocument.Documentation[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DocumentationDocument.Documentation getDocumentationArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
        public int sizeOfDocumentationArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]);
            }
        }

        @Override
        public void setDocumentationArray(DocumentationDocument.Documentation[] documentationArray) {
            this.check_orphaned();
            this.arraySetterHelper(documentationArray, PROPERTY_QNAME[1]);
        }

        @Override
        public void setDocumentationArray(int i, DocumentationDocument.Documentation documentation) {
            this.generatedSetterHelperImpl(documentation, PROPERTY_QNAME[1], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DocumentationDocument.Documentation insertNewDocumentation(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DocumentationDocument.Documentation addNewDocumentation() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                DocumentationDocument.Documentation target = null;
                target = (DocumentationDocument.Documentation)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeDocumentation(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[1], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getId() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlID xgetId() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetId() {
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
        public void setId(String id) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.setStringValue(id);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetId(XmlID id) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.set(id);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetId() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[2]);
            }
        }
    }
}

