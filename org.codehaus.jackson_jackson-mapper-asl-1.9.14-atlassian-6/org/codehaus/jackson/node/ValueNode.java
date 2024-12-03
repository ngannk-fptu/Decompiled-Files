/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.node.BaseJsonNode;
import org.codehaus.jackson.node.MissingNode;

public abstract class ValueNode
extends BaseJsonNode {
    protected ValueNode() {
    }

    public boolean isValueNode() {
        return true;
    }

    public abstract JsonToken asToken();

    public void serializeWithType(JsonGenerator jg, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(this, jg);
        this.serialize(jg, provider);
        typeSer.writeTypeSuffixForScalar(this, jg);
    }

    public JsonNode path(String fieldName) {
        return MissingNode.getInstance();
    }

    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    public String toString() {
        return this.asText();
    }
}

