/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import java.io.IOException;
import java.util.Iterator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import software.amazon.awssdk.thirdparty.jackson.core.TreeCodec;
import software.amazon.awssdk.thirdparty.jackson.core.TreeNode;
import software.amazon.awssdk.thirdparty.jackson.core.Version;
import software.amazon.awssdk.thirdparty.jackson.core.Versioned;
import software.amazon.awssdk.thirdparty.jackson.core.type.ResolvedType;
import software.amazon.awssdk.thirdparty.jackson.core.type.TypeReference;

public abstract class ObjectCodec
extends TreeCodec
implements Versioned {
    protected ObjectCodec() {
    }

    @Override
    public abstract Version version();

    public abstract <T> T readValue(JsonParser var1, Class<T> var2) throws IOException;

    public abstract <T> T readValue(JsonParser var1, TypeReference<T> var2) throws IOException;

    public abstract <T> T readValue(JsonParser var1, ResolvedType var2) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, Class<T> var2) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, TypeReference<T> var2) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, ResolvedType var2) throws IOException;

    public abstract void writeValue(JsonGenerator var1, Object var2) throws IOException;

    @Override
    public abstract <T extends TreeNode> T readTree(JsonParser var1) throws IOException;

    @Override
    public abstract void writeTree(JsonGenerator var1, TreeNode var2) throws IOException;

    @Override
    public abstract TreeNode createObjectNode();

    @Override
    public abstract TreeNode createArrayNode();

    @Override
    public abstract JsonParser treeAsTokens(TreeNode var1);

    public abstract <T> T treeToValue(TreeNode var1, Class<T> var2) throws JsonProcessingException;

    @Deprecated
    public JsonFactory getJsonFactory() {
        return this.getFactory();
    }

    public JsonFactory getFactory() {
        return this.getJsonFactory();
    }
}

