/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.classic;

import java.io.Serializable;
import org.hibernate.CallbackException;
import org.hibernate.Session;

public interface Lifecycle {
    public static final boolean VETO = true;
    public static final boolean NO_VETO = false;

    public boolean onSave(Session var1) throws CallbackException;

    public boolean onUpdate(Session var1) throws CallbackException;

    public boolean onDelete(Session var1) throws CallbackException;

    public void onLoad(Session var1, Serializable var2);
}

