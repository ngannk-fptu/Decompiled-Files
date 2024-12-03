/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnPos;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumRestart;

public interface CTFtnProps
extends XmlObject {
    public static final DocumentFactory<CTFtnProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctftnprops2df8type");
    public static final SchemaType type = Factory.getType();

    public CTFtnPos getPos();

    public boolean isSetPos();

    public void setPos(CTFtnPos var1);

    public CTFtnPos addNewPos();

    public void unsetPos();

    public CTNumFmt getNumFmt();

    public boolean isSetNumFmt();

    public void setNumFmt(CTNumFmt var1);

    public CTNumFmt addNewNumFmt();

    public void unsetNumFmt();

    public CTDecimalNumber getNumStart();

    public boolean isSetNumStart();

    public void setNumStart(CTDecimalNumber var1);

    public CTDecimalNumber addNewNumStart();

    public void unsetNumStart();

    public CTNumRestart getNumRestart();

    public boolean isSetNumRestart();

    public void setNumRestart(CTNumRestart var1);

    public CTNumRestart addNewNumRestart();

    public void unsetNumRestart();
}

