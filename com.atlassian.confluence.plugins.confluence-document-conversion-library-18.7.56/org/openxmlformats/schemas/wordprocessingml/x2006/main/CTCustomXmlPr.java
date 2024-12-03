/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAttr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

public interface CTCustomXmlPr
extends XmlObject {
    public static final DocumentFactory<CTCustomXmlPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomxmlpr4b8atype");
    public static final SchemaType type = Factory.getType();

    public CTString getPlaceholder();

    public boolean isSetPlaceholder();

    public void setPlaceholder(CTString var1);

    public CTString addNewPlaceholder();

    public void unsetPlaceholder();

    public List<CTAttr> getAttrList();

    public CTAttr[] getAttrArray();

    public CTAttr getAttrArray(int var1);

    public int sizeOfAttrArray();

    public void setAttrArray(CTAttr[] var1);

    public void setAttrArray(int var1, CTAttr var2);

    public CTAttr insertNewAttr(int var1);

    public CTAttr addNewAttr();

    public void removeAttr(int var1);
}

