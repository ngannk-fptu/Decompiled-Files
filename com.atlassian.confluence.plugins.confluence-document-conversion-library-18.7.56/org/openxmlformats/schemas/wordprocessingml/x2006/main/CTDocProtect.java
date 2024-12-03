/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STAlgClass;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STAlgType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STCryptProv;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;

public interface CTDocProtect
extends XmlObject {
    public static final DocumentFactory<CTDocProtect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdocprotectc611type");
    public static final SchemaType type = Factory.getType();

    public STDocProtect.Enum getEdit();

    public STDocProtect xgetEdit();

    public boolean isSetEdit();

    public void setEdit(STDocProtect.Enum var1);

    public void xsetEdit(STDocProtect var1);

    public void unsetEdit();

    public Object getFormatting();

    public STOnOff xgetFormatting();

    public boolean isSetFormatting();

    public void setFormatting(Object var1);

    public void xsetFormatting(STOnOff var1);

    public void unsetFormatting();

    public Object getEnforcement();

    public STOnOff xgetEnforcement();

    public boolean isSetEnforcement();

    public void setEnforcement(Object var1);

    public void xsetEnforcement(STOnOff var1);

    public void unsetEnforcement();

    public String getAlgorithmName();

    public STString xgetAlgorithmName();

    public boolean isSetAlgorithmName();

    public void setAlgorithmName(String var1);

    public void xsetAlgorithmName(STString var1);

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

    public BigInteger getSpinCount();

    public STDecimalNumber xgetSpinCount();

    public boolean isSetSpinCount();

    public void setSpinCount(BigInteger var1);

    public void xsetSpinCount(STDecimalNumber var1);

    public void unsetSpinCount();

    public STCryptProv.Enum getCryptProviderType();

    public STCryptProv xgetCryptProviderType();

    public boolean isSetCryptProviderType();

    public void setCryptProviderType(STCryptProv.Enum var1);

    public void xsetCryptProviderType(STCryptProv var1);

    public void unsetCryptProviderType();

    public STAlgClass.Enum getCryptAlgorithmClass();

    public STAlgClass xgetCryptAlgorithmClass();

    public boolean isSetCryptAlgorithmClass();

    public void setCryptAlgorithmClass(STAlgClass.Enum var1);

    public void xsetCryptAlgorithmClass(STAlgClass var1);

    public void unsetCryptAlgorithmClass();

    public STAlgType.Enum getCryptAlgorithmType();

    public STAlgType xgetCryptAlgorithmType();

    public boolean isSetCryptAlgorithmType();

    public void setCryptAlgorithmType(STAlgType.Enum var1);

    public void xsetCryptAlgorithmType(STAlgType var1);

    public void unsetCryptAlgorithmType();

    public BigInteger getCryptAlgorithmSid();

    public STDecimalNumber xgetCryptAlgorithmSid();

    public boolean isSetCryptAlgorithmSid();

    public void setCryptAlgorithmSid(BigInteger var1);

    public void xsetCryptAlgorithmSid(STDecimalNumber var1);

    public void unsetCryptAlgorithmSid();

    public BigInteger getCryptSpinCount();

    public STDecimalNumber xgetCryptSpinCount();

    public boolean isSetCryptSpinCount();

    public void setCryptSpinCount(BigInteger var1);

    public void xsetCryptSpinCount(STDecimalNumber var1);

    public void unsetCryptSpinCount();

    public String getCryptProvider();

    public STString xgetCryptProvider();

    public boolean isSetCryptProvider();

    public void setCryptProvider(String var1);

    public void xsetCryptProvider(STString var1);

    public void unsetCryptProvider();

    public byte[] getAlgIdExt();

    public STLongHexNumber xgetAlgIdExt();

    public boolean isSetAlgIdExt();

    public void setAlgIdExt(byte[] var1);

    public void xsetAlgIdExt(STLongHexNumber var1);

    public void unsetAlgIdExt();

    public String getAlgIdExtSource();

    public STString xgetAlgIdExtSource();

    public boolean isSetAlgIdExtSource();

    public void setAlgIdExtSource(String var1);

    public void xsetAlgIdExtSource(STString var1);

    public void unsetAlgIdExtSource();

    public byte[] getCryptProviderTypeExt();

    public STLongHexNumber xgetCryptProviderTypeExt();

    public boolean isSetCryptProviderTypeExt();

    public void setCryptProviderTypeExt(byte[] var1);

    public void xsetCryptProviderTypeExt(STLongHexNumber var1);

    public void unsetCryptProviderTypeExt();

    public String getCryptProviderTypeExtSource();

    public STString xgetCryptProviderTypeExtSource();

    public boolean isSetCryptProviderTypeExtSource();

    public void setCryptProviderTypeExtSource(String var1);

    public void xsetCryptProviderTypeExtSource(STString var1);

    public void unsetCryptProviderTypeExtSource();

    public byte[] getHash();

    public XmlBase64Binary xgetHash();

    public boolean isSetHash();

    public void setHash(byte[] var1);

    public void xsetHash(XmlBase64Binary var1);

    public void unsetHash();

    public byte[] getSalt();

    public XmlBase64Binary xgetSalt();

    public boolean isSetSalt();

    public void setSalt(byte[] var1);

    public void xsetSalt(XmlBase64Binary var1);

    public void unsetSalt();
}

