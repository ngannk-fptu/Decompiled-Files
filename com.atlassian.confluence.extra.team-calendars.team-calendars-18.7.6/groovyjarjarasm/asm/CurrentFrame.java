/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm;

import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Frame;
import groovyjarjarasm.asm.Item;

class CurrentFrame
extends Frame {
    CurrentFrame() {
    }

    void execute(int opcode, int arg, ClassWriter cw, Item item) {
        super.execute(opcode, arg, cw, item);
        Frame successor = new Frame();
        this.merge(cw, successor, 0);
        this.set(successor);
        this.owner.inputStackTop = 0;
    }
}

