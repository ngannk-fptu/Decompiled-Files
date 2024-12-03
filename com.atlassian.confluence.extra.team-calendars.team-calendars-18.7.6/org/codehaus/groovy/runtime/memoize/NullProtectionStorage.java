/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.memoize;

import org.codehaus.groovy.runtime.memoize.ProtectionStorage;

public final class NullProtectionStorage
implements ProtectionStorage {
    @Override
    public void touch(Object key, Object value) {
    }
}

