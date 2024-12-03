/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortMethod
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortCondition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortMethod;

public interface CTSortState
extends XmlObject {
    public static final DocumentFactory<CTSortState> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsortstatea372type");
    public static final SchemaType type = Factory.getType();

    public List<CTSortCondition> getSortConditionList();

    public CTSortCondition[] getSortConditionArray();

    public CTSortCondition getSortConditionArray(int var1);

    public int sizeOfSortConditionArray();

    public void setSortConditionArray(CTSortCondition[] var1);

    public void setSortConditionArray(int var1, CTSortCondition var2);

    public CTSortCondition insertNewSortCondition(int var1);

    public CTSortCondition addNewSortCondition();

    public void removeSortCondition(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getColumnSort();

    public XmlBoolean xgetColumnSort();

    public boolean isSetColumnSort();

    public void setColumnSort(boolean var1);

    public void xsetColumnSort(XmlBoolean var1);

    public void unsetColumnSort();

    public boolean getCaseSensitive();

    public XmlBoolean xgetCaseSensitive();

    public boolean isSetCaseSensitive();

    public void setCaseSensitive(boolean var1);

    public void xsetCaseSensitive(XmlBoolean var1);

    public void unsetCaseSensitive();

    public STSortMethod.Enum getSortMethod();

    public STSortMethod xgetSortMethod();

    public boolean isSetSortMethod();

    public void setSortMethod(STSortMethod.Enum var1);

    public void xsetSortMethod(STSortMethod var1);

    public void unsetSortMethod();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);
}

