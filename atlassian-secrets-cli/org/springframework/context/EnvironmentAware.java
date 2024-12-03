/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

public interface EnvironmentAware
extends Aware {
    public void setEnvironment(Environment var1);
}

