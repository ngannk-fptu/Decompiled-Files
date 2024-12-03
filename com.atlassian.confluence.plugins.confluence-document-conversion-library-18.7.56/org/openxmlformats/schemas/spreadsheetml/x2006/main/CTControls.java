/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControl;

public interface CTControls
extends XmlObject {
    public static final DocumentFactory<CTControls> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcontrols75fftype");
    public static final SchemaType type = Factory.getType();

    public List<CTControl> getControlList();

    public CTControl[] getControlArray();

    public CTControl getControlArray(int var1);

    public int sizeOfControlArray();

    public void setControlArray(CTControl[] var1);

    public void setControlArray(int var1, CTControl var2);

    public CTControl insertNewControl(int var1);

    public CTControl addNewControl();

    public void removeControl(int var1);
}

