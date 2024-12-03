/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDFAnnotationLink
extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationLink.class);
    public static final String SUBTYPE = "Link";

    public FDFAnnotationLink() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationLink(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationLink(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            Node namedItem;
            NodeList uri = (NodeList)xpath.evaluate("OnActivation/Action/URI", element, XPathConstants.NODESET);
            if (uri.getLength() > 0 && (namedItem = uri.item(0).getAttributes().getNamedItem("Name")) != null && namedItem.getNodeValue() != null) {
                PDActionURI actionURI = new PDActionURI();
                actionURI.setURI(namedItem.getNodeValue());
                this.annot.setItem(COSName.A, (COSObjectable)actionURI);
            }
        }
        catch (XPathExpressionException e) {
            LOG.debug((Object)"Error while evaluating XPath expression", (Throwable)e);
        }
    }
}

