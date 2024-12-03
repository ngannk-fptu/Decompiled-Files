/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.io.InputStream;
import java.net.URL;
import org.hibernate.HibernateException;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class UrlInputStreamAccess
implements InputStreamAccess {
    private final URL url;

    public UrlInputStreamAccess(URL url) {
        this.url = url;
    }

    @Override
    public String getStreamName() {
        return this.url.toExternalForm();
    }

    @Override
    public InputStream accessInputStream() {
        try {
            return this.url.openStream();
        }
        catch (Exception e) {
            throw new HibernateException("Could not open url stream : " + this.url.toExternalForm());
        }
    }
}

