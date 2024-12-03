/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.OpenAttrsImpl;

public class RedefineDocumentImpl
extends XmlComplexContentImpl
implements RedefineDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "redefine")};

    public RedefineDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RedefineDocument.Redefine getRedefine() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RedefineDocument.Redefine target = null;
            target = (RedefineDocument.Redefine)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setRedefine(RedefineDocument.Redefine redefine) {
        this.generatedSetterHelperImpl(redefine, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RedefineDocument.Redefine addNewRedefine() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RedefineDocument.Redefine target = null;
            target = (RedefineDocument.Redefine)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class RedefineImpl
    extends OpenAttrsImpl
    implements RedefineDocument.Redefine {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "annotation"), new QName("http://www.w3.org/2001/XMLSchema", "simpleType"), new QName("http://www.w3.org/2001/XMLSchema", "complexType"), new QName("http://www.w3.org/2001/XMLSchema", "group"), new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup"), new QName("", "schemaLocation"), new QName("", "id")};

        public RedefineImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<AnnotationDocument.Annotation> getAnnotationList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<AnnotationDocument.Annotation>(this::getAnnotationArray, this::setAnnotationArray, this::insertNewAnnotation, this::removeAnnotation, this::sizeOfAnnotationArray);
            }
        }

        @Override
        public AnnotationDocument.Annotation[] getAnnotationArray() {
            return (AnnotationDocument.Annotation[])this.getXmlObjectArray(PROPERTY_QNAME[0], new AnnotationDocument.Annotation[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AnnotationDocument.Annotation getAnnotationArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
        public int sizeOfAnnotationArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setAnnotationArray(AnnotationDocument.Annotation[] annotationArray) {
            this.check_orphaned();
            this.arraySetterHelper(annotationArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setAnnotationArray(int i, AnnotationDocument.Annotation annotation) {
            this.generatedSetterHelperImpl(annotation, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AnnotationDocument.Annotation insertNewAnnotation(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
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

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeAnnotation(int i) {
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
        public List<TopLevelSimpleType> getSimpleTypeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<TopLevelSimpleType>(this::getSimpleTypeArray, this::setSimpleTypeArray, this::insertNewSimpleType, this::removeSimpleType, this::sizeOfSimpleTypeArray);
            }
        }

        @Override
        public TopLevelSimpleType[] getSimpleTypeArray() {
            return (TopLevelSimpleType[])this.getXmlObjectArray(PROPERTY_QNAME[1], new TopLevelSimpleType[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelSimpleType getSimpleTypeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
        public int sizeOfSimpleTypeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]);
            }
        }

        @Override
        public void setSimpleTypeArray(TopLevelSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, PROPERTY_QNAME[1]);
        }

        @Override
        public void setSimpleTypeArray(int i, TopLevelSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, PROPERTY_QNAME[1], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelSimpleType insertNewSimpleType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelSimpleType addNewSimpleType() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeSimpleType(int i) {
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
        public List<TopLevelComplexType> getComplexTypeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<TopLevelComplexType>(this::getComplexTypeArray, this::setComplexTypeArray, this::insertNewComplexType, this::removeComplexType, this::sizeOfComplexTypeArray);
            }
        }

        @Override
        public TopLevelComplexType[] getComplexTypeArray() {
            return (TopLevelComplexType[])this.getXmlObjectArray(PROPERTY_QNAME[2], new TopLevelComplexType[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelComplexType getComplexTypeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
        public int sizeOfComplexTypeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[2]);
            }
        }

        @Override
        public void setComplexTypeArray(TopLevelComplexType[] complexTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(complexTypeArray, PROPERTY_QNAME[2]);
        }

        @Override
        public void setComplexTypeArray(int i, TopLevelComplexType complexType) {
            this.generatedSetterHelperImpl(complexType, PROPERTY_QNAME[2], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelComplexType insertNewComplexType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelComplexType addNewComplexType() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeComplexType(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[2], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<NamedGroup> getGroupList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<NamedGroup>(this::getGroupArray, this::setGroupArray, this::insertNewGroup, this::removeGroup, this::sizeOfGroupArray);
            }
        }

        @Override
        public NamedGroup[] getGroupArray() {
            return (NamedGroup[])this.getXmlObjectArray(PROPERTY_QNAME[3], new NamedGroup[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedGroup getGroupArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
        public int sizeOfGroupArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[3]);
            }
        }

        @Override
        public void setGroupArray(NamedGroup[] groupArray) {
            this.check_orphaned();
            this.arraySetterHelper(groupArray, PROPERTY_QNAME[3]);
        }

        @Override
        public void setGroupArray(int i, NamedGroup group) {
            this.generatedSetterHelperImpl(group, PROPERTY_QNAME[3], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedGroup insertNewGroup(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedGroup addNewGroup() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeGroup(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[3], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<NamedAttributeGroup> getAttributeGroupList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<NamedAttributeGroup>(this::getAttributeGroupArray, this::setAttributeGroupArray, this::insertNewAttributeGroup, this::removeAttributeGroup, this::sizeOfAttributeGroupArray);
            }
        }

        @Override
        public NamedAttributeGroup[] getAttributeGroupArray() {
            return (NamedAttributeGroup[])this.getXmlObjectArray(PROPERTY_QNAME[4], new NamedAttributeGroup[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedAttributeGroup getAttributeGroupArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
        public int sizeOfAttributeGroupArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[4]);
            }
        }

        @Override
        public void setAttributeGroupArray(NamedAttributeGroup[] attributeGroupArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeGroupArray, PROPERTY_QNAME[4]);
        }

        @Override
        public void setAttributeGroupArray(int i, NamedAttributeGroup attributeGroup) {
            this.generatedSetterHelperImpl(attributeGroup, PROPERTY_QNAME[4], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedAttributeGroup insertNewAttributeGroup(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NamedAttributeGroup addNewAttributeGroup() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeAttributeGroup(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[4], i);
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
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
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
                return target;
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
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
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
                if (target == null) {
                    target = (XmlAnyURI)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
                }
                target.set(schemaLocation);
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
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
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
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
                return this.get_store().find_attribute_user(PROPERTY_QNAME[6]) != null;
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
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
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
                if (target == null) {
                    target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
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
                this.get_store().remove_attribute(PROPERTY_QNAME[6]);
            }
        }
    }
}

