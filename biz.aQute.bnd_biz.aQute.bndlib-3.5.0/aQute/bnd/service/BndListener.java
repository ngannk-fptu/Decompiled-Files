/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.service.reporter.Reporter;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class BndListener {
    final AtomicInteger inside = new AtomicInteger();

    public void changed(File file) {
    }

    public void begin() {
        this.inside.incrementAndGet();
    }

    public void end() {
        this.inside.decrementAndGet();
    }

    public boolean isInside() {
        return this.inside.get() != 0;
    }

    public void signal(Reporter reporter) {
    }
}

