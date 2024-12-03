/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationXMLReader;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class HierarchicalConfigurationXMLReader<T>
extends ConfigurationXMLReader {
    private HierarchicalConfiguration<T> configuration;

    public HierarchicalConfigurationXMLReader() {
    }

    public HierarchicalConfigurationXMLReader(HierarchicalConfiguration<T> config) {
        this();
        this.setConfiguration(config);
    }

    public HierarchicalConfiguration<T> getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(HierarchicalConfiguration<T> config) {
        this.configuration = config;
    }

    @Override
    public Configuration getParsedConfiguration() {
        return this.getConfiguration();
    }

    @Override
    protected void processKeys() {
        NodeHandler nodeHandler = this.getConfiguration().getNodeModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS(nodeHandler.getRootNode(), new SAXVisitor(), nodeHandler);
    }

    private class SAXVisitor
    extends ConfigurationNodeVisitorAdapter<T> {
        private static final String ATTR_TYPE = "CDATA";

        private SAXVisitor() {
        }

        @Override
        public void visitAfterChildren(T node, NodeHandler<T> handler) {
            HierarchicalConfigurationXMLReader.this.fireElementEnd(this.nodeName(node, handler));
        }

        @Override
        public void visitBeforeChildren(T node, NodeHandler<T> handler) {
            HierarchicalConfigurationXMLReader.this.fireElementStart(this.nodeName(node, handler), this.fetchAttributes(node, handler));
            Object value = handler.getValue(node);
            if (value != null) {
                HierarchicalConfigurationXMLReader.this.fireCharacters(value.toString());
            }
        }

        @Override
        public boolean terminate() {
            return HierarchicalConfigurationXMLReader.this.getException() != null;
        }

        protected Attributes fetchAttributes(T node, NodeHandler<T> handler) {
            AttributesImpl attrs = new AttributesImpl();
            handler.getAttributes(node).forEach(attr -> {
                Object value = handler.getAttributeValue(node, (String)attr);
                if (value != null) {
                    attrs.addAttribute("", (String)attr, (String)attr, ATTR_TYPE, value.toString());
                }
            });
            return attrs;
        }

        private String nodeName(T node, NodeHandler<T> handler) {
            String nodeName = handler.nodeName(node);
            return nodeName == null ? HierarchicalConfigurationXMLReader.this.getRootName() : nodeName;
        }
    }
}

