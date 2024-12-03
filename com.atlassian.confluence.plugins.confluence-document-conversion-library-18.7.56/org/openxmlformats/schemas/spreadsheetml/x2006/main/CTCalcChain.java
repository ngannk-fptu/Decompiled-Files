/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;

public interface CTCalcChain
extends XmlObject {
    public static final DocumentFactory<CTCalcChain> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcalcchain5a0btype");
    public static final SchemaType type = Factory.getType();

    public List<CTCalcCell> getCList();

    public CTCalcCell[] getCArray();

    public CTCalcCell getCArray(int var1);

    public int sizeOfCArray();

    public void setCArray(CTCalcCell[] var1);

    public void setCArray(int var1, CTCalcCell var2);

    public CTCalcCell insertNewC(int var1);

    public CTCalcCell addNewC();

    public void removeC(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

