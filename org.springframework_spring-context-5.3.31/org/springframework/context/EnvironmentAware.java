/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 *  org.springframework.core.env.Environment
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

public interface EnvironmentAware
extends Aware {
    public void setEnvironment(Environment var1);
}

