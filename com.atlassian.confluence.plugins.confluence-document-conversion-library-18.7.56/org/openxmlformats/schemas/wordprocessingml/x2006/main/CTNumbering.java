/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPicBullet
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPicBullet;

public interface CTNumbering
extends XmlObject {
    public static final DocumentFactory<CTNumbering> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumberingfdf9type");
    public static final SchemaType type = Factory.getType();

    public List<CTNumPicBullet> getNumPicBulletList();

    public CTNumPicBullet[] getNumPicBulletArray();

    public CTNumPicBullet getNumPicBulletArray(int var1);

    public int sizeOfNumPicBulletArray();

    public void setNumPicBulletArray(CTNumPicBullet[] var1);

    public void setNumPicBulletArray(int var1, CTNumPicBullet var2);

    public CTNumPicBullet insertNewNumPicBullet(int var1);

    public CTNumPicBullet addNewNumPicBullet();

    public void removeNumPicBullet(int var1);

    public List<CTAbstractNum> getAbstractNumList();

    public CTAbstractNum[] getAbstractNumArray();

    public CTAbstractNum getAbstractNumArray(int var1);

    public int sizeOfAbstractNumArray();

    public void setAbstractNumArray(CTAbstractNum[] var1);

    public void setAbstractNumArray(int var1, CTAbstractNum var2);

    public CTAbstractNum insertNewAbstractNum(int var1);

    public CTAbstractNum addNewAbstractNum();

    public void removeAbstractNum(int var1);

    public List<CTNum> getNumList();

    public CTNum[] getNumArray();

    public CTNum getNumArray(int var1);

    public int sizeOfNumArray();

    public void setNumArray(CTNum[] var1);

    public void setNumArray(int var1, CTNum var2);

    public CTNum insertNewNum(int var1);

    public CTNum addNewNum();

    public void removeNum(int var1);

    public CTDecimalNumber getNumIdMacAtCleanup();

    public boolean isSetNumIdMacAtCleanup();

    public void setNumIdMacAtCleanup(CTDecimalNumber var1);

    public CTDecimalNumber addNewNumIdMacAtCleanup();

    public void unsetNumIdMacAtCleanup();
}

