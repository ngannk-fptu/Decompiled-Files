/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.user.configuration.xml;

import com.atlassian.user.configuration.xml.XMLConfigUtil;
import com.atlassian.user.util.ClassLoaderUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XMLDefaultsParser {
    private static final Logger log = Logger.getLogger(XMLDefaultsParser.class);
    public static final String DEFAULTS_FILE_NAME = "atlassian-user-defaults.xml";
    private final List<Node> defaultsBaseNodes = new ArrayList<Node>();

    public XMLDefaultsParser() throws IOException, DocumentException {
        this(DEFAULTS_FILE_NAME);
    }

    public XMLDefaultsParser(String defaultsFileName) throws IOException, DocumentException {
        this(new String[]{defaultsFileName});
    }

    private void initialiseDefaultsNodesForFile(String defaultsFileName) throws IOException, DocumentException {
        Enumeration defaultsFileUrls = ClassLoaderUtils.getResources(defaultsFileName, this.getClass());
        while (defaultsFileUrls.hasMoreElements()) {
            URL url = (URL)defaultsFileUrls.nextElement();
            Node defaultsNode = this.findBaseNodeInFile(url);
            if (defaultsNode != null) {
                this.defaultsBaseNodes.add(defaultsNode);
                continue;
            }
            log.error((Object)("Unable to find valid atlassian-user defaults data in file: " + url));
        }
    }

    public XMLDefaultsParser(String[] defaultsFileNames) throws DocumentException, IOException {
        for (String defaultsFileName : defaultsFileNames) {
            this.initialiseDefaultsNodesForFile(defaultsFileName);
        }
        if (this.defaultsBaseNodes.isEmpty()) {
            throw new FileNotFoundException("No valid user defaults files found in classpath with name: " + StringUtils.join((Object[])defaultsFileNames, (String)", "));
        }
    }

    public Map<String, String> getDefaultClassesConfigForKey(String key) throws DocumentException, IOException {
        HashMap<String, String> defaults = new HashMap<String, String>();
        for (Node node : this.defaultsBaseNodes) {
            Node defaultsNode = node.selectSingleNode(key);
            if (defaultsNode == null) continue;
            defaults.putAll(XMLConfigUtil.parseRepositoryElementForClassNames((Element)defaultsNode));
        }
        return defaults;
    }

    private Node findBaseNodeInFile(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(url);
        return doc.selectSingleNode("//default");
    }

    public Map<String, String> getDefaultParameterConfigForKey(String key) {
        HashMap<String, String> defaults = new HashMap<String, String>();
        for (Node node : this.defaultsBaseNodes) {
            Node defaultsNode = node.selectSingleNode(key);
            if (defaultsNode == null) continue;
            defaults.putAll(XMLConfigUtil.parseRepositoryElementForStringData((Element)defaultsNode));
            break;
        }
        return defaults;
    }
}

