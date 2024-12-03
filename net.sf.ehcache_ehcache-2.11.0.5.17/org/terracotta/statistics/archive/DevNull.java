/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.archive;

import org.terracotta.statistics.archive.SampleSink;

public class DevNull
implements SampleSink<Object> {
    public static final SampleSink<Object> DEV_NULL = new DevNull();

    private DevNull() {
    }

    @Override
    public void accept(Object object) {
    }
}

