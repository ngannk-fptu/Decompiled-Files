/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.ConstructorArg;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.tree.NodeHandler;

public class XMLBeanDeclaration
implements BeanDeclaration {
    public static final String RESERVED_PREFIX = "config-";
    public static final String ATTR_PREFIX = "[@config-";
    public static final String ATTR_BEAN_CLASS = "[@config-class]";
    public static final String ATTR_BEAN_FACTORY = "[@config-factory]";
    public static final String ATTR_FACTORY_PARAM = "[@config-factoryParam]";
    private static final String ATTR_BEAN_CLASS_NAME = "config-class";
    private static final String ELEM_CTOR_ARG = "config-constrarg";
    private static final String ATTR_CTOR_VALUE = "config-value";
    private static final String ATTR_CTOR_TYPE = "config-type";
    private final HierarchicalConfiguration<?> configuration;
    private final NodeData<?> nodeData;
    private final String defaultBeanClassName;

    public <T> XMLBeanDeclaration(HierarchicalConfiguration<T> config, String key) {
        this(config, key, false);
    }

    public <T> XMLBeanDeclaration(HierarchicalConfiguration<T> config, String key, boolean optional) {
        this(config, key, optional, null);
    }

    public <T> XMLBeanDeclaration(HierarchicalConfiguration<T> config, String key, boolean optional, String defBeanClsName) {
        BaseHierarchicalConfiguration tmpconfiguration;
        if (config == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        try {
            tmpconfiguration = config.configurationAt(key);
        }
        catch (ConfigurationRuntimeException iex) {
            if (!optional || config.getMaxIndex(key) > 0) {
                throw iex;
            }
            tmpconfiguration = new BaseHierarchicalConfiguration();
        }
        this.nodeData = XMLBeanDeclaration.createNodeDataFromConfiguration(tmpconfiguration);
        this.configuration = tmpconfiguration;
        this.defaultBeanClassName = defBeanClsName;
        this.initSubnodeConfiguration(this.getConfiguration());
    }

    public <T> XMLBeanDeclaration(HierarchicalConfiguration<T> config) {
        this(config, (String)null);
    }

    XMLBeanDeclaration(HierarchicalConfiguration<?> config, NodeData<?> node) {
        this.nodeData = node;
        this.configuration = config;
        this.defaultBeanClassName = null;
        this.initSubnodeConfiguration(config);
    }

    public HierarchicalConfiguration<?> getConfiguration() {
        return this.configuration;
    }

    public String getDefaultBeanClassName() {
        return this.defaultBeanClassName;
    }

    @Override
    public String getBeanFactoryName() {
        return this.getConfiguration().getString(ATTR_BEAN_FACTORY, null);
    }

    @Override
    public Object getBeanFactoryParameter() {
        return this.getConfiguration().getProperty(ATTR_FACTORY_PARAM);
    }

    @Override
    public String getBeanClassName() {
        return this.getConfiguration().getString(ATTR_BEAN_CLASS, this.getDefaultBeanClassName());
    }

    @Override
    public Map<String, Object> getBeanProperties() {
        return this.getAttributeNames().stream().filter(e -> !this.isReservedAttributeName((String)e)).collect(Collectors.toMap(Function.identity(), e -> this.interpolate(this.getNode().getAttribute((String)e))));
    }

    @Override
    public Map<String, Object> getNestedBeanDeclarations() {
        HashMap<String, Object> nested = new HashMap<String, Object>();
        this.getNode().getChildren().forEach(child -> {
            if (!this.isReservedChildName(child.nodeName())) {
                Object obj = nested.get(child.nodeName());
                if (obj != null) {
                    List<BeanDeclaration> list;
                    if (obj instanceof List) {
                        List tmpList = (List)obj;
                        list = tmpList;
                    } else {
                        list = new ArrayList();
                        list.add((BeanDeclaration)obj);
                        nested.put(child.nodeName(), list);
                    }
                    list.add(this.createBeanDeclaration((NodeData<?>)child));
                } else {
                    nested.put(child.nodeName(), this.createBeanDeclaration((NodeData<?>)child));
                }
            }
        });
        return nested;
    }

    @Override
    public Collection<ConstructorArg> getConstructorArgs() {
        return this.getNode().getChildren(ELEM_CTOR_ARG).stream().map(this::createConstructorArg).collect(Collectors.toCollection(LinkedList::new));
    }

    protected Object interpolate(Object value) {
        ConfigurationInterpolator interpolator = this.getConfiguration().getInterpolator();
        return interpolator != null ? interpolator.interpolate(value) : value;
    }

    protected boolean isReservedChildName(String name) {
        return this.isReservedName(name);
    }

    protected boolean isReservedAttributeName(String name) {
        return this.isReservedName(name);
    }

    protected boolean isReservedName(String name) {
        return name == null || name.startsWith(RESERVED_PREFIX);
    }

    protected Set<String> getAttributeNames() {
        return this.getNode().getAttributes();
    }

    NodeData<?> getNode() {
        return this.nodeData;
    }

    BeanDeclaration createBeanDeclaration(NodeData<?> nodeData) {
        for (HierarchicalConfiguration<?> config : this.getConfiguration().configurationsAt(nodeData.escapedNodeName(this.getConfiguration()))) {
            if (!nodeData.matchesConfigRootNode(config)) continue;
            return new XMLBeanDeclaration(config, nodeData);
        }
        throw new ConfigurationRuntimeException("Unable to match node for " + nodeData.nodeName());
    }

    private void initSubnodeConfiguration(HierarchicalConfiguration<?> conf) {
        conf.setExpressionEngine(null);
    }

    private ConstructorArg createConstructorArg(NodeData<?> child) {
        String type = this.getAttribute(child, ATTR_CTOR_TYPE);
        if (XMLBeanDeclaration.isBeanDeclarationArgument(child)) {
            return ConstructorArg.forValue(this.getAttribute(child, ATTR_CTOR_VALUE), type);
        }
        return ConstructorArg.forBeanDeclaration(this.createBeanDeclaration(child), type);
    }

    private String getAttribute(NodeData<?> nodeData, String attribute) {
        Object value = nodeData.getAttribute(attribute);
        return value == null ? null : String.valueOf(this.interpolate(value));
    }

    private static boolean isBeanDeclarationArgument(NodeData<?> nodeData) {
        return !nodeData.getAttributes().contains(ATTR_BEAN_CLASS_NAME);
    }

    private static <T> NodeData<T> createNodeDataFromConfiguration(HierarchicalConfiguration<T> config) {
        NodeHandler handler = config.getNodeModel().getNodeHandler();
        return new NodeData(handler.getRootNode(), handler);
    }

    static class NodeData<T> {
        private final T node;
        private final NodeHandler<T> nodeHandler;

        NodeData(T node, NodeHandler<T> nodeHandler) {
            this.node = node;
            this.nodeHandler = nodeHandler;
        }

        String nodeName() {
            return this.nodeHandler.nodeName(this.node);
        }

        String escapedNodeName(HierarchicalConfiguration<?> config) {
            return config.getExpressionEngine().nodeKey(this.node, "", this.nodeHandler);
        }

        List<NodeData<T>> getChildren() {
            return this.wrapInNodeData(this.nodeHandler.getChildren(this.node));
        }

        List<NodeData<T>> getChildren(String name) {
            return this.wrapInNodeData(this.nodeHandler.getChildren(this.node, name));
        }

        Set<String> getAttributes() {
            return this.nodeHandler.getAttributes(this.node);
        }

        Object getAttribute(String key) {
            return this.nodeHandler.getAttributeValue(this.node, key);
        }

        boolean matchesConfigRootNode(HierarchicalConfiguration<?> config) {
            return config.getNodeModel().getNodeHandler().getRootNode().equals(this.node);
        }

        List<NodeData<T>> wrapInNodeData(List<T> nodes) {
            return nodes.stream().map(n -> new NodeData<Object>(n, this.nodeHandler)).collect(Collectors.toList());
        }
    }
}

