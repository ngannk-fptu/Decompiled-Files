/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.IncludeType
 *  org.etsi.uri.x01903.v13.ReferenceInfoType
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.etsi.uri.x01903.v13.IncludeType;
import org.etsi.uri.x01903.v13.ReferenceInfoType;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;

public interface GenericTimeStampType
extends XmlObject {
    public static final AbstractDocumentFactory<GenericTimeStampType> Factory = new AbstractDocumentFactory(TypeSystemHolder.typeSystem, "generictimestamptypecdadtype");
    public static final SchemaType type = Factory.getType();

    public List<IncludeType> getIncludeList();

    public IncludeType[] getIncludeArray();

    public IncludeType getIncludeArray(int var1);

    public int sizeOfIncludeArray();

    public void setIncludeArray(IncludeType[] var1);

    public void setIncludeArray(int var1, IncludeType var2);

    public IncludeType insertNewInclude(int var1);

    public IncludeType addNewInclude();

    public void removeInclude(int var1);

    public List<ReferenceInfoType> getReferenceInfoList();

    public ReferenceInfoType[] getReferenceInfoArray();

    public ReferenceInfoType getReferenceInfoArray(int var1);

    public int sizeOfReferenceInfoArray();

    public void setReferenceInfoArray(ReferenceInfoType[] var1);

    public void setReferenceInfoArray(int var1, ReferenceInfoType var2);

    public ReferenceInfoType insertNewReferenceInfo(int var1);

    public ReferenceInfoType addNewReferenceInfo();

    public void removeReferenceInfo(int var1);

    public CanonicalizationMethodType getCanonicalizationMethod();

    public boolean isSetCanonicalizationMethod();

    public void setCanonicalizationMethod(CanonicalizationMethodType var1);

    public CanonicalizationMethodType addNewCanonicalizationMethod();

    public void unsetCanonicalizationMethod();

    public List<EncapsulatedPKIDataType> getEncapsulatedTimeStampList();

    public EncapsulatedPKIDataType[] getEncapsulatedTimeStampArray();

    public EncapsulatedPKIDataType getEncapsulatedTimeStampArray(int var1);

    public int sizeOfEncapsulatedTimeStampArray();

    public void setEncapsulatedTimeStampArray(EncapsulatedPKIDataType[] var1);

    public void setEncapsulatedTimeStampArray(int var1, EncapsulatedPKIDataType var2);

    public EncapsulatedPKIDataType insertNewEncapsulatedTimeStamp(int var1);

    public EncapsulatedPKIDataType addNewEncapsulatedTimeStamp();

    public void removeEncapsulatedTimeStamp(int var1);

    public List<AnyType> getXMLTimeStampList();

    public AnyType[] getXMLTimeStampArray();

    public AnyType getXMLTimeStampArray(int var1);

    public int sizeOfXMLTimeStampArray();

    public void setXMLTimeStampArray(AnyType[] var1);

    public void setXMLTimeStampArray(int var1, AnyType var2);

    public AnyType insertNewXMLTimeStamp(int var1);

    public AnyType addNewXMLTimeStamp();

    public void removeXMLTimeStamp(int var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

