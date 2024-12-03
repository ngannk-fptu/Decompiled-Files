/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.xslf.draw.geom.XSLFAdjustValue;
import org.apache.poi.xslf.draw.geom.XSLFConnectionSite;
import org.apache.poi.xslf.draw.geom.XSLFGuide;
import org.apache.poi.xslf.draw.geom.XSLFPath;
import org.apache.poi.xslf.draw.geom.XSLFPolarAdjustHandle;
import org.apache.poi.xslf.draw.geom.XSLFXYAdjustHandle;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;

public class XSLFCustomGeometry {
    public static CustomGeometry convertCustomGeometry(CTCustomGeometry2D custGeom) {
        CTPath2DList pl;
        int n;
        int n2;
        XmlObject[] xmlObjectArray;
        CustomGeometry cg = new CustomGeometry();
        if (custGeom.isSetAhLst()) {
            CTAdjustHandleList ahLst = custGeom.getAhLst();
            for (XmlObject xmlObject : ahLst.getAhXYArray()) {
                cg.addAdjustHandle(new XSLFXYAdjustHandle((CTXYAdjustHandle)xmlObject));
            }
            xmlObjectArray = ahLst.getAhPolarArray();
            n2 = xmlObjectArray.length;
            for (n = 0; n < n2; ++n) {
                XmlObject xmlObject = xmlObjectArray[n];
                cg.addAdjustHandle(new XSLFPolarAdjustHandle((CTPolarAdjustHandle)xmlObject));
            }
        }
        if (custGeom.isSetAvLst()) {
            CTGeomGuideList avLst = custGeom.getAvLst();
            xmlObjectArray = avLst.getGdArray();
            n2 = xmlObjectArray.length;
            for (n = 0; n < n2; ++n) {
                XmlObject xmlObject = xmlObjectArray[n];
                cg.addAdjustGuide(new XSLFAdjustValue((CTGeomGuide)xmlObject));
            }
        }
        if (custGeom.isSetGdLst()) {
            CTGeomGuideList gdLst = custGeom.getGdLst();
            xmlObjectArray = gdLst.getGdArray();
            n2 = xmlObjectArray.length;
            for (n = 0; n < n2; ++n) {
                XmlObject xmlObject = xmlObjectArray[n];
                cg.addGeomGuide(new XSLFGuide((CTGeomGuide)xmlObject));
            }
        }
        if (custGeom.isSetRect()) {
            CTConnectionSite[] r = custGeom.getRect();
            cg.setTextBounds(r.xgetL().getStringValue(), r.xgetT().getStringValue(), r.xgetR().getStringValue(), r.xgetB().getStringValue());
        }
        if (custGeom.isSetCxnLst()) {
            for (CTConnectionSite cxn : custGeom.getCxnLst().getCxnArray()) {
                cg.addConnectionSite(new XSLFConnectionSite(cxn));
            }
        }
        if ((pl = custGeom.getPathLst()) != null) {
            for (CTPath2D cTPath2D : pl.getPathArray()) {
                cg.addPath(new XSLFPath(cTPath2D));
            }
        }
        return cg;
    }
}

