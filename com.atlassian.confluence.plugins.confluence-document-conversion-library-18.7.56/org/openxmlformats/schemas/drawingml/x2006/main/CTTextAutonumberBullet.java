/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextBulletStartAtNum;

public interface CTTextAutonumberBullet
extends XmlObject {
    public static final DocumentFactory<CTTextAutonumberBullet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextautonumberbulletd602type");
    public static final SchemaType type = Factory.getType();

    public STTextAutonumberScheme.Enum getType();

    public STTextAutonumberScheme xgetType();

    public void setType(STTextAutonumberScheme.Enum var1);

    public void xsetType(STTextAutonumberScheme var1);

    public int getStartAt();

    public STTextBulletStartAtNum xgetStartAt();

    public boolean isSetStartAt();

    public void setStartAt(int var1);

    public void xsetStartAt(STTextBulletStartAtNum var1);

    public void unsetStartAt();
}

