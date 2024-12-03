/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.service.progress;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ProgressPlugin {
    public Task startTask(String var1, int var2);

    @ProviderType
    public static interface Task {
        public void worked(int var1);

        public void done(String var1, Throwable var2);

        public boolean isCanceled();
    }
}

