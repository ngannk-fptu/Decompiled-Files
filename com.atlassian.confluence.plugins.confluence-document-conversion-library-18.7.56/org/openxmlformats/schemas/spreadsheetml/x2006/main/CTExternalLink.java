/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDdeLink
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleLink
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDdeLink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalBook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleLink;

public interface CTExternalLink
extends XmlObject {
    public static final DocumentFactory<CTExternalLink> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternallink966etype");
    public static final SchemaType type = Factory.getType();

    public CTExternalBook getExternalBook();

    public boolean isSetExternalBook();

    public void setExternalBook(CTExternalBook var1);

    public CTExternalBook addNewExternalBook();

    public void unsetExternalBook();

    public CTDdeLink getDdeLink();

    public boolean isSetDdeLink();

    public void setDdeLink(CTDdeLink var1);

    public CTDdeLink addNewDdeLink();

    public void unsetDdeLink();

    public CTOleLink getOleLink();

    public boolean isSetOleLink();

    public void setOleLink(CTOleLink var1);

    public CTOleLink addNewOleLink();

    public void unsetOleLink();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

