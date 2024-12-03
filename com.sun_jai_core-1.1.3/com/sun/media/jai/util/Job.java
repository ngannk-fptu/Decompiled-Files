/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import javax.media.jai.PlanarImage;

interface Job {
    public void compute();

    public boolean notDone();

    public PlanarImage getOwner();

    public boolean isBlocking();

    public Exception getException();
}

