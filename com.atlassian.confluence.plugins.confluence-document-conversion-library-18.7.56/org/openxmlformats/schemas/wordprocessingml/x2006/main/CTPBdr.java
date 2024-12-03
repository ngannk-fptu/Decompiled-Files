/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;

public interface CTPBdr
extends XmlObject {
    public static final DocumentFactory<CTPBdr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpbdre388type");
    public static final SchemaType type = Factory.getType();

    public CTBorder getTop();

    public boolean isSetTop();

    public void setTop(CTBorder var1);

    public CTBorder addNewTop();

    public void unsetTop();

    public CTBorder getLeft();

    public boolean isSetLeft();

    public void setLeft(CTBorder var1);

    public CTBorder addNewLeft();

    public void unsetLeft();

    public CTBorder getBottom();

    public boolean isSetBottom();

    public void setBottom(CTBorder var1);

    public CTBorder addNewBottom();

    public void unsetBottom();

    public CTBorder getRight();

    public boolean isSetRight();

    public void setRight(CTBorder var1);

    public CTBorder addNewRight();

    public void unsetRight();

    public CTBorder getBetween();

    public boolean isSetBetween();

    public void setBetween(CTBorder var1);

    public CTBorder addNewBetween();

    public void unsetBetween();

    public CTBorder getBar();

    public boolean isSetBar();

    public void setBar(CTBorder var1);

    public CTBorder addNewBar();

    public void unsetBar();
}

