/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class AttachmentExtractedTextHelper {
    private static final Logger log = LoggerFactory.getLogger(AttachmentExtractedTextHelper.class);

    public static Optional<String> toString(InputStreamSource input) {
        Optional<String> optional;
        block8: {
            InputStream is = input.getInputStream();
            try {
                optional = Optional.of(IOUtils.toString((InputStream)is, (Charset)StandardCharsets.UTF_8));
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
                    log.error("Error decoding a stream of extracted text", (Throwable)e);
                    return Optional.empty();
                }
            }
            is.close();
        }
        return optional;
    }

    public static InputStreamSource toStreamSource(String input) {
        return () -> IOUtils.toInputStream((String)input, (Charset)StandardCharsets.UTF_8);
    }
}

