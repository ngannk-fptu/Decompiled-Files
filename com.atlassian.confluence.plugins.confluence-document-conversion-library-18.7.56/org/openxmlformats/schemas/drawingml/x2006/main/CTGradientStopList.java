/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;

public interface CTGradientStopList
extends XmlObject {
    public static final DocumentFactory<CTGradientStopList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgradientstoplist7eabtype");
    public static final SchemaType type = Factory.getType();

    public List<CTGradientStop> getGsList();

    public CTGradientStop[] getGsArray();

    public CTGradientStop getGsArray(int var1);

    public int sizeOfGsArray();

    public void setGsArray(CTGradientStop[] var1);

    public void setGsArray(int var1, CTGradientStop var2);

    public CTGradientStop insertNewGs(int var1);

    public CTGradientStop addNewGs();

    public void removeGs(int var1);
}

