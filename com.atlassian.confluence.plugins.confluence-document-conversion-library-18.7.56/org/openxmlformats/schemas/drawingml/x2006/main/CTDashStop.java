/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;

public interface CTDashStop
extends XmlObject {
    public static final DocumentFactory<CTDashStop> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdashstopdc4ftype");
    public static final SchemaType type = Factory.getType();

    public Object getD();

    public STPositivePercentage xgetD();

    public void setD(Object var1);

    public void xsetD(STPositivePercentage var1);

    public Object getSp();

    public STPositivePercentage xgetSp();

    public void setSp(Object var1);

    public void xsetSp(STPositivePercentage var1);
}

