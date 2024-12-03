/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Member;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.util.ClassUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AnnotatedMember
extends Annotated {
    protected final AnnotationMap _annotations;

    protected AnnotatedMember(AnnotationMap annotations) {
        this._annotations = annotations;
    }

    public abstract Class<?> getDeclaringClass();

    public abstract Member getMember();

    @Override
    protected AnnotationMap getAllAnnotations() {
        return this._annotations;
    }

    public final void fixAccess() {
        ClassUtil.checkAndFixAccess(this.getMember());
    }

    public abstract void setValue(Object var1, Object var2) throws UnsupportedOperationException, IllegalArgumentException;
}

