/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;

public interface CTWorkbookProtection
extends XmlObject {
    public static final DocumentFactory<CTWorkbookProtection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctworkbookprotection56bctype");
    public static final SchemaType type = Factory.getType();

    public byte[] getWorkbookPassword();

    public STUnsignedShortHex xgetWorkbookPassword();

    public boolean isSetWorkbookPassword();

    public void setWorkbookPassword(byte[] var1);

    public void xsetWorkbookPassword(STUnsignedShortHex var1);

    public void unsetWorkbookPassword();

    public String getWorkbookPasswordCharacterSet();

    public XmlString xgetWorkbookPasswordCharacterSet();

    public boolean isSetWorkbookPasswordCharacterSet();

    public void setWorkbookPasswordCharacterSet(String var1);

    public void xsetWorkbookPasswordCharacterSet(XmlString var1);

    public void unsetWorkbookPasswordCharacterSet();

    public byte[] getRevisionsPassword();

    public STUnsignedShortHex xgetRevisionsPassword();

    public boolean isSetRevisionsPassword();

    public void setRevisionsPassword(byte[] var1);

    public void xsetRevisionsPassword(STUnsignedShortHex var1);

    public void unsetRevisionsPassword();

    public String getRevisionsPasswordCharacterSet();

    public XmlString xgetRevisionsPasswordCharacterSet();

    public boolean isSetRevisionsPasswordCharacterSet();

    public void setRevisionsPasswordCharacterSet(String var1);

    public void xsetRevisionsPasswordCharacterSet(XmlString var1);

    public void unsetRevisionsPasswordCharacterSet();

    public boolean getLockStructure();

    public XmlBoolean xgetLockStructure();

    public boolean isSetLockStructure();

    public void setLockStructure(boolean var1);

    public void xsetLockStructure(XmlBoolean var1);

    public void unsetLockStructure();

    public boolean getLockWindows();

    public XmlBoolean xgetLockWindows();

    public boolean isSetLockWindows();

    public void setLockWindows(boolean var1);

    public void xsetLockWindows(XmlBoolean var1);

    public void unsetLockWindows();

    public boolean getLockRevision();

    public XmlBoolean xgetLockRevision();

    public boolean isSetLockRevision();

    public void setLockRevision(boolean var1);

    public void xsetLockRevision(XmlBoolean var1);

    public void unsetLockRevision();

    public String getRevisionsAlgorithmName();

    public STXstring xgetRevisionsAlgorithmName();

    public boolean isSetRevisionsAlgorithmName();

    public void setRevisionsAlgorithmName(String var1);

    public void xsetRevisionsAlgorithmName(STXstring var1);

    public void unsetRevisionsAlgorithmName();

    public byte[] getRevisionsHashValue();

    public XmlBase64Binary xgetRevisionsHashValue();

    public boolean isSetRevisionsHashValue();

    public void setRevisionsHashValue(byte[] var1);

    public void xsetRevisionsHashValue(XmlBase64Binary var1);

    public void unsetRevisionsHashValue();

    public byte[] getRevisionsSaltValue();

    public XmlBase64Binary xgetRevisionsSaltValue();

    public boolean isSetRevisionsSaltValue();

    public void setRevisionsSaltValue(byte[] var1);

    public void xsetRevisionsSaltValue(XmlBase64Binary var1);

    public void unsetRevisionsSaltValue();

    public long getRevisionsSpinCount();

    public XmlUnsignedInt xgetRevisionsSpinCount();

    public boolean isSetRevisionsSpinCount();

    public void setRevisionsSpinCount(long var1);

    public void xsetRevisionsSpinCount(XmlUnsignedInt var1);

    public void unsetRevisionsSpinCount();

    public String getWorkbookAlgorithmName();

    public STXstring xgetWorkbookAlgorithmName();

    public boolean isSetWorkbookAlgorithmName();

    public void setWorkbookAlgorithmName(String var1);

    public void xsetWorkbookAlgorithmName(STXstring var1);

    public void unsetWorkbookAlgorithmName();

    public byte[] getWorkbookHashValue();

    public XmlBase64Binary xgetWorkbookHashValue();

    public boolean isSetWorkbookHashValue();

    public void setWorkbookHashValue(byte[] var1);

    public void xsetWorkbookHashValue(XmlBase64Binary var1);

    public void unsetWorkbookHashValue();

    public byte[] getWorkbookSaltValue();

    public XmlBase64Binary xgetWorkbookSaltValue();

    public boolean isSetWorkbookSaltValue();

    public void setWorkbookSaltValue(byte[] var1);

    public void xsetWorkbookSaltValue(XmlBase64Binary var1);

    public void unsetWorkbookSaltValue();

    public long getWorkbookSpinCount();

    public XmlUnsignedInt xgetWorkbookSpinCount();

    public boolean isSetWorkbookSpinCount();

    public void setWorkbookSpinCount(long var1);

    public void xsetWorkbookSpinCount(XmlUnsignedInt var1);

    public void unsetWorkbookSpinCount();
}

