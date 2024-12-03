/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.json.JsonUtil;

class JsonWriter {
    private final Writer writer;

    JsonWriter(Writer writer) {
        this.writer = writer;
    }

    void write(Node node, int maxLevels) throws RepositoryException, IOException {
        this.write(node, 0, maxLevels);
    }

    void write(Collection<Node> nodes, int maxLevels) throws RepositoryException, IOException {
        this.writer.write(123);
        this.writeKey("nodes");
        this.writer.write(123);
        boolean first = true;
        for (Node node : nodes) {
            if (first) {
                first = false;
            } else {
                this.writer.write(44);
            }
            this.writeKey(node.getPath());
            this.write(node, maxLevels);
        }
        this.writer.write(125);
        this.writer.write(125);
    }

    private void write(Node node, int currentLevel, int maxLevels) throws RepositoryException, IOException {
        this.writer.write(123);
        PropertyIterator props = node.getProperties();
        while (props.hasNext()) {
            Property prop = props.nextProperty();
            this.writeProperty(prop);
            this.writer.write(44);
        }
        NodeIterator children = node.getNodes();
        if (!children.hasNext()) {
            this.writeKeyValue("::NodeIteratorSize", 0L);
        } else {
            while (children.hasNext()) {
                Node n = children.nextNode();
                String name = n.getName();
                int index = n.getIndex();
                if (index > 1) {
                    this.writeKey(name + "[" + index + "]");
                } else {
                    this.writeKey(name);
                }
                if (maxLevels < 0 || currentLevel < maxLevels) {
                    this.write(n, currentLevel + 1, maxLevels);
                } else {
                    this.writeChildInfo(n);
                }
                if (!children.hasNext()) continue;
                this.writer.write(44);
            }
        }
        this.writer.write(125);
    }

    private void writeChildInfo(Node n) throws RepositoryException, IOException {
        this.writer.write(123);
        if (n.isNodeType("mix:referenceable") && n.hasProperty("jcr:uuid")) {
            this.writeProperty(n.getProperty("jcr:uuid"));
        }
        this.writer.write(125);
    }

    private void writeProperty(Property p) throws RepositoryException, IOException {
        int type = p.getType();
        if (type == 2) {
            String key = ":" + p.getName();
            if (p.isMultiple()) {
                long[] binLengths = p.getLengths();
                this.writeKeyArray(key, binLengths);
            } else {
                this.writeKeyValue(key, p.getLength());
            }
        } else {
            boolean isMultiple = p.isMultiple();
            if (JsonWriter.requiresTypeInfo(p) || isMultiple && p.getValues().length == 0) {
                this.writeKeyValue(":" + p.getName(), PropertyType.nameFromValue(type), true);
            }
            if (isMultiple) {
                this.writeKeyArray(p.getName(), p.getValues());
            } else {
                this.writeKeyValue(p.getName(), p.getValue());
            }
        }
    }

    private static boolean requiresTypeInfo(Property p) throws RepositoryException {
        switch (p.getType()) {
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    private void writeKeyValue(String key, String value, boolean hasNext) throws IOException {
        this.writeKey(key);
        this.writer.write(JsonUtil.getJsonString(value));
        if (hasNext) {
            this.writer.write(44);
        }
    }

    private void writeKeyValue(String key, Value value) throws RepositoryException, IOException {
        this.writeKey(key);
        this.writeJsonValue(value);
    }

    private void writeKeyArray(String key, Value[] values) throws RepositoryException, IOException {
        this.writeKey(key);
        this.writer.write(91);
        for (int i = 0; i < values.length; ++i) {
            if (i > 0) {
                this.writer.write(44);
            }
            this.writeJsonValue(values[i]);
        }
        this.writer.write(93);
    }

    private void writeKeyValue(String key, long binLength) throws IOException {
        this.writeKey(key);
        this.writer.write(String.valueOf(binLength));
    }

    private void writeKeyArray(String key, long[] binLengths) throws RepositoryException, IOException {
        this.writeKey(key);
        this.writer.write(91);
        for (int i = 0; i < binLengths.length; ++i) {
            if (i > 0) {
                this.writer.write(44);
            }
            this.writer.write(String.valueOf(binLengths[i]));
        }
        this.writer.write(93);
    }

    private void writeKey(String key) throws IOException {
        this.writer.write(JsonUtil.getJsonString(key));
        this.writer.write(58);
    }

    private void writeJsonValue(Value v) throws RepositoryException, IOException {
        switch (v.getType()) {
            case 2: {
                throw new IllegalArgumentException();
            }
            case 3: 
            case 6: {
                this.writer.write(v.getString());
                break;
            }
            case 4: {
                double d = v.getDouble();
                String str = v.getString();
                if (Double.isNaN(d) || Double.isInfinite(d)) {
                    str = JsonUtil.getJsonString(str);
                }
                this.writer.write(str);
                break;
            }
            default: {
                this.writer.write(JsonUtil.getJsonString(v.getString()));
            }
        }
    }
}

