/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AppinfoDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.OpenAttrs;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AnnotationDocument
extends XmlObject {
    public static final DocumentFactory<AnnotationDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "annotationb034doctype");
    public static final SchemaType type = Factory.getType();

    public Annotation getAnnotation();

    public void setAnnotation(Annotation var1);

    public Annotation addNewAnnotation();

    public static interface Annotation
    extends OpenAttrs {
        public static final ElementFactory<Annotation> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "annotation5abfelemtype");
        public static final SchemaType type = Factory.getType();

        public List<AppinfoDocument.Appinfo> getAppinfoList();

        public AppinfoDocument.Appinfo[] getAppinfoArray();

        public AppinfoDocument.Appinfo getAppinfoArray(int var1);

        public int sizeOfAppinfoArray();

        public void setAppinfoArray(AppinfoDocument.Appinfo[] var1);

        public void setAppinfoArray(int var1, AppinfoDocument.Appinfo var2);

        public AppinfoDocument.Appinfo insertNewAppinfo(int var1);

        public AppinfoDocument.Appinfo addNewAppinfo();

        public void removeAppinfo(int var1);

        public List<DocumentationDocument.Documentation> getDocumentationList();

        public DocumentationDocument.Documentation[] getDocumentationArray();

        public DocumentationDocument.Documentation getDocumentationArray(int var1);

        public int sizeOfDocumentationArray();

        public void setDocumentationArray(DocumentationDocument.Documentation[] var1);

        public void setDocumentationArray(int var1, DocumentationDocument.Documentation var2);

        public DocumentationDocument.Documentation insertNewDocumentation(int var1);

        public DocumentationDocument.Documentation addNewDocumentation();

        public void removeDocumentation(int var1);

        public String getId();

        public XmlID xgetId();

        public boolean isSetId();

        public void setId(String var1);

        public void xsetId(XmlID var1);

        public void unsetId();
    }
}

