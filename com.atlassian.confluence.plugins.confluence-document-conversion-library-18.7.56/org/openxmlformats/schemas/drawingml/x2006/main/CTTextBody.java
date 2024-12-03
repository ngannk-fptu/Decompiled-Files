/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

public interface CTTextBody
extends XmlObject {
    public static final DocumentFactory<CTTextBody> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextbodya3catype");
    public static final SchemaType type = Factory.getType();

    public CTTextBodyProperties getBodyPr();

    public void setBodyPr(CTTextBodyProperties var1);

    public CTTextBodyProperties addNewBodyPr();

    public CTTextListStyle getLstStyle();

    public boolean isSetLstStyle();

    public void setLstStyle(CTTextListStyle var1);

    public CTTextListStyle addNewLstStyle();

    public void unsetLstStyle();

    public List<CTTextParagraph> getPList();

    public CTTextParagraph[] getPArray();

    public CTTextParagraph getPArray(int var1);

    public int sizeOfPArray();

    public void setPArray(CTTextParagraph[] var1);

    public void setPArray(int var1, CTTextParagraph var2);

    public CTTextParagraph insertNewP(int var1);

    public CTTextParagraph addNewP();

    public void removeP(int var1);
}

