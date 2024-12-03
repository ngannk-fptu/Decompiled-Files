/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.xmlparser.MyEntityResolver;
import org.apache.sling.scripting.jsp.jasper.xmlparser.MyErrorHandler;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParserUtils {
    static ErrorHandler errorHandler = new MyErrorHandler();
    static EntityResolver entityResolver = new MyEntityResolver();
    public static boolean validating = false;

    public TreeNode parseXMLDocument(String uri, InputSource is) throws JasperException {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(validating);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(errorHandler);
            document = builder.parse(is);
        }
        catch (ParserConfigurationException ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.parse.xml", uri), ex);
        }
        catch (SAXParseException ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.parse.xml.line", uri, Integer.toString(ex.getLineNumber()), Integer.toString(ex.getColumnNumber())), ex);
        }
        catch (SAXException sx) {
            throw new JasperException(Localizer.getMessage("jsp.error.parse.xml", uri), sx);
        }
        catch (IOException io) {
            throw new JasperException(Localizer.getMessage("jsp.error.parse.xml", uri), io);
        }
        return this.convert(null, document.getDocumentElement());
    }

    public TreeNode parseXMLDocument(String uri, InputStream is) throws JasperException {
        return this.parseXMLDocument(uri, new InputSource(is));
    }

    protected TreeNode convert(TreeNode parent, Node node) {
        NodeList children;
        TreeNode treeNode = new TreeNode(node.getNodeName(), parent);
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            int n = attributes.getLength();
            for (int i = 0; i < n; ++i) {
                Node attribute = attributes.item(i);
                treeNode.addAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        if ((children = node.getChildNodes()) != null) {
            int n = children.getLength();
            for (int i = 0; i < n; ++i) {
                Node child = children.item(i);
                if (child instanceof Comment) continue;
                if (child instanceof Text) {
                    String body = ((Text)child).getData();
                    if (body == null || (body = body.trim()).length() <= 0) continue;
                    treeNode.setBody(body);
                    continue;
                }
                TreeNode treeNode2 = this.convert(treeNode, child);
            }
        }
        return treeNode;
    }
}

