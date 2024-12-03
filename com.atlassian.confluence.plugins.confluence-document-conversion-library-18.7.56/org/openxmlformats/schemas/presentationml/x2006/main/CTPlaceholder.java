/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 *  org.openxmlformats.schemas.presentationml.x2006.main.STDirection
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.STDirection;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderSize;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderType;

public interface CTPlaceholder
extends XmlObject {
    public static final DocumentFactory<CTPlaceholder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctplaceholder9efctype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public STPlaceholderType.Enum getType();

    public STPlaceholderType xgetType();

    public boolean isSetType();

    public void setType(STPlaceholderType.Enum var1);

    public void xsetType(STPlaceholderType var1);

    public void unsetType();

    public STDirection.Enum getOrient();

    public STDirection xgetOrient();

    public boolean isSetOrient();

    public void setOrient(STDirection.Enum var1);

    public void xsetOrient(STDirection var1);

    public void unsetOrient();

    public STPlaceholderSize.Enum getSz();

    public STPlaceholderSize xgetSz();

    public boolean isSetSz();

    public void setSz(STPlaceholderSize.Enum var1);

    public void xsetSz(STPlaceholderSize var1);

    public void unsetSz();

    public long getIdx();

    public XmlUnsignedInt xgetIdx();

    public boolean isSetIdx();

    public void setIdx(long var1);

    public void xsetIdx(XmlUnsignedInt var1);

    public void unsetIdx();

    public boolean getHasCustomPrompt();

    public XmlBoolean xgetHasCustomPrompt();

    public boolean isSetHasCustomPrompt();

    public void setHasCustomPrompt(boolean var1);

    public void xsetHasCustomPrompt(XmlBoolean var1);

    public void unsetHasCustomPrompt();
}

