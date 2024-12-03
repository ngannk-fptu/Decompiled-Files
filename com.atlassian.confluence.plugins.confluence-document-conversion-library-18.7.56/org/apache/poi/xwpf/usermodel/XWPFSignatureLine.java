/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.vml.CTImageData;
import javax.xml.namespace.QName;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.dsig.SignatureLine;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;

public class XWPFSignatureLine
extends SignatureLine {
    private static final String MS_VML_URN = "urn:schemas-microsoft-com:vml";
    private CTSignatureLine line;

    public void parse(XWPFDocument doc) throws XmlException {
        this.line = XPathHelper.selectProperty(doc.getDocument(), CTSignatureLine.class, null, {new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "body")}, {new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p")}, {new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r")}, {new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pict")}, {new QName(MS_VML_URN, "shape")}, {QNAME_SIGNATURE_LINE});
        if (this.line != null) {
            this.setSignatureShape(this.line);
            this.parse();
        }
    }

    public void add(XWPFParagraph paragraph) {
        XWPFRun r = paragraph.createRun();
        CTPicture pict = r.getCTR().addNewPict();
        this.add(pict, (image, type) -> paragraph.getDocument().addPictureData(image, XWPFSignatureLine.mapType(type)));
    }

    @Override
    protected void setRelationId(CTImageData imageData, String relId) {
        imageData.setId2(relId);
    }

    private static PictureType mapType(PictureType type) throws InvalidFormatException {
        switch (type) {
            case BMP: {
                return PictureType.BMP;
            }
            case DIB: {
                return PictureType.DIB;
            }
            case EMF: {
                return PictureType.EMF;
            }
            case EPS: {
                return PictureType.EPS;
            }
            case GIF: {
                return PictureType.GIF;
            }
            case JPEG: {
                return PictureType.JPEG;
            }
            case PICT: {
                return PictureType.PICT;
            }
            case PNG: {
                return PictureType.PNG;
            }
            case TIFF: {
                return PictureType.TIFF;
            }
            case WMF: {
                return PictureType.WMF;
            }
            case WPG: {
                return PictureType.WPG;
            }
            case WDP: {
                return PictureType.WDP;
            }
        }
        throw new InvalidFormatException("Unsupported picture format " + (Object)((Object)type));
    }
}

