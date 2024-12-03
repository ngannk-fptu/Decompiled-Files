/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ShapesType
extends XmlObject {
    public static final DocumentFactory<ShapesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "shapestypef507type");
    public static final SchemaType type = Factory.getType();

    public List<ShapeSheetType> getShapeList();

    public ShapeSheetType[] getShapeArray();

    public ShapeSheetType getShapeArray(int var1);

    public int sizeOfShapeArray();

    public void setShapeArray(ShapeSheetType[] var1);

    public void setShapeArray(int var1, ShapeSheetType var2);

    public ShapeSheetType insertNewShape(int var1);

    public ShapeSheetType addNewShape();

    public void removeShape(int var1);
}

