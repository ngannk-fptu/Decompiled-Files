/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator;

public class NameClassCollisionChecker
extends NameClassComparator {
    protected void probe(String uri, String local) {
        if (this.nc1.accepts(uri, local) && this.nc2.accepts(uri, local)) {
            throw this.eureka;
        }
    }
}

