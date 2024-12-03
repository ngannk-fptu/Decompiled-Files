/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.DataType
 *  com.microsoft.schemas.office.visio.x2012.main.ForeignDataType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.DataType;
import com.microsoft.schemas.office.visio.x2012.main.ForeignDataType;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import com.microsoft.schemas.office.visio.x2012.main.TextType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ShapeSheetType
extends SheetType {
    public static final DocumentFactory<ShapeSheetType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "shapesheettype59bbtype");
    public static final SchemaType type = Factory.getType();

    public TextType getText();

    public boolean isSetText();

    public void setText(TextType var1);

    public TextType addNewText();

    public void unsetText();

    public DataType getData1();

    public boolean isSetData1();

    public void setData1(DataType var1);

    public DataType addNewData1();

    public void unsetData1();

    public DataType getData2();

    public boolean isSetData2();

    public void setData2(DataType var1);

    public DataType addNewData2();

    public void unsetData2();

    public DataType getData3();

    public boolean isSetData3();

    public void setData3(DataType var1);

    public DataType addNewData3();

    public void unsetData3();

    public ForeignDataType getForeignData();

    public boolean isSetForeignData();

    public void setForeignData(ForeignDataType var1);

    public ForeignDataType addNewForeignData();

    public void unsetForeignData();

    public ShapesType getShapes();

    public boolean isSetShapes();

    public void setShapes(ShapesType var1);

    public ShapesType addNewShapes();

    public void unsetShapes();

    public long getID();

    public XmlUnsignedInt xgetID();

    public void setID(long var1);

    public void xsetID(XmlUnsignedInt var1);

    public long getOriginalID();

    public XmlUnsignedInt xgetOriginalID();

    public boolean isSetOriginalID();

    public void setOriginalID(long var1);

    public void xsetOriginalID(XmlUnsignedInt var1);

    public void unsetOriginalID();

    public boolean getDel();

    public XmlBoolean xgetDel();

    public boolean isSetDel();

    public void setDel(boolean var1);

    public void xsetDel(XmlBoolean var1);

    public void unsetDel();

    public long getMasterShape();

    public XmlUnsignedInt xgetMasterShape();

    public boolean isSetMasterShape();

    public void setMasterShape(long var1);

    public void xsetMasterShape(XmlUnsignedInt var1);

    public void unsetMasterShape();

    public String getUniqueID();

    public XmlString xgetUniqueID();

    public boolean isSetUniqueID();

    public void setUniqueID(String var1);

    public void xsetUniqueID(XmlString var1);

    public void unsetUniqueID();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();

    public String getNameU();

    public XmlString xgetNameU();

    public boolean isSetNameU();

    public void setNameU(String var1);

    public void xsetNameU(XmlString var1);

    public void unsetNameU();

    public boolean getIsCustomName();

    public XmlBoolean xgetIsCustomName();

    public boolean isSetIsCustomName();

    public void setIsCustomName(boolean var1);

    public void xsetIsCustomName(XmlBoolean var1);

    public void unsetIsCustomName();

    public boolean getIsCustomNameU();

    public XmlBoolean xgetIsCustomNameU();

    public boolean isSetIsCustomNameU();

    public void setIsCustomNameU(boolean var1);

    public void xsetIsCustomNameU(XmlBoolean var1);

    public void unsetIsCustomNameU();

    public long getMaster();

    public XmlUnsignedInt xgetMaster();

    public boolean isSetMaster();

    public void setMaster(long var1);

    public void xsetMaster(XmlUnsignedInt var1);

    public void unsetMaster();

    public String getType();

    public XmlToken xgetType();

    public boolean isSetType();

    public void setType(String var1);

    public void xsetType(XmlToken var1);

    public void unsetType();
}

