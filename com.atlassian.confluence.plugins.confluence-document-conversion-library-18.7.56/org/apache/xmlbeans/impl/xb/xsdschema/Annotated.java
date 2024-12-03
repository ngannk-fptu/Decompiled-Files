/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.OpenAttrs;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Annotated
extends OpenAttrs {
    public static final DocumentFactory<Annotated> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "annotateda52dtype");
    public static final SchemaType type = Factory.getType();

    public AnnotationDocument.Annotation getAnnotation();

    public boolean isSetAnnotation();

    public void setAnnotation(AnnotationDocument.Annotation var1);

    public AnnotationDocument.Annotation addNewAnnotation();

    public void unsetAnnotation();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

