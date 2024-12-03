/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.STName;

public interface CTCommentAuthor
extends XmlObject {
    public static final DocumentFactory<CTCommentAuthor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcommentauthora405type");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public XmlUnsignedInt xgetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public String getName();

    public STName xgetName();

    public void setName(String var1);

    public void xsetName(STName var1);

    public String getInitials();

    public STName xgetInitials();

    public void setInitials(String var1);

    public void xsetInitials(STName var1);

    public long getLastIdx();

    public XmlUnsignedInt xgetLastIdx();

    public void setLastIdx(long var1);

    public void xsetLastIdx(XmlUnsignedInt var1);

    public long getClrIdx();

    public XmlUnsignedInt xgetClrIdx();

    public void setClrIdx(long var1);

    public void xsetClrIdx(XmlUnsignedInt var1);
}

