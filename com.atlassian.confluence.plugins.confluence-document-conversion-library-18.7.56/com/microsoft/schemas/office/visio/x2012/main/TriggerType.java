/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.RefByType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.RefByType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface TriggerType
extends XmlObject {
    public static final DocumentFactory<TriggerType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "triggertype2933type");
    public static final SchemaType type = Factory.getType();

    public List<RefByType> getRefByList();

    public RefByType[] getRefByArray();

    public RefByType getRefByArray(int var1);

    public int sizeOfRefByArray();

    public void setRefByArray(RefByType[] var1);

    public void setRefByArray(int var1, RefByType var2);

    public RefByType insertNewRefBy(int var1);

    public RefByType addNewRefBy();

    public void removeRefBy(int var1);

    public String getN();

    public XmlString xgetN();

    public void setN(String var1);

    public void xsetN(XmlString var1);
}

