/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.IElement;
import org.apache.axiom.om.impl.llom.LiveNamespaceContext;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public class OMElementImplUtil {
    private OMElementImplUtil() {
    }

    public static NamespaceContext getNamespaceContext(OMElement element, boolean detached) {
        if (detached) {
            HashMap<String, String> namespaces = new HashMap<String, String>();
            Iterator it = element.getNamespacesInScope();
            while (it.hasNext()) {
                OMNamespace ns = (OMNamespace)it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return new MapBasedNamespaceContext(namespaces);
        }
        return new LiveNamespaceContext(element);
    }

    public static String getText(OMElement element) {
        String childText = null;
        StringBuffer buffer = null;
        for (OMNode child = element.getFirstOMChild(); child != null; child = child.getNextOMSibling()) {
            OMText textNode;
            String textValue;
            int type = child.getType();
            if (type != 4 && type != 12 || (textValue = (textNode = (OMText)child).getText()) == null || textValue.length() == 0) continue;
            if (childText == null) {
                childText = textValue;
                continue;
            }
            if (buffer == null) {
                buffer = new StringBuffer(childText);
            }
            buffer.append(textValue);
        }
        if (childText == null) {
            return "";
        }
        if (buffer != null) {
            return buffer.toString();
        }
        return childText;
    }

    public static Reader getTextAsStream(OMElement element, boolean cache) {
        if (!(element instanceof OMSourcedElement || cache && !element.isComplete())) {
            OMNode child = element.getFirstOMChild();
            if (child == null) {
                return new StringReader("");
            }
            if (child.getNextOMSibling() == null) {
                return new StringReader(child instanceof OMText ? ((OMText)child).getText() : "");
            }
        }
        try {
            XMLStreamReader reader = element.getXMLStreamReader(cache);
            if (reader.getEventType() == 7) {
                reader.next();
            }
            return XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        }
        catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    public static void writeTextTo(OMElement element, Writer out, boolean cache) throws IOException {
        try {
            XMLStreamReader reader = element.getXMLStreamReader(cache);
            int depth = 0;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case 4: 
                    case 12: {
                        if (depth != 1) break;
                        out.write(reader.getText());
                        break;
                    }
                    case 1: {
                        ++depth;
                        break;
                    }
                    case 2: {
                        --depth;
                    }
                }
            }
        }
        catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    public static void discard(IElement that) {
        if (that.getState() == 0 && that.getBuilder() != null) {
            ((StAXOMBuilder)that.getBuilder()).discard((OMContainer)that);
        }
        that.detach();
    }
}

