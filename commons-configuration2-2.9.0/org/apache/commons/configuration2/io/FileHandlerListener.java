/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import org.apache.commons.configuration2.io.FileHandler;

public interface FileHandlerListener {
    public void loading(FileHandler var1);

    public void loaded(FileHandler var1);

    public void saving(FileHandler var1);

    public void saved(FileHandler var1);

    public void locationChanged(FileHandler var1);
}

