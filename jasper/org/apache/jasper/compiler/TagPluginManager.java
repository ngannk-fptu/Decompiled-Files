/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.tomcat.util.descriptor.tagplugin.TagPluginParser
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 */
package org.apache.jasper.compiler;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.tagplugin.TagPlugin;
import org.apache.jasper.compiler.tagplugin.TagPluginContext;
import org.apache.tomcat.util.descriptor.tagplugin.TagPluginParser;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.xml.sax.SAXException;

public class TagPluginManager {
    private static final String META_INF_JASPER_TAG_PLUGINS_XML = "META-INF/org.apache.jasper/tagPlugins.xml";
    private static final String TAG_PLUGINS_XML = "/WEB-INF/tagPlugins.xml";
    private final ServletContext ctxt;
    private HashMap<String, TagPlugin> tagPlugins;
    private boolean initialized = false;

    public TagPluginManager(ServletContext ctxt) {
        this.ctxt = ctxt;
    }

    public void apply(Node.Nodes page, ErrorDispatcher err, PageInfo pageInfo) throws JasperException {
        this.init(err);
        if (!this.tagPlugins.isEmpty()) {
            page.visit(new NodeVisitor(this, pageInfo));
        }
    }

    private void init(ErrorDispatcher err) throws JasperException {
        TagPluginParser parser;
        ClassLoader original;
        PrivilegedGetTccl pa;
        if (this.initialized) {
            return;
        }
        String blockExternalString = this.ctxt.getInitParameter("org.apache.jasper.XML_BLOCK_EXTERNAL");
        boolean blockExternal = blockExternalString == null ? true : Boolean.parseBoolean(blockExternalString);
        Thread currentThread = Thread.currentThread();
        if (Constants.IS_SECURITY_ENABLED) {
            pa = new PrivilegedGetTccl(currentThread);
            original = (ClassLoader)AccessController.doPrivileged(pa);
        } else {
            original = currentThread.getContextClassLoader();
        }
        try {
            URL url;
            if (Constants.IS_SECURITY_ENABLED) {
                pa = new PrivilegedSetTccl(currentThread, TagPluginManager.class.getClassLoader());
                AccessController.doPrivileged(pa);
            } else {
                currentThread.setContextClassLoader(TagPluginManager.class.getClassLoader());
            }
            parser = new TagPluginParser(this.ctxt, blockExternal);
            Enumeration<URL> urls = this.ctxt.getClassLoader().getResources(META_INF_JASPER_TAG_PLUGINS_XML);
            while (urls.hasMoreElements()) {
                url = urls.nextElement();
                parser.parse(url);
            }
            url = this.ctxt.getResource(TAG_PLUGINS_XML);
            if (url != null) {
                parser.parse(url);
            }
        }
        catch (IOException | SAXException e) {
            throw new JasperException(e);
        }
        finally {
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa2 = new PrivilegedSetTccl(currentThread, original);
                AccessController.doPrivileged(pa2);
            } else {
                currentThread.setContextClassLoader(original);
            }
        }
        Map plugins = parser.getPlugins();
        this.tagPlugins = new HashMap(plugins.size());
        for (Map.Entry entry : plugins.entrySet()) {
            try {
                String tagClass = (String)entry.getKey();
                String pluginName = (String)entry.getValue();
                Class<?> pluginClass = this.ctxt.getClassLoader().loadClass(pluginName);
                TagPlugin plugin = (TagPlugin)pluginClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                this.tagPlugins.put(tagClass, plugin);
            }
            catch (Exception e) {
                err.jspError(e);
            }
        }
        this.initialized = true;
    }

    private void invokePlugin(Node.CustomTag n, PageInfo pageInfo) {
        TagPlugin tagPlugin = this.tagPlugins.get(n.getTagHandlerClass().getName());
        if (tagPlugin == null) {
            return;
        }
        TagPluginContextImpl tagPluginContext = new TagPluginContextImpl(n, pageInfo);
        n.setTagPluginContext(tagPluginContext);
        tagPlugin.doTag(tagPluginContext);
    }

    private static class NodeVisitor
    extends Node.Visitor {
        private final TagPluginManager manager;
        private final PageInfo pageInfo;

        NodeVisitor(TagPluginManager manager, PageInfo pageInfo) {
            this.manager = manager;
            this.pageInfo = pageInfo;
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.manager.invokePlugin(n, this.pageInfo);
            this.visitBody(n);
        }
    }

    private static class TagPluginContextImpl
    implements TagPluginContext {
        private final Node.CustomTag node;
        private final PageInfo pageInfo;
        private final HashMap<String, Object> pluginAttributes;
        private Node.Nodes curNodes;

        TagPluginContextImpl(Node.CustomTag n, PageInfo pageInfo) {
            this.node = n;
            this.pageInfo = pageInfo;
            this.curNodes = new Node.Nodes();
            n.setAtETag(this.curNodes);
            this.curNodes = new Node.Nodes();
            n.setAtSTag(this.curNodes);
            n.setUseTagPlugin(true);
            this.pluginAttributes = new HashMap();
        }

        @Override
        public TagPluginContext getParentContext() {
            Node parent = this.node.getParent();
            if (!(parent instanceof Node.CustomTag)) {
                return null;
            }
            return ((Node.CustomTag)parent).getTagPluginContext();
        }

        @Override
        public void setPluginAttribute(String key, Object value) {
            this.pluginAttributes.put(key, value);
        }

        @Override
        public Object getPluginAttribute(String key) {
            return this.pluginAttributes.get(key);
        }

        @Override
        public boolean isScriptless() {
            return this.node.getChildInfo().isScriptless();
        }

        @Override
        public boolean isConstantAttribute(String attribute) {
            Node.JspAttribute attr = this.getNodeAttribute(attribute);
            if (attr == null) {
                return false;
            }
            return attr.isLiteral();
        }

        @Override
        public String getConstantAttribute(String attribute) {
            Node.JspAttribute attr = this.getNodeAttribute(attribute);
            if (attr == null) {
                return null;
            }
            return attr.getValue();
        }

        @Override
        public boolean isAttributeSpecified(String attribute) {
            return this.getNodeAttribute(attribute) != null;
        }

        @Override
        public String getTemporaryVariableName() {
            return this.node.getRoot().nextTemporaryVariableName();
        }

        @Override
        public void generateImport(String imp) {
            this.pageInfo.addImport(imp);
        }

        @Override
        public void generateDeclaration(String id, String text) {
            if (this.pageInfo.isPluginDeclared(id)) {
                return;
            }
            this.curNodes.add(new Node.Declaration(text, this.node.getStart(), null));
        }

        @Override
        public void generateJavaSource(String sourceCode) {
            this.curNodes.add(new Node.Scriptlet(sourceCode, this.node.getStart(), null));
        }

        @Override
        public void generateAttribute(String attributeName) {
            this.curNodes.add(new Node.AttributeGenerator(this.node.getStart(), attributeName, this.node));
        }

        @Override
        public void dontUseTagPlugin() {
            this.node.setUseTagPlugin(false);
        }

        @Override
        public void generateBody() {
            this.curNodes = this.node.getAtETag();
        }

        @Override
        public boolean isTagFile() {
            return this.pageInfo.isTagFile();
        }

        private Node.JspAttribute getNodeAttribute(String attribute) {
            Node.JspAttribute[] attrs = this.node.getJspAttributes();
            for (int i = 0; attrs != null && i < attrs.length; ++i) {
                if (!attrs[i].getName().equals(attribute)) continue;
                return attrs[i];
            }
            return null;
        }
    }
}

