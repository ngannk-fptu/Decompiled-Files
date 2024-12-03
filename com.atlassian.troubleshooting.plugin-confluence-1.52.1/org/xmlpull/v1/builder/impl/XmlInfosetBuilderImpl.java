/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlCharacters;
import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlInfosetBuilder;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.XmlSerializable;
import org.xmlpull.v1.builder.impl.XmlDocumentImpl;
import org.xmlpull.v1.builder.impl.XmlElementImpl;
import org.xmlpull.v1.builder.impl.XmlNamespaceImpl;

public class XmlInfosetBuilderImpl
extends XmlInfosetBuilder {
    private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
    private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";

    public XmlDocument newDocument(String version, Boolean standalone, String characterEncoding) {
        return new XmlDocumentImpl(version, standalone, characterEncoding);
    }

    public XmlElement newFragment(String elementName) {
        return new XmlElementImpl((XmlNamespace)null, elementName);
    }

    public XmlElement newFragment(String elementNamespaceName, String elementName) {
        return new XmlElementImpl(elementNamespaceName, elementName);
    }

    public XmlElement newFragment(XmlNamespace elementNamespace, String elementName) {
        return new XmlElementImpl(elementNamespace, elementName);
    }

    public XmlNamespace newNamespace(String namespaceName) {
        return new XmlNamespaceImpl(null, namespaceName);
    }

    public XmlNamespace newNamespace(String prefix, String namespaceName) {
        return new XmlNamespaceImpl(prefix, namespaceName);
    }

    public XmlDocument parse(XmlPullParser pp) {
        XmlDocument doc = this.parseDocumentStart(pp);
        XmlElement root = this.parseFragment(pp);
        doc.setDocumentElement(root);
        return doc;
    }

    public Object parseItem(XmlPullParser pp) {
        try {
            int eventType = pp.getEventType();
            if (eventType == 2) {
                return this.parseStartTag(pp);
            }
            if (eventType == 4) {
                return pp.getText();
            }
            if (eventType == 0) {
                return this.parseDocumentStart(pp);
            }
            throw new XmlBuilderException("currently unsupported event type " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription());
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not parse XML item", e);
        }
    }

    private XmlDocument parseDocumentStart(XmlPullParser pp) {
        XmlDocumentImpl doc = null;
        try {
            if (pp.getEventType() != 0) {
                throw new XmlBuilderException("parser must be positioned on beginning of document and not " + pp.getPositionDescription());
            }
            pp.next();
            String xmlDeclVersion = (String)pp.getProperty(PROPERTY_XMLDECL_VERSION);
            Boolean xmlDeclStandalone = (Boolean)pp.getProperty(PROPERTY_XMLDECL_STANDALONE);
            String characterEncoding = pp.getInputEncoding();
            doc = new XmlDocumentImpl(xmlDeclVersion, xmlDeclStandalone, characterEncoding);
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not parse XML document prolog", e);
        }
        catch (IOException e) {
            throw new XmlBuilderException("could not read XML document prolog", e);
        }
        return doc;
    }

    public XmlElement parseFragment(XmlPullParser pp) {
        try {
            int depth = pp.getDepth();
            int eventType = pp.getEventType();
            if (eventType != 2) {
                throw new XmlBuilderException("expected parser to be on start tag and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription());
            }
            XmlElement curElem = this.parseStartTag(pp);
            while (true) {
                if ((eventType = pp.next()) == 2) {
                    XmlElement child = this.parseStartTag(pp);
                    curElem.addElement(child);
                    curElem = child;
                    continue;
                }
                if (eventType == 3) {
                    XmlContainer parent = curElem.getParent();
                    if (parent == null) {
                        if (pp.getDepth() != depth) {
                            throw new XmlBuilderException("unbalanced input" + pp.getPositionDescription());
                        }
                        return curElem;
                    }
                    curElem = (XmlElement)parent;
                    continue;
                }
                if (eventType != 4) continue;
                curElem.addChild(pp.getText());
            }
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not build tree from XML", e);
        }
        catch (IOException e) {
            throw new XmlBuilderException("could not read XML tree content", e);
        }
    }

    public XmlElement parseStartTag(XmlPullParser pp) {
        try {
            int i;
            if (pp.getEventType() != 2) {
                throw new XmlBuilderException("parser must be on START_TAG and not " + pp.getPositionDescription());
            }
            String elNsPrefix = pp.getPrefix();
            XmlNamespaceImpl elementNs = new XmlNamespaceImpl(elNsPrefix, pp.getNamespace());
            XmlElementImpl el = new XmlElementImpl(elementNs, pp.getName());
            for (i = pp.getNamespaceCount(pp.getDepth() - 1); i < pp.getNamespaceCount(pp.getDepth()); ++i) {
                String prefix = pp.getNamespacePrefix(i);
                el.declareNamespace(prefix == null ? "" : prefix, pp.getNamespaceUri(i));
            }
            for (i = 0; i < pp.getAttributeCount(); ++i) {
                el.addAttribute(pp.getAttributeType(i), pp.getAttributePrefix(i), pp.getAttributeNamespace(i), pp.getAttributeName(i), pp.getAttributeValue(i), !pp.isAttributeDefault(i));
            }
            return el;
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not parse XML start tag", e);
        }
    }

    public XmlDocument parseLocation(String locationUrl) {
        URL url = null;
        try {
            url = new URL(locationUrl);
        }
        catch (MalformedURLException e) {
            throw new XmlBuilderException("could not parse URL " + locationUrl, e);
        }
        try {
            return this.parseInputStream(url.openStream());
        }
        catch (IOException e) {
            throw new XmlBuilderException("could not open connection to URL " + locationUrl, e);
        }
    }

    public void serialize(Object item, XmlSerializer serializer) {
        if (item instanceof Collection) {
            Collection c = (Collection)item;
            Iterator i = c.iterator();
            while (i.hasNext()) {
                this.serialize(i.next(), serializer);
            }
        } else if (item instanceof XmlContainer) {
            this.serializeContainer((XmlContainer)item, serializer);
        } else {
            this.serializeItem(item, serializer);
        }
    }

    private void serializeContainer(XmlContainer node, XmlSerializer serializer) {
        if (node instanceof XmlSerializable) {
            try {
                ((XmlSerializable)((Object)node)).serialize(serializer);
            }
            catch (IOException e) {
                throw new XmlBuilderException("could not serialize node " + node + ": " + e, e);
            }
        } else if (node instanceof XmlDocument) {
            this.serializeDocument((XmlDocument)node, serializer);
        } else if (node instanceof XmlElement) {
            this.serializeFragment((XmlElement)node, serializer);
        } else {
            throw new IllegalArgumentException("could not serialzie unknown XML container " + node.getClass());
        }
    }

    public void serializeItem(Object item, XmlSerializer ser) {
        block8: {
            try {
                if (item instanceof XmlSerializable) {
                    try {
                        ((XmlSerializable)((Object)item)).serialize(ser);
                        break block8;
                    }
                    catch (IOException e) {
                        throw new XmlBuilderException("could not serialize item " + item + ": " + e, e);
                    }
                }
                if (item instanceof String) {
                    ser.text(((Object)item).toString());
                    break block8;
                }
                if (item instanceof XmlCharacters) {
                    ser.text(((XmlCharacters)((Object)item)).getText());
                    break block8;
                }
                if (item instanceof XmlComment) {
                    ser.comment(((XmlComment)((Object)item)).getContent());
                    break block8;
                }
                throw new IllegalArgumentException("could not serialize " + (item != null ? item.getClass() : item));
            }
            catch (IOException e) {
                throw new XmlBuilderException("serializing XML start tag failed", e);
            }
        }
    }

    public void serializeStartTag(XmlElement el, XmlSerializer ser) {
        try {
            Iterator iter;
            String elPrefix;
            XmlNamespace elNamespace = el.getNamespace();
            String string = elPrefix = elNamespace != null ? elNamespace.getPrefix() : "";
            if (elPrefix == null) {
                elPrefix = "";
            }
            String nToDeclare = null;
            if (el.hasNamespaceDeclarations()) {
                iter = el.namespaces();
                while (iter.hasNext()) {
                    XmlNamespace n = (XmlNamespace)iter.next();
                    String nPrefix = n.getPrefix();
                    if (!elPrefix.equals(nPrefix)) {
                        ser.setPrefix(nPrefix, n.getNamespaceName());
                        continue;
                    }
                    nToDeclare = n.getNamespaceName();
                }
            }
            if (nToDeclare != null) {
                ser.setPrefix(elPrefix, nToDeclare);
            } else if (elNamespace != null) {
                String namespaceName = elNamespace.getNamespaceName();
                if (namespaceName == null) {
                    namespaceName = "";
                }
                String serPrefix = null;
                if (namespaceName.length() > 0) {
                    ser.getPrefix(namespaceName, false);
                }
                if (serPrefix == null) {
                    serPrefix = "";
                }
                if (serPrefix != elPrefix && !serPrefix.equals(elPrefix)) {
                    ser.setPrefix(elPrefix, namespaceName);
                }
            }
            ser.startTag(el.getNamespaceName(), el.getName());
            if (el.hasAttributes()) {
                iter = el.attributes();
                while (iter.hasNext()) {
                    XmlAttribute a = (XmlAttribute)iter.next();
                    if (a instanceof XmlSerializable) {
                        ((XmlSerializable)((Object)a)).serialize(ser);
                        continue;
                    }
                    ser.attribute(a.getNamespaceName(), a.getName(), a.getValue());
                }
            }
        }
        catch (IOException e) {
            throw new XmlBuilderException("serializing XML start tag failed", e);
        }
    }

    public void serializeEndTag(XmlElement el, XmlSerializer ser) {
        try {
            ser.endTag(el.getNamespaceName(), el.getName());
        }
        catch (IOException e) {
            throw new XmlBuilderException("serializing XML end tag failed", e);
        }
    }

    private void serializeDocument(XmlDocument doc, XmlSerializer ser) {
        try {
            ser.startDocument(doc.getCharacterEncodingScheme(), doc.isStandalone());
        }
        catch (IOException e) {
            throw new XmlBuilderException("serializing XML document start failed", e);
        }
        if (doc.getDocumentElement() == null) {
            throw new XmlBuilderException("could not serialize document without root element " + doc + ": ");
        }
        this.serializeFragment(doc.getDocumentElement(), ser);
        try {
            ser.endDocument();
        }
        catch (IOException e) {
            throw new XmlBuilderException("serializing XML document end failed", e);
        }
    }

    private void serializeFragment(XmlElement el, XmlSerializer ser) {
        this.serializeStartTag(el, ser);
        if (el.hasChildren()) {
            Iterator iter = el.children();
            while (iter.hasNext()) {
                Object child = iter.next();
                if (child instanceof XmlSerializable) {
                    try {
                        ((XmlSerializable)child).serialize(ser);
                        continue;
                    }
                    catch (IOException e) {
                        throw new XmlBuilderException("could not serialize item " + child + ": " + e, e);
                    }
                }
                if (child instanceof XmlElement) {
                    this.serializeFragment((XmlElement)child, ser);
                    continue;
                }
                this.serializeItem(child, ser);
            }
        }
        this.serializeEndTag(el, ser);
    }
}

