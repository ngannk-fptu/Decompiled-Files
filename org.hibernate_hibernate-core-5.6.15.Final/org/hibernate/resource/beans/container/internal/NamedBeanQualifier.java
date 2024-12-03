/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.util.AnnotationLiteral
 *  javax.inject.Named
 */
package org.hibernate.resource.beans.container.internal;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

public class NamedBeanQualifier
extends AnnotationLiteral<Named>
implements Named {
    private final String name;

    NamedBeanQualifier(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }
}

