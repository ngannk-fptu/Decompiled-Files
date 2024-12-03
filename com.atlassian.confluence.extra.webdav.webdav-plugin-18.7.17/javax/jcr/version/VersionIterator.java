/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.RangeIterator;
import javax.jcr.version.Version;

public interface VersionIterator
extends RangeIterator {
    public Version nextVersion();
}

