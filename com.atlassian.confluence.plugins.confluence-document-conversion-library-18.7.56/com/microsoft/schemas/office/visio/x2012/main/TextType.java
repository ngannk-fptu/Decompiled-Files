/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.CpType
 *  com.microsoft.schemas.office.visio.x2012.main.FldType
 *  com.microsoft.schemas.office.visio.x2012.main.PpType
 *  com.microsoft.schemas.office.visio.x2012.main.TpType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.CpType;
import com.microsoft.schemas.office.visio.x2012.main.FldType;
import com.microsoft.schemas.office.visio.x2012.main.PpType;
import com.microsoft.schemas.office.visio.x2012.main.TpType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface TextType
extends XmlObject {
    public static final DocumentFactory<TextType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "texttyped2ectype");
    public static final SchemaType type = Factory.getType();

    public List<CpType> getCpList();

    public CpType[] getCpArray();

    public CpType getCpArray(int var1);

    public int sizeOfCpArray();

    public void setCpArray(CpType[] var1);

    public void setCpArray(int var1, CpType var2);

    public CpType insertNewCp(int var1);

    public CpType addNewCp();

    public void removeCp(int var1);

    public List<PpType> getPpList();

    public PpType[] getPpArray();

    public PpType getPpArray(int var1);

    public int sizeOfPpArray();

    public void setPpArray(PpType[] var1);

    public void setPpArray(int var1, PpType var2);

    public PpType insertNewPp(int var1);

    public PpType addNewPp();

    public void removePp(int var1);

    public List<TpType> getTpList();

    public TpType[] getTpArray();

    public TpType getTpArray(int var1);

    public int sizeOfTpArray();

    public void setTpArray(TpType[] var1);

    public void setTpArray(int var1, TpType var2);

    public TpType insertNewTp(int var1);

    public TpType addNewTp();

    public void removeTp(int var1);

    public List<FldType> getFldList();

    public FldType[] getFldArray();

    public FldType getFldArray(int var1);

    public int sizeOfFldArray();

    public void setFldArray(FldType[] var1);

    public void setFldArray(int var1, FldType var2);

    public FldType insertNewFld(int var1);

    public FldType addNewFld();

    public void removeFld(int var1);
}

