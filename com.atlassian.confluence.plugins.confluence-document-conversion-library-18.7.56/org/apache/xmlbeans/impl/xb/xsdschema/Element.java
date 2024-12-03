/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.math.BigInteger;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.BlockSet;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationSet;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Element
extends Annotated {
    public static final AbstractDocumentFactory<Element> Factory = new AbstractDocumentFactory(TypeSystemHolder.typeSystem, "elementd189type");
    public static final SchemaType type = Factory.getType();

    public LocalSimpleType getSimpleType();

    public boolean isSetSimpleType();

    public void setSimpleType(LocalSimpleType var1);

    public LocalSimpleType addNewSimpleType();

    public void unsetSimpleType();

    public LocalComplexType getComplexType();

    public boolean isSetComplexType();

    public void setComplexType(LocalComplexType var1);

    public LocalComplexType addNewComplexType();

    public void unsetComplexType();

    public List<Keybase> getUniqueList();

    public Keybase[] getUniqueArray();

    public Keybase getUniqueArray(int var1);

    public int sizeOfUniqueArray();

    public void setUniqueArray(Keybase[] var1);

    public void setUniqueArray(int var1, Keybase var2);

    public Keybase insertNewUnique(int var1);

    public Keybase addNewUnique();

    public void removeUnique(int var1);

    public List<Keybase> getKeyList();

    public Keybase[] getKeyArray();

    public Keybase getKeyArray(int var1);

    public int sizeOfKeyArray();

    public void setKeyArray(Keybase[] var1);

    public void setKeyArray(int var1, Keybase var2);

    public Keybase insertNewKey(int var1);

    public Keybase addNewKey();

    public void removeKey(int var1);

    public List<KeyrefDocument.Keyref> getKeyrefList();

    public KeyrefDocument.Keyref[] getKeyrefArray();

    public KeyrefDocument.Keyref getKeyrefArray(int var1);

    public int sizeOfKeyrefArray();

    public void setKeyrefArray(KeyrefDocument.Keyref[] var1);

    public void setKeyrefArray(int var1, KeyrefDocument.Keyref var2);

    public KeyrefDocument.Keyref insertNewKeyref(int var1);

    public KeyrefDocument.Keyref addNewKeyref();

    public void removeKeyref(int var1);

    public String getName();

    public XmlNCName xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlNCName var1);

    public void unsetName();

    public QName getRef();

    public XmlQName xgetRef();

    public boolean isSetRef();

    public void setRef(QName var1);

    public void xsetRef(XmlQName var1);

    public void unsetRef();

    public QName getType();

    public XmlQName xgetType();

    public boolean isSetType();

    public void setType(QName var1);

    public void xsetType(XmlQName var1);

    public void unsetType();

    public QName getSubstitutionGroup();

    public XmlQName xgetSubstitutionGroup();

    public boolean isSetSubstitutionGroup();

    public void setSubstitutionGroup(QName var1);

    public void xsetSubstitutionGroup(XmlQName var1);

    public void unsetSubstitutionGroup();

    public BigInteger getMinOccurs();

    public XmlNonNegativeInteger xgetMinOccurs();

    public boolean isSetMinOccurs();

    public void setMinOccurs(BigInteger var1);

    public void xsetMinOccurs(XmlNonNegativeInteger var1);

    public void unsetMinOccurs();

    public Object getMaxOccurs();

    public AllNNI xgetMaxOccurs();

    public boolean isSetMaxOccurs();

    public void setMaxOccurs(Object var1);

    public void xsetMaxOccurs(AllNNI var1);

    public void unsetMaxOccurs();

    public String getDefault();

    public XmlString xgetDefault();

    public boolean isSetDefault();

    public void setDefault(String var1);

    public void xsetDefault(XmlString var1);

    public void unsetDefault();

    public String getFixed();

    public XmlString xgetFixed();

    public boolean isSetFixed();

    public void setFixed(String var1);

    public void xsetFixed(XmlString var1);

    public void unsetFixed();

    public boolean getNillable();

    public XmlBoolean xgetNillable();

    public boolean isSetNillable();

    public void setNillable(boolean var1);

    public void xsetNillable(XmlBoolean var1);

    public void unsetNillable();

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

    public BlockSet xgetBlock();

    public boolean isSetBlock();

    public void setBlock(Object var1);

    public void xsetBlock(BlockSet var1);

    public void unsetBlock();

    public FormChoice.Enum getForm();

    public FormChoice xgetForm();

    public boolean isSetForm();

    public void setForm(FormChoice.Enum var1);

    public void xsetForm(FormChoice var1);

    public void unsetForm();
}

