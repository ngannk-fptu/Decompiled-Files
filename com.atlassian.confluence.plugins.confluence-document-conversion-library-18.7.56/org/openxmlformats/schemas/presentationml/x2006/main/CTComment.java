/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 *  org.openxmlformats.schemas.presentationml.x2006.main.STIndex
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.STIndex;

public interface CTComment
extends XmlObject {
    public static final DocumentFactory<CTComment> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcomment2d10type");
    public static final SchemaType type = Factory.getType();

    public CTPoint2D getPos();

    public void setPos(CTPoint2D var1);

    public CTPoint2D addNewPos();

    public String getText();

    public XmlString xgetText();

    public void setText(String var1);

    public void xsetText(XmlString var1);

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public long getAuthorId();

    public XmlUnsignedInt xgetAuthorId();

    public void setAuthorId(long var1);

    public void xsetAuthorId(XmlUnsignedInt var1);

    public Calendar getDt();

    public XmlDateTime xgetDt();

    public boolean isSetDt();

    public void setDt(Calendar var1);

    public void xsetDt(XmlDateTime var1);

    public void unsetDt();

    public long getIdx();

    public STIndex xgetIdx();

    public void setIdx(long var1);

    public void xsetIdx(STIndex var1);
}

