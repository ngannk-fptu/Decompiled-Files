/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.STShapeID
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectEmbed
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectLink
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeID;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectEmbed;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectLink;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;

public interface CTOleObject
extends XmlObject {
    public static final DocumentFactory<CTOleObject> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoleobject5da8type");
    public static final SchemaType type = Factory.getType();

    public CTOleObjectEmbed getEmbed();

    public boolean isSetEmbed();

    public void setEmbed(CTOleObjectEmbed var1);

    public CTOleObjectEmbed addNewEmbed();

    public void unsetEmbed();

    public CTOleObjectLink getLink();

    public boolean isSetLink();

    public void setLink(CTOleObjectLink var1);

    public CTOleObjectLink addNewLink();

    public void unsetLink();

    public CTPicture getPic();

    public boolean isSetPic();

    public void setPic(CTPicture var1);

    public CTPicture addNewPic();

    public void unsetPic();

    public String getSpid();

    public STShapeID xgetSpid();

    public boolean isSetSpid();

    public void setSpid(String var1);

    public void xsetSpid(STShapeID var1);

    public void unsetSpid();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();

    public boolean getShowAsIcon();

    public XmlBoolean xgetShowAsIcon();

    public boolean isSetShowAsIcon();

    public void setShowAsIcon(boolean var1);

    public void xsetShowAsIcon(XmlBoolean var1);

    public void unsetShowAsIcon();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();

    public int getImgW();

    public STPositiveCoordinate32 xgetImgW();

    public boolean isSetImgW();

    public void setImgW(int var1);

    public void xsetImgW(STPositiveCoordinate32 var1);

    public void unsetImgW();

    public int getImgH();

    public STPositiveCoordinate32 xgetImgH();

    public boolean isSetImgH();

    public void setImgH(int var1);

    public void xsetImgH(STPositiveCoordinate32 var1);

    public void unsetImgH();

    public String getProgId();

    public XmlString xgetProgId();

    public boolean isSetProgId();

    public void setProgId(String var1);

    public void xsetProgId(XmlString var1);

    public void unsetProgId();
}

