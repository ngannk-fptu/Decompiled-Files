/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.xml;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public final class DOMSerializer {
    private static final String PARAM_PRETTY_PRINT = "format-pretty-print";
    private static final String PARAM_XML_DECLARATION = "xml-declaration";
    private final LSSerializer serializer;
    private final LSOutput output;

    private DOMSerializer() {
        DOMImplementationLS dOMImplementationLS = Support.getImplementation();
        this.serializer = dOMImplementationLS.createLSSerializer();
        this.output = dOMImplementationLS.createLSOutput();
    }

    public DOMSerializer(OutputStream outputStream, String string) {
        this();
        this.output.setByteStream(outputStream);
        this.output.setEncoding(string);
    }

    public DOMSerializer(Writer writer) {
        this();
        this.output.setCharacterStream(writer);
    }

    public void setPrettyPrint(boolean bl) {
        DOMConfiguration dOMConfiguration = this.serializer.getDomConfig();
        if (dOMConfiguration.canSetParameter(PARAM_PRETTY_PRINT, bl)) {
            dOMConfiguration.setParameter(PARAM_PRETTY_PRINT, bl);
        }
    }

    public boolean getPrettyPrint() {
        return Boolean.TRUE.equals(this.serializer.getDomConfig().getParameter(PARAM_PRETTY_PRINT));
    }

    private void setXMLDeclaration(boolean bl) {
        this.serializer.getDomConfig().setParameter(PARAM_XML_DECLARATION, bl);
    }

    public void serialize(Document document) {
        this.serializeImpl(document, true);
    }

    public void serialize(Node node) {
        this.serializeImpl(node, false);
    }

    private void serializeImpl(Node node, boolean bl) {
        this.setXMLDeclaration(bl);
        this.serializer.write(node, this.output);
    }

    private static class Support {
        private static final DOMImplementationRegistry DOM_REGISTRY = Support.createDOMRegistry();

        private Support() {
        }

        static DOMImplementationLS getImplementation() {
            DOMImplementationLS dOMImplementationLS = (DOMImplementationLS)((Object)DOM_REGISTRY.getDOMImplementation("LS 3.0"));
            if (dOMImplementationLS == null) {
                DOMImplementationList dOMImplementationList = DOM_REGISTRY.getDOMImplementationList("");
                System.err.println("DOM implementations (" + dOMImplementationList.getLength() + "):");
                for (int i = 0; i < dOMImplementationList.getLength(); ++i) {
                    System.err.println("    " + dOMImplementationList.item(i));
                }
                throw new IllegalStateException("Could not create DOM Implementation (no LS support found)");
            }
            return dOMImplementationLS;
        }

        private static DOMImplementationRegistry createDOMRegistry() {
            try {
                return DOMImplementationRegistry.newInstance();
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException reflectiveOperationException) {
                throw new IllegalStateException(reflectiveOperationException);
            }
        }
    }
}

