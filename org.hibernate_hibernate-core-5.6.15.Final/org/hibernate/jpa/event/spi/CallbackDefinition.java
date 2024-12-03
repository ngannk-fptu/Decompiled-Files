/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.spi;

import java.io.Serializable;
import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;

public interface CallbackDefinition
extends Serializable {
    public Callback createCallback(ManagedBeanRegistry var1);
}

