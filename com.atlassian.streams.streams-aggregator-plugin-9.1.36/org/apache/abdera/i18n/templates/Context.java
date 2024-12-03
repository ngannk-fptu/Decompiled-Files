/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Context
extends Cloneable,
Serializable,
Iterable<String> {
    public <T> T resolve(String var1);

    public boolean isIri();

    public void setIri(boolean var1);

    public void clear();
}

