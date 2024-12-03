/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.ConnectsType;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PageContentsType
extends XmlObject {
    public static final DocumentFactory<PageContentsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pagecontentstypea5d0type");
    public static final SchemaType type = Factory.getType();

    public ShapesType getShapes();

    public boolean isSetShapes();

    public void setShapes(ShapesType var1);

    public ShapesType addNewShapes();

    public void unsetShapes();

    public ConnectsType getConnects();

    public boolean isSetConnects();

    public void setConnects(ConnectsType var1);

    public ConnectsType addNewConnects();

    public void unsetConnects();
}

