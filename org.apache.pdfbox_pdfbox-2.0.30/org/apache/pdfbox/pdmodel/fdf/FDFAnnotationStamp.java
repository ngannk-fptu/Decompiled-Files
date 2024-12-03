/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.apache.pdfbox.util.Hex;
import org.apache.pdfbox.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDFAnnotationStamp
extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationStamp.class);
    public static final String SUBTYPE = "Stamp";

    public FDFAnnotationStamp() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationStamp(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationStamp(Element element) throws IOException {
        super(element);
        byte[] decodedAppearanceXML;
        String base64EncodedAppearance;
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        XPath xpath = XPathFactory.newInstance().newXPath();
        LOG.debug((Object)"Get the DOM Document for the stamp appearance");
        try {
            base64EncodedAppearance = xpath.evaluate("appearance", element);
        }
        catch (XPathExpressionException e) {
            LOG.error((Object)("Error while evaluating XPath expression for appearance: " + e));
            return;
        }
        try {
            decodedAppearanceXML = Hex.decodeBase64(base64EncodedAppearance);
        }
        catch (IllegalArgumentException ex) {
            LOG.error((Object)"Bad base64 encoded appearance ignored", (Throwable)ex);
            return;
        }
        if (base64EncodedAppearance != null && !base64EncodedAppearance.isEmpty()) {
            LOG.debug((Object)("Decoded XML: " + new String(decodedAppearanceXML)));
            Document stampAppearance = XMLUtil.parse(new ByteArrayInputStream(decodedAppearanceXML));
            Element appearanceEl = stampAppearance.getDocumentElement();
            if (!"dict".equalsIgnoreCase(appearanceEl.getNodeName())) {
                throw new IOException("Error while reading stamp document, root should be 'dict' and not '" + appearanceEl.getNodeName() + "'");
            }
            LOG.debug((Object)"Generate and set the appearance dictionary to the stamp annotation");
            this.annot.setItem(COSName.AP, (COSBase)this.parseStampAnnotationAppearanceXML(appearanceEl));
        }
    }

    private COSDictionary parseStampAnnotationAppearanceXML(Element appearanceXML) throws IOException {
        COSDictionary dictionary = new COSDictionary();
        dictionary.setItem(COSName.N, (COSBase)new COSStream());
        LOG.debug((Object)"Build dictionary for Appearance based on the appearanceXML");
        NodeList nodeList = appearanceXML.getChildNodes();
        String parentAttrKey = appearanceXML.getAttribute("KEY");
        LOG.debug((Object)("Appearance Root - tag: " + appearanceXML.getTagName() + ", name: " + appearanceXML.getNodeName() + ", key: " + parentAttrKey + ", children: " + nodeList.getLength()));
        if (!"AP".equals(appearanceXML.getAttribute("KEY"))) {
            LOG.warn((Object)(parentAttrKey + " => Not handling element: " + appearanceXML.getTagName() + " with key: " + appearanceXML.getAttribute("KEY")));
            return dictionary;
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " => Process " + child.getAttribute("KEY") + " item in the dictionary after processing the " + child.getTagName()));
                dictionary.setItem(child.getAttribute("KEY"), (COSBase)this.parseStreamElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + child.getAttribute("KEY")));
                continue;
            }
            LOG.warn((Object)(parentAttrKey + " => Not handling element: " + child.getTagName()));
        }
        return dictionary;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private COSStream parseStreamElement(Element streamEl) throws IOException {
        LOG.debug((Object)("Parse " + streamEl.getAttribute("KEY") + " Stream"));
        COSStream stream = new COSStream();
        NodeList nodeList = streamEl.getChildNodes();
        String parentAttrKey = streamEl.getAttribute("KEY");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            String childAttrKey = child.getAttribute("KEY");
            String childAttrVal = child.getAttribute("VAL");
            LOG.debug((Object)(parentAttrKey + " => reading child: " + child.getTagName() + " with key: " + childAttrKey));
            if ("INT".equalsIgnoreCase(child.getTagName())) {
                if ("Length".equals(childAttrKey)) continue;
                stream.setInt(COSName.getPDFName(childAttrKey), Integer.parseInt(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("FIXED".equalsIgnoreCase(child.getTagName())) {
                stream.setFloat(COSName.getPDFName(childAttrKey), Float.parseFloat(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("NAME".equalsIgnoreCase(child.getTagName())) {
                stream.setName(COSName.getPDFName(childAttrKey), childAttrVal);
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                stream.setBoolean(COSName.getPDFName(childAttrKey), Boolean.parseBoolean(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrVal));
                continue;
            }
            if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                stream.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseArrayElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey));
                continue;
            }
            if ("DICT".equalsIgnoreCase(child.getTagName())) {
                stream.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseDictElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey));
                continue;
            }
            if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                stream.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseStreamElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey));
                continue;
            }
            if ("DATA".equalsIgnoreCase(child.getTagName())) {
                OutputStream os;
                LOG.debug((Object)(parentAttrKey + " => Handling DATA with encoding: " + child.getAttribute("ENCODING")));
                if ("HEX".equals(child.getAttribute("ENCODING"))) {
                    os = null;
                    try {
                        os = stream.createRawOutputStream();
                        os.write(Hex.decodeHex(child.getTextContent()));
                        LOG.debug((Object)(parentAttrKey + " => Data was streamed"));
                        continue;
                    }
                    finally {
                        IOUtils.closeQuietly(os);
                    }
                }
                if ("ASCII".equals(child.getAttribute("ENCODING"))) {
                    os = null;
                    try {
                        os = stream.createOutputStream();
                        os.write(child.getTextContent().getBytes());
                        LOG.debug((Object)(parentAttrKey + " => Data was streamed"));
                        continue;
                    }
                    finally {
                        IOUtils.closeQuietly(os);
                    }
                }
                LOG.warn((Object)(parentAttrKey + " => Not handling element DATA encoding: " + child.getAttribute("ENCODING")));
                continue;
            }
            LOG.warn((Object)(parentAttrKey + " => Not handling child element: " + child.getTagName()));
        }
        return stream;
    }

    private COSArray parseArrayElement(Element arrayEl) throws IOException {
        LOG.debug((Object)("Parse " + arrayEl.getAttribute("KEY") + " Array"));
        COSArray array = new COSArray();
        NodeList nodeList = arrayEl.getChildNodes();
        String parentAttrKey = arrayEl.getAttribute("KEY");
        if ("BBox".equals(parentAttrKey) && nodeList.getLength() < 4) {
            throw new IOException("BBox does not have enough coordinates, only has: " + nodeList.getLength());
        }
        if ("Matrix".equals(parentAttrKey) && nodeList.getLength() < 6) {
            throw new IOException("Matrix does not have enough coordinates, only has: " + nodeList.getLength());
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            String childAttrKey = child.getAttribute("KEY");
            String childAttrVal = child.getAttribute("VAL");
            LOG.debug((Object)(parentAttrKey + " => reading child: " + child.getTagName() + " with key: " + childAttrKey));
            if ("INT".equalsIgnoreCase(child.getTagName()) || "FIXED".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(COSNumber.get(childAttrVal));
                continue;
            }
            if ("NAME".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(COSName.getPDFName(childAttrVal));
                continue;
            }
            if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(COSBoolean.getBoolean(Boolean.parseBoolean(childAttrVal)));
                continue;
            }
            if ("DICT".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(this.parseDictElement(child));
                continue;
            }
            if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(this.parseStreamElement(child));
                continue;
            }
            if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " value(" + i + "): " + childAttrVal));
                array.add(this.parseArrayElement(child));
                continue;
            }
            LOG.warn((Object)(parentAttrKey + " => Not handling child element: " + child.getTagName()));
        }
        return array;
    }

    private COSDictionary parseDictElement(Element dictEl) throws IOException {
        LOG.debug((Object)("Parse " + dictEl.getAttribute("KEY") + " Dictionary"));
        COSDictionary dict = new COSDictionary();
        NodeList nodeList = dictEl.getChildNodes();
        String parentAttrKey = dictEl.getAttribute("KEY");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            String childAttrKey = child.getAttribute("KEY");
            String childAttrVal = child.getAttribute("VAL");
            if ("DICT".equals(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " => Handling DICT element with key: " + childAttrKey));
                dict.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseDictElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey));
                continue;
            }
            if ("STREAM".equals(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " => Handling STREAM element with key: " + childAttrKey));
                dict.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseStreamElement(child));
                continue;
            }
            if ("NAME".equals(child.getTagName())) {
                LOG.debug((Object)(parentAttrKey + " => Handling NAME element with key: " + childAttrKey));
                dict.setName(COSName.getPDFName(childAttrKey), childAttrVal);
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("INT".equalsIgnoreCase(child.getTagName())) {
                dict.setInt(COSName.getPDFName(childAttrKey), Integer.parseInt(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("FIXED".equalsIgnoreCase(child.getTagName())) {
                dict.setFloat(COSName.getPDFName(childAttrKey), Float.parseFloat(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal));
                continue;
            }
            if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                dict.setBoolean(COSName.getPDFName(childAttrKey), Boolean.parseBoolean(childAttrVal));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrVal));
                continue;
            }
            if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                dict.setItem(COSName.getPDFName(childAttrKey), (COSBase)this.parseArrayElement(child));
                LOG.debug((Object)(parentAttrKey + " => Set " + childAttrKey));
                continue;
            }
            LOG.warn((Object)(parentAttrKey + " => NOT handling child element: " + child.getTagName()));
        }
        return dict;
    }
}

