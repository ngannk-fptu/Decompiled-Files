/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.constructor;

import com.hazelcast.org.snakeyaml.engine.v2.api.ConstructNode;
import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.constructor.BaseConstructor;
import com.hazelcast.org.snakeyaml.engine.v2.env.EnvConfig;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.DuplicateKeyException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.MissingEnvironmentVariableException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.MappingNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeTuple;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.ScalarNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.SequenceNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;

public class StandardConstructor
extends BaseConstructor {
    private static final String ERROR_PREFIX = "while constructing an ordered map";
    private static final Map<String, Boolean> BOOL_VALUES = new HashMap<String, Boolean>();

    public StandardConstructor(LoadSettings settings) {
        super(settings);
        this.tagConstructors.put(Tag.NULL, new ConstructYamlNull());
        this.tagConstructors.put(Tag.BOOL, new ConstructYamlBool());
        this.tagConstructors.put(Tag.INT, new ConstructYamlInt());
        this.tagConstructors.put(Tag.FLOAT, new ConstructYamlFloat());
        this.tagConstructors.put(Tag.BINARY, new ConstructYamlBinary());
        this.tagConstructors.put(Tag.SET, new ConstructYamlSet());
        this.tagConstructors.put(Tag.STR, new ConstructYamlStr());
        this.tagConstructors.put(Tag.SEQ, new ConstructYamlSeq());
        this.tagConstructors.put(Tag.MAP, new ConstructYamlMap());
        this.tagConstructors.put(Tag.ENV_TAG, new ConstructEnv());
        this.tagConstructors.put(new Tag(UUID.class), new ConstructUuidClass());
        this.tagConstructors.put(new Tag(Optional.class), new ConstructOptionalClass());
        this.tagConstructors.putAll(settings.getTagConstructors());
    }

    protected void flattenMapping(MappingNode node) {
        this.processDuplicateKeys(node);
        if (node.isMerged()) {
            node.setValue(this.mergeNode(node, true, new HashMap<Object, Integer>(), new ArrayList<NodeTuple>()));
        }
    }

    protected void processDuplicateKeys(MappingNode node) {
        List<NodeTuple> nodeValue = node.getValue();
        HashMap<Object, Integer> keys = new HashMap<Object, Integer>(nodeValue.size());
        TreeSet<Integer> toRemove = new TreeSet<Integer>();
        int i = 0;
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Object key = this.constructKey(keyNode, node.getStartMark(), tuple.getKeyNode().getStartMark());
            Integer prevIndex = keys.put(key, i);
            if (prevIndex != null) {
                if (!this.settings.getAllowDuplicateKeys()) {
                    throw new DuplicateKeyException(node.getStartMark(), key, tuple.getKeyNode().getStartMark());
                }
                toRemove.add(prevIndex);
            }
            ++i;
        }
        Iterator indices2remove = toRemove.descendingIterator();
        while (indices2remove.hasNext()) {
            nodeValue.remove((Integer)indices2remove.next());
        }
    }

    private Object constructKey(Node keyNode, Optional<Mark> contextMark, Optional<Mark> problemMark) {
        Object key = this.constructObject(keyNode);
        if (key != null) {
            try {
                key.hashCode();
            }
            catch (Exception e) {
                throw new ConstructorException("while constructing a mapping", contextMark, "found unacceptable key " + key, problemMark, e);
            }
        }
        return key;
    }

    private List<NodeTuple> mergeNode(MappingNode node, boolean isPreferred, Map<Object, Integer> key2index, List<NodeTuple> values) {
        for (NodeTuple nodeTuple : node.getValue()) {
            Node keyNode = nodeTuple.getKeyNode();
            Object key = this.constructObject(keyNode);
            if (!key2index.containsKey(key)) {
                values.add(nodeTuple);
                key2index.put(key, values.size() - 1);
                continue;
            }
            if (!isPreferred) continue;
            values.set(key2index.get(key), nodeTuple);
        }
        return values;
    }

    @Override
    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        this.flattenMapping(node);
        super.constructMapping2ndStep(node, mapping);
    }

    @Override
    protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
        this.flattenMapping(node);
        super.constructSet2ndStep(node, set);
    }

    static {
        BOOL_VALUES.put("true", Boolean.TRUE);
        BOOL_VALUES.put("false", Boolean.FALSE);
    }

    public class ConstructEnv
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String val = StandardConstructor.this.constructScalar((ScalarNode)node);
            Optional<EnvConfig> opt = StandardConstructor.this.settings.getEnvConfig();
            if (opt.isPresent()) {
                EnvConfig config = opt.get();
                Matcher matcher = JsonScalarResolver.ENV_FORMAT.matcher(val);
                matcher.matches();
                String name = matcher.group("name");
                String value = matcher.group("value");
                String nonNullValue = value != null ? value : "";
                String separator = matcher.group("separator");
                String env = this.getEnv(name);
                Optional<String> overruled = config.getValueFor(name, separator, nonNullValue, env);
                if (overruled.isPresent()) {
                    return overruled.get();
                }
                return this.apply(name, separator, nonNullValue, env);
            }
            return val;
        }

        public String apply(String name, String separator, String value, String environment) {
            if (environment != null && !environment.isEmpty()) {
                return environment;
            }
            if (separator != null) {
                if (separator.equals("?") && environment == null) {
                    throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
                }
                if (separator.equals(":?")) {
                    if (environment == null) {
                        throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
                    }
                    if (environment.isEmpty()) {
                        throw new MissingEnvironmentVariableException("Empty mandatory variable " + name + ": " + value);
                    }
                }
                if (separator.startsWith(":") ? environment == null || environment.isEmpty() : environment == null) {
                    return value;
                }
            }
            return "";
        }

        public String getEnv(String key) {
            return System.getenv(key);
        }
    }

    public class ConstructYamlMap
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            MappingNode mappingNode = (MappingNode)node;
            if (node.isRecursive()) {
                return StandardConstructor.this.createDefaultMap(mappingNode.getValue().size());
            }
            return StandardConstructor.this.constructMapping(mappingNode);
        }

        @Override
        public void constructRecursive(Node node, Object object) {
            if (!node.isRecursive()) {
                throw new YamlEngineException("Unexpected recursive mapping structure. Node: " + node);
            }
            StandardConstructor.this.constructMapping2ndStep((MappingNode)node, (Map)object);
        }
    }

    public class ConstructYamlSeq
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            SequenceNode seqNode = (SequenceNode)node;
            if (node.isRecursive()) {
                return StandardConstructor.this.settings.getDefaultList().apply(seqNode.getValue().size());
            }
            return StandardConstructor.this.constructSequence(seqNode);
        }

        @Override
        public void constructRecursive(Node node, Object data) {
            if (!node.isRecursive()) {
                throw new YamlEngineException("Unexpected recursive sequence structure. Node: " + node);
            }
            StandardConstructor.this.constructSequenceStep2((SequenceNode)node, (List)data);
        }
    }

    public class ConstructYamlStr
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            return StandardConstructor.this.constructScalar((ScalarNode)node);
        }
    }

    public class ConstructYamlSet
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            if (node.isRecursive()) {
                return StandardConstructor.this.constructedObjects.containsKey(node) ? StandardConstructor.this.constructedObjects.get(node) : StandardConstructor.this.createDefaultSet(((MappingNode)node).getValue().size());
            }
            return StandardConstructor.this.constructSet((MappingNode)node);
        }

        @Override
        public void constructRecursive(Node node, Object object) {
            if (!node.isRecursive()) {
                throw new YamlEngineException("Unexpected recursive set structure. Node: " + node);
            }
            StandardConstructor.this.constructSet2ndStep((MappingNode)node, (Set)object);
        }
    }

    public class ConstructYamlOmap
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            LinkedHashMap<Object, Object> omap = new LinkedHashMap<Object, Object>();
            if (!(node instanceof SequenceNode)) {
                throw new ConstructorException(StandardConstructor.ERROR_PREFIX, node.getStartMark(), "expected a sequence, but found " + (Object)((Object)node.getNodeType()), node.getStartMark());
            }
            SequenceNode sequenceNode = (SequenceNode)node;
            for (Node subNode : sequenceNode.getValue()) {
                if (!(subNode instanceof MappingNode)) {
                    throw new ConstructorException(StandardConstructor.ERROR_PREFIX, node.getStartMark(), "expected a mapping of length 1, but found " + (Object)((Object)subNode.getNodeType()), subNode.getStartMark());
                }
                MappingNode mappingNode = (MappingNode)subNode;
                if (mappingNode.getValue().size() != 1) {
                    throw new ConstructorException(StandardConstructor.ERROR_PREFIX, node.getStartMark(), "expected a single mapping item, but found " + mappingNode.getValue().size() + " items", mappingNode.getStartMark());
                }
                Node keyNode = mappingNode.getValue().get(0).getKeyNode();
                Node valueNode = mappingNode.getValue().get(0).getValueNode();
                Object key = StandardConstructor.this.constructObject(keyNode);
                Object value = StandardConstructor.this.constructObject(valueNode);
                omap.put(key, value);
            }
            return omap;
        }
    }

    public class ConstructOptionalClass
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            if (node.getNodeType() != NodeType.SCALAR) {
                throw new ConstructorException("while constructing Optional", Optional.empty(), "found non scalar node", node.getStartMark());
            }
            String value = StandardConstructor.this.constructScalar((ScalarNode)node);
            Tag implicitTag = StandardConstructor.this.settings.getScalarResolver().resolve(value, true);
            if (implicitTag.equals(Tag.NULL)) {
                return Optional.empty();
            }
            return Optional.of(value);
        }
    }

    public class ConstructUuidClass
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String uuidValue = StandardConstructor.this.constructScalar((ScalarNode)node);
            return UUID.fromString(uuidValue);
        }
    }

    public class ConstructYamlBinary
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String noWhiteSpaces = StandardConstructor.this.constructScalar((ScalarNode)node).replaceAll("\\s", "");
            return Base64.getDecoder().decode(noWhiteSpaces);
        }
    }

    public class ConstructYamlFloat
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String value = StandardConstructor.this.constructScalar((ScalarNode)node);
            return Double.valueOf(value);
        }
    }

    public class ConstructYamlInt
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String value = StandardConstructor.this.constructScalar((ScalarNode)node);
            return this.createIntNumber(value);
        }

        protected Number createIntNumber(String number) {
            Number result;
            try {
                result = Integer.valueOf(number);
            }
            catch (NumberFormatException e) {
                try {
                    result = Long.valueOf(number);
                }
                catch (NumberFormatException e1) {
                    result = new BigInteger(number);
                }
            }
            return result;
        }
    }

    public class ConstructYamlBool
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            String val = StandardConstructor.this.constructScalar((ScalarNode)node);
            return BOOL_VALUES.get(val.toLowerCase());
        }
    }

    public class ConstructYamlNull
    implements ConstructNode {
        @Override
        public Object construct(Node node) {
            if (node != null) {
                StandardConstructor.this.constructScalar((ScalarNode)node);
            }
            return null;
        }
    }
}

