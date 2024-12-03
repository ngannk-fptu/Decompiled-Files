/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import java.io.InputStream;

public interface InputStreamAccess {
    public String getStreamName();

    public InputStream accessInputStream();
}

