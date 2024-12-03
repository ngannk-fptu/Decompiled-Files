/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletContext;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPlugin;
import org.apache.sling.scripting.jsp.jasper.compiler.tagplugin.TagPluginContext;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ParserUtils;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;

public class TagPluginManager {
    private static final String TAG_PLUGINS_XML = "/WEB-INF/tagPlugins.xml";
    private static final String TAG_PLUGINS_ROOT_ELEM = "tag-plugins";
    private boolean initialized = false;
    private HashMap tagPlugins = null;
    private ServletContext ctxt;
    private PageInfo pageInfo;

    public TagPluginManager(ServletContext ctxt) {
        this.ctxt = ctxt;
    }

    public void apply(Node.Nodes page, ErrorDispatcher err, PageInfo pageInfo) throws JasperException {
        this.init(err);
        if (this.tagPlugins == null || this.tagPlugins.size() == 0) {
            return;
        }
        this.pageInfo = pageInfo;
        page.visit(new Node.Visitor(){

            @Override
            public void visit(Node.CustomTag n) throws JasperException {
                TagPluginManager.this.invokePlugin(n);
                this.visitBody(n);
            }
        });
    }

    private void init(ErrorDispatcher err) throws JasperException {
        if (this.initialized) {
            return;
        }
        InputStream is = this.ctxt.getResourceAsStream(TAG_PLUGINS_XML);
        if (is == null) {
            return;
        }
        TreeNode root = new ParserUtils().parseXMLDocument(TAG_PLUGINS_XML, is);
        if (root == null) {
            return;
        }
        if (!TAG_PLUGINS_ROOT_ELEM.equals(root.getName())) {
            err.jspError("jsp.error.plugin.wrongRootElement", TAG_PLUGINS_XML, TAG_PLUGINS_ROOT_ELEM);
        }
        this.tagPlugins = new HashMap();
        Iterator pluginList = root.findChildren("tag-plugin");
        while (pluginList.hasNext()) {
            TreeNode pluginNode = (TreeNode)pluginList.next();
            TreeNode tagClassNode = pluginNode.findChild("tag-class");
            if (tagClassNode == null) {
                return;
            }
            String tagClass = tagClassNode.getBody().trim();
            TreeNode pluginClassNode = pluginNode.findChild("plugin-class");
            if (pluginClassNode == null) {
                return;
            }
            String pluginClassStr = pluginClassNode.getBody();
            TagPlugin tagPlugin = null;
            try {
                Class<?> pluginClass = this.getClass().getClassLoader().loadClass(pluginClassStr);
                tagPlugin = (TagPlugin)pluginClass.newInstance();
            }
            catch (Exception e) {
                throw new JasperException(e);
            }
            if (tagPlugin == null) {
                return;
            }
            this.tagPlugins.put(tagClass, tagPlugin);
        }
        this.initialized = true;
    }

    private void invokePlugin(Node.CustomTag n) {
        TagPlugin tagPlugin = (TagPlugin)this.tagPlugins.get(n.getTagHandlerClass().getName());
        if (tagPlugin == null) {
            return;
        }
        TagPluginContextImpl tagPluginContext = new TagPluginContextImpl(n, this.pageInfo);
        n.setTagPluginContext(tagPluginContext);
        tagPlugin.doTag(tagPluginContext);
    }

    static class TagPluginContextImpl
    implements TagPluginContext {
        private Node.CustomTag node;
        private Node.Nodes curNodes;
        private PageInfo pageInfo;
        private HashMap pluginAttributes;

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

