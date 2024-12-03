/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class TransformerUtils {
    public static final Charset UTF8 = StandardCharsets.UTF_8;

    public static void transformAndStreamResource(DownloadableResource originalResource, Charset encoding, OutputStream outputStream, Function<CharSequence, CharSequence> transform) throws DownloadException {
        try {
            ByteArrayOutputStream originalResourceStream = new ByteArrayOutputStream();
            originalResource.streamResource((OutputStream)originalResourceStream);
            originalResourceStream.flush();
            outputStream.write(transform.apply(originalResourceStream.toString(encoding.name())).toString().getBytes(encoding));
        }
        catch (IOException e) {
            throw new DownloadException("Unable to stream to the output", (Exception)e);
        }
    }
}

