/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

import javax.imageio.metadata.IIOMetadata;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public final class ImageIODebugUtil {
    private ImageIODebugUtil() {
    }

    public static void dumpMetadata(IIOMetadata meta, boolean nativeFormat) {
        String format = nativeFormat ? meta.getNativeMetadataFormatName() : "javax_imageio_1.0";
        Node node = meta.getAsTree(format);
        ImageIODebugUtil.dumpNode(node);
    }

    public static void dumpNode(Node node) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty("omit-xml-declaration", "yes");
            DOMSource src = new DOMSource(node);
            StreamResult res = new StreamResult(System.out);
            t.transform(src, res);
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}

