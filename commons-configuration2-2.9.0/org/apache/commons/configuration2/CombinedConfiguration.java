/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.FindNodeVisitor;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.tree.DefaultConfigurationKey;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.configuration2.tree.TreeUtils;
import org.apache.commons.configuration2.tree.UnionCombiner;
import org.apache.commons.lang3.StringUtils;

public class CombinedConfiguration
extends BaseHierarchicalConfiguration
implements EventListener<ConfigurationEvent> {
    public static final EventType<ConfigurationEvent> COMBINED_INVALIDATE = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "COMBINED_INVALIDATE");
    private static final DefaultExpressionEngine AT_ENGINE = DefaultExpressionEngine.INSTANCE;
    private static final NodeCombiner DEFAULT_COMBINER = new UnionCombiner();
    private static final ImmutableNode EMPTY_ROOT = new ImmutableNode.Builder().create();
    private NodeCombiner nodeCombiner;
    private List<ConfigData> configurations;
    private Map<String, Configuration> namedConfigurations;
    private ExpressionEngine conversionExpressionEngine;
    private boolean upToDate;

    public CombinedConfiguration(NodeCombiner comb) {
        this.nodeCombiner = comb != null ? comb : DEFAULT_COMBINER;
        this.initChildCollections();
    }

    public CombinedConfiguration() {
        this((NodeCombiner)null);
    }

    public NodeCombiner getNodeCombiner() {
        this.beginRead(true);
        try {
            NodeCombiner nodeCombiner = this.nodeCombiner;
            return nodeCombiner;
        }
        finally {
            this.endRead();
        }
    }

    public void setNodeCombiner(NodeCombiner nodeCombiner) {
        if (nodeCombiner == null) {
            throw new IllegalArgumentException("Node combiner must not be null!");
        }
        this.beginWrite(true);
        try {
            this.nodeCombiner = nodeCombiner;
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
    }

    public ExpressionEngine getConversionExpressionEngine() {
        this.beginRead(true);
        try {
            ExpressionEngine expressionEngine = this.conversionExpressionEngine;
            return expressionEngine;
        }
        finally {
            this.endRead();
        }
    }

    public void setConversionExpressionEngine(ExpressionEngine conversionExpressionEngine) {
        this.beginWrite(true);
        try {
            this.conversionExpressionEngine = conversionExpressionEngine;
        }
        finally {
            this.endWrite();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addConfiguration(Configuration config, String name, String at) {
        if (config == null) {
            throw new IllegalArgumentException("Added configuration must not be null!");
        }
        this.beginWrite(true);
        try {
            if (name != null && this.namedConfigurations.containsKey(name)) {
                throw new ConfigurationRuntimeException("A configuration with the name '" + name + "' already exists in this combined configuration!");
            }
            ConfigData cd = new ConfigData(config, name, at);
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Adding configuration " + config + " with name " + name);
            }
            this.configurations.add(cd);
            if (name != null) {
                this.namedConfigurations.put(name, config);
            }
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
        this.registerListenerAt(config);
    }

    public void addConfiguration(Configuration config, String name) {
        this.addConfiguration(config, name, null);
    }

    public void addConfiguration(Configuration config) {
        this.addConfiguration(config, null, null);
    }

    public int getNumberOfConfigurations() {
        this.beginRead(true);
        try {
            int n = this.getNumberOfConfigurationsInternal();
            return n;
        }
        finally {
            this.endRead();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Configuration getConfiguration(int index) {
        this.beginRead(true);
        try {
            ConfigData cd = this.configurations.get(index);
            Configuration configuration = cd.getConfiguration();
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    public Configuration getConfiguration(String name) {
        this.beginRead(true);
        try {
            Configuration configuration = this.namedConfigurations.get(name);
            return configuration;
        }
        finally {
            this.endRead();
        }
    }

    public List<Configuration> getConfigurations() {
        this.beginRead(true);
        try {
            List<Configuration> list = this.configurations.stream().map(ConfigData::getConfiguration).collect(Collectors.toList());
            return list;
        }
        finally {
            this.endRead();
        }
    }

    public List<String> getConfigurationNameList() {
        this.beginRead(true);
        try {
            List<String> list = this.configurations.stream().map(ConfigData::getName).collect(Collectors.toList());
            return list;
        }
        finally {
            this.endRead();
        }
    }

    public boolean removeConfiguration(Configuration config) {
        for (int index = 0; index < this.getNumberOfConfigurations(); ++index) {
            if (this.configurations.get(index).getConfiguration() != config) continue;
            this.removeConfigurationAt(index);
            return true;
        }
        return false;
    }

    public Configuration removeConfigurationAt(int index) {
        ConfigData cd = this.configurations.remove(index);
        if (cd.getName() != null) {
            this.namedConfigurations.remove(cd.getName());
        }
        this.unregisterListenerAt(cd.getConfiguration());
        this.invalidateInternal();
        return cd.getConfiguration();
    }

    public Configuration removeConfiguration(String name) {
        Configuration conf = this.getConfiguration(name);
        if (conf != null) {
            this.removeConfiguration(conf);
        }
        return conf;
    }

    public Set<String> getConfigurationNames() {
        this.beginRead(true);
        try {
            Set<String> set = this.namedConfigurations.keySet();
            return set;
        }
        finally {
            this.endRead();
        }
    }

    public void invalidate() {
        this.beginWrite(true);
        try {
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
    }

    @Override
    public void onEvent(ConfigurationEvent event) {
        if (event.isBeforeUpdate()) {
            this.invalidate();
        }
    }

    @Override
    protected void clearInternal() {
        this.unregisterListenerAtChildren();
        this.initChildCollections();
        this.invalidateInternal();
    }

    @Override
    public Object clone() {
        this.beginRead(false);
        try {
            CombinedConfiguration copy = (CombinedConfiguration)super.clone();
            copy.initChildCollections();
            this.configurations.forEach(cd -> copy.addConfiguration(ConfigurationUtils.cloneConfiguration(cd.getConfiguration()), cd.getName(), cd.getAt()));
            CombinedConfiguration combinedConfiguration = copy;
            return combinedConfiguration;
        }
        finally {
            this.endRead();
        }
    }

    public Configuration getSource(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        Set<Configuration> sources = this.getSources(key);
        if (sources.isEmpty()) {
            return null;
        }
        Iterator<Configuration> iterator = sources.iterator();
        Configuration source = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
        }
        return source;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<Configuration> getSources(String key) {
        this.beginRead(false);
        try {
            List<QueryResult<QueryResult>> results = this.fetchNodeList(key);
            HashSet<Configuration> sources = new HashSet<Configuration>();
            results.forEach(result -> {
                Set<Configuration> resultSources = this.findSourceConfigurations((ImmutableNode)result.getNode());
                if (resultSources.isEmpty()) {
                    sources.add(this);
                } else {
                    sources.addAll(resultSources);
                }
            });
            HashSet<Configuration> hashSet = sources;
            return hashSet;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    protected void beginRead(boolean optimize) {
        if (optimize) {
            super.beginRead(true);
            return;
        }
        boolean lockObtained = false;
        do {
            super.beginRead(false);
            if (this.isUpToDate()) {
                lockObtained = true;
                continue;
            }
            this.endRead();
            this.beginWrite(false);
            this.endWrite();
        } while (!lockObtained);
    }

    @Override
    protected void beginWrite(boolean optimize) {
        super.beginWrite(true);
        if (optimize) {
            return;
        }
        boolean success = false;
        try {
            if (!this.isUpToDate()) {
                this.getSubConfigurationParentModel().replaceRoot(this.constructCombinedNode(), this);
                this.upToDate = true;
            }
            success = true;
        }
        finally {
            if (!success) {
                this.endWrite();
            }
        }
    }

    private boolean isUpToDate() {
        return this.upToDate;
    }

    private void invalidateInternal() {
        this.upToDate = false;
        this.fireEvent(COMBINED_INVALIDATE, null, null, false);
    }

    private void initChildCollections() {
        this.configurations = new ArrayList<ConfigData>();
        this.namedConfigurations = new HashMap<String, Configuration>();
    }

    private ImmutableNode constructCombinedNode() {
        if (this.getNumberOfConfigurationsInternal() < 1) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("No configurations defined for " + this);
            }
            return EMPTY_ROOT;
        }
        Iterator<ConfigData> it = this.configurations.iterator();
        ImmutableNode node = it.next().getTransformedRoot();
        while (it.hasNext()) {
            node = this.nodeCombiner.combine(node, it.next().getTransformedRoot());
        }
        if (this.getLogger().isDebugEnabled()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(os);
            TreeUtils.printTree(stream, node);
            this.getLogger().debug(os.toString());
        }
        return node;
    }

    private Set<Configuration> findSourceConfigurations(ImmutableNode node) {
        HashSet<Configuration> result = new HashSet<Configuration>();
        FindNodeVisitor<ImmutableNode> visitor = new FindNodeVisitor<ImmutableNode>(node);
        this.configurations.forEach(cd -> {
            NodeTreeWalker.INSTANCE.walkBFS(cd.getRootNode(), visitor, this.getModel().getNodeHandler());
            if (visitor.isFound()) {
                result.add(cd.getConfiguration());
                visitor.reset();
            }
        });
        return result;
    }

    private void registerListenerAt(Configuration configuration) {
        if (configuration instanceof EventSource) {
            ((EventSource)((Object)configuration)).addEventListener(ConfigurationEvent.ANY, this);
        }
    }

    private void unregisterListenerAt(Configuration configuration) {
        if (configuration instanceof EventSource) {
            ((EventSource)((Object)configuration)).removeEventListener(ConfigurationEvent.ANY, this);
        }
    }

    private void unregisterListenerAtChildren() {
        if (this.configurations != null) {
            this.configurations.forEach(child -> this.unregisterListenerAt(child.getConfiguration()));
        }
    }

    private int getNumberOfConfigurationsInternal() {
        return this.configurations.size();
    }

    private class ConfigData {
        private final Configuration configuration;
        private final String name;
        private final Collection<String> atPath;
        private final String at;
        private ImmutableNode rootNode;

        public ConfigData(Configuration config, String n, String at) {
            this.configuration = config;
            this.name = n;
            this.atPath = this.parseAt(at);
            this.at = at;
        }

        public Configuration getConfiguration() {
            return this.configuration;
        }

        public String getName() {
            return this.name;
        }

        public String getAt() {
            return this.at;
        }

        public ImmutableNode getRootNode() {
            return this.rootNode;
        }

        public ImmutableNode getTransformedRoot() {
            ImmutableNode configRoot = this.getRootNodeOfConfiguration();
            return this.atPath == null ? configRoot : this.prependAtPath(configRoot);
        }

        private ImmutableNode prependAtPath(ImmutableNode node) {
            ImmutableNode.Builder pathBuilder = new ImmutableNode.Builder();
            Iterator<String> pathIterator = this.atPath.iterator();
            this.prependAtPathComponent(pathBuilder, pathIterator.next(), pathIterator, node);
            return new ImmutableNode.Builder(1).addChild(pathBuilder.create()).create();
        }

        private void prependAtPathComponent(ImmutableNode.Builder builder, String currentComponent, Iterator<String> components, ImmutableNode orgRoot) {
            builder.name(currentComponent);
            if (components.hasNext()) {
                ImmutableNode.Builder childBuilder = new ImmutableNode.Builder();
                this.prependAtPathComponent(childBuilder, components.next(), components, orgRoot);
                builder.addChild(childBuilder.create());
            } else {
                builder.addChildren(orgRoot.getChildren());
                builder.addAttributes(orgRoot.getAttributes());
                builder.value(orgRoot.getValue());
            }
        }

        private ImmutableNode getRootNodeOfConfiguration() {
            this.getConfiguration().lock(LockMode.READ);
            try {
                ImmutableNode root;
                this.rootNode = root = ConfigurationUtils.convertToHierarchical(this.getConfiguration(), CombinedConfiguration.this.conversionExpressionEngine).getNodeModel().getInMemoryRepresentation();
                ImmutableNode immutableNode = root;
                return immutableNode;
            }
            finally {
                this.getConfiguration().unlock(LockMode.READ);
            }
        }

        private Collection<String> parseAt(String at) {
            if (StringUtils.isEmpty((CharSequence)at)) {
                return null;
            }
            ArrayList<String> result = new ArrayList<String>();
            DefaultConfigurationKey.KeyIterator it = new DefaultConfigurationKey(AT_ENGINE, at).iterator();
            while (it.hasNext()) {
                result.add(it.nextKey());
            }
            return result;
        }
    }
}

