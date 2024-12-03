/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeCondition;

public interface CTTLTimeConditionList
extends XmlObject {
    public static final DocumentFactory<CTTLTimeConditionList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttltimeconditionlistebbbtype");
    public static final SchemaType type = Factory.getType();

    public List<CTTLTimeCondition> getCondList();

    public CTTLTimeCondition[] getCondArray();

    public CTTLTimeCondition getCondArray(int var1);

    public int sizeOfCondArray();

    public void setCondArray(CTTLTimeCondition[] var1);

    public void setCondArray(int var1, CTTLTimeCondition var2);

    public CTTLTimeCondition insertNewCond(int var1);

    public CTTLTimeCondition addNewCond();

    public void removeCond(int var1);
}

