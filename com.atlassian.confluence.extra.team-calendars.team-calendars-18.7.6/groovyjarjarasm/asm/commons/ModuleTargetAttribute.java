/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.Attribute;
import groovyjarjarasm.asm.ByteVector;
import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;

public final class ModuleTargetAttribute
extends Attribute {
    public String platform;

    public ModuleTargetAttribute(String platform) {
        super("ModuleTarget");
        this.platform = platform;
    }

    public ModuleTargetAttribute() {
        this(null);
    }

    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
        String platform = cr.readUTF8(off, buf);
        return new ModuleTargetAttribute(platform);
    }

    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        int index = this.platform == null ? 0 : cw.newUTF8(this.platform);
        v.putShort(index);
        return v;
    }
}

