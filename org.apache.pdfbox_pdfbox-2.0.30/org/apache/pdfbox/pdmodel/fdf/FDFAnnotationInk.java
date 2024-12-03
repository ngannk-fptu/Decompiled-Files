/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDFAnnotationInk
extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationInk.class);
    public static final String SUBTYPE = "Ink";

    public FDFAnnotationInk() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationInk(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationInk(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            NodeList gestures = (NodeList)xpath.evaluate("inklist/gesture", element, XPathConstants.NODESET);
            if (gestures.getLength() == 0) {
                throw new IOException("Error: missing element 'gesture'");
            }
            ArrayList<float[]> inklist = new ArrayList<float[]>();
            for (int i = 0; i < gestures.getLength(); ++i) {
                Node node = gestures.item(i);
                if (!(node instanceof Element)) continue;
                String gesture = node.getFirstChild().getNodeValue();
                String[] gestureValues = gesture.split(",|;");
                float[] values = new float[gestureValues.length];
                for (int j = 0; j < gestureValues.length; ++j) {
                    values[j] = Float.parseFloat(gestureValues[j]);
                }
                inklist.add(values);
            }
            this.setInkList(inklist);
        }
        catch (XPathExpressionException e) {
            LOG.debug((Object)"Error while evaluating XPath expression for inklist gestures");
        }
    }

    public final void setInkList(List<float[]> inklist) {
        COSArray newInklist = new COSArray();
        for (float[] array : inklist) {
            COSArray newArray = new COSArray();
            newArray.setFloatArray(array);
            newInklist.add(newArray);
        }
        this.annot.setItem(COSName.INKLIST, (COSBase)newInklist);
    }

    public List<float[]> getInkList() {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.INKLIST);
        if (array != null) {
            ArrayList<float[]> retval = new ArrayList<float[]>();
            for (COSBase entry : array) {
                retval.add(((COSArray)entry).toFloatArray());
            }
            return retval;
        }
        return null;
    }
}

