/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

public interface CTFillStyleList
extends XmlObject {
    public static final DocumentFactory<CTFillStyleList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfillstylelist959dtype");
    public static final SchemaType type = Factory.getType();

    public List<CTNoFillProperties> getNoFillList();

    public CTNoFillProperties[] getNoFillArray();

    public CTNoFillProperties getNoFillArray(int var1);

    public int sizeOfNoFillArray();

    public void setNoFillArray(CTNoFillProperties[] var1);

    public void setNoFillArray(int var1, CTNoFillProperties var2);

    public CTNoFillProperties insertNewNoFill(int var1);

    public CTNoFillProperties addNewNoFill();

    public void removeNoFill(int var1);

    public List<CTSolidColorFillProperties> getSolidFillList();

    public CTSolidColorFillProperties[] getSolidFillArray();

    public CTSolidColorFillProperties getSolidFillArray(int var1);

    public int sizeOfSolidFillArray();

    public void setSolidFillArray(CTSolidColorFillProperties[] var1);

    public void setSolidFillArray(int var1, CTSolidColorFillProperties var2);

    public CTSolidColorFillProperties insertNewSolidFill(int var1);

    public CTSolidColorFillProperties addNewSolidFill();

    public void removeSolidFill(int var1);

    public List<CTGradientFillProperties> getGradFillList();

    public CTGradientFillProperties[] getGradFillArray();

    public CTGradientFillProperties getGradFillArray(int var1);

    public int sizeOfGradFillArray();

    public void setGradFillArray(CTGradientFillProperties[] var1);

    public void setGradFillArray(int var1, CTGradientFillProperties var2);

    public CTGradientFillProperties insertNewGradFill(int var1);

    public CTGradientFillProperties addNewGradFill();

    public void removeGradFill(int var1);

    public List<CTBlipFillProperties> getBlipFillList();

    public CTBlipFillProperties[] getBlipFillArray();

    public CTBlipFillProperties getBlipFillArray(int var1);

    public int sizeOfBlipFillArray();

    public void setBlipFillArray(CTBlipFillProperties[] var1);

    public void setBlipFillArray(int var1, CTBlipFillProperties var2);

    public CTBlipFillProperties insertNewBlipFill(int var1);

    public CTBlipFillProperties addNewBlipFill();

    public void removeBlipFill(int var1);

    public List<CTPatternFillProperties> getPattFillList();

    public CTPatternFillProperties[] getPattFillArray();

    public CTPatternFillProperties getPattFillArray(int var1);

    public int sizeOfPattFillArray();

    public void setPattFillArray(CTPatternFillProperties[] var1);

    public void setPattFillArray(int var1, CTPatternFillProperties var2);

    public CTPatternFillProperties insertNewPattFill(int var1);

    public CTPatternFillProperties addNewPattFill();

    public void removePattFill(int var1);

    public List<CTGroupFillProperties> getGrpFillList();

    public CTGroupFillProperties[] getGrpFillArray();

    public CTGroupFillProperties getGrpFillArray(int var1);

    public int sizeOfGrpFillArray();

    public void setGrpFillArray(CTGroupFillProperties[] var1);

    public void setGrpFillArray(int var1, CTGroupFillProperties var2);

    public CTGroupFillProperties insertNewGrpFill(int var1);

    public CTGroupFillProperties addNewGrpFill();

    public void removeGrpFill(int var1);
}

