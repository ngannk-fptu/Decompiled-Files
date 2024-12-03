/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.util;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OMSerializerUtil {
    private static final Log log = LogFactory.getLog(OMSerializerUtil.class);
    private static boolean ADV_DEBUG_ENABLED = true;
    static long nsCounter = 0L;
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";

    public static void serializeEndpart(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    public static void serializeAttribute(OMAttribute attr, XMLStreamWriter writer) throws XMLStreamException {
        OMNamespace ns = attr.getNamespace();
        String prefix = null;
        String namespaceName = null;
        if (ns != null) {
            prefix = ns.getPrefix();
            namespaceName = ns.getNamespaceURI();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName, attr.getLocalName(), attr.getAttributeValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(), attr.getAttributeValue());
            }
        } else {
            String localName = attr.getLocalName();
            String attributeValue = attr.getAttributeValue();
            writer.writeAttribute(localName, attributeValue);
        }
    }

    public static void serializeNamespace(OMNamespace namespace, XMLStreamWriter writer) throws XMLStreamException {
        if (namespace == null) {
            return;
        }
        String uri = namespace.getNamespaceURI();
        String prefix = namespace.getPrefix();
        if (uri != null && !"".equals(uri)) {
            String prefixFromWriter = writer.getPrefix(uri);
            if ("".equals(prefix) && "".equals(prefixFromWriter) && !uri.equals(writer.getNamespaceContext().getNamespaceURI("")) || prefix != null && "".equals(prefix) && (prefixFromWriter == null || !prefix.equals(prefixFromWriter))) {
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            } else {
                String string = prefix = prefix == null ? OMSerializerUtil.getNextNSPrefix(writer) : prefix;
                if (prefix != null && !prefix.equals(prefixFromWriter) && !OMSerializerUtil.checkForPrefixInTheCurrentContext(writer, uri, prefix)) {
                    writer.writeNamespace(prefix, uri);
                    writer.setPrefix(prefix, uri);
                }
            }
        } else {
            String currentDefaultNSURI = writer.getNamespaceContext().getNamespaceURI("");
            if (currentDefaultNSURI != null && !currentDefaultNSURI.equals(uri) || uri != null && !uri.equals(currentDefaultNSURI)) {
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            }
        }
    }

    public static boolean isSetPrefixBeforeStartElement(XMLStreamWriter writer) {
        return false;
    }

    public static void serializeStartpart(OMElement element, XMLStreamWriter writer) throws XMLStreamException {
        OMSerializerUtil.serializeStartpart(element, element.getLocalName(), writer);
    }

    public static void serializeStartpart(OMElement element, String localName, XMLStreamWriter writer) throws XMLStreamException {
        String namespace;
        String prefix;
        OMNamespace omNamespace;
        OMAttribute attr;
        ArrayList<String> writePrefixList = null;
        ArrayList<String> writeNSList = null;
        OMNamespace eOMNamespace = element.getNamespace();
        String ePrefix = null;
        String eNamespace = null;
        if (eOMNamespace != null) {
            ePrefix = eOMNamespace.getPrefix();
            eNamespace = eOMNamespace.getNamespaceURI();
        }
        ePrefix = ePrefix != null && ePrefix.length() == 0 ? null : ePrefix;
        String string = eNamespace = eNamespace != null && eNamespace.length() == 0 ? null : eNamespace;
        if (eNamespace != null) {
            if (ePrefix == null) {
                if (!OMSerializerUtil.isAssociated("", eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList<String>();
                        writeNSList = new ArrayList<String>();
                    }
                    if (!writePrefixList.contains("")) {
                        writePrefixList.add("");
                        writeNSList.add(eNamespace);
                    }
                }
                writer.writeStartElement("", localName, eNamespace);
            } else {
                if (!OMSerializerUtil.isAssociated(ePrefix, eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    if (!writePrefixList.contains(ePrefix)) {
                        writePrefixList.add(ePrefix);
                        writeNSList.add(eNamespace);
                    }
                }
                writer.writeStartElement(ePrefix, localName, eNamespace);
            }
        } else {
            writer.writeStartElement(localName);
        }
        Iterator it = element.getAllDeclaredNamespaces();
        while (it != null && it.hasNext()) {
            String newPrefix;
            OMNamespace omNamespace2 = (OMNamespace)it.next();
            String prefix2 = null;
            String namespace2 = null;
            if (omNamespace2 != null) {
                prefix2 = omNamespace2.getPrefix();
                namespace2 = omNamespace2.getNamespaceURI();
            }
            if ((newPrefix = OMSerializerUtil.generateSetPrefix(prefix2 = prefix2 != null && prefix2.length() == 0 ? null : prefix2, namespace2 = namespace2 != null && namespace2.length() == 0 ? null : namespace2, writer, false)) == null) continue;
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(namespace2);
        }
        String newPrefix = OMSerializerUtil.generateSetPrefix(ePrefix, eNamespace, writer, false);
        if (newPrefix != null) {
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (!writePrefixList.contains(newPrefix)) {
                writePrefixList.add(newPrefix);
                writeNSList.add(eNamespace);
            }
        }
        Iterator attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            attr = (OMAttribute)attrs.next();
            omNamespace = attr.getNamespace();
            prefix = null;
            namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            String string2 = namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            if (prefix == null && namespace != null) {
                String writerPrefix = writer.getPrefix(namespace);
                writerPrefix = writerPrefix != null && writerPrefix.length() == 0 ? null : writerPrefix;
                String string3 = prefix = writerPrefix != null ? writerPrefix : OMSerializerUtil.getNextNSPrefix();
            }
            if ((newPrefix = OMSerializerUtil.generateSetPrefix(prefix, namespace, writer, true)) == null) continue;
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(namespace);
        }
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            String refPrefix;
            OMNamespace omNS;
            String refNamespace;
            attr = (OMAttribute)attrs.next();
            omNamespace = attr.getNamespace();
            prefix = null;
            namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            String local = attr.getLocalName();
            if (!XSI_URI.equals(namespace) || !XSI_LOCAL_NAME.equals(local)) continue;
            String value = attr.getAttributeValue();
            if (log.isDebugEnabled()) {
                log.debug((Object)("The value of xsi:type is " + value));
            }
            if (value == null || (value = value.trim()).indexOf(":") <= 0 || (refNamespace = (omNS = element.findNamespaceURI(refPrefix = value.substring(0, value.indexOf(":")))) == null ? null : omNS.getNamespaceURI()) == null || refNamespace.length() <= 0 || (newPrefix = OMSerializerUtil.generateSetPrefix(refPrefix, refNamespace, writer, true)) == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("An xmlns:" + newPrefix + "=\"" + refNamespace + "\" will be written"));
            }
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(refNamespace);
        }
        if (writePrefixList != null) {
            for (int i = 0; i < writePrefixList.size(); ++i) {
                String prefix3 = (String)writePrefixList.get(i);
                String namespace3 = (String)writeNSList.get(i);
                if (prefix3 != null) {
                    if (namespace3 == null) {
                        writer.writeNamespace(prefix3, "");
                        continue;
                    }
                    writer.writeNamespace(prefix3, namespace3);
                    continue;
                }
                writer.writeDefaultNamespace(namespace3);
            }
        }
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            String writerPrefix;
            OMAttribute attr2 = (OMAttribute)attrs.next();
            omNamespace = attr2.getNamespace();
            prefix = null;
            namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            String string4 = namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            if (prefix == null && namespace != null) {
                prefix = writer.getPrefix(namespace);
                if (prefix == null || "".equals(prefix)) {
                    for (int i = 0; i < writePrefixList.size(); ++i) {
                        if (!namespace.equals((String)writeNSList.get(i))) continue;
                        prefix = (String)writePrefixList.get(i);
                    }
                }
            } else if (namespace != null && !prefix.equals(writerPrefix = writer.getPrefix(namespace)) && writerPrefix != null && !"".equals(writerPrefix)) {
                prefix = writerPrefix;
            }
            if (namespace != null) {
                if (prefix == null && "http://www.w3.org/XML/1998/namespace".equals(namespace)) {
                    prefix = "xml";
                }
                writer.writeAttribute(prefix, namespace, attr2.getLocalName(), attr2.getAttributeValue());
                continue;
            }
            writer.writeAttribute(attr2.getLocalName(), attr2.getAttributeValue());
        }
    }

    private static boolean checkForPrefixInTheCurrentContext(XMLStreamWriter writer, String nameSpaceName, String prefix) throws XMLStreamException {
        Iterator<String> prefixesIter = writer.getNamespaceContext().getPrefixes(nameSpaceName);
        while (prefixesIter.hasNext()) {
            String prefix_w = prefixesIter.next();
            if (!prefix_w.equals(prefix)) continue;
            return true;
        }
        return false;
    }

    public static void serializeNamespaces(OMElement element, XMLStreamWriter writer) throws XMLStreamException {
        Iterator namespaces = element.getAllDeclaredNamespaces();
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                OMSerializerUtil.serializeNamespace((OMNamespace)namespaces.next(), writer);
            }
        }
    }

    public static void serializeAttributes(OMElement element, XMLStreamWriter writer) throws XMLStreamException {
        Iterator attributes = element.getAllAttributes();
        if (attributes != null && attributes.hasNext()) {
            while (attributes.hasNext()) {
                OMSerializerUtil.serializeAttribute((OMAttribute)attributes.next(), writer);
            }
        }
    }

    public static void serializeNormal(OMElement element, XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        if (cache) {
            element.build();
        }
        OMSerializerUtil.serializeStartpart(element, writer);
        OMNode firstChild = element.getFirstOMChild();
        if (firstChild != null) {
            if (cache) {
                firstChild.serialize(writer);
            } else {
                firstChild.serializeAndConsume(writer);
            }
        }
        OMSerializerUtil.serializeEndpart(writer);
    }

    public static void serializeByPullStream(OMElement element, XMLStreamWriter writer) throws XMLStreamException {
        OMSerializerUtil.serializeByPullStream(element, writer, false);
    }

    public static void serializeByPullStream(OMElement element, XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        new StreamingOMSerializer().serialize(element.getXMLStreamReader(cache), writer);
    }

    public static void serializeChildren(OMContainer container, XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        if (cache) {
            Iterator children = container.getChildren();
            while (children.hasNext()) {
                ((OMNodeEx)children.next()).internalSerialize(writer, true);
            }
        } else {
            for (OMNodeEx child = (OMNodeEx)container.getFirstOMChild(); child != null; child = (OMNodeEx)child.getNextOMSiblingIfAvailable()) {
                if (!(child instanceof OMElement) || child.isComplete() || ((OMElement)((Object)child)).getBuilder() == null) {
                    child.internalSerialize(writer, false);
                    continue;
                }
                OMElement element = (OMElement)((Object)child);
                element.getBuilder().setCache(false);
                OMSerializerUtil.serializeByPullStream(element, writer, cache);
            }
        }
    }

    public static String getNextNSPrefix() {
        String prefix = "axis2ns" + ++nsCounter % Long.MAX_VALUE;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Obtained next prefix:" + prefix));
            if (ADV_DEBUG_ENABLED && log.isTraceEnabled()) {
                log.trace((Object)CommonUtils.callStackToString());
            }
        }
        return prefix;
    }

    public static String getNextNSPrefix(XMLStreamWriter writer) {
        String prefix = OMSerializerUtil.getNextNSPrefix();
        while (writer.getNamespaceContext().getNamespaceURI(prefix) != null) {
            prefix = OMSerializerUtil.getNextNSPrefix();
        }
        return prefix;
    }

    public static String generateSetPrefix(String prefix, String namespace, XMLStreamWriter writer, boolean attr) throws XMLStreamException {
        String string = prefix = prefix == null ? "" : prefix;
        if (OMSerializerUtil.isAssociated(prefix, namespace, writer)) {
            return null;
        }
        if (prefix.length() == 0 && namespace == null && attr) {
            return null;
        }
        String newPrefix = null;
        if (namespace != null) {
            if (prefix.length() == 0) {
                writer.setDefaultNamespace(namespace);
                newPrefix = "";
            } else {
                writer.setPrefix(prefix, namespace);
                newPrefix = prefix;
            }
        } else {
            writer.setDefaultNamespace("");
            newPrefix = "";
        }
        return newPrefix;
    }

    public static boolean isAssociated(String prefix, String namespace, XMLStreamWriter writer) throws XMLStreamException {
        String writerNS;
        NamespaceContext nsContext;
        block8: {
            if ("xml".equals(prefix)) {
                return true;
            }
            prefix = prefix == null ? "" : prefix;
            String string = namespace = namespace == null ? "" : namespace;
            if (namespace.length() > 0) {
                NamespaceContext nsContext2;
                String writerPrefix = writer.getPrefix(namespace);
                if (prefix.equals(writerPrefix)) {
                    return true;
                }
                if (writerPrefix != null && (nsContext2 = writer.getNamespaceContext()) != null) {
                    String writerNS2 = nsContext2.getNamespaceURI(prefix);
                    return namespace.equals(writerNS2);
                }
                return false;
            }
            if (prefix.length() > 0) {
                throw new OMException("Invalid namespace declaration: Prefixed namespace bindings may not be empty.");
            }
            try {
                String writerPrefix = writer.getPrefix("");
                if (writerPrefix != null && writerPrefix.length() == 0) {
                    return true;
                }
            }
            catch (Throwable t) {
                if (!log.isDebugEnabled()) break block8;
                log.debug((Object)("Caught exception from getPrefix(\"\"). Processing continues: " + t));
            }
        }
        return (nsContext = writer.getNamespaceContext()) == null || (writerNS = nsContext.getNamespaceURI("")) == null || writerNS.length() <= 0;
    }
}

