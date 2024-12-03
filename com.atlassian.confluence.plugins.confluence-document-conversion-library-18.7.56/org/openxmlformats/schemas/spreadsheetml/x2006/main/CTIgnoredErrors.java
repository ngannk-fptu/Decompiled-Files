/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;

public interface CTIgnoredErrors
extends XmlObject {
    public static final DocumentFactory<CTIgnoredErrors> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctignorederrorsbebctype");
    public static final SchemaType type = Factory.getType();

    public List<CTIgnoredError> getIgnoredErrorList();

    public CTIgnoredError[] getIgnoredErrorArray();

    public CTIgnoredError getIgnoredErrorArray(int var1);

    public int sizeOfIgnoredErrorArray();

    public void setIgnoredErrorArray(CTIgnoredError[] var1);

    public void setIgnoredErrorArray(int var1, CTIgnoredError var2);

    public CTIgnoredError insertNewIgnoredError(int var1);

    public CTIgnoredError addNewIgnoredError();

    public void removeIgnoredError(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

