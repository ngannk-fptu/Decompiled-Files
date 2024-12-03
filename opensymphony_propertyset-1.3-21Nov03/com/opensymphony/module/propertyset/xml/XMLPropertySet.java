/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  com.opensymphony.util.TextUtils
 *  com.opensymphony.util.XMLUtils
 */
package com.opensymphony.module.propertyset.xml;

import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.memory.SerializablePropertySet;
import com.opensymphony.util.Data;
import com.opensymphony.util.TextUtils;
import com.opensymphony.util.XMLUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLPropertySet
extends SerializablePropertySet {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    public void load(Reader in) throws ParserConfigurationException, IOException, SAXException {
        this.loadFromDocument(XMLUtils.parse((Reader)in));
    }

    public void load(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        this.loadFromDocument(XMLUtils.parse((InputStream)in));
    }

    public void loadFromDocument(Document doc) throws PropertyImplementationException {
        try {
            NodeList nodeList = XMLUtils.xpathList((Node)doc, (String)"/property-set/property");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                int type;
                String key;
                Element e = (Element)nodeList.item(i);
                Object value = this.loadValue(e, key = e.getAttribute("key"), type = this.type(e.getAttribute("type")));
                if (value == null) continue;
                this.setImpl(type, key, value);
            }
        }
        catch (TransformerException e) {
            throw new PropertyImplementationException(e);
        }
    }

    public void save(Writer out) throws ParserConfigurationException, IOException {
        XMLUtils.print((Document)this.saveToDocument(), (Writer)out);
    }

    public void save(OutputStream out) throws ParserConfigurationException, IOException {
        XMLUtils.print((Document)this.saveToDocument(), (OutputStream)out);
    }

    public Document saveToDocument() throws ParserConfigurationException {
        Document doc = XMLUtils.newDocument((String)"property-set");
        Iterator keys = this.getKeys().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            int type = this.getType(key);
            Object value = this.get(type, key);
            this.saveValue(doc, key, type, value);
        }
        return doc;
    }

    private Object loadValue(Element e, String key, int type) {
        String text = XMLUtils.getElementText((Element)e);
        switch (type) {
            case 1: {
                return new Boolean(TextUtils.parseBoolean((String)text));
            }
            case 2: {
                return new Integer(TextUtils.parseInt((String)text));
            }
            case 3: {
                return new Long(TextUtils.parseLong((String)text));
            }
            case 4: {
                return new Double(TextUtils.parseDouble((String)text));
            }
            case 5: 
            case 6: {
                return text;
            }
            case 7: {
                try {
                    return this.dateFormat.parse(text);
                }
                catch (ParseException pe) {
                    return null;
                }
            }
            case 8: {
                try {
                    return TextUtils.decodeObject((String)text);
                }
                catch (Exception ex) {
                    return null;
                }
            }
            case 9: {
                try {
                    return XMLUtils.parse((String)text);
                }
                catch (Exception ex) {
                    return null;
                }
            }
            case 10: {
                try {
                    return new Data(TextUtils.decodeBytes((String)text));
                }
                catch (IOException ioe) {
                    return null;
                }
            }
            case 11: {
                try {
                    Properties props = new Properties();
                    NodeList pElements = XMLUtils.xpathList((Node)e, (String)"properties/property");
                    for (int i = 0; i < pElements.getLength(); ++i) {
                        Element pElement = (Element)pElements.item(i);
                        props.put(pElement.getAttribute("key"), XMLUtils.getElementText((Element)pElement));
                    }
                    return props;
                }
                catch (TransformerException te) {
                    return null;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    private void saveValue(Document doc, String key, int type, Object value) {
        void var6_6;
        Element element = doc.createElement("property");
        element.setAttribute("key", key);
        element.setAttribute("type", this.type(type));
        switch (type) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                Node valueNode = doc.createTextNode(value.toString());
                break;
            }
            case 6: {
                Node valueNode = doc.createCDATASection(value.toString());
                break;
            }
            case 7: {
                Node valueNode = doc.createTextNode(this.dateFormat.format((Date)value));
                break;
            }
            case 8: {
                Node valueNode;
                try {
                    valueNode = doc.createCDATASection(TextUtils.encodeObject((Object)value));
                    break;
                }
                catch (IOException ioe) {
                    return;
                }
            }
            case 9: {
                Node valueNode;
                try {
                    valueNode = doc.createCDATASection(XMLUtils.print((Document)((Document)value)));
                    break;
                }
                catch (IOException ioe) {
                    return;
                }
            }
            case 10: {
                Node valueNode;
                try {
                    valueNode = doc.createCDATASection(TextUtils.encodeBytes((byte[])((Data)value).getBytes()));
                    break;
                }
                catch (IOException ioe) {
                    return;
                }
            }
            case 11: {
                Node valueNode = doc.createElement("properties");
                Properties props = (Properties)value;
                Iterator<Object> pKeys = props.keySet().iterator();
                while (pKeys.hasNext()) {
                    String pKey = (String)pKeys.next();
                    Element pElement = doc.createElement("property");
                    pElement.setAttribute("key", pKey);
                    pElement.setAttribute("type", "string");
                    pElement.appendChild(doc.createTextNode(props.getProperty(pKey)));
                    valueNode.appendChild(pElement);
                }
                break;
            }
            default: {
                return;
            }
        }
        element.appendChild((Node)var6_6);
        doc.getDocumentElement().appendChild(element);
    }
}

