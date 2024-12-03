/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import java.io.File;

public interface Refreshable {
    public boolean refresh() throws Exception;

    public File getRoot() throws Exception;
}

