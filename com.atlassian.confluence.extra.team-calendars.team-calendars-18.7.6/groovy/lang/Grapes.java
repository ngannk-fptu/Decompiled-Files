/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Grab;

public @interface Grapes {
    public Grab[] value();

    public boolean initClass() default true;
}

