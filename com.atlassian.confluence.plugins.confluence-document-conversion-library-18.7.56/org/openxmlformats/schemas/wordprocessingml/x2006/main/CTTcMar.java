/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;

public interface CTTcMar
extends XmlObject {
    public static final DocumentFactory<CTTcMar> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttcmar23c3type");
    public static final SchemaType type = Factory.getType();

    public CTTblWidth getTop();

    public boolean isSetTop();

    public void setTop(CTTblWidth var1);

    public CTTblWidth addNewTop();

    public void unsetTop();

    public CTTblWidth getStart();

    public boolean isSetStart();

    public void setStart(CTTblWidth var1);

    public CTTblWidth addNewStart();

    public void unsetStart();

    public CTTblWidth getLeft();

    public boolean isSetLeft();

    public void setLeft(CTTblWidth var1);

    public CTTblWidth addNewLeft();

    public void unsetLeft();

    public CTTblWidth getBottom();

    public boolean isSetBottom();

    public void setBottom(CTTblWidth var1);

    public CTTblWidth addNewBottom();

    public void unsetBottom();

    public CTTblWidth getEnd();

    public boolean isSetEnd();

    public void setEnd(CTTblWidth var1);

    public CTTblWidth addNewEnd();

    public void unsetEnd();

    public CTTblWidth getRight();

    public boolean isSetRight();

    public void setRight(CTTblWidth var1);

    public CTTblWidth addNewRight();

    public void unsetRight();
}

