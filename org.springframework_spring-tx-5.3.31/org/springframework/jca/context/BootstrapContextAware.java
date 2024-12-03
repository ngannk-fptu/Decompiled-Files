/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.BootstrapContext
 *  org.springframework.beans.factory.Aware
 */
package org.springframework.jca.context;

import javax.resource.spi.BootstrapContext;
import org.springframework.beans.factory.Aware;

public interface BootstrapContextAware
extends Aware {
    public void setBootstrapContext(BootstrapContext var1);
}

