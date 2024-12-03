/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.TypeTarget;

public interface AnnotationTarget {
    public Kind kind();

    public ClassInfo asClass();

    public FieldInfo asField();

    public MethodInfo asMethod();

    public MethodParameterInfo asMethodParameter();

    public TypeTarget asType();

    public RecordComponentInfo asRecordComponent();

    public static enum Kind {
        CLASS,
        FIELD,
        METHOD,
        METHOD_PARAMETER,
        TYPE,
        RECORD_COMPONENT;

    }
}

