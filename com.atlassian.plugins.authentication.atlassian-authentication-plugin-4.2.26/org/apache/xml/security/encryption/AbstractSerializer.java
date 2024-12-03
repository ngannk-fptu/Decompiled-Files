/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.encryption.Serializer;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSerializer
implements Serializer {
    private final Canonicalizer canon;
    protected final boolean secureValidation;

    protected AbstractSerializer(String canonAlg, boolean secureValidation) throws InvalidCanonicalizerException {
        this.canon = Canonicalizer.getInstance(canonAlg);
        this.secureValidation = secureValidation;
    }

    @Override
    public byte[] serializeToByteArray(Element element) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            this.canon.canonicalizeSubtree(element, baos);
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    @Override
    public byte[] serializeToByteArray(NodeList content) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            for (int i = 0; i < content.getLength(); ++i) {
                this.canon.canonicalizeSubtree(content.item(i), baos);
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected static byte[] createContext(byte[] source, Node ctx) throws XMLEncryptionException {
        try {
            Throwable throwable = null;
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();){
                byte[] byArray;
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)byteArrayOutputStream, StandardCharsets.UTF_8);
                Throwable throwable2 = null;
                try {
                    outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
                    HashMap<String, String> storedNamespaces = new HashMap<String, String>();
                    for (Node wk = ctx; wk != null; wk = wk.getParentNode()) {
                        NamedNodeMap atts = wk.getAttributes();
                        if (atts == null) continue;
                        for (int i = 0; i < atts.getLength(); ++i) {
                            Node att = atts.item(i);
                            String nodeName = att.getNodeName();
                            if (!"xmlns".equals(nodeName) && !nodeName.startsWith("xmlns:") || storedNamespaces.containsKey(att.getNodeName())) continue;
                            outputStreamWriter.write(" ");
                            outputStreamWriter.write(nodeName);
                            outputStreamWriter.write("=\"");
                            outputStreamWriter.write(att.getNodeValue());
                            outputStreamWriter.write("\"");
                            storedNamespaces.put(nodeName, att.getNodeValue());
                        }
                    }
                    outputStreamWriter.write(">");
                    outputStreamWriter.flush();
                    byteArrayOutputStream.write(source);
                    outputStreamWriter.write("</dummy>");
                    outputStreamWriter.close();
                    byArray = byteArrayOutputStream.toByteArray();
                }
                catch (Throwable throwable3) {
                    try {
                        try {
                            throwable2 = throwable3;
                            throw throwable3;
                        }
                        catch (Throwable throwable4) {
                            AbstractSerializer.$closeResource(throwable2, outputStreamWriter);
                            throw throwable4;
                        }
                    }
                    catch (Throwable throwable5) {
                        throwable = throwable5;
                        throw throwable5;
                    }
                }
                AbstractSerializer.$closeResource(throwable2, outputStreamWriter);
                return byArray;
            }
        }
        catch (IOException e) {
            throw new XMLEncryptionException(e);
        }
    }
}

