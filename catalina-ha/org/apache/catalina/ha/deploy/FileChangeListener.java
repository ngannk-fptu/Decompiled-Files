/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ha.deploy;

import java.io.File;

public interface FileChangeListener {
    public void fileModified(File var1);

    public void fileRemoved(File var1);
}

