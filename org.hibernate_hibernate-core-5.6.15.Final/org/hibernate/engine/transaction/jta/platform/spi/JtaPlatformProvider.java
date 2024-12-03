/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.jta.platform.spi;

import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;

public interface JtaPlatformProvider {
    public JtaPlatform getProvidedJtaPlatform();
}

