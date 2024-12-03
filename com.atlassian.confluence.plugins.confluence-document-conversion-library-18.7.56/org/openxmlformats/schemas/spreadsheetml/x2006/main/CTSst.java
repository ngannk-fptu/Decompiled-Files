/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;

public interface CTSst
extends XmlObject {
    public static final DocumentFactory<CTSst> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsst44f3type");
    public static final SchemaType type = Factory.getType();

    public List<CTRst> getSiList();

    public CTRst[] getSiArray();

    public CTRst getSiArray(int var1);

    public int sizeOfSiArray();

    public void setSiArray(CTRst[] var1);

    public void setSiArray(int var1, CTRst var2);

    public CTRst insertNewSi(int var1);

    public CTRst addNewSi();

    public void removeSi(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();

    public long getUniqueCount();

    public XmlUnsignedInt xgetUniqueCount();

    public boolean isSetUniqueCount();

    public void setUniqueCount(long var1);

    public void xsetUniqueCount(XmlUnsignedInt var1);

    public void unsetUniqueCount();
}

