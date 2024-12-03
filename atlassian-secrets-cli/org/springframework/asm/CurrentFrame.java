/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.asm;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Frame;
import org.springframework.asm.Item;

class CurrentFrame
extends Frame {
    CurrentFrame() {
    }

    @Override
    void execute(int opcode, int arg, ClassWriter cw, Item item) {
        super.execute(opcode, arg, cw, item);
        Frame successor = new Frame();
        this.merge(cw, successor, 0);
        this.set(successor);
        this.owner.inputStackTop = 0;
    }
}

