/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;

public interface CTDashStopList
extends XmlObject {
    public static final DocumentFactory<CTDashStopList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdashstoplist920dtype");
    public static final SchemaType type = Factory.getType();

    public List<CTDashStop> getDsList();

    public CTDashStop[] getDsArray();

    public CTDashStop getDsArray(int var1);

    public int sizeOfDsArray();

    public void setDsArray(CTDashStop[] var1);

    public void setDsArray(int var1, CTDashStop var2);

    public CTDashStop insertNewDs(int var1);

    public CTDashStop addNewDs();

    public void removeDs(int var1);
}

