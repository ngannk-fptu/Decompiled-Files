/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.layout.internal;

import java.util.List;
import org.apache.logging.log4j.core.layout.internal.ListChecker;

public class ExcludeChecker
implements ListChecker {
    private final List<String> list;

    public ExcludeChecker(List<String> list) {
        this.list = list;
    }

    @Override
    public boolean check(String key) {
        return !this.list.contains(key);
    }

    public String toString() {
        return "ThreadContextExcludes=" + this.list.toString();
    }
}

