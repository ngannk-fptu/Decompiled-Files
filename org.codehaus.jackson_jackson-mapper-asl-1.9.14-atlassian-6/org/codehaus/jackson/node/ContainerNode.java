/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.node;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BaseJsonNode;
import org.codehaus.jackson.node.BinaryNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.NumericNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.POJONode;
import org.codehaus.jackson.node.TextNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ContainerNode
extends BaseJsonNode {
    JsonNodeFactory _nodeFactory;

    protected ContainerNode(JsonNodeFactory nc) {
        this._nodeFactory = nc;
    }

    public boolean isContainerNode() {
        return true;
    }

    @Override
    public abstract JsonToken asToken();

    public String getValueAsText() {
        return null;
    }

    public String asText() {
        return "";
    }

    @Override
    public abstract JsonNode findValue(String var1);

    @Override
    public abstract ObjectNode findParent(String var1);

    @Override
    public abstract List<JsonNode> findValues(String var1, List<JsonNode> var2);

    @Override
    public abstract List<JsonNode> findParents(String var1, List<JsonNode> var2);

    @Override
    public abstract List<String> findValuesAsText(String var1, List<String> var2);

    public abstract int size();

    public abstract JsonNode get(int var1);

    public abstract JsonNode get(String var1);

    public final ArrayNode arrayNode() {
        return this._nodeFactory.arrayNode();
    }

    public final ObjectNode objectNode() {
        return this._nodeFactory.objectNode();
    }

    public final NullNode nullNode() {
        return this._nodeFactory.nullNode();
    }

    public final BooleanNode booleanNode(boolean v) {
        return this._nodeFactory.booleanNode(v);
    }

    public final NumericNode numberNode(byte v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(short v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(int v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(long v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(float v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(double v) {
        return this._nodeFactory.numberNode(v);
    }

    public final NumericNode numberNode(BigDecimal v) {
        return this._nodeFactory.numberNode(v);
    }

    public final TextNode textNode(String text) {
        return this._nodeFactory.textNode(text);
    }

    public final BinaryNode binaryNode(byte[] data) {
        return this._nodeFactory.binaryNode(data);
    }

    public final BinaryNode binaryNode(byte[] data, int offset, int length) {
        return this._nodeFactory.binaryNode(data, offset, length);
    }

    public final POJONode POJONode(Object pojo) {
        return this._nodeFactory.POJONode(pojo);
    }

    public abstract ContainerNode removeAll();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class NoStringsIterator
    implements Iterator<String> {
        static final NoStringsIterator instance = new NoStringsIterator();

        private NoStringsIterator() {
        }

        public static NoStringsIterator instance() {
            return instance;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class NoNodesIterator
    implements Iterator<JsonNode> {
        static final NoNodesIterator instance = new NoNodesIterator();

        private NoNodesIterator() {
        }

        public static NoNodesIterator instance() {
            return instance;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public JsonNode next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }
}

