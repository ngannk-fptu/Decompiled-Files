/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SelectorDocument;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Keybase
extends Annotated {
    public static final DocumentFactory<Keybase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "keybase3955type");
    public static final SchemaType type = Factory.getType();

    public SelectorDocument.Selector getSelector();

    public void setSelector(SelectorDocument.Selector var1);

    public SelectorDocument.Selector addNewSelector();

    public List<FieldDocument.Field> getFieldList();

    public FieldDocument.Field[] getFieldArray();

    public FieldDocument.Field getFieldArray(int var1);

    public int sizeOfFieldArray();

    public void setFieldArray(FieldDocument.Field[] var1);

    public void setFieldArray(int var1, FieldDocument.Field var2);

    public FieldDocument.Field insertNewField(int var1);

    public FieldDocument.Field addNewField();

    public void removeField(int var1);

    public String getName();

    public XmlNCName xgetName();

    public void setName(String var1);

    public void xsetName(XmlNCName var1);
}

