/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigLoader;
import com.hazelcast.config.ConfigReplacerHelper;
import com.hazelcast.config.ConfigSections;
import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.DomVariableReplacer;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.YamlDomVariableReplacer;
import com.hazelcast.config.replacer.PropertyReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import com.hazelcast.config.yaml.ElementAdapter;
import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.internal.yaml.MutableYamlMapping;
import com.hazelcast.internal.yaml.MutableYamlSequence;
import com.hazelcast.internal.yaml.YamlLoader;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNameNodePair;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;
import com.hazelcast.util.StringUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.w3c.dom.Node;

public abstract class AbstractYamlConfigBuilder {
    private final Set<String> currentlyImportedFiles = new HashSet<String>();
    private Properties properties = System.getProperties();

    protected Properties getProperties() {
        return this.properties;
    }

    protected void importDocuments(YamlNode imdgRoot) throws Exception {
        YamlMapping rootAsMapping = YamlUtil.asMapping(imdgRoot);
        YamlSequence importSeq = rootAsMapping.childAsSequence(ConfigSections.IMPORT.name);
        if (importSeq == null || importSeq.childCount() == 0) {
            return;
        }
        for (YamlNode importNode : importSeq.children()) {
            YamlNode rootLoaded;
            String resource = (String)YamlUtil.asScalar(importNode).nodeValue();
            URL url = ConfigLoader.locateConfig(resource);
            if (url == null) {
                throw new InvalidConfigurationException("Failed to load resource: " + resource);
            }
            if (!this.currentlyImportedFiles.add(url.getPath())) {
                throw new InvalidConfigurationException("Cyclic loading of resource '" + url.getPath() + "' detected!");
            }
            try (InputStream inputStream = null;){
                inputStream = url.openStream();
                rootLoaded = YamlLoader.load(inputStream);
            }
            YamlNode imdgRootLoaded = YamlUtil.asMapping(rootLoaded).child(this.getConfigRoot());
            if (imdgRootLoaded == null) {
                return;
            }
            this.replaceVariables(W3cDomUtil.asW3cNode(imdgRootLoaded));
            this.importDocuments(imdgRootLoaded);
            this.merge(imdgRootLoaded, imdgRoot);
        }
        ((MutableYamlMapping)rootAsMapping).removeChild(ConfigSections.IMPORT.name);
    }

    protected abstract String getConfigRoot();

    private void merge(YamlNode source, YamlNode target) {
        if (source == null) {
            return;
        }
        this.checkAmbiguousConfiguration(source, target);
        if (YamlUtil.isMapping(source)) {
            this.mergeMappingNodes(YamlUtil.asMapping(source), YamlUtil.asMapping(target));
        } else if (YamlUtil.isSequence(source)) {
            this.mergeSequenceNodes(YamlUtil.asSequence(source), YamlUtil.asSequence(target));
        }
    }

    private void checkAmbiguousConfiguration(YamlNode source, YamlNode target) {
        if (!YamlUtil.isOfSameType(source, target)) {
            String message = String.format("Ambiguous configuration of '%s': node types differ in the already loaded and imported configuration. Type of already loaded node: %s, type of imported node: %s", target.path(), target.getClass().getSimpleName(), source.getClass().getSimpleName());
            throw new InvalidConfigurationException(message);
        }
        if (YamlUtil.isScalar(source) && YamlUtil.isScalar(target)) {
            Object sourceValue = ((YamlScalar)source).nodeValue();
            Object targetValue = ((YamlScalar)target).nodeValue();
            if (!targetValue.equals(sourceValue)) {
                throw new InvalidConfigurationException(String.format("Ambiguous configuration of '%s': current and imported values differ. Current value: %s, imported value: %s", target.path(), targetValue, sourceValue));
            }
        }
    }

    private void mergeSequenceNodes(YamlSequence sourceAsSequence, YamlSequence targetAsSequence) {
        for (YamlNode sourceChild : sourceAsSequence.children()) {
            if (!(targetAsSequence instanceof MutableYamlSequence)) continue;
            ((MutableYamlSequence)targetAsSequence).addChild(sourceChild);
        }
    }

    private void mergeMappingNodes(YamlMapping sourceAsMapping, YamlMapping targetAsMapping) {
        for (YamlNode sourceChild : sourceAsMapping.children()) {
            YamlNode targetChild = targetAsMapping.child(sourceChild.nodeName());
            if (targetChild != null) {
                this.merge(sourceChild, targetChild);
                continue;
            }
            if (!(targetAsMapping instanceof MutableYamlMapping)) continue;
            ((MutableYamlMapping)targetAsMapping).addChild(sourceChild.nodeName(), sourceChild);
        }
    }

    protected void replaceVariables(Node node) throws Exception {
        boolean failFast = false;
        ArrayList<ConfigReplacer> replacers = new ArrayList<ConfigReplacer>();
        PropertyReplacer propertyReplacer = new PropertyReplacer();
        propertyReplacer.init(this.properties);
        replacers.add(propertyReplacer);
        Node replacersNode = node.getAttributes().getNamedItem(ConfigSections.CONFIG_REPLACERS.name);
        if (replacersNode != null) {
            String failFastAttr = DomConfigHelper.getAttribute(replacersNode, "fail-if-value-missing", true);
            failFast = StringUtil.isNullOrEmpty(failFastAttr) || Boolean.parseBoolean(failFastAttr);
            for (Node n : DomConfigHelper.childElements(replacersNode)) {
                String nodeName = DomConfigHelper.cleanNodeName(n);
                if (!"replacers".equals(nodeName)) continue;
                for (Node replacerNode : DomConfigHelper.childElements(n)) {
                    replacers.add(this.createReplacer(replacerNode));
                }
            }
        }
        ConfigReplacerHelper.traverseChildrenAndReplaceVariables(node, replacers, failFast, (DomVariableReplacer)new YamlDomVariableReplacer());
    }

    private ConfigReplacer createReplacer(Node node) throws Exception {
        String replacerClass = DomConfigHelper.getAttribute(node, "class-name", true);
        Properties properties = new Properties();
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = DomConfigHelper.cleanNodeName(n);
            if (!"properties".equals(value)) continue;
            this.fillReplacerProperties(n, properties);
        }
        ConfigReplacer replacer = (ConfigReplacer)Class.forName(replacerClass).newInstance();
        replacer.init(properties);
        return replacer;
    }

    protected void setPropertiesInternal(Properties properties) {
        this.properties = properties;
    }

    private void fillReplacerProperties(Node node, Properties properties) {
        YamlMapping propertiesMapping = YamlUtil.asMapping(((ElementAdapter)node).getYamlNode());
        for (YamlNameNodePair childNodePair : propertiesMapping.childrenPairs()) {
            String childName = childNodePair.nodeName();
            YamlNode child = childNodePair.childNode();
            Object nodeValue = YamlUtil.asScalar(child).nodeValue();
            properties.put(childName, nodeValue != null ? nodeValue.toString() : "");
        }
    }
}

