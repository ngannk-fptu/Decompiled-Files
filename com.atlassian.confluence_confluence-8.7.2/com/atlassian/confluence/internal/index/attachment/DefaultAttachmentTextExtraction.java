/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import com.atlassian.confluence.util.io.DataCompressor;
import com.atlassian.confluence.util.io.InputStreamSource;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

public class DefaultAttachmentTextExtraction
implements AttachmentTextExtraction {
    private final byte[] bytes;
    private final boolean compressed;

    public static AttachmentTextExtraction of(byte[] bytes, boolean compressed) {
        Objects.requireNonNull(bytes);
        return new DefaultAttachmentTextExtraction(compressed ? DefaultAttachmentTextExtraction.getDataCompressor().compress(bytes) : bytes, compressed);
    }

    public static AttachmentTextExtraction of(String text, boolean compressed) {
        Objects.requireNonNull(text);
        return DefaultAttachmentTextExtraction.of(text.getBytes(StandardCharsets.UTF_8), compressed);
    }

    public static AttachmentTextExtraction empty() {
        return new DefaultAttachmentTextExtraction(null, false);
    }

    public static AttachmentTextExtraction of(InputStreamSource source, boolean compressed) {
        AttachmentTextExtraction attachmentTextExtraction;
        block8: {
            InputStream is = source.getInputStream();
            try {
                attachmentTextExtraction = DefaultAttachmentTextExtraction.of(IOUtils.toByteArray((InputStream)is), compressed);
                if (is == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return DefaultAttachmentTextExtraction.empty();
                }
            }
            is.close();
        }
        return attachmentTextExtraction;
    }

    private DefaultAttachmentTextExtraction(byte[] bytes, boolean compressed) {
        this.bytes = bytes;
        this.compressed = compressed;
    }

    @Override
    public Optional<String> getText() {
        if (this.bytes != null) {
            byte[] data = this.compressed ? DefaultAttachmentTextExtraction.getDataCompressor().uncompress(this.bytes) : this.bytes;
            return Optional.of(new String(data, StandardCharsets.UTF_8));
        }
        return Optional.empty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultAttachmentTextExtraction)) {
            return false;
        }
        DefaultAttachmentTextExtraction that = (DefaultAttachmentTextExtraction)o;
        return this.compressed == that.compressed && Arrays.equals(this.bytes, that.bytes);
    }

    public int hashCode() {
        return Objects.hash(this.compressed, Arrays.hashCode(this.bytes));
    }

    private static DataCompressor getDataCompressor() {
        DataCompressor dataCompressor = (DataCompressor)ContainerManager.getInstance().getContainerContext().getComponent((Object)"dataCompressor");
        return dataCompressor;
    }
}

