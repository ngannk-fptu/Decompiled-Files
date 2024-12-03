/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.OpenAttrs;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface RedefineDocument
extends XmlObject {
    public static final DocumentFactory<RedefineDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "redefine3f55doctype");
    public static final SchemaType type = Factory.getType();

    public Redefine getRedefine();

    public void setRedefine(Redefine var1);

    public Redefine addNewRedefine();

    public static interface Redefine
    extends OpenAttrs {
        public static final ElementFactory<Redefine> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "redefine9e9felemtype");
        public static final SchemaType type = Factory.getType();

        public List<AnnotationDocument.Annotation> getAnnotationList();

        public AnnotationDocument.Annotation[] getAnnotationArray();

        public AnnotationDocument.Annotation getAnnotationArray(int var1);

        public int sizeOfAnnotationArray();

        public void setAnnotationArray(AnnotationDocument.Annotation[] var1);

        public void setAnnotationArray(int var1, AnnotationDocument.Annotation var2);

        public AnnotationDocument.Annotation insertNewAnnotation(int var1);

        public AnnotationDocument.Annotation addNewAnnotation();

        public void removeAnnotation(int var1);

        public List<TopLevelSimpleType> getSimpleTypeList();

        public TopLevelSimpleType[] getSimpleTypeArray();

        public TopLevelSimpleType getSimpleTypeArray(int var1);

        public int sizeOfSimpleTypeArray();

        public void setSimpleTypeArray(TopLevelSimpleType[] var1);

        public void setSimpleTypeArray(int var1, TopLevelSimpleType var2);

        public TopLevelSimpleType insertNewSimpleType(int var1);

        public TopLevelSimpleType addNewSimpleType();

        public void removeSimpleType(int var1);

        public List<TopLevelComplexType> getComplexTypeList();

        public TopLevelComplexType[] getComplexTypeArray();

        public TopLevelComplexType getComplexTypeArray(int var1);

        public int sizeOfComplexTypeArray();

        public void setComplexTypeArray(TopLevelComplexType[] var1);

        public void setComplexTypeArray(int var1, TopLevelComplexType var2);

        public TopLevelComplexType insertNewComplexType(int var1);

        public TopLevelComplexType addNewComplexType();

        public void removeComplexType(int var1);

        public List<NamedGroup> getGroupList();

        public NamedGroup[] getGroupArray();

        public NamedGroup getGroupArray(int var1);

        public int sizeOfGroupArray();

        public void setGroupArray(NamedGroup[] var1);

        public void setGroupArray(int var1, NamedGroup var2);

        public NamedGroup insertNewGroup(int var1);

        public NamedGroup addNewGroup();

        public void removeGroup(int var1);

        public List<NamedAttributeGroup> getAttributeGroupList();

        public NamedAttributeGroup[] getAttributeGroupArray();

        public NamedAttributeGroup getAttributeGroupArray(int var1);

        public int sizeOfAttributeGroupArray();

        public void setAttributeGroupArray(NamedAttributeGroup[] var1);

        public void setAttributeGroupArray(int var1, NamedAttributeGroup var2);

        public NamedAttributeGroup insertNewAttributeGroup(int var1);

        public NamedAttributeGroup addNewAttributeGroup();

        public void removeAttributeGroup(int var1);

        public String getSchemaLocation();

        public XmlAnyURI xgetSchemaLocation();

        public void setSchemaLocation(String var1);

        public void xsetSchemaLocation(XmlAnyURI var1);

        public String getId();

        public XmlID xgetId();

        public boolean isSetId();

        public void setId(String var1);

        public void xsetId(XmlID var1);

        public void unsetId();
    }
}

