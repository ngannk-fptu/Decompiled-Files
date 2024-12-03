/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.mail.embed;

import com.atlassian.confluence.util.HtmlUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MimeBodyPartDataSource
implements DataSource {
    private final DataSource source;
    private final String sourceName;

    public MimeBodyPartDataSource(DataSource source) {
        Preconditions.checkNotNull((Object)source, (Object)"Given DataSource is null.");
        String sourceName = source.getName();
        if (sourceName == null) {
            throw new IllegalArgumentException(String.format("Given DataSource [%s] must have a name.", ToStringBuilder.reflectionToString((Object)source)));
        }
        this.source = source;
        this.sourceName = MimeBodyPartDataSource.encode(sourceName);
    }

    public String getName() {
        return this.sourceName;
    }

    public InputStream getInputStream() throws IOException {
        return this.source.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.source.getOutputStream();
    }

    public String getContentType() {
        return this.source.getContentType();
    }

    @VisibleForTesting
    public DataSource getWrappedSource() {
        return this.source;
    }

    public static String encode(String str) {
        String encoded = HtmlUtil.urlEncode(str);
        return encoded.replaceAll("%[0-9A-F]{2}", "_");
    }
}

