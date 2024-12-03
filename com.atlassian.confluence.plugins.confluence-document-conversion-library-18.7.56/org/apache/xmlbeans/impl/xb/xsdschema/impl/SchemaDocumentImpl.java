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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xmlschema.LangAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.BlockSet;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.FullDerivationSet;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.OpenAttrsImpl;

public class SchemaDocumentImpl
extends XmlComplexContentImpl
implements SchemaDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "schema")};

    public SchemaDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SchemaDocument.Schema getSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SchemaDocument.Schema target = null;
            target = (SchemaDocument.Schema)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSchema(SchemaDocument.Schema schema) {
        this.generatedSetterHelperImpl(schema, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SchemaDocument.Schema addNewSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SchemaDocument.Schema target = null;
            target = (SchemaDocument.Schema)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class SchemaImpl
    extends OpenAttrsImpl
    implements SchemaDocument.Schema {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "include"), new QName("http://www.w3.org/2001/XMLSchema", "import"), new QName("http://www.w3.org/2001/XMLSchema", "redefine"), new QName("http://www.w3.org/2001/XMLSchema", "annotation"), new QName("http://www.w3.org/2001/XMLSchema", "simpleType"), new QName("http://www.w3.org/2001/XMLSchema", "complexType"), new QName("http://www.w3.org/2001/XMLSchema", "group"), new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup"), new QName("http://www.w3.org/2001/XMLSchema", "element"), new QName("http://www.w3.org/2001/XMLSchema", "attribute"), new QName("http://www.w3.org/2001/XMLSchema", "notation"), new QName("", "targetNamespace"), new QName("", "version"), new QName("", "finalDefault"), new QName("", "blockDefault"), new QName("", "attributeFormDefault"), new QName("", "elementFormDefault"), new QName("", "id"), new QName("http://www.w3.org/XML/1998/namespace", "lang")};

        public SchemaImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<IncludeDocument.Include> getIncludeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<IncludeDocument.Include>(this::getIncludeArray, this::setIncludeArray, this::insertNewInclude, this::removeInclude, this::sizeOfIncludeArray);
            }
        }

        @Override
        public IncludeDocument.Include[] getIncludeArray() {
            return (IncludeDocument.Include[])this.getXmlObjectArray(PROPERTY_QNAME[0], new IncludeDocument.Include[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IncludeDocument.Include getIncludeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
        public int sizeOfIncludeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setIncludeArray(IncludeDocument.Include[] includeArray) {
            this.check_orphaned();
            this.arraySetterHelper(includeArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setIncludeArray(int i, IncludeDocument.Include include) {
            this.generatedSetterHelperImpl(include, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IncludeDocument.Include insertNewInclude(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IncludeDocument.Include addNewInclude() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeInclude(int i) {
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
        public List<ImportDocument.Import> getImportList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<ImportDocument.Import>(this::getImportArray, this::setImportArray, this::insertNewImport, this::removeImport, this::sizeOfImportArray);
            }
        }

        @Override
        public ImportDocument.Import[] getImportArray() {
            return (ImportDocument.Import[])this.getXmlObjectArray(PROPERTY_QNAME[1], new ImportDocument.Import[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ImportDocument.Import getImportArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ImportDocument.Import target = null;
                target = (ImportDocument.Import)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
        public int sizeOfImportArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]);
            }
        }

        @Override
        public void setImportArray(ImportDocument.Import[] ximportArray) {
            this.check_orphaned();
            this.arraySetterHelper(ximportArray, PROPERTY_QNAME[1]);
        }

        @Override
        public void setImportArray(int i, ImportDocument.Import ximport) {
            this.generatedSetterHelperImpl(ximport, PROPERTY_QNAME[1], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ImportDocument.Import insertNewImport(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                ImportDocument.Import target = null;
                target = (ImportDocument.Import)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
                return target;
            }
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
                target = (ImportDocument.Import)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeImport(int i) {
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
        public List<RedefineDocument.Redefine> getRedefineList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<RedefineDocument.Redefine>(this::getRedefineArray, this::setRedefineArray, this::insertNewRedefine, this::removeRedefine, this::sizeOfRedefineArray);
            }
        }

        @Override
        public RedefineDocument.Redefine[] getRedefineArray() {
            return (RedefineDocument.Redefine[])this.getXmlObjectArray(PROPERTY_QNAME[2], new RedefineDocument.Redefine[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public RedefineDocument.Redefine getRedefineArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                RedefineDocument.Redefine target = null;
                target = (RedefineDocument.Redefine)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
        public int sizeOfRedefineArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[2]);
            }
        }

        @Override
        public void setRedefineArray(RedefineDocument.Redefine[] redefineArray) {
            this.check_orphaned();
            this.arraySetterHelper(redefineArray, PROPERTY_QNAME[2]);
        }

        @Override
        public void setRedefineArray(int i, RedefineDocument.Redefine redefine) {
            this.generatedSetterHelperImpl(redefine, PROPERTY_QNAME[2], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public RedefineDocument.Redefine insertNewRedefine(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                RedefineDocument.Redefine target = null;
                target = (RedefineDocument.Redefine)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
                return target;
            }
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
                target = (RedefineDocument.Redefine)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeRedefine(int i) {
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
        public List<AnnotationDocument.Annotation> getAnnotationList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<AnnotationDocument.Annotation>(this::getAnnotationArray, this::setAnnotationArray, this::insertNewAnnotation, this::removeAnnotation, this::sizeOfAnnotationArray);
            }
        }

        @Override
        public AnnotationDocument.Annotation[] getAnnotationArray() {
            return (AnnotationDocument.Annotation[])this.getXmlObjectArray(PROPERTY_QNAME[3], new AnnotationDocument.Annotation[0]);
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
                target = (AnnotationDocument.Annotation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
                return this.get_store().count_elements(PROPERTY_QNAME[3]);
            }
        }

        @Override
        public void setAnnotationArray(AnnotationDocument.Annotation[] annotationArray) {
            this.check_orphaned();
            this.arraySetterHelper(annotationArray, PROPERTY_QNAME[3]);
        }

        @Override
        public void setAnnotationArray(int i, AnnotationDocument.Annotation annotation) {
            this.generatedSetterHelperImpl(annotation, PROPERTY_QNAME[3], i, (short)2);
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
                target = (AnnotationDocument.Annotation)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
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
                target = (AnnotationDocument.Annotation)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
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
                this.get_store().remove_element(PROPERTY_QNAME[3], i);
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
            return (TopLevelSimpleType[])this.getXmlObjectArray(PROPERTY_QNAME[4], new TopLevelSimpleType[0]);
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
                target = (TopLevelSimpleType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
                return this.get_store().count_elements(PROPERTY_QNAME[4]);
            }
        }

        @Override
        public void setSimpleTypeArray(TopLevelSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, PROPERTY_QNAME[4]);
        }

        @Override
        public void setSimpleTypeArray(int i, TopLevelSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, PROPERTY_QNAME[4], i, (short)2);
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
                target = (TopLevelSimpleType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
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
                target = (TopLevelSimpleType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
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
                this.get_store().remove_element(PROPERTY_QNAME[4], i);
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
            return (TopLevelComplexType[])this.getXmlObjectArray(PROPERTY_QNAME[5], new TopLevelComplexType[0]);
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
                target = (TopLevelComplexType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
                return this.get_store().count_elements(PROPERTY_QNAME[5]);
            }
        }

        @Override
        public void setComplexTypeArray(TopLevelComplexType[] complexTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(complexTypeArray, PROPERTY_QNAME[5]);
        }

        @Override
        public void setComplexTypeArray(int i, TopLevelComplexType complexType) {
            this.generatedSetterHelperImpl(complexType, PROPERTY_QNAME[5], i, (short)2);
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
                target = (TopLevelComplexType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
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
                target = (TopLevelComplexType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
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
                this.get_store().remove_element(PROPERTY_QNAME[5], i);
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
            return (NamedGroup[])this.getXmlObjectArray(PROPERTY_QNAME[6], new NamedGroup[0]);
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
                target = (NamedGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
                return this.get_store().count_elements(PROPERTY_QNAME[6]);
            }
        }

        @Override
        public void setGroupArray(NamedGroup[] groupArray) {
            this.check_orphaned();
            this.arraySetterHelper(groupArray, PROPERTY_QNAME[6]);
        }

        @Override
        public void setGroupArray(int i, NamedGroup group) {
            this.generatedSetterHelperImpl(group, PROPERTY_QNAME[6], i, (short)2);
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
                target = (NamedGroup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
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
                target = (NamedGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
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
                this.get_store().remove_element(PROPERTY_QNAME[6], i);
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
            return (NamedAttributeGroup[])this.getXmlObjectArray(PROPERTY_QNAME[7], new NamedAttributeGroup[0]);
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
                target = (NamedAttributeGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
                return this.get_store().count_elements(PROPERTY_QNAME[7]);
            }
        }

        @Override
        public void setAttributeGroupArray(NamedAttributeGroup[] attributeGroupArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeGroupArray, PROPERTY_QNAME[7]);
        }

        @Override
        public void setAttributeGroupArray(int i, NamedAttributeGroup attributeGroup) {
            this.generatedSetterHelperImpl(attributeGroup, PROPERTY_QNAME[7], i, (short)2);
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
                target = (NamedAttributeGroup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
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
                target = (NamedAttributeGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
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
                this.get_store().remove_element(PROPERTY_QNAME[7], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<TopLevelElement> getElementList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<TopLevelElement>(this::getElementArray, this::setElementArray, this::insertNewElement, this::removeElement, this::sizeOfElementArray);
            }
        }

        @Override
        public TopLevelElement[] getElementArray() {
            return (TopLevelElement[])this.getXmlObjectArray(PROPERTY_QNAME[8], new TopLevelElement[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelElement getElementArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
        public int sizeOfElementArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[8]);
            }
        }

        @Override
        public void setElementArray(TopLevelElement[] elementArray) {
            this.check_orphaned();
            this.arraySetterHelper(elementArray, PROPERTY_QNAME[8]);
        }

        @Override
        public void setElementArray(int i, TopLevelElement element) {
            this.generatedSetterHelperImpl(element, PROPERTY_QNAME[8], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelElement insertNewElement(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelElement addNewElement() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElement(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[8], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<TopLevelAttribute> getAttributeList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<TopLevelAttribute>(this::getAttributeArray, this::setAttributeArray, this::insertNewAttribute, this::removeAttribute, this::sizeOfAttributeArray);
            }
        }

        @Override
        public TopLevelAttribute[] getAttributeArray() {
            return (TopLevelAttribute[])this.getXmlObjectArray(PROPERTY_QNAME[9], new TopLevelAttribute[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelAttribute getAttributeArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
        public int sizeOfAttributeArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[9]);
            }
        }

        @Override
        public void setAttributeArray(TopLevelAttribute[] attributeArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeArray, PROPERTY_QNAME[9]);
        }

        @Override
        public void setAttributeArray(int i, TopLevelAttribute attribute) {
            this.generatedSetterHelperImpl(attribute, PROPERTY_QNAME[9], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelAttribute insertNewAttribute(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopLevelAttribute addNewAttribute() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeAttribute(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[9], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<NotationDocument.Notation> getNotationList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<NotationDocument.Notation>(this::getNotationArray, this::setNotationArray, this::insertNewNotation, this::removeNotation, this::sizeOfNotationArray);
            }
        }

        @Override
        public NotationDocument.Notation[] getNotationArray() {
            return (NotationDocument.Notation[])this.getXmlObjectArray(PROPERTY_QNAME[10], new NotationDocument.Notation[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NotationDocument.Notation getNotationArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
        public int sizeOfNotationArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[10]);
            }
        }

        @Override
        public void setNotationArray(NotationDocument.Notation[] notationArray) {
            this.check_orphaned();
            this.arraySetterHelper(notationArray, PROPERTY_QNAME[10]);
        }

        @Override
        public void setNotationArray(int i, NotationDocument.Notation notation) {
            this.generatedSetterHelperImpl(notation, PROPERTY_QNAME[10], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NotationDocument.Notation insertNewNotation(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public NotationDocument.Notation addNewNotation() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeNotation(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[10], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getTargetNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlAnyURI xgetTargetNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetTargetNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[11]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setTargetNamespace(String targetNamespace) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
                }
                target.setStringValue(targetNamespace);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetTargetNamespace(XmlAnyURI targetNamespace) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[11]));
                if (target == null) {
                    target = (XmlAnyURI)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[11]));
                }
                target.set(targetNamespace);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetTargetNamespace() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[11]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getVersion() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlToken xgetVersion() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetVersion() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[12]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setVersion(String version) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
                }
                target.setStringValue(version);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetVersion(XmlToken version) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[12]));
                if (target == null) {
                    target = (XmlToken)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[12]));
                }
                target.set(version);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetVersion() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[12]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object getFinalDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[13]));
                }
                return target == null ? null : target.getObjectValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FullDerivationSet xgetFinalDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FullDerivationSet target = null;
                target = (FullDerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
                if (target == null) {
                    target = (FullDerivationSet)this.get_default_attribute_value(PROPERTY_QNAME[13]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetFinalDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[13]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setFinalDefault(Object finalDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
                }
                target.setObjectValue(finalDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetFinalDefault(FullDerivationSet finalDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FullDerivationSet target = null;
                target = (FullDerivationSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
                if (target == null) {
                    target = (FullDerivationSet)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
                }
                target.set(finalDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetFinalDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[13]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object getBlockDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[14]));
                }
                return target == null ? null : target.getObjectValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BlockSet xgetBlockDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                BlockSet target = null;
                target = (BlockSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
                if (target == null) {
                    target = (BlockSet)this.get_default_attribute_value(PROPERTY_QNAME[14]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetBlockDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[14]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setBlockDefault(Object blockDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[14]));
                }
                target.setObjectValue(blockDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetBlockDefault(BlockSet blockDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                BlockSet target = null;
                target = (BlockSet)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[14]));
                if (target == null) {
                    target = (BlockSet)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[14]));
                }
                target.set(blockDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetBlockDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[14]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FormChoice.Enum getAttributeFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[15]));
                }
                return target == null ? null : (FormChoice.Enum)target.getEnumValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FormChoice xgetAttributeFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
                if (target == null) {
                    target = (FormChoice)this.get_default_attribute_value(PROPERTY_QNAME[15]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetAttributeFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[15]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setAttributeFormDefault(FormChoice.Enum attributeFormDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[15]));
                }
                target.setEnumValue(attributeFormDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetAttributeFormDefault(FormChoice attributeFormDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[15]));
                if (target == null) {
                    target = (FormChoice)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[15]));
                }
                target.set(attributeFormDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetAttributeFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[15]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FormChoice.Enum getElementFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[16]));
                }
                return target == null ? null : (FormChoice.Enum)target.getEnumValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FormChoice xgetElementFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
                if (target == null) {
                    target = (FormChoice)this.get_default_attribute_value(PROPERTY_QNAME[16]);
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetElementFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[16]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElementFormDefault(FormChoice.Enum elementFormDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[16]));
                }
                target.setEnumValue(elementFormDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetElementFormDefault(FormChoice elementFormDefault) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[16]));
                if (target == null) {
                    target = (FormChoice)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[16]));
                }
                target.set(elementFormDefault);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetElementFormDefault() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[16]);
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
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
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
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
                return this.get_store().find_attribute_user(PROPERTY_QNAME[17]) != null;
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
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[17]));
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
                target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[17]));
                if (target == null) {
                    target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[17]));
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
                this.get_store().remove_attribute(PROPERTY_QNAME[17]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getLang() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LangAttribute.Lang xgetLang() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                LangAttribute.Lang target = null;
                target = (LangAttribute.Lang)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetLang() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[18]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setLang(String lang) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[18]));
                }
                target.setStringValue(lang);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetLang(LangAttribute.Lang lang) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                LangAttribute.Lang target = null;
                target = (LangAttribute.Lang)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[18]));
                if (target == null) {
                    target = (LangAttribute.Lang)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[18]));
                }
                target.set(lang);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetLang() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[18]);
            }
        }
    }
}

