/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationSet;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ComplexType
extends Annotated {
    public static final AbstractDocumentFactory<ComplexType> Factory = new AbstractDocumentFactory(TypeSystemHolder.typeSystem, "complextype5dbbtype");
    public static final SchemaType type = Factory.getType();

    public SimpleContentDocument.SimpleContent getSimpleContent();

    public boolean isSetSimpleContent();

    public void setSimpleContent(SimpleContentDocument.SimpleContent var1);

    public SimpleContentDocument.SimpleContent addNewSimpleContent();

    public void unsetSimpleContent();

    public ComplexContentDocument.ComplexContent getComplexContent();

    public boolean isSetComplexContent();

    public void setComplexContent(ComplexContentDocument.ComplexContent var1);

    public ComplexContentDocument.ComplexContent addNewComplexContent();

    public void unsetComplexContent();

    public GroupRef getGroup();

    public boolean isSetGroup();

    public void setGroup(GroupRef var1);

    public GroupRef addNewGroup();

    public void unsetGroup();

    public All getAll();

    public boolean isSetAll();

    public void setAll(All var1);

    public All addNewAll();

    public void unsetAll();

    public ExplicitGroup getChoice();

    public boolean isSetChoice();

    public void setChoice(ExplicitGroup var1);

    public ExplicitGroup addNewChoice();

    public void unsetChoice();

    public ExplicitGroup getSequence();

    public boolean isSetSequence();

    public void setSequence(ExplicitGroup var1);

    public ExplicitGroup addNewSequence();

    public void unsetSequence();

    public List<Attribute> getAttributeList();

    public Attribute[] getAttributeArray();

    public Attribute getAttributeArray(int var1);

    public int sizeOfAttributeArray();

    public void setAttributeArray(Attribute[] var1);

    public void setAttributeArray(int var1, Attribute var2);

    public Attribute insertNewAttribute(int var1);

    public Attribute addNewAttribute();

    public void removeAttribute(int var1);

    public List<AttributeGroupRef> getAttributeGroupList();

    public AttributeGroupRef[] getAttributeGroupArray();

    public AttributeGroupRef getAttributeGroupArray(int var1);

    public int sizeOfAttributeGroupArray();

    public void setAttributeGroupArray(AttributeGroupRef[] var1);

    public void setAttributeGroupArray(int var1, AttributeGroupRef var2);

    public AttributeGroupRef insertNewAttributeGroup(int var1);

    public AttributeGroupRef addNewAttributeGroup();

    public void removeAttributeGroup(int var1);

    public Wildcard getAnyAttribute();

    public boolean isSetAnyAttribute();

    public void setAnyAttribute(Wildcard var1);

    public Wildcard addNewAnyAttribute();

    public void unsetAnyAttribute();

    public String getName();

    public XmlNCName xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlNCName var1);

    public void unsetName();

    public boolean getMixed();

    public XmlBoolean xgetMixed();

    public boolean isSetMixed();

    public void setMixed(boolean var1);

    public void xsetMixed(XmlBoolean var1);

    public void unsetMixed();

    public boolean getAbstract();

    public XmlBoolean xgetAbstract();

    public boolean isSetAbstract();

    public void setAbstract(boolean var1);

    public void xsetAbstract(XmlBoolean var1);

    public void unsetAbstract();

    public Object getFinal();

    public DerivationSet xgetFinal();

    public boolean isSetFinal();

    public void setFinal(Object var1);

    public void xsetFinal(DerivationSet var1);

    public void unsetFinal();

    public Object getBlock();

    public DerivationSet xgetBlock();

    public boolean isSetBlock();

    public void setBlock(Object var1);

    public void xsetBlock(DerivationSet var1);

    public void unsetBlock();
}

