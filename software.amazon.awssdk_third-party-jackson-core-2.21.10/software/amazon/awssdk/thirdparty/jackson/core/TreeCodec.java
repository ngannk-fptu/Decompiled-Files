/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import java.io.IOException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import software.amazon.awssdk.thirdparty.jackson.core.TreeNode;

public abstract class TreeCodec {
    public abstract <T extends TreeNode> T readTree(JsonParser var1) throws IOException, JsonProcessingException;

    public abstract void writeTree(JsonGenerator var1, TreeNode var2) throws IOException, JsonProcessingException;

    public TreeNode missingNode() {
        return null;
    }

    public TreeNode nullNode() {
        return null;
    }

    public abstract TreeNode createArrayNode();

    public abstract TreeNode createObjectNode();

    public abstract JsonParser treeAsTokens(TreeNode var1);
}

