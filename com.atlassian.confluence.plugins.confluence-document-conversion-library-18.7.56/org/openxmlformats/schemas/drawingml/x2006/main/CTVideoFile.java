/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTVideoFile
extends XmlObject {
    public static final DocumentFactory<CTVideoFile> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctvideofile85c8type");
    public static final SchemaType type = Factory.getType();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getLink();

    public STRelationshipId xgetLink();

    public void setLink(String var1);

    public void xsetLink(STRelationshipId var1);

    public String getContentType();

    public XmlString xgetContentType();

    public boolean isSetContentType();

    public void setContentType(String var1);

    public void xsetContentType(XmlString var1);

    public void unsetContentType();
}

