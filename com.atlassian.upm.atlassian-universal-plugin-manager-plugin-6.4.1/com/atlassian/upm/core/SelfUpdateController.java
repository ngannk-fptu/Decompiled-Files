/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Either;
import java.io.File;
import java.net.URI;

public interface SelfUpdateController {
    public boolean isUpmPlugin(File var1);

    public Either<String, URI> prepareSelfUpdate(File var1, boolean var2);

    public Either<String, File> executeInternalSelfUpdate(URI var1, File var2);

    public boolean cleanupAfterSelfUpdate();

    public boolean isCleanupNeeded();
}

