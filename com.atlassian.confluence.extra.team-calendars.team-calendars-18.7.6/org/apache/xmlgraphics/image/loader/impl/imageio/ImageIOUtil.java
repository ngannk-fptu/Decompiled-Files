/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl.imageio;

import javax.imageio.metadata.IIOMetadata;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ImageIOUtil {
    public static final Object IMAGEIO_METADATA = IIOMetadata.class;

    private ImageIOUtil() {
    }

    public static void extractResolution(IIOMetadata iiometa, ImageSize size) {
        Element metanode;
        Element dim;
        if (iiometa != null && iiometa.isStandardMetadataFormatSupported() && (dim = ImageIOUtil.getChild(metanode = (Element)iiometa.getAsTree("javax_imageio_1.0"), "Dimension")) != null) {
            float value;
            double dpiHorz = size.getDpiHorizontal();
            double dpiVert = size.getDpiVertical();
            Element child = ImageIOUtil.getChild(dim, "HorizontalPixelSize");
            if (child != null && (value = Float.parseFloat(child.getAttribute("value"))) != 0.0f && !Float.isInfinite(value)) {
                dpiHorz = 25.4f / value;
            }
            if ((child = ImageIOUtil.getChild(dim, "VerticalPixelSize")) != null && (value = Float.parseFloat(child.getAttribute("value"))) != 0.0f && !Float.isInfinite(value)) {
                dpiVert = 25.4f / value;
            }
            size.setResolution(dpiHorz, dpiVert);
            size.calcSizeFromPixels();
        }
    }

    public static Element getChild(Element el, String name) {
        NodeList nodes = el.getElementsByTagName(name);
        if (nodes.getLength() > 0) {
            return (Element)nodes.item(0);
        }
        return null;
    }

    public static void dumpMetadataToSystemOut(IIOMetadata iiometa) {
        String[] metanames;
        for (String metaname : metanames = iiometa.getMetadataFormatNames()) {
            System.out.println("--->" + metaname);
            ImageIOUtil.dumpNodeToSystemOut(iiometa.getAsTree(metaname));
        }
    }

    private static void dumpNodeToSystemOut(Node node) {
        Transformer trans = null;
        try {
            trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty("omit-xml-declaration", "yes");
            trans.setOutputProperty("indent", "yes");
            DOMSource src = new DOMSource(node);
            StreamResult res = new StreamResult(System.out);
            trans.transform(src, res);
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}

