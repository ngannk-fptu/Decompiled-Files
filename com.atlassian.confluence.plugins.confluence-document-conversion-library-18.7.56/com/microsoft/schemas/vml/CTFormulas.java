/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.vml.CTF;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTFormulas
extends XmlObject {
    public static final DocumentFactory<CTFormulas> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctformulas808btype");
    public static final SchemaType type = Factory.getType();

    public List<CTF> getFList();

    public CTF[] getFArray();

    public CTF getFArray(int var1);

    public int sizeOfFArray();

    public void setFArray(CTF[] var1);

    public void setFArray(int var1, CTF var2);

    public CTF insertNewF(int var1);

    public CTF addNewF();

    public void removeF(int var1);
}

