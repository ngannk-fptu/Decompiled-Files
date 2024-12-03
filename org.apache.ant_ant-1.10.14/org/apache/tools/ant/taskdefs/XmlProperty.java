/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class XmlProperty
extends Task {
    private static final String ID = "id";
    private static final String REF_ID = "refid";
    private static final String LOCATION = "location";
    private static final String VALUE = "value";
    private static final String PATH = "path";
    private static final String PATHID = "pathid";
    private static final String[] ATTRIBUTES = new String[]{"id", "refid", "location", "value", "path", "pathid"};
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Resource src;
    private String prefix = "";
    private boolean keepRoot = true;
    private boolean validate = false;
    private boolean collapseAttributes = false;
    private boolean semanticAttributes = false;
    private boolean includeSemanticAttribute = false;
    private File rootDirectory = null;
    private Map<String, String> addedAttributes = new Hashtable<String, String>();
    private XMLCatalog xmlCatalog = new XMLCatalog();
    private String delimiter = ",";

    @Override
    public void init() {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
    }

    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }

    @Override
    public void execute() throws BuildException {
        Resource r = this.getResource();
        if (r == null) {
            throw new BuildException("XmlProperty task requires a source resource");
        }
        try {
            this.log("Loading " + this.src, 3);
            if (r.isExists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(this.validate);
                factory.setNamespaceAware(false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(this.getEntityResolver());
                FileProvider fp = this.src.as(FileProvider.class);
                Document document = fp != null ? builder.parse(fp.getFile()) : builder.parse(this.src.getInputStream());
                Element topElement = document.getDocumentElement();
                this.addedAttributes = new Hashtable<String, String>();
                if (this.keepRoot) {
                    this.addNodeRecursively(topElement, this.prefix, null);
                } else {
                    NodeList topChildren = topElement.getChildNodes();
                    int numChildren = topChildren.getLength();
                    for (int i = 0; i < numChildren; ++i) {
                        this.addNodeRecursively(topChildren.item(i), this.prefix, null);
                    }
                }
            } else {
                this.log("Unable to find property resource: " + r, 3);
            }
        }
        catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            throw new BuildException("Failed to load " + this.src, x);
        }
        catch (ParserConfigurationException pce) {
            throw new BuildException(pce);
        }
        catch (IOException ioe) {
            throw new BuildException("Failed to load " + this.src, ioe);
        }
    }

    private void addNodeRecursively(Node node, String prefix, Object container) {
        String nodePrefix = prefix;
        if (node.getNodeType() != 3) {
            if (!prefix.trim().isEmpty()) {
                nodePrefix = nodePrefix + ".";
            }
            nodePrefix = nodePrefix + node.getNodeName();
        }
        Object nodeObject = this.processNode(node, nodePrefix, container);
        if (node.hasChildNodes()) {
            NodeList nodeChildren = node.getChildNodes();
            int numChildren = nodeChildren.getLength();
            for (int i = 0; i < numChildren; ++i) {
                this.addNodeRecursively(nodeChildren.item(i), nodePrefix, nodeObject);
            }
        }
    }

    void addNodeRecursively(Node node, String prefix) {
        this.addNodeRecursively(node, prefix, null);
    }

    public Object processNode(Node node, String prefix, Object container) {
        boolean semanticEmptyOverride;
        Object addedPath = null;
        String id = null;
        if (node.hasAttributes()) {
            NamedNodeMap nodeAttributes = node.getAttributes();
            Node idNode = nodeAttributes.getNamedItem(ID);
            id = this.semanticAttributes && idNode != null ? idNode.getNodeValue() : null;
            for (int i = 0; i < nodeAttributes.getLength(); ++i) {
                Path containingPath;
                String attributeValue;
                Node attributeNode = nodeAttributes.item(i);
                if (!this.semanticAttributes) {
                    String attributeName = this.getAttributeName(attributeNode);
                    attributeValue = this.getAttributeValue(attributeNode);
                    this.addProperty(prefix + attributeName, attributeValue, null);
                    continue;
                }
                String nodeName = attributeNode.getNodeName();
                attributeValue = this.getAttributeValue(attributeNode);
                Path path = containingPath = container instanceof Path ? (Path)container : null;
                if (ID.equals(nodeName)) continue;
                if (containingPath != null && PATH.equals(nodeName)) {
                    containingPath.setPath(attributeValue);
                    continue;
                }
                if (containingPath != null && container instanceof Path && REF_ID.equals(nodeName)) {
                    containingPath.setPath(attributeValue);
                    continue;
                }
                if (containingPath != null && container instanceof Path && LOCATION.equals(nodeName)) {
                    containingPath.setLocation(this.resolveFile(attributeValue));
                    continue;
                }
                if (PATHID.equals(nodeName)) {
                    if (container != null) {
                        throw new BuildException("XmlProperty does not support nested paths");
                    }
                    addedPath = new Path(this.getProject());
                    this.getProject().addReference(attributeValue, addedPath);
                    continue;
                }
                String attributeName = this.getAttributeName(attributeNode);
                this.addProperty(prefix + attributeName, attributeValue, id);
            }
        }
        String nodeText = null;
        boolean emptyNode = false;
        boolean bl = semanticEmptyOverride = node.getNodeType() == 1 && this.semanticAttributes && node.hasAttributes() && (node.getAttributes().getNamedItem(VALUE) != null || node.getAttributes().getNamedItem(LOCATION) != null || node.getAttributes().getNamedItem(REF_ID) != null || node.getAttributes().getNamedItem(PATH) != null || node.getAttributes().getNamedItem(PATHID) != null);
        if (node.getNodeType() == 3) {
            nodeText = this.getAttributeValue(node);
        } else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == 4) {
            nodeText = node.getFirstChild().getNodeValue();
            if (nodeText.isEmpty() && !semanticEmptyOverride) {
                emptyNode = true;
            }
        } else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 0 && !semanticEmptyOverride) {
            nodeText = "";
            emptyNode = true;
        } else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == 3 && node.getFirstChild().getNodeValue().isEmpty() && !semanticEmptyOverride) {
            nodeText = "";
            emptyNode = true;
        }
        if (nodeText != null) {
            if (this.semanticAttributes && id == null && container instanceof String) {
                id = (String)container;
            }
            if (!nodeText.trim().isEmpty() || emptyNode) {
                this.addProperty(prefix, nodeText, id);
            }
        }
        return addedPath != null ? addedPath : id;
    }

    private void addProperty(String name, String value, String id) {
        String msg = name + ":" + value;
        if (id != null) {
            msg = msg + "(id=" + id + ")";
        }
        this.log(msg, 4);
        if (this.addedAttributes.containsKey(name)) {
            value = this.addedAttributes.get(name) + this.getDelimiter() + value;
            this.getProject().setProperty(name, value);
            this.addedAttributes.put(name, value);
        } else if (this.getProject().getProperty(name) == null) {
            this.getProject().setNewProperty(name, value);
            this.addedAttributes.put(name, value);
        } else {
            this.log("Override ignored for property " + name, 3);
        }
        if (id != null) {
            this.getProject().addReference(id, value);
        }
    }

    private String getAttributeName(Node attributeNode) {
        String attributeName = attributeNode.getNodeName();
        if (this.semanticAttributes) {
            if (REF_ID.equals(attributeName)) {
                return "";
            }
            if (!XmlProperty.isSemanticAttribute(attributeName) || this.includeSemanticAttribute) {
                return "." + attributeName;
            }
            return "";
        }
        return this.collapseAttributes ? "." + attributeName : "(" + attributeName + ")";
    }

    private static boolean isSemanticAttribute(String attributeName) {
        return Arrays.asList(ATTRIBUTES).contains(attributeName);
    }

    private String getAttributeValue(Node attributeNode) {
        String nodeValue = attributeNode.getNodeValue().trim();
        if (this.semanticAttributes) {
            Object ref;
            String attributeName = attributeNode.getNodeName();
            nodeValue = this.getProject().replaceProperties(nodeValue);
            if (LOCATION.equals(attributeName)) {
                File f = this.resolveFile(nodeValue);
                return f.getPath();
            }
            if (REF_ID.equals(attributeName) && (ref = this.getProject().getReference(nodeValue)) != null) {
                return ref.toString();
            }
        }
        return nodeValue;
    }

    public void setFile(File src) {
        this.setSrcResource(new FileResource(src));
    }

    public void setSrcResource(Resource src) {
        if (src.isDirectory()) {
            throw new BuildException("the source can't be a directory");
        }
        if (src.as(FileProvider.class) == null && !this.supportsNonFileResources()) {
            throw new BuildException("Only FileSystem resources are supported.");
        }
        this.src = src;
    }

    public void addConfigured(ResourceCollection a) {
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.setSrcResource((Resource)a.iterator().next());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix.trim();
    }

    public void setKeeproot(boolean keepRoot) {
        this.keepRoot = keepRoot;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public void setCollapseAttributes(boolean collapseAttributes) {
        this.collapseAttributes = collapseAttributes;
    }

    public void setSemanticAttributes(boolean semanticAttributes) {
        this.semanticAttributes = semanticAttributes;
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void setIncludeSemanticAttribute(boolean includeSemanticAttribute) {
        this.includeSemanticAttribute = includeSemanticAttribute;
    }

    public void addConfiguredXMLCatalog(XMLCatalog catalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(catalog);
    }

    protected File getFile() {
        FileProvider fp = this.src.as(FileProvider.class);
        return fp != null ? fp.getFile() : null;
    }

    protected Resource getResource() {
        File f = this.getFile();
        FileProvider fp = this.src.as(FileProvider.class);
        return f == null ? this.src : (fp != null && fp.getFile().equals(f) ? this.src : new FileResource(f));
    }

    protected String getPrefix() {
        return this.prefix;
    }

    protected boolean getKeeproot() {
        return this.keepRoot;
    }

    protected boolean getValidate() {
        return this.validate;
    }

    protected boolean getCollapseAttributes() {
        return this.collapseAttributes;
    }

    protected boolean getSemanticAttributes() {
        return this.semanticAttributes;
    }

    protected File getRootDirectory() {
        return this.rootDirectory;
    }

    @Deprecated
    protected boolean getIncludeSementicAttribute() {
        return this.getIncludeSemanticAttribute();
    }

    protected boolean getIncludeSemanticAttribute() {
        return this.includeSemanticAttribute;
    }

    private File resolveFile(String fileName) {
        return FILE_UTILS.resolveFile(this.rootDirectory == null ? this.getProject().getBaseDir() : this.rootDirectory, fileName);
    }

    protected boolean supportsNonFileResources() {
        return this.getClass().equals(XmlProperty.class);
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}

