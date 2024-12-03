/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Attribute
 *  org.objectweb.asm.ClassReader
 *  org.objectweb.asm.ClassVisitor
 */
package net.sf.cglib.transform;

import net.sf.cglib.core.ClassGenerator;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class ClassReaderGenerator
implements ClassGenerator {
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;

    public ClassReaderGenerator(ClassReader r, int flags) {
        this(r, null, flags);
    }

    public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
        this.r = r;
        this.attrs = attrs != null ? attrs : new Attribute[]{};
        this.flags = flags;
    }

    public void generateClass(ClassVisitor v) {
        this.r.accept(v, this.attrs, this.flags);
    }
}

