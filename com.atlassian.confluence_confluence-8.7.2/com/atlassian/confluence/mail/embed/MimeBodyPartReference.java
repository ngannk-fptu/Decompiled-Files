/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.mail.embed;

import com.atlassian.confluence.mail.embed.MimeBodyPartDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.activation.DataSource;

public final class MimeBodyPartReference {
    private final DataSource source;
    private final URI locator;

    public MimeBodyPartReference(DataSource source) {
        this.source = new MimeBodyPartDataSource(source);
        try {
            this.locator = new URI("cid:" + this.source.getName());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI getLocator() {
        return this.locator;
    }

    public DataSource getSource() {
        return this.source;
    }
}

