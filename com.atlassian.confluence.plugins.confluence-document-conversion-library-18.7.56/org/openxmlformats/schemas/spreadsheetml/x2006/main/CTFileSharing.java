/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;

public interface CTFileSharing
extends XmlObject {
    public static final DocumentFactory<CTFileSharing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfilesharing5c9ftype");
    public static final SchemaType type = Factory.getType();

    public boolean getReadOnlyRecommended();

    public XmlBoolean xgetReadOnlyRecommended();

    public boolean isSetReadOnlyRecommended();

    public void setReadOnlyRecommended(boolean var1);

    public void xsetReadOnlyRecommended(XmlBoolean var1);

    public void unsetReadOnlyRecommended();

    public String getUserName();

    public STXstring xgetUserName();

    public boolean isSetUserName();

    public void setUserName(String var1);

    public void xsetUserName(STXstring var1);

    public void unsetUserName();

    public byte[] getReservationPassword();

    public STUnsignedShortHex xgetReservationPassword();

    public boolean isSetReservationPassword();

    public void setReservationPassword(byte[] var1);

    public void xsetReservationPassword(STUnsignedShortHex var1);

    public void unsetReservationPassword();

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

