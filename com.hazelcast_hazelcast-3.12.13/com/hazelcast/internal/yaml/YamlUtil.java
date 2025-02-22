/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.util.JavaVersion;
import com.hazelcast.internal.yaml.YamlException;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import java.util.regex.Pattern;

public final class YamlUtil {
    private static final Pattern NAME_APOSTROPHE_PATTERN = Pattern.compile("(.*[\\t\\s,;:]+.*)");

    private YamlUtil() {
    }

    public static YamlMapping asMapping(YamlNode node) {
        if (node != null && !(node instanceof YamlMapping)) {
            String nodeName = node.nodeName();
            throw new YamlException(String.format("Child %s is not a mapping, it's actual type is %s", nodeName, node.getClass()));
        }
        return (YamlMapping)node;
    }

    public static YamlSequence asSequence(YamlNode node) {
        if (node != null && !(node instanceof YamlSequence)) {
            String nodeName = node.nodeName();
            throw new YamlException(String.format("Child %s is not a sequence, it's actual type is %s", nodeName, node.getClass()));
        }
        return (YamlSequence)node;
    }

    public static YamlScalar asScalar(YamlNode node) {
        if (node != null && !(node instanceof YamlScalar)) {
            String nodeName = node.nodeName();
            throw new YamlException(String.format("Child %s is not a scalar, it's actual type is %s", nodeName, node.getClass()));
        }
        return (YamlScalar)node;
    }

    public static <T> T asType(YamlNode node, Class<T> type) {
        if (node != null && !type.isAssignableFrom(node.getClass())) {
            String nodeName = node.nodeName();
            throw new YamlException(String.format("Child %s is not a %s, it's actual type is %s", nodeName, type.getSimpleName(), node.getClass().getSimpleName()));
        }
        return (T)node;
    }

    public static void ensureRunningOnJava8OrHigher() {
        if (!JavaVersion.isAtLeast(JavaVersion.JAVA_1_8)) {
            throw new UnsupportedOperationException("Processing YAML documents requires Java 8 or higher version");
        }
    }

    public static String constructPath(YamlNode parent, String childName) {
        if (childName != null) {
            childName = NAME_APOSTROPHE_PATTERN.matcher(childName).replaceAll("\"$1\"");
        }
        if (parent != null && parent.path() != null) {
            return parent.path() + "/" + childName;
        }
        return childName;
    }

    public static boolean isMapping(YamlNode node) {
        return node instanceof YamlMapping;
    }

    public static boolean isSequence(YamlNode node) {
        return node instanceof YamlSequence;
    }

    public static boolean isScalar(YamlNode node) {
        return node instanceof YamlScalar;
    }

    public static boolean isOfSameType(YamlNode left, YamlNode right) {
        return left instanceof YamlMapping && right instanceof YamlMapping || left instanceof YamlSequence && right instanceof YamlSequence || left instanceof YamlScalar && right instanceof YamlScalar;
    }
}

