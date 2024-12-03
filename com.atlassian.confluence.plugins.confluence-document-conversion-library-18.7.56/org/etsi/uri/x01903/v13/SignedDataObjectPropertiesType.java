/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CommitmentTypeIndicationType;
import org.etsi.uri.x01903.v13.DataObjectFormatType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;

public interface SignedDataObjectPropertiesType
extends XmlObject {
    public static final DocumentFactory<SignedDataObjectPropertiesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signeddataobjectpropertiestype19a6type");
    public static final SchemaType type = Factory.getType();

    public List<DataObjectFormatType> getDataObjectFormatList();

    public DataObjectFormatType[] getDataObjectFormatArray();

    public DataObjectFormatType getDataObjectFormatArray(int var1);

    public int sizeOfDataObjectFormatArray();

    public void setDataObjectFormatArray(DataObjectFormatType[] var1);

    public void setDataObjectFormatArray(int var1, DataObjectFormatType var2);

    public DataObjectFormatType insertNewDataObjectFormat(int var1);

    public DataObjectFormatType addNewDataObjectFormat();

    public void removeDataObjectFormat(int var1);

    public List<CommitmentTypeIndicationType> getCommitmentTypeIndicationList();

    public CommitmentTypeIndicationType[] getCommitmentTypeIndicationArray();

    public CommitmentTypeIndicationType getCommitmentTypeIndicationArray(int var1);

    public int sizeOfCommitmentTypeIndicationArray();

    public void setCommitmentTypeIndicationArray(CommitmentTypeIndicationType[] var1);

    public void setCommitmentTypeIndicationArray(int var1, CommitmentTypeIndicationType var2);

    public CommitmentTypeIndicationType insertNewCommitmentTypeIndication(int var1);

    public CommitmentTypeIndicationType addNewCommitmentTypeIndication();

    public void removeCommitmentTypeIndication(int var1);

    public List<XAdESTimeStampType> getAllDataObjectsTimeStampList();

    public XAdESTimeStampType[] getAllDataObjectsTimeStampArray();

    public XAdESTimeStampType getAllDataObjectsTimeStampArray(int var1);

    public int sizeOfAllDataObjectsTimeStampArray();

    public void setAllDataObjectsTimeStampArray(XAdESTimeStampType[] var1);

    public void setAllDataObjectsTimeStampArray(int var1, XAdESTimeStampType var2);

    public XAdESTimeStampType insertNewAllDataObjectsTimeStamp(int var1);

    public XAdESTimeStampType addNewAllDataObjectsTimeStamp();

    public void removeAllDataObjectsTimeStamp(int var1);

    public List<XAdESTimeStampType> getIndividualDataObjectsTimeStampList();

    public XAdESTimeStampType[] getIndividualDataObjectsTimeStampArray();

    public XAdESTimeStampType getIndividualDataObjectsTimeStampArray(int var1);

    public int sizeOfIndividualDataObjectsTimeStampArray();

    public void setIndividualDataObjectsTimeStampArray(XAdESTimeStampType[] var1);

    public void setIndividualDataObjectsTimeStampArray(int var1, XAdESTimeStampType var2);

    public XAdESTimeStampType insertNewIndividualDataObjectsTimeStamp(int var1);

    public XAdESTimeStampType addNewIndividualDataObjectsTimeStamp();

    public void removeIndividualDataObjectsTimeStamp(int var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

