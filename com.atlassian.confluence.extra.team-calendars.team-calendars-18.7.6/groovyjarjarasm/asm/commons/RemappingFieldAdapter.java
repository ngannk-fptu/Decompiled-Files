/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.AnnotationVisitor;
import groovyjarjarasm.asm.FieldVisitor;
import groovyjarjarasm.asm.TypePath;
import groovyjarjarasm.asm.commons.Remapper;
import groovyjarjarasm.asm.commons.RemappingAnnotationAdapter;

@Deprecated
public class RemappingFieldAdapter
extends FieldVisitor {
    private final Remapper remapper;

    public RemappingFieldAdapter(FieldVisitor fv, Remapper remapper) {
        this(393216, fv, remapper);
    }

    protected RemappingFieldAdapter(int api, FieldVisitor fv, Remapper remapper) {
        super(api, fv);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = this.fv.visitAnnotation(this.remapper.mapDesc(desc), visible);
        return av == null ? null : new RemappingAnnotationAdapter(av, this.remapper);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, this.remapper.mapDesc(desc), visible);
        return av == null ? null : new RemappingAnnotationAdapter(av, this.remapper);
    }
}

