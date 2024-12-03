/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.context.InternalHousekeepingContext;
import org.apache.velocity.context.InternalWrapperContext;

public interface InternalContextAdapter
extends InternalHousekeepingContext,
Context,
InternalWrapperContext,
InternalEventContext {
}

