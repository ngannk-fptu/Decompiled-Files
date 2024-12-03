/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.name;

import com.google.inject.internal.util.$Preconditions;
import com.google.inject.name.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class NamedImpl
implements Named,
Serializable {
    private final String value;
    private static final long serialVersionUID = 0L;

    public NamedImpl(String value) {
        this.value = $Preconditions.checkNotNull(value, "name");
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return 127 * "value".hashCode() ^ this.value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Named)) {
            return false;
        }
        Named other = (Named)o;
        return this.value.equals(other.value());
    }

    @Override
    public String toString() {
        return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }
}

