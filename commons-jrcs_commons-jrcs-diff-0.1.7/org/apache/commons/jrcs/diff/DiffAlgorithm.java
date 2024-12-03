/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;

public interface DiffAlgorithm {
    public Revision diff(Object[] var1, Object[] var2) throws DifferentiationFailedException;
}

