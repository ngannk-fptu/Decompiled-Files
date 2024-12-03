/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;

public interface CTSlideMasterTextStyles
extends XmlObject {
    public static final DocumentFactory<CTSlideMasterTextStyles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidemastertextstylesb48dtype");
    public static final SchemaType type = Factory.getType();

    public CTTextListStyle getTitleStyle();

    public boolean isSetTitleStyle();

    public void setTitleStyle(CTTextListStyle var1);

    public CTTextListStyle addNewTitleStyle();

    public void unsetTitleStyle();

    public CTTextListStyle getBodyStyle();

    public boolean isSetBodyStyle();

    public void setBodyStyle(CTTextListStyle var1);

    public CTTextListStyle addNewBodyStyle();

    public void unsetBodyStyle();

    public CTTextListStyle getOtherStyle();

    public boolean isSetOtherStyle();

    public void setOtherStyle(CTTextListStyle var1);

    public CTTextListStyle addNewOtherStyle();

    public void unsetOtherStyle();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

