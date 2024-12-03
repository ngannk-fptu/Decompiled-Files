/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ResolveContext;

final class ResolveResult<V extends AbstractConfigValue> {
    public final ResolveContext context;
    public final V value;

    private ResolveResult(ResolveContext context, V value) {
        this.context = context;
        this.value = value;
    }

    static <V extends AbstractConfigValue> ResolveResult<V> make(ResolveContext context, V value) {
        return new ResolveResult<V>(context, value);
    }

    ResolveResult<AbstractConfigObject> asObjectResult() {
        if (!(this.value instanceof AbstractConfigObject)) {
            throw new ConfigException.BugOrBroken("Expecting a resolve result to be an object, but it was " + this.value);
        }
        ResolveResult o = this;
        return o;
    }

    ResolveResult<AbstractConfigValue> asValueResult() {
        ResolveResult o = this;
        return o;
    }

    ResolveResult<V> popTrace() {
        return ResolveResult.make(this.context.popTrace(), this.value);
    }

    public String toString() {
        return "ResolveResult(" + this.value + ")";
    }
}

