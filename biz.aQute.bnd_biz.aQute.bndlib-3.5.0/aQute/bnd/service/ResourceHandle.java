/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import java.io.File;
import java.io.IOException;

public interface ResourceHandle {
    public String getName();

    public Location getLocation();

    public File request() throws IOException, Exception;

    public static enum Location {
        local,
        remote_cached,
        remote;

    }
}

