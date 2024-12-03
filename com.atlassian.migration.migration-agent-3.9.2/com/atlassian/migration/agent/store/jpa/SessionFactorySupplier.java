/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 */
package com.atlassian.migration.agent.store.jpa;

import java.util.function.Supplier;
import org.hibernate.SessionFactory;

public interface SessionFactorySupplier
extends Supplier<SessionFactory> {
}

