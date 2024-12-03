/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.service.blueprint.container.BlueprintEvent
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.event;

import org.osgi.service.blueprint.container.BlueprintEvent;

interface EventDispatcher {
    public void beforeClose(BlueprintEvent var1);

    public void beforeRefresh(BlueprintEvent var1);

    public void afterClose(BlueprintEvent var1);

    public void afterRefresh(BlueprintEvent var1);

    public void refreshFailure(BlueprintEvent var1);

    public void waiting(BlueprintEvent var1);

    public void grace(BlueprintEvent var1);
}

