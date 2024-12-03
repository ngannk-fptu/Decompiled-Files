/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathArg;

public interface CTMR
extends XmlObject {
    public static final DocumentFactory<CTMR> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmr7ccdtype");
    public static final SchemaType type = Factory.getType();

    public List<CTOMathArg> getEList();

    public CTOMathArg[] getEArray();

    public CTOMathArg getEArray(int var1);

    public int sizeOfEArray();

    public void setEArray(CTOMathArg[] var1);

    public void setEArray(int var1, CTOMathArg var2);

    public CTOMathArg insertNewE(int var1);

    public CTOMathArg addNewE();

    public void removeE(int var1);
}

