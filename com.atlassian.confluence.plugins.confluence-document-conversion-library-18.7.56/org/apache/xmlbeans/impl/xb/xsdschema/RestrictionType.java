/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TotalDigitsDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.WhiteSpaceDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface RestrictionType
extends Annotated {
    public static final DocumentFactory<RestrictionType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "restrictiontype939ftype");
    public static final SchemaType type = Factory.getType();

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

    public LocalSimpleType getSimpleType();

    public boolean isSetSimpleType();

    public void setSimpleType(LocalSimpleType var1);

    public LocalSimpleType addNewSimpleType();

    public void unsetSimpleType();

    public List<Facet> getMinExclusiveList();

    public Facet[] getMinExclusiveArray();

    public Facet getMinExclusiveArray(int var1);

    public int sizeOfMinExclusiveArray();

    public void setMinExclusiveArray(Facet[] var1);

    public void setMinExclusiveArray(int var1, Facet var2);

    public Facet insertNewMinExclusive(int var1);

    public Facet addNewMinExclusive();

    public void removeMinExclusive(int var1);

    public List<Facet> getMinInclusiveList();

    public Facet[] getMinInclusiveArray();

    public Facet getMinInclusiveArray(int var1);

    public int sizeOfMinInclusiveArray();

    public void setMinInclusiveArray(Facet[] var1);

    public void setMinInclusiveArray(int var1, Facet var2);

    public Facet insertNewMinInclusive(int var1);

    public Facet addNewMinInclusive();

    public void removeMinInclusive(int var1);

    public List<Facet> getMaxExclusiveList();

    public Facet[] getMaxExclusiveArray();

    public Facet getMaxExclusiveArray(int var1);

    public int sizeOfMaxExclusiveArray();

    public void setMaxExclusiveArray(Facet[] var1);

    public void setMaxExclusiveArray(int var1, Facet var2);

    public Facet insertNewMaxExclusive(int var1);

    public Facet addNewMaxExclusive();

    public void removeMaxExclusive(int var1);

    public List<Facet> getMaxInclusiveList();

    public Facet[] getMaxInclusiveArray();

    public Facet getMaxInclusiveArray(int var1);

    public int sizeOfMaxInclusiveArray();

    public void setMaxInclusiveArray(Facet[] var1);

    public void setMaxInclusiveArray(int var1, Facet var2);

    public Facet insertNewMaxInclusive(int var1);

    public Facet addNewMaxInclusive();

    public void removeMaxInclusive(int var1);

    public List<TotalDigitsDocument.TotalDigits> getTotalDigitsList();

    public TotalDigitsDocument.TotalDigits[] getTotalDigitsArray();

    public TotalDigitsDocument.TotalDigits getTotalDigitsArray(int var1);

    public int sizeOfTotalDigitsArray();

    public void setTotalDigitsArray(TotalDigitsDocument.TotalDigits[] var1);

    public void setTotalDigitsArray(int var1, TotalDigitsDocument.TotalDigits var2);

    public TotalDigitsDocument.TotalDigits insertNewTotalDigits(int var1);

    public TotalDigitsDocument.TotalDigits addNewTotalDigits();

    public void removeTotalDigits(int var1);

    public List<NumFacet> getFractionDigitsList();

    public NumFacet[] getFractionDigitsArray();

    public NumFacet getFractionDigitsArray(int var1);

    public int sizeOfFractionDigitsArray();

    public void setFractionDigitsArray(NumFacet[] var1);

    public void setFractionDigitsArray(int var1, NumFacet var2);

    public NumFacet insertNewFractionDigits(int var1);

    public NumFacet addNewFractionDigits();

    public void removeFractionDigits(int var1);

    public List<NumFacet> getLengthList();

    public NumFacet[] getLengthArray();

    public NumFacet getLengthArray(int var1);

    public int sizeOfLengthArray();

    public void setLengthArray(NumFacet[] var1);

    public void setLengthArray(int var1, NumFacet var2);

    public NumFacet insertNewLength(int var1);

    public NumFacet addNewLength();

    public void removeLength(int var1);

    public List<NumFacet> getMinLengthList();

    public NumFacet[] getMinLengthArray();

    public NumFacet getMinLengthArray(int var1);

    public int sizeOfMinLengthArray();

    public void setMinLengthArray(NumFacet[] var1);

    public void setMinLengthArray(int var1, NumFacet var2);

    public NumFacet insertNewMinLength(int var1);

    public NumFacet addNewMinLength();

    public void removeMinLength(int var1);

    public List<NumFacet> getMaxLengthList();

    public NumFacet[] getMaxLengthArray();

    public NumFacet getMaxLengthArray(int var1);

    public int sizeOfMaxLengthArray();

    public void setMaxLengthArray(NumFacet[] var1);

    public void setMaxLengthArray(int var1, NumFacet var2);

    public NumFacet insertNewMaxLength(int var1);

    public NumFacet addNewMaxLength();

    public void removeMaxLength(int var1);

    public List<NoFixedFacet> getEnumerationList();

    public NoFixedFacet[] getEnumerationArray();

    public NoFixedFacet getEnumerationArray(int var1);

    public int sizeOfEnumerationArray();

    public void setEnumerationArray(NoFixedFacet[] var1);

    public void setEnumerationArray(int var1, NoFixedFacet var2);

    public NoFixedFacet insertNewEnumeration(int var1);

    public NoFixedFacet addNewEnumeration();

    public void removeEnumeration(int var1);

    public List<WhiteSpaceDocument.WhiteSpace> getWhiteSpaceList();

    public WhiteSpaceDocument.WhiteSpace[] getWhiteSpaceArray();

    public WhiteSpaceDocument.WhiteSpace getWhiteSpaceArray(int var1);

    public int sizeOfWhiteSpaceArray();

    public void setWhiteSpaceArray(WhiteSpaceDocument.WhiteSpace[] var1);

    public void setWhiteSpaceArray(int var1, WhiteSpaceDocument.WhiteSpace var2);

    public WhiteSpaceDocument.WhiteSpace insertNewWhiteSpace(int var1);

    public WhiteSpaceDocument.WhiteSpace addNewWhiteSpace();

    public void removeWhiteSpace(int var1);

    public List<PatternDocument.Pattern> getPatternList();

    public PatternDocument.Pattern[] getPatternArray();

    public PatternDocument.Pattern getPatternArray(int var1);

    public int sizeOfPatternArray();

    public void setPatternArray(PatternDocument.Pattern[] var1);

    public void setPatternArray(int var1, PatternDocument.Pattern var2);

    public PatternDocument.Pattern insertNewPattern(int var1);

    public PatternDocument.Pattern addNewPattern();

    public void removePattern(int var1);

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

    public QName getBase();

    public XmlQName xgetBase();

    public void setBase(QName var1);

    public void xsetBase(XmlQName var1);
}

