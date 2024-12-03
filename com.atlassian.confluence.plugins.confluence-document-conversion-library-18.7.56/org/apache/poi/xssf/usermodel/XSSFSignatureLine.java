/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.excel.STObjectType;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTShape;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.dsig.SignatureLine;
import org.apache.poi.schemas.vmldrawing.CTXML;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFVMLDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public class XSSFSignatureLine
extends SignatureLine {
    private static final String MS_VML_URN = "urn:schemas-microsoft-com:vml";

    public void parse(XSSFSheet sheet) throws XmlException {
        XSSFVMLDrawing vml = sheet.getVMLDrawing(false);
        if (vml == null) {
            return;
        }
        CTSignatureLine line = XPathHelper.selectProperty(vml.getDocument(), CTSignatureLine.class, null, {XSSFVMLDrawing.QNAME_VMLDRAWING}, {new QName(MS_VML_URN, "shape")}, {QNAME_SIGNATURE_LINE});
        if (line != null) {
            this.setSignatureShape(line);
            this.parse();
        }
    }

    public void add(XSSFSheet sheet, XSSFClientAnchor anchor) {
        XSSFVMLDrawing vml = sheet.getVMLDrawing(true);
        CTXML root = vml.getDocument().getXml();
        this.add(root, (byte[] image, PictureType type) -> this.addPicture(image, type, sheet));
        CTShape shape = this.getSignatureShape();
        CTClientData clientData = shape.addNewClientData();
        String anchorStr = anchor.getCol1() + ", " + anchor.getDx1() + ", " + anchor.getRow1() + ", " + anchor.getDy1() + ", " + anchor.getCol2() + ", " + anchor.getDx2() + ", " + anchor.getRow2() + ", " + anchor.getDy2();
        clientData.addAnchor(anchorStr);
        clientData.setObjectType(STObjectType.PICT);
        clientData.addSizeWithCells(STTrueFalseBlank.X);
        clientData.addCF("pict");
        clientData.addAutoPict(STTrueFalseBlank.X);
    }

    @Override
    protected void setRelationId(CTImageData imageData, String relId) {
        imageData.setRelid(relId);
    }

    private String addPicture(byte[] image, PictureType type, XSSFSheet sheet) throws InvalidFormatException {
        XSSFWorkbook wb = sheet.getWorkbook();
        XSSFVMLDrawing vml = sheet.getVMLDrawing(false);
        POIXMLRelation xtype = XSSFSignatureLine.mapType(type);
        int idx = wb.getNextPartNumber(xtype, -1);
        POIXMLDocumentPart.RelationPart rp = vml.createRelationship(xtype, wb.getXssfFactory(), idx, false);
        Object dp = rp.getDocumentPart();
        try (OutputStream out = ((POIXMLDocumentPart)dp).getPackagePart().getOutputStream();){
            out.write(image);
        }
        catch (IOException e) {
            throw new POIXMLException(e);
        }
        return rp.getRelationship().getId();
    }

    private static POIXMLRelation mapType(PictureType type) throws InvalidFormatException {
        switch (type) {
            case BMP: {
                return XSSFRelation.IMAGE_BMP;
            }
            case DIB: {
                return XSSFRelation.IMAGE_DIB;
            }
            case EMF: {
                return XSSFRelation.IMAGE_EMF;
            }
            case EPS: {
                return XSSFRelation.IMAGE_EPS;
            }
            case GIF: {
                return XSSFRelation.IMAGE_GIF;
            }
            case JPEG: {
                return XSSFRelation.IMAGE_JPEG;
            }
            case PICT: {
                return XSSFRelation.IMAGE_PICT;
            }
            case PNG: {
                return XSSFRelation.IMAGE_PNG;
            }
            case TIFF: {
                return XSSFRelation.IMAGE_TIFF;
            }
            case WMF: {
                return XSSFRelation.IMAGE_WMF;
            }
            case WPG: {
                return XSSFRelation.IMAGE_WPG;
            }
        }
        throw new InvalidFormatException("Unsupported picture format " + (Object)((Object)type));
    }
}

