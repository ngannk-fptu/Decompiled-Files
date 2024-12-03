/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyPr;

public interface CTRuby
extends XmlObject {
    public static final DocumentFactory<CTRuby> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctruby9dcetype");
    public static final SchemaType type = Factory.getType();

    public CTRubyPr getRubyPr();

    public void setRubyPr(CTRubyPr var1);

    public CTRubyPr addNewRubyPr();

    public CTRubyContent getRt();

    public void setRt(CTRubyContent var1);

    public CTRubyContent addNewRt();

    public CTRubyContent getRubyBase();

    public void setRubyBase(CTRubyContent var1);

    public CTRubyContent addNewRubyBase();
}

