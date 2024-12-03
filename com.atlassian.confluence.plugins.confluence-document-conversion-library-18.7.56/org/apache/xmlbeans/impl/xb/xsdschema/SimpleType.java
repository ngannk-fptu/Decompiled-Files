/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleDerivationSet;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SimpleType
extends Annotated {
    public static final AbstractDocumentFactory<SimpleType> Factory = new AbstractDocumentFactory(TypeSystemHolder.typeSystem, "simpletype0707type");
    public static final SchemaType type = Factory.getType();

    public RestrictionDocument.Restriction getRestriction();

    public boolean isSetRestriction();

    public void setRestriction(RestrictionDocument.Restriction var1);

    public RestrictionDocument.Restriction addNewRestriction();

    public void unsetRestriction();

    public ListDocument.List getList();

    public boolean isSetList();

    public void setList(ListDocument.List var1);

    public ListDocument.List addNewList();

    public void unsetList();

    public UnionDocument.Union getUnion();

    public boolean isSetUnion();

    public void setUnion(UnionDocument.Union var1);

    public UnionDocument.Union addNewUnion();

    public void unsetUnion();

    public Object getFinal();

    public SimpleDerivationSet xgetFinal();

    public boolean isSetFinal();

    public void setFinal(Object var1);

    public void xsetFinal(SimpleDerivationSet var1);

    public void unsetFinal();

    public String getName();

    public XmlNCName xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlNCName var1);

    public void unsetName();
}

