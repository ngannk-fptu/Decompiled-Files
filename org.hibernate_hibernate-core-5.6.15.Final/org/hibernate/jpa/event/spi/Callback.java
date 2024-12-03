/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import java.io.Serializable;
import org.hibernate.jpa.event.spi.CallbackType;

public interface Callback
extends Serializable {
    public CallbackType getCallbackType();

    public boolean performCallback(Object var1);
}

