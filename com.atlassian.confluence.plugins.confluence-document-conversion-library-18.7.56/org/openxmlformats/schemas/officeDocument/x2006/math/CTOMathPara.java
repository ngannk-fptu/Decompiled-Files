/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathParaPr
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathParaPr;

public interface CTOMathPara
extends XmlObject {
    public static final DocumentFactory<CTOMathPara> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctomathpara8825type");
    public static final SchemaType type = Factory.getType();

    public CTOMathParaPr getOMathParaPr();

    public boolean isSetOMathParaPr();

    public void setOMathParaPr(CTOMathParaPr var1);

    public CTOMathParaPr addNewOMathParaPr();

    public void unsetOMathParaPr();

    public List<CTOMath> getOMathList();

    public CTOMath[] getOMathArray();

    public CTOMath getOMathArray(int var1);

    public int sizeOfOMathArray();

    public void setOMathArray(CTOMath[] var1);

    public void setOMathArray(int var1, CTOMath var2);

    public CTOMath insertNewOMath(int var1);

    public CTOMath addNewOMath();

    public void removeOMath(int var1);
}

