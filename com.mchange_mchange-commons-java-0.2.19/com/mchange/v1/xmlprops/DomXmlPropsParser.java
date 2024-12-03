/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xmlprops;

import com.mchange.v1.xml.ResourceEntityResolver;
import com.mchange.v1.xml.StdErrErrorHandler;
import com.mchange.v1.xmlprops.XmlPropsException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class DomXmlPropsParser {
    static final String XMLPROPS_NAMESPACE_URI = "http://www.mchange.com/namespaces/xmlprops";
    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public Properties parseXmlProps(InputStream inputStream) throws XmlPropsException {
        return this.parseXmlProps(new InputSource(inputStream), new ResourceEntityResolver(this.getClass()), new StdErrErrorHandler());
    }

    private Properties parseXmlProps(InputSource inputSource, EntityResolver entityResolver, ErrorHandler errorHandler) throws XmlPropsException {
        try {
            Properties properties = new Properties();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setEntityResolver(entityResolver);
            documentBuilder.setErrorHandler(errorHandler);
            Document document = documentBuilder.parse(inputSource);
            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("property");
            int n = nodeList.getLength();
            for (int i = 0; i < n; ++i) {
                Element element2 = (Element)nodeList.item(i);
                String string = element2.getAttribute("name");
                StringBuffer stringBuffer = new StringBuffer();
                NodeList nodeList2 = element2.getChildNodes();
                int n2 = nodeList2.getLength();
                for (int j = 0; j < n2; ++j) {
                    Node node = nodeList2.item(j);
                    if (node.getNodeType() != 3) continue;
                    stringBuffer.append(node.getNodeValue());
                }
                properties.put(string, stringBuffer.toString());
            }
            return properties;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new XmlPropsException(exception);
        }
    }

    public static void main(String[] stringArray) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(stringArray[0]));
            DomXmlPropsParser domXmlPropsParser = new DomXmlPropsParser();
            Properties properties = domXmlPropsParser.parseXmlProps(bufferedInputStream);
            for (String string : properties.keySet()) {
                String string2 = properties.getProperty(string);
                System.err.println(string + '=' + string2);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static {
        factory.setNamespaceAware(true);
        factory.setValidating(true);
    }
}

