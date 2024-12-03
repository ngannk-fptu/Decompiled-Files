/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaClass;

public interface AdaptingMetaClass
extends MetaClass {
    public MetaClass getAdaptee();

    public void setAdaptee(MetaClass var1);
}

