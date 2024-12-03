/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;

public interface CTPicture
extends XmlObject {
    public static final DocumentFactory<CTPicture> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpicture1054type");
    public static final SchemaType type = Factory.getType();

    public CTRel getMovie();

    public boolean isSetMovie();

    public void setMovie(CTRel var1);

    public CTRel addNewMovie();

    public void unsetMovie();

    public CTControl getControl();

    public boolean isSetControl();

    public void setControl(CTControl var1);

    public CTControl addNewControl();

    public void unsetControl();
}

