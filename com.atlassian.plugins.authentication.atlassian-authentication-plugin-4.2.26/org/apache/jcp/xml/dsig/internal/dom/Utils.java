/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Utils {
    private Utils() {
    }

    public static byte[] readBytesFromStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            int read;
            byte[] buf = new byte[1024];
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
                if (read >= 1024) continue;
                break;
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    static Set<Node> toNodeSet(Iterator<Node> i) {
        HashSet<Node> nodeSet = new HashSet<Node>();
        while (i.hasNext()) {
            Node n = i.next();
            nodeSet.add(n);
            if (n.getNodeType() != 1) continue;
            NamedNodeMap nnm = n.getAttributes();
            int length = nnm.getLength();
            for (int j = 0; j < length; ++j) {
                nodeSet.add(nnm.item(j));
            }
        }
        return nodeSet;
    }

    public static String parseIdFromSameDocumentURI(String uri) {
        if (uri.length() == 0) {
            return null;
        }
        String id = uri.substring(1);
        if (id.startsWith("xpointer(id(")) {
            int i1 = id.indexOf(39);
            int i2 = id.indexOf(39, i1 + 1);
            if (i1 >= 0 && i2 >= 0) {
                id = id.substring(i1 + 1, i2);
            }
        }
        return id;
    }

    public static boolean sameDocumentURI(String uri) {
        return uri != null && (uri.length() == 0 || uri.charAt(0) == '#');
    }

    static boolean secureValidation(XMLCryptoContext xc) {
        Boolean value;
        boolean secureValidation = true;
        if (xc != null && (value = (Boolean)xc.getProperty("org.apache.jcp.xml.dsig.secureValidation")) != null) {
            secureValidation = value;
        }
        return secureValidation;
    }
}

