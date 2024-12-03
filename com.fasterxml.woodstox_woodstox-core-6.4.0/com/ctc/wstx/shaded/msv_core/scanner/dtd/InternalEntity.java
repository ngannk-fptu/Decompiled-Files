/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.EntityDecl;

class InternalEntity
extends EntityDecl {
    char[] buf;

    InternalEntity(String name, char[] value) {
        this.name = name;
        this.buf = value;
    }
}

