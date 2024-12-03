/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.Attribute;
import groovyjarjarasm.asm.ByteVector;
import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;

public final class ModuleResolutionAttribute
extends Attribute {
    public static final int RESOLUTION_DO_NOT_RESOLVE_BY_DEFAULT = 1;
    public static final int RESOLUTION_WARN_DEPRECATED = 2;
    public static final int RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL = 4;
    public static final int RESOLUTION_WARN_INCUBATING = 8;
    public int resolution;

    public ModuleResolutionAttribute(int resolution) {
        super("ModuleResolution");
        this.resolution = resolution;
    }

    public ModuleResolutionAttribute() {
        this(0);
    }

    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
        int resolution = cr.readUnsignedShort(off);
        return new ModuleResolutionAttribute(resolution);
    }

    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        v.putShort(this.resolution);
        return v;
    }
}

