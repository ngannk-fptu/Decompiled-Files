/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnPos
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnPos;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumRestart;

public interface CTEdnProps
extends XmlObject {
    public static final DocumentFactory<CTEdnProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctednpropsd887type");
    public static final SchemaType type = Factory.getType();

    public CTEdnPos getPos();

    public boolean isSetPos();

    public void setPos(CTEdnPos var1);

    public CTEdnPos addNewPos();

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

