/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.replacer;

import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.replacer.AbstractPbeReplacer;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.Base64;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EncryptionReplacer
extends AbstractPbeReplacer {
    public static final String PROPERTY_PASSWORD_USER_PROPERTIES = "passwordUserProperties";
    public static final String PROPERTY_PASSWORD_NETWORK_INTERFACE = "passwordNetworkInterface";
    public static final String PROPERTY_PASSWORD_FILE = "passwordFile";
    private static final String PREFIX = "ENC";
    private static final int DEFAULT_ITERATIONS = 531;
    private boolean passwordUserProperties;
    private String passwordNetworkInterface;
    private String passwordFile;

    @Override
    public void init(Properties properties) {
        super.init(properties);
        this.passwordFile = properties.getProperty(PROPERTY_PASSWORD_FILE);
        this.passwordUserProperties = Boolean.parseBoolean(properties.getProperty(PROPERTY_PASSWORD_USER_PROPERTIES, "true"));
        this.passwordNetworkInterface = properties.getProperty(PROPERTY_PASSWORD_NETWORK_INTERFACE);
        Preconditions.checkFalse(this.passwordFile == null && this.passwordNetworkInterface == null && !this.passwordUserProperties, "At least one of the properties used to generate encryption password has to be configured");
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    protected char[] getPassword() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (this.passwordFile != null) {
                FileInputStream fis = new FileInputStream(this.passwordFile);
                try {
                    baos.write(IOUtil.toByteArray(fis));
                }
                finally {
                    IOUtil.closeResource(fis);
                }
            }
            if (this.passwordUserProperties) {
                baos.write(System.getProperty("user.home").getBytes(StringUtil.UTF8_CHARSET));
                baos.write(System.getProperty("user.name").getBytes(StringUtil.UTF8_CHARSET));
            }
            if (this.passwordNetworkInterface != null) {
                try {
                    NetworkInterface iface = NetworkInterface.getByName(this.passwordNetworkInterface);
                    baos.write(iface.getHardwareAddress());
                }
                catch (SocketException e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
            return new String(Base64.encode(baos.toByteArray()), StringUtil.UTF8_CHARSET).toCharArray();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    public static final void main(String ... args) throws Exception {
        if (args == null || args.length < 1 || args.length > 2) {
            System.err.println("Usage:");
            System.err.println("\tjava -D<propertyName>=<propertyValue>  " + EncryptionReplacer.class.getName() + " \"<String To Encrypt>\" [iterations]");
            System.err.println();
            System.err.println("The replacer configuration can be loaded either from hazelcast/hazelcast-client XML file:");
            System.err.println("\t-Dhazelcast.config=/path/to/hazelcast.xml");
            System.err.println();
            System.err.println("or provided directly via following property names:");
            System.err.println("\tcipherAlgorithm");
            System.err.println("\tkeyLengthBits");
            System.err.println("\tsaltLengthBytes");
            System.err.println("\tsecretKeyAlgorithm");
            System.err.println("\tsecretKeyFactoryAlgorithm");
            System.err.println("\tsecurityProvider");
            System.err.println("\tpasswordFile");
            System.err.println("\tpasswordNetworkInterface");
            System.err.println("\tpasswordUserProperties");
            System.err.println();
            System.err.println("Values available for property passwordNetworkInterface");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null) continue;
                System.err.println("\t" + networkInterface.getName());
            }
            System.err.println();
            System.exit(1);
        }
        System.out.println(EncryptionReplacer.encrypt(args));
    }

    protected static String encrypt(String ... args) throws Exception {
        int iterations = args.length == 2 ? Integer.parseInt(args[1]) : 531;
        EncryptionReplacer replacer = new EncryptionReplacer();
        String xmlPath = System.getProperty("hazelcast.config");
        Properties properties = xmlPath == null ? System.getProperties() : EncryptionReplacer.loadPropertiesFromConfig(new FileInputStream(xmlPath));
        replacer.init(properties);
        String encrypted = replacer.encrypt(args[0], iterations);
        String variable = "$" + replacer.getPrefix() + "{" + encrypted + "}";
        return variable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Properties loadPropertiesFromConfig(FileInputStream fileInputStream) throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(fileInputStream);
            Element root = doc.getDocumentElement();
            Properties properties = EncryptionReplacer.loadProperties(EncryptionReplacer.findReplacerDefinition(root));
            return properties;
        }
        finally {
            IOUtil.closeResource(fileInputStream);
        }
    }

    private static Node findReplacerDefinition(Element root) throws XPathException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new HzNsContext());
        String xpathExp = "//%s:config-replacers/%1$s:replacer[@class-name='%s']";
        NodeList replaceTags = (NodeList)xpath.evaluate(String.format(xpathExp, "hz", EncryptionReplacer.class.getName()), root, XPathConstants.NODESET);
        if (replaceTags.getLength() < 1) {
            replaceTags = (NodeList)xpath.evaluate(String.format(xpathExp, "hz-client", EncryptionReplacer.class.getName()), root, XPathConstants.NODESET);
            Preconditions.checkPositive(replaceTags.getLength(), "No EncryptionReplacer definition found within the provided XML document.");
        }
        return replaceTags.item(0);
    }

    private static Properties loadProperties(Node node) {
        Properties properties = new Properties();
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = DomConfigHelper.cleanNodeName(n);
            if (!"properties".equals(value)) continue;
            EncryptionReplacer.fillProperties(n, properties);
        }
        return properties;
    }

    private static void fillProperties(Node node, Properties properties) {
        if (properties == null) {
            return;
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(n);
            if (!"property".equals(name)) continue;
            String propertyName = EncryptionReplacer.getTextContent(n.getAttributes().getNamedItem("name"));
            String value = StringUtil.trim(EncryptionReplacer.getTextContent(n));
            properties.setProperty(propertyName, value == null ? "" : value);
        }
    }

    private static String getTextContent(Node node) {
        try {
            return node.getTextContent();
        }
        catch (Exception e) {
            return EncryptionReplacer.getTextContentOld(node);
        }
    }

    private static String getTextContentOld(Node node) {
        Node child = node.getFirstChild();
        if (child != null) {
            Node next = child.getNextSibling();
            if (next == null) {
                return EncryptionReplacer.hasTextContent(child) ? child.getNodeValue() : null;
            }
            StringBuilder buf = new StringBuilder();
            EncryptionReplacer.appendTextContents(node, buf);
            return buf.toString();
        }
        return null;
    }

    private static void appendTextContents(Node node, StringBuilder buf) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!EncryptionReplacer.hasTextContent(child)) continue;
            buf.append(child.getNodeValue());
        }
    }

    private static boolean hasTextContent(Node node) {
        short nodeType = node.getNodeType();
        return nodeType != 8 && nodeType != 7;
    }

    private static class HzNsContext
    implements NamespaceContext {
        private HzNsContext() {
        }

        @Override
        public String getNamespaceURI(String prefix) {
            if ("hz".equals(prefix)) {
                return "http://www.hazelcast.com/schema/config";
            }
            if ("hz-client".equals(prefix)) {
                return "http://www.hazelcast.com/schema/client-config";
            }
            return null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }
}

