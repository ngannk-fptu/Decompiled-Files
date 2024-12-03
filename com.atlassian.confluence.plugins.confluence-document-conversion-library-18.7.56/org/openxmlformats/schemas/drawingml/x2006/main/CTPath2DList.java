/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;

public interface CTPath2DList
extends XmlObject {
    public static final DocumentFactory<CTPath2DList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath2dlistb010type");
    public static final SchemaType type = Factory.getType();

    public List<CTPath2D> getPathList();

    public CTPath2D[] getPathArray();

    public CTPath2D getPathArray(int var1);

    public int sizeOfPathArray();

    public void setPathArray(CTPath2D[] var1);

    public void setPathArray(int var1, CTPath2D var2);

    public CTPath2D insertNewPath(int var1);

    public CTPath2D addNewPath();

    public void removePath(int var1);
}

