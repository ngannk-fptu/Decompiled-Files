/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.SpecialBuiltIn;

abstract class BuiltInWithDirectCallOptimization
extends SpecialBuiltIn {
    BuiltInWithDirectCallOptimization() {
    }

    protected abstract void setDirectlyCalled();
}

