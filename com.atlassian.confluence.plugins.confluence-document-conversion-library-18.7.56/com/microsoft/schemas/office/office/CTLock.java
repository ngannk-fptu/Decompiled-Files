/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.vml.STExt;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTLock
extends XmlObject {
    public static final DocumentFactory<CTLock> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlock6b8etype");
    public static final SchemaType type = Factory.getType();

    public STExt.Enum getExt();

    public STExt xgetExt();

    public boolean isSetExt();

    public void setExt(STExt.Enum var1);

    public void xsetExt(STExt var1);

    public void unsetExt();

    public STTrueFalse.Enum getPosition();

    public STTrueFalse xgetPosition();

    public boolean isSetPosition();

    public void setPosition(STTrueFalse.Enum var1);

    public void xsetPosition(STTrueFalse var1);

    public void unsetPosition();

    public STTrueFalse.Enum getSelection();

    public STTrueFalse xgetSelection();

    public boolean isSetSelection();

    public void setSelection(STTrueFalse.Enum var1);

    public void xsetSelection(STTrueFalse var1);

    public void unsetSelection();

    public STTrueFalse.Enum getGrouping();

    public STTrueFalse xgetGrouping();

    public boolean isSetGrouping();

    public void setGrouping(STTrueFalse.Enum var1);

    public void xsetGrouping(STTrueFalse var1);

    public void unsetGrouping();

    public STTrueFalse.Enum getUngrouping();

    public STTrueFalse xgetUngrouping();

    public boolean isSetUngrouping();

    public void setUngrouping(STTrueFalse.Enum var1);

    public void xsetUngrouping(STTrueFalse var1);

    public void unsetUngrouping();

    public STTrueFalse.Enum getRotation();

    public STTrueFalse xgetRotation();

    public boolean isSetRotation();

    public void setRotation(STTrueFalse.Enum var1);

    public void xsetRotation(STTrueFalse var1);

    public void unsetRotation();

    public STTrueFalse.Enum getCropping();

    public STTrueFalse xgetCropping();

    public boolean isSetCropping();

    public void setCropping(STTrueFalse.Enum var1);

    public void xsetCropping(STTrueFalse var1);

    public void unsetCropping();

    public STTrueFalse.Enum getVerticies();

    public STTrueFalse xgetVerticies();

    public boolean isSetVerticies();

    public void setVerticies(STTrueFalse.Enum var1);

    public void xsetVerticies(STTrueFalse var1);

    public void unsetVerticies();

    public STTrueFalse.Enum getAdjusthandles();

    public STTrueFalse xgetAdjusthandles();

    public boolean isSetAdjusthandles();

    public void setAdjusthandles(STTrueFalse.Enum var1);

    public void xsetAdjusthandles(STTrueFalse var1);

    public void unsetAdjusthandles();

    public STTrueFalse.Enum getText();

    public STTrueFalse xgetText();

    public boolean isSetText();

    public void setText(STTrueFalse.Enum var1);

    public void xsetText(STTrueFalse var1);

    public void unsetText();

    public STTrueFalse.Enum getAspectratio();

    public STTrueFalse xgetAspectratio();

    public boolean isSetAspectratio();

    public void setAspectratio(STTrueFalse.Enum var1);

    public void xsetAspectratio(STTrueFalse var1);

    public void unsetAspectratio();

    public STTrueFalse.Enum getShapetype();

    public STTrueFalse xgetShapetype();

    public boolean isSetShapetype();

    public void setShapetype(STTrueFalse.Enum var1);

    public void xsetShapetype(STTrueFalse var1);

    public void unsetShapetype();
}

