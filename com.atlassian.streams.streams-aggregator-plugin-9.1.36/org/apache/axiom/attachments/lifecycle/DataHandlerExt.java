/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.lifecycle;

import java.io.IOException;
import java.io.InputStream;

public interface DataHandlerExt {
    public InputStream readOnce() throws IOException;

    public void purgeDataSource() throws IOException;

    public void deleteWhenReadOnce() throws IOException;
}

