/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.lifecycle;

import java.io.File;
import java.io.IOException;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;

public interface LifecycleManager {
    public FileAccessor create(String var1) throws IOException;

    public void delete(File var1) throws IOException;

    public void deleteOnExit(File var1) throws IOException;

    public void deleteOnTimeInterval(int var1, File var2) throws IOException;

    public FileAccessor getFileAccessor(String var1) throws IOException;
}

