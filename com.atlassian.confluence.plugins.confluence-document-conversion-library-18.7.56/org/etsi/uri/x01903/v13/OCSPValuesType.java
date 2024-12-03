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

public interface OCSPValuesType
extends XmlObject {
    public static final DocumentFactory<OCSPValuesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ocspvaluestypeb421type");
    public static final SchemaType type = Factory.getType();

    public List<EncapsulatedPKIDataType> getEncapsulatedOCSPValueList();

    public EncapsulatedPKIDataType[] getEncapsulatedOCSPValueArray();

    public EncapsulatedPKIDataType getEncapsulatedOCSPValueArray(int var1);

    public int sizeOfEncapsulatedOCSPValueArray();

    public void setEncapsulatedOCSPValueArray(EncapsulatedPKIDataType[] var1);

    public void setEncapsulatedOCSPValueArray(int var1, EncapsulatedPKIDataType var2);

    public EncapsulatedPKIDataType insertNewEncapsulatedOCSPValue(int var1);

    public EncapsulatedPKIDataType addNewEncapsulatedOCSPValue();

    public void removeEncapsulatedOCSPValue(int var1);
}

