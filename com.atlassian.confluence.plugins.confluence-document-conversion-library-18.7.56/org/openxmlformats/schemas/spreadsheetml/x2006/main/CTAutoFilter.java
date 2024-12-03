/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTAutoFilter
extends XmlObject {
    public static final DocumentFactory<CTAutoFilter> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctautofiltera8d0type");
    public static final SchemaType type = Factory.getType();

    public List<CTFilterColumn> getFilterColumnList();

    public CTFilterColumn[] getFilterColumnArray();

    public CTFilterColumn getFilterColumnArray(int var1);

    public int sizeOfFilterColumnArray();

    public void setFilterColumnArray(CTFilterColumn[] var1);

    public void setFilterColumnArray(int var1, CTFilterColumn var2);

    public CTFilterColumn insertNewFilterColumn(int var1);

    public CTFilterColumn addNewFilterColumn();

    public void removeFilterColumn(int var1);

    public CTSortState getSortState();

    public boolean isSetSortState();

    public void setSortState(CTSortState var1);

    public CTSortState addNewSortState();

    public void unsetSortState();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getRef();

    public STRef xgetRef();

    public boolean isSetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public void unsetRef();
}

