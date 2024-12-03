/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTAuthors
extends XmlObject {
    public static final DocumentFactory<CTAuthors> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctauthorsb8a7type");
    public static final SchemaType type = Factory.getType();

    public List<String> getAuthorList();

    public String[] getAuthorArray();

    public String getAuthorArray(int var1);

    public List<STXstring> xgetAuthorList();

    public STXstring[] xgetAuthorArray();

    public STXstring xgetAuthorArray(int var1);

    public int sizeOfAuthorArray();

    public void setAuthorArray(String[] var1);

    public void setAuthorArray(int var1, String var2);

    public void xsetAuthorArray(STXstring[] var1);

    public void xsetAuthorArray(int var1, STXstring var2);

    public void insertAuthor(int var1, String var2);

    public void addAuthor(String var1);

    public STXstring insertNewAuthor(int var1);

    public STXstring addNewAuthor();

    public void removeAuthor(int var1);
}

