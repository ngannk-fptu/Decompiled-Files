/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Processor;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PomParser
extends Processor {
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    static XPathFactory xpathf = XPathFactory.newInstance();
    static Set<String> multiple = new HashSet<String>();
    static Set<String> skip = new HashSet<String>();

    public Properties getProperties(File pom) throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();
        XPath xpath = xpathf.newXPath();
        pom = pom.getAbsoluteFile();
        Document doc = db.parse(pom);
        UTF8Properties p = new UTF8Properties();
        String relativePath = xpath.evaluate("project/parent/relativePath", doc);
        if (relativePath != null && relativePath.length() != 0) {
            File parentPom = IO.getFile(pom.getParentFile(), relativePath);
            if (parentPom.isFile()) {
                Properties parentProps = this.getProperties(parentPom);
                p.putAll((Map<?, ?>)parentProps);
            } else {
                this.error("Parent pom for %s is not an existing file (could be directory): %s", pom, parentPom);
            }
        }
        Element e = doc.getDocumentElement();
        PomParser.traverse("pom", e, p);
        String[] scopes = new String[]{"provided", "runtime", "test", "system"};
        NodeList set = (NodeList)xpath.evaluate("//dependency[not(scope) or scope='compile']", doc, XPathConstants.NODESET);
        if (set.getLength() != 0) {
            p.put("pom.scope.compile", this.toBsn(set));
        }
        for (String scope : scopes) {
            set = (NodeList)xpath.evaluate("//dependency[scope='" + scope + "']", doc, XPathConstants.NODESET);
            if (set.getLength() == 0) continue;
            p.put("pom.scope." + scope, this.toBsn(set));
        }
        return p;
    }

    private Object toBsn(NodeList set) throws XPathExpressionException {
        XPath xpath = xpathf.newXPath();
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < set.getLength(); ++i) {
            Node child = set.item(i);
            String version = xpath.evaluate("version", child);
            sb.append(del);
            sb.append(xpath.evaluate("groupId", child));
            sb.append(".");
            sb.append(xpath.evaluate("artifactId", child));
            if (version != null && version.trim().length() != 0) {
                sb.append(";version=");
                sb.append(Analyzer.cleanupVersion(version));
            }
            del = ", ";
        }
        return sb.toString();
    }

    static void traverse(String name, Node parent, Properties p) {
        if (skip.contains(parent.getNodeName())) {
            return;
        }
        NodeList children = parent.getChildNodes();
        if (multiple.contains(parent.getNodeName())) {
            int n = 0;
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child instanceof Text) continue;
                PomParser.traverse(name + "." + n++, child, p);
            }
        } else {
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child instanceof Text) {
                    String value = child.getNodeValue().trim();
                    if (value.length() == 0) continue;
                    p.put(name, value);
                    continue;
                }
                PomParser.traverse(name + "." + child.getNodeName(), child, p);
            }
        }
    }

    static {
        dbf.setNamespaceAware(false);
        multiple.add("mailingLists");
        multiple.add("pluginRepositories");
        multiple.add("repositories");
        multiple.add("resources");
        multiple.add("executions");
        multiple.add("goals");
        multiple.add("includes");
        multiple.add("excludes");
        skip.add("plugins");
        skip.add("dependencies");
        skip.add("reporting");
        skip.add("extensions");
    }
}

