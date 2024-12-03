/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DArcTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DClose;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathFillMode;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;

public interface CTPath2D
extends XmlObject {
    public static final DocumentFactory<CTPath2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath2d73d2type");
    public static final SchemaType type = Factory.getType();

    public List<CTPath2DClose> getCloseList();

    public CTPath2DClose[] getCloseArray();

    public CTPath2DClose getCloseArray(int var1);

    public int sizeOfCloseArray();

    public void setCloseArray(CTPath2DClose[] var1);

    public void setCloseArray(int var1, CTPath2DClose var2);

    public CTPath2DClose insertNewClose(int var1);

    public CTPath2DClose addNewClose();

    public void removeClose(int var1);

    public List<CTPath2DMoveTo> getMoveToList();

    public CTPath2DMoveTo[] getMoveToArray();

    public CTPath2DMoveTo getMoveToArray(int var1);

    public int sizeOfMoveToArray();

    public void setMoveToArray(CTPath2DMoveTo[] var1);

    public void setMoveToArray(int var1, CTPath2DMoveTo var2);

    public CTPath2DMoveTo insertNewMoveTo(int var1);

    public CTPath2DMoveTo addNewMoveTo();

    public void removeMoveTo(int var1);

    public List<CTPath2DLineTo> getLnToList();

    public CTPath2DLineTo[] getLnToArray();

    public CTPath2DLineTo getLnToArray(int var1);

    public int sizeOfLnToArray();

    public void setLnToArray(CTPath2DLineTo[] var1);

    public void setLnToArray(int var1, CTPath2DLineTo var2);

    public CTPath2DLineTo insertNewLnTo(int var1);

    public CTPath2DLineTo addNewLnTo();

    public void removeLnTo(int var1);

    public List<CTPath2DArcTo> getArcToList();

    public CTPath2DArcTo[] getArcToArray();

    public CTPath2DArcTo getArcToArray(int var1);

    public int sizeOfArcToArray();

    public void setArcToArray(CTPath2DArcTo[] var1);

    public void setArcToArray(int var1, CTPath2DArcTo var2);

    public CTPath2DArcTo insertNewArcTo(int var1);

    public CTPath2DArcTo addNewArcTo();

    public void removeArcTo(int var1);

    public List<CTPath2DQuadBezierTo> getQuadBezToList();

    public CTPath2DQuadBezierTo[] getQuadBezToArray();

    public CTPath2DQuadBezierTo getQuadBezToArray(int var1);

    public int sizeOfQuadBezToArray();

    public void setQuadBezToArray(CTPath2DQuadBezierTo[] var1);

    public void setQuadBezToArray(int var1, CTPath2DQuadBezierTo var2);

    public CTPath2DQuadBezierTo insertNewQuadBezTo(int var1);

    public CTPath2DQuadBezierTo addNewQuadBezTo();

    public void removeQuadBezTo(int var1);

    public List<CTPath2DCubicBezierTo> getCubicBezToList();

    public CTPath2DCubicBezierTo[] getCubicBezToArray();

    public CTPath2DCubicBezierTo getCubicBezToArray(int var1);

    public int sizeOfCubicBezToArray();

    public void setCubicBezToArray(CTPath2DCubicBezierTo[] var1);

    public void setCubicBezToArray(int var1, CTPath2DCubicBezierTo var2);

    public CTPath2DCubicBezierTo insertNewCubicBezTo(int var1);

    public CTPath2DCubicBezierTo addNewCubicBezTo();

    public void removeCubicBezTo(int var1);

    public long getW();

    public STPositiveCoordinate xgetW();

    public boolean isSetW();

    public void setW(long var1);

    public void xsetW(STPositiveCoordinate var1);

    public void unsetW();

    public long getH();

    public STPositiveCoordinate xgetH();

    public boolean isSetH();

    public void setH(long var1);

    public void xsetH(STPositiveCoordinate var1);

    public void unsetH();

    public STPathFillMode.Enum getFill();

    public STPathFillMode xgetFill();

    public boolean isSetFill();

    public void setFill(STPathFillMode.Enum var1);

    public void xsetFill(STPathFillMode var1);

    public void unsetFill();

    public boolean getStroke();

    public XmlBoolean xgetStroke();

    public boolean isSetStroke();

    public void setStroke(boolean var1);

    public void xsetStroke(XmlBoolean var1);

    public void unsetStroke();

    public boolean getExtrusionOk();

    public XmlBoolean xgetExtrusionOk();

    public boolean isSetExtrusionOk();

    public void setExtrusionOk(boolean var1);

    public void xsetExtrusionOk(XmlBoolean var1);

    public void unsetExtrusionOk();
}

