/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class PersistableTransfer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public final String serialize() {
        return Jackson.toJsonString(this);
    }

    public final void serialize(OutputStream out) throws IOException {
        out.write(Jackson.toJsonString(this).getBytes(StringUtils.UTF8));
        out.flush();
    }

    public static <T extends PersistableTransfer> T deserializeFrom(InputStream in) {
        Class clazz;
        String type;
        JsonNode tree;
        try {
            tree = MAPPER.readTree(in);
            JsonNode pauseType = tree.get("pauseType");
            if (pauseType == null) {
                throw new IllegalArgumentException("Unrecognized serialized state");
            }
            type = pauseType.asText();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        if ("download".equals(type)) {
            clazz = PersistableDownload.class;
        } else if ("upload".equals(type)) {
            clazz = PersistableUpload.class;
        } else {
            throw new UnsupportedOperationException("Unsupported paused transfer type: " + type);
        }
        try {
            PersistableTransfer t = MAPPER.treeToValue((TreeNode)tree, clazz);
            return (T)t;
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T extends PersistableTransfer> T deserializeFrom(String serialized) {
        if (serialized == null) {
            return null;
        }
        ByteArrayInputStream byteStream = new ByteArrayInputStream(serialized.getBytes(StringUtils.UTF8));
        T t = PersistableTransfer.deserializeFrom(byteStream);
        return t;
        finally {
            try {
                byteStream.close();
            }
            catch (IOException iOException) {}
        }
    }
}

