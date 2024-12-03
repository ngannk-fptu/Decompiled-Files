/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
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
import org.apache.xmlbeans.impl.xb.xsdschema.OpenAttrs;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SchemaDocument
extends XmlObject {
    public static final DocumentFactory<SchemaDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "schema0782doctype");
    public static final SchemaType type = Factory.getType();

    public Schema getSchema();

    public void setSchema(Schema var1);

    public Schema addNewSchema();

    public static interface Schema
    extends OpenAttrs {
        public static final ElementFactory<Schema> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "schemad77felemtype");
        public static final SchemaType type = Factory.getType();

        public List<IncludeDocument.Include> getIncludeList();

        public IncludeDocument.Include[] getIncludeArray();

        public IncludeDocument.Include getIncludeArray(int var1);

        public int sizeOfIncludeArray();

        public void setIncludeArray(IncludeDocument.Include[] var1);

        public void setIncludeArray(int var1, IncludeDocument.Include var2);

        public IncludeDocument.Include insertNewInclude(int var1);

        public IncludeDocument.Include addNewInclude();

        public void removeInclude(int var1);

        public List<ImportDocument.Import> getImportList();

        public ImportDocument.Import[] getImportArray();

        public ImportDocument.Import getImportArray(int var1);

        public int sizeOfImportArray();

        public void setImportArray(ImportDocument.Import[] var1);

        public void setImportArray(int var1, ImportDocument.Import var2);

        public ImportDocument.Import insertNewImport(int var1);

        public ImportDocument.Import addNewImport();

        public void removeImport(int var1);

        public List<RedefineDocument.Redefine> getRedefineList();

        public RedefineDocument.Redefine[] getRedefineArray();

        public RedefineDocument.Redefine getRedefineArray(int var1);

        public int sizeOfRedefineArray();

        public void setRedefineArray(RedefineDocument.Redefine[] var1);

        public void setRedefineArray(int var1, RedefineDocument.Redefine var2);

        public RedefineDocument.Redefine insertNewRedefine(int var1);

        public RedefineDocument.Redefine addNewRedefine();

        public void removeRedefine(int var1);

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

        public List<TopLevelElement> getElementList();

        public TopLevelElement[] getElementArray();

        public TopLevelElement getElementArray(int var1);

        public int sizeOfElementArray();

        public void setElementArray(TopLevelElement[] var1);

        public void setElementArray(int var1, TopLevelElement var2);

        public TopLevelElement insertNewElement(int var1);

        public TopLevelElement addNewElement();

        public void removeElement(int var1);

        public List<TopLevelAttribute> getAttributeList();

        public TopLevelAttribute[] getAttributeArray();

        public TopLevelAttribute getAttributeArray(int var1);

        public int sizeOfAttributeArray();

        public void setAttributeArray(TopLevelAttribute[] var1);

        public void setAttributeArray(int var1, TopLevelAttribute var2);

        public TopLevelAttribute insertNewAttribute(int var1);

        public TopLevelAttribute addNewAttribute();

        public void removeAttribute(int var1);

        public List<NotationDocument.Notation> getNotationList();

        public NotationDocument.Notation[] getNotationArray();

        public NotationDocument.Notation getNotationArray(int var1);

        public int sizeOfNotationArray();

        public void setNotationArray(NotationDocument.Notation[] var1);

        public void setNotationArray(int var1, NotationDocument.Notation var2);

        public NotationDocument.Notation insertNewNotation(int var1);

        public NotationDocument.Notation addNewNotation();

        public void removeNotation(int var1);

        public String getTargetNamespace();

        public XmlAnyURI xgetTargetNamespace();

        public boolean isSetTargetNamespace();

        public void setTargetNamespace(String var1);

        public void xsetTargetNamespace(XmlAnyURI var1);

        public void unsetTargetNamespace();

        public String getVersion();

        public XmlToken xgetVersion();

        public boolean isSetVersion();

        public void setVersion(String var1);

        public void xsetVersion(XmlToken var1);

        public void unsetVersion();

        public Object getFinalDefault();

        public FullDerivationSet xgetFinalDefault();

        public boolean isSetFinalDefault();

        public void setFinalDefault(Object var1);

        public void xsetFinalDefault(FullDerivationSet var1);

        public void unsetFinalDefault();

        public Object getBlockDefault();

        public BlockSet xgetBlockDefault();

        public boolean isSetBlockDefault();

        public void setBlockDefault(Object var1);

        public void xsetBlockDefault(BlockSet var1);

        public void unsetBlockDefault();

        public FormChoice.Enum getAttributeFormDefault();

        public FormChoice xgetAttributeFormDefault();

        public boolean isSetAttributeFormDefault();

        public void setAttributeFormDefault(FormChoice.Enum var1);

        public void xsetAttributeFormDefault(FormChoice var1);

        public void unsetAttributeFormDefault();

        public FormChoice.Enum getElementFormDefault();

        public FormChoice xgetElementFormDefault();

        public boolean isSetElementFormDefault();

        public void setElementFormDefault(FormChoice.Enum var1);

        public void xsetElementFormDefault(FormChoice var1);

        public void unsetElementFormDefault();

        public String getId();

        public XmlID xgetId();

        public boolean isSetId();

        public void setId(String var1);

        public void xsetId(XmlID var1);

        public void unsetId();

        public String getLang();

        public LangAttribute.Lang xgetLang();

        public boolean isSetLang();

        public void setLang(String var1);

        public void xsetLang(LangAttribute.Lang var1);

        public void unsetLang();
    }
}

