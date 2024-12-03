/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.control.HasCleanup;

public class Janitor
implements HasCleanup {
    private final Set pending = new HashSet();

    public void register(HasCleanup object) {
        this.pending.add(object);
    }

    @Override
    public void cleanup() {
        for (HasCleanup object : this.pending) {
            try {
                object.cleanup();
            }
            catch (Exception exception) {}
        }
        this.pending.clear();
    }
}

