/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;

public interface CRLValuesType
extends XmlObject {
    public static final DocumentFactory<CRLValuesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "crlvaluestype0ebbtype");
    public static final SchemaType type = Factory.getType();

    public List<EncapsulatedPKIDataType> getEncapsulatedCRLValueList();

    public EncapsulatedPKIDataType[] getEncapsulatedCRLValueArray();

    public EncapsulatedPKIDataType getEncapsulatedCRLValueArray(int var1);

    public int sizeOfEncapsulatedCRLValueArray();

    public void setEncapsulatedCRLValueArray(EncapsulatedPKIDataType[] var1);

    public void setEncapsulatedCRLValueArray(int var1, EncapsulatedPKIDataType var2);

    public EncapsulatedPKIDataType insertNewEncapsulatedCRLValue(int var1);

    public EncapsulatedPKIDataType addNewEncapsulatedCRLValue();

    public void removeEncapsulatedCRLValue(int var1);
}

