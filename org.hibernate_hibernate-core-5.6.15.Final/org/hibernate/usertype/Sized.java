/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import org.hibernate.engine.jdbc.Size;

public interface Sized {
    public Size[] dictatedSizes();

    public Size[] defaultSizes();
}

