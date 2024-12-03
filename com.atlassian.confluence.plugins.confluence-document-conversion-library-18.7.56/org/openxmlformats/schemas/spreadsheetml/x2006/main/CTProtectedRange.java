/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;

public interface CTProtectedRange
extends XmlObject {
    public static final DocumentFactory<CTProtectedRange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctprotectedrange7078type");
    public static final SchemaType type = Factory.getType();

    public List<String> getSecurityDescriptorList();

    public String[] getSecurityDescriptorArray();

    public String getSecurityDescriptorArray(int var1);

    public List<XmlString> xgetSecurityDescriptorList();

    public XmlString[] xgetSecurityDescriptorArray();

    public XmlString xgetSecurityDescriptorArray(int var1);

    public int sizeOfSecurityDescriptorArray();

    public void setSecurityDescriptorArray(String[] var1);

    public void setSecurityDescriptorArray(int var1, String var2);

    public void xsetSecurityDescriptorArray(XmlString[] var1);

    public void xsetSecurityDescriptorArray(int var1, XmlString var2);

    public void insertSecurityDescriptor(int var1, String var2);

    public void addSecurityDescriptor(String var1);

    public XmlString insertNewSecurityDescriptor(int var1);

    public XmlString addNewSecurityDescriptor();

    public void removeSecurityDescriptor(int var1);

    public byte[] getPassword();

    public STUnsignedShortHex xgetPassword();

    public boolean isSetPassword();

    public void setPassword(byte[] var1);

    public void xsetPassword(STUnsignedShortHex var1);

    public void unsetPassword();

    public List getSqref();

    public STSqref xgetSqref();

    public void setSqref(List var1);

    public void xsetSqref(STSqref var1);

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public String getSecurityDescriptor2();

    public XmlString xgetSecurityDescriptor2();

    public boolean isSetSecurityDescriptor2();

    public void setSecurityDescriptor2(String var1);

    public void xsetSecurityDescriptor2(XmlString var1);

    public void unsetSecurityDescriptor2();

    public String getAlgorithmName();

    public STXstring xgetAlgorithmName();

    public boolean isSetAlgorithmName();

    public void setAlgorithmName(String var1);

    public void xsetAlgorithmName(STXstring var1);

    public void unsetAlgorithmName();

    public byte[] getHashValue();

    public XmlBase64Binary xgetHashValue();

    public boolean isSetHashValue();

    public void setHashValue(byte[] var1);

    public void xsetHashValue(XmlBase64Binary var1);

    public void unsetHashValue();

    public byte[] getSaltValue();

    public XmlBase64Binary xgetSaltValue();

    public boolean isSetSaltValue();

    public void setSaltValue(byte[] var1);

    public void xsetSaltValue(XmlBase64Binary var1);

    public void unsetSaltValue();

    public long getSpinCount();

    public XmlUnsignedInt xgetSpinCount();

    public boolean isSetSpinCount();

    public void setSpinCount(long var1);

    public void xsetSpinCount(XmlUnsignedInt var1);

    public void unsetSpinCount();
}

