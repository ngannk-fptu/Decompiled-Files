/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.xpath;

import aQute.lib.converter.Converter;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathParser {
    static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    static final XPathFactory xpf = XPathFactory.newInstance();
    static DocumentBuilder db;
    static XPath xp;
    final Document doc;

    public XPathParser(File file) throws Exception {
        this.doc = db.parse(file);
    }

    public <X> void parse(String what, Class<X> type, List<X> map) throws XPathExpressionException, Exception {
        NodeList proxies = (NodeList)xp.evaluate(what, this.doc, XPathConstants.NODESET);
        for (int i = 0; i < proxies.getLength(); ++i) {
            Node node = proxies.item(i);
            X dto = type.getConstructor(new Class[0]).newInstance(new Object[0]);
            this.parse(node, dto);
            map.add(dto);
        }
    }

    public <X> void parse(Node node, X dto) throws Exception {
        for (Field f : dto.getClass().getFields()) {
            String value;
            if (Modifier.isStatic(f.getModifiers()) || (value = xp.evaluate(f.getName(), node)) == null || value.isEmpty()) continue;
            if (f.getType().isAnnotation()) {
                value = value.toUpperCase();
            }
            Object o = Converter.cnv(f.getGenericType(), (Object)value);
            f.set(dto, o);
        }
    }

    public String parse(String expression) throws Exception {
        return xp.evaluate(expression, this.doc);
    }

    static {
        try {
            db = dbf.newDocumentBuilder();
            xp = xpf.newXPath();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

