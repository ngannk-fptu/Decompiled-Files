/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNameNodePair;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;

public final class YamlDomChecker {
    private YamlDomChecker() {
    }

    public static void check(YamlNode node) {
        if (node instanceof YamlMapping) {
            for (YamlNameNodePair nodePair : ((YamlMapping)node).childrenPairs()) {
                YamlNode child = nodePair.childNode();
                if (child == null) {
                    String path = YamlUtil.constructPath(node, nodePair.nodeName());
                    YamlDomChecker.reportNullEntryOnConcretePath(path);
                }
                YamlDomChecker.check(nodePair.childNode());
            }
        } else if (node instanceof YamlSequence) {
            for (YamlNode child : ((YamlSequence)node).children()) {
                if (child == null) {
                    throw new InvalidConfigurationException("There is a null configuration entry under sequence " + node.path() + ". Please check if the provided YAML configuration is well-indented and no blocks started without sub-nodes.");
                }
                YamlDomChecker.check(child);
            }
        } else if (((YamlScalar)node).nodeValue() == null) {
            YamlDomChecker.reportNullEntryOnConcretePath(node.path());
        }
    }

    private static void reportNullEntryOnConcretePath(String path) {
        throw new InvalidConfigurationException("The configuration entry under " + path + " is null. Please check if the provided YAML configuration is well-indented and no blocks started without sub-nodes.");
    }
}

