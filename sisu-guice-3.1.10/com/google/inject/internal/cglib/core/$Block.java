/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.cglib.core.$CodeEmitter;

public class $Block {
    private $CodeEmitter e;
    private $Label start;
    private $Label end;

    public $Block($CodeEmitter e) {
        this.e = e;
        this.start = e.mark();
    }

    public $CodeEmitter getCodeEmitter() {
        return this.e;
    }

    public void end() {
        if (this.end != null) {
            throw new IllegalStateException("end of label already set");
        }
        this.end = this.e.mark();
    }

    public $Label getStart() {
        return this.start;
    }

    public $Label getEnd() {
        return this.end;
    }
}

