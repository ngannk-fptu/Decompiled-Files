/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class AbstractYAMLBasedConfiguration
extends BaseHierarchicalConfiguration {
    protected AbstractYAMLBasedConfiguration() {
        this.initLogger(new ConfigurationLogger(this.getClass()));
    }

    protected AbstractYAMLBasedConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
        this.initLogger(new ConfigurationLogger(this.getClass()));
    }

    protected void load(Map<String, Object> map) {
        List<ImmutableNode> roots = AbstractYAMLBasedConfiguration.constructHierarchy("", map);
        this.getNodeModel().setRootNode(roots.get(0));
    }

    protected Map<String, Object> constructMap(ImmutableNode node) {
        HashMap<String, Object> map = new HashMap<String, Object>(node.getChildren().size());
        node.forEach(cNode -> AbstractYAMLBasedConfiguration.addEntry(map, cNode.getNodeName(), cNode.getChildren().isEmpty() ? cNode.getValue() : this.constructMap((ImmutableNode)cNode)));
        return map;
    }

    private static void addEntry(Map<String, Object> map, String key, Object value) {
        Object oldValue = map.get(key);
        if (oldValue == null) {
            map.put(key, value);
        } else if (oldValue instanceof Collection) {
            Collection values = (Collection)oldValue;
            values.add(value);
        } else {
            ArrayList<Object> values = new ArrayList<Object>();
            values.add(oldValue);
            values.add(value);
            map.put(key, values);
        }
    }

    private static List<ImmutableNode> constructHierarchy(String key, Object elem) {
        if (elem instanceof Map) {
            return AbstractYAMLBasedConfiguration.parseMap((Map)elem, key);
        }
        if (elem instanceof Collection) {
            return AbstractYAMLBasedConfiguration.parseCollection((Collection)elem, key);
        }
        return Collections.singletonList(new ImmutableNode.Builder().name(key).value(elem).create());
    }

    private static List<ImmutableNode> parseMap(Map<String, Object> map, String key) {
        ImmutableNode.Builder subtree = new ImmutableNode.Builder().name(key);
        map.forEach((k, v) -> AbstractYAMLBasedConfiguration.constructHierarchy(k, v).forEach(subtree::addChild));
        return Collections.singletonList(subtree.create());
    }

    private static List<ImmutableNode> parseCollection(Collection<Object> col, String key) {
        return col.stream().flatMap(elem -> AbstractYAMLBasedConfiguration.constructHierarchy(key, elem).stream()).collect(Collectors.toList());
    }

    static void rethrowException(Exception e) throws ConfigurationException {
        if (e instanceof ClassCastException) {
            throw new ConfigurationException("Error parsing", e);
        }
        throw new ConfigurationException("Unable to load the configuration", e);
    }
}

