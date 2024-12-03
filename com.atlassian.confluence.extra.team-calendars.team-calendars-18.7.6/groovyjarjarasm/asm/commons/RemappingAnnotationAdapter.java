/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.AnnotationVisitor;
import groovyjarjarasm.asm.commons.Remapper;

@Deprecated
public class RemappingAnnotationAdapter
extends AnnotationVisitor {
    protected final Remapper remapper;

    public RemappingAnnotationAdapter(AnnotationVisitor av, Remapper remapper) {
        this(393216, av, remapper);
    }

    protected RemappingAnnotationAdapter(int api, AnnotationVisitor av, Remapper remapper) {
        super(api, av);
        this.remapper = remapper;
    }

    public void visit(String name, Object value) {
        this.av.visit(name, this.remapper.mapValue(value));
    }

    public void visitEnum(String name, String desc, String value) {
        this.av.visitEnum(name, this.remapper.mapDesc(desc), value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor v = this.av.visitAnnotation(name, this.remapper.mapDesc(desc));
        return v == null ? null : (v == this.av ? this : new RemappingAnnotationAdapter(v, this.remapper));
    }

    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor v = this.av.visitArray(name);
        return v == null ? null : (v == this.av ? this : new RemappingAnnotationAdapter(v, this.remapper));
    }
}

