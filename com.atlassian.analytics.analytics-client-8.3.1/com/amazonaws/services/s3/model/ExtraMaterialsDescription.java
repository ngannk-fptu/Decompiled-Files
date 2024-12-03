/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtraMaterialsDescription
implements Serializable {
    public static final ExtraMaterialsDescription NONE = new ExtraMaterialsDescription(Collections.EMPTY_MAP);
    private final Map<String, String> extra;
    private final ConflictResolution resolve;

    public ExtraMaterialsDescription(Map<String, String> matdesc) {
        this(matdesc, ConflictResolution.FAIL_FAST);
    }

    public ExtraMaterialsDescription(Map<String, String> matdesc, ConflictResolution resolve) {
        if (matdesc == null || resolve == null) {
            throw new IllegalArgumentException();
        }
        this.extra = Collections.unmodifiableMap(new HashMap<String, String>(matdesc));
        this.resolve = resolve;
    }

    public Map<String, String> getMaterialDescription() {
        return this.extra;
    }

    public ConflictResolution getConflictResolution() {
        return this.resolve;
    }

    public Map<String, String> mergeInto(Map<String, String> core) {
        if (this.extra.size() == 0) {
            return core;
        }
        if (core == null || core.size() == 0) {
            return this.extra;
        }
        switch (this.resolve) {
            case FAIL_FAST: {
                int total = core.size() + this.extra.size();
                HashMap<String, String> merged = new HashMap<String, String>(core);
                merged.putAll(this.extra);
                if (total != merged.size()) {
                    throw new IllegalArgumentException("The supplemental material descriptions contains conflicting entries");
                }
                return Collections.unmodifiableMap(merged);
            }
            case OVERRIDDEN: {
                HashMap<String, String> merged = new HashMap<String, String>(this.extra);
                merged.putAll(core);
                return Collections.unmodifiableMap(merged);
            }
            case OVERRIDE: {
                HashMap<String, String> merged = new HashMap<String, String>(core);
                merged.putAll(this.extra);
                return Collections.unmodifiableMap(merged);
            }
        }
        throw new UnsupportedOperationException();
    }

    public static enum ConflictResolution {
        FAIL_FAST,
        OVERRIDE,
        OVERRIDDEN;

    }
}

