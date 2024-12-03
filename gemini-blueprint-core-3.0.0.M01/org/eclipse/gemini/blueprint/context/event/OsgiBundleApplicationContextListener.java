/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.context.event;

import java.util.EventListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;

public interface OsgiBundleApplicationContextListener<E extends OsgiBundleApplicationContextEvent>
extends EventListener {
    public void onOsgiApplicationEvent(E var1);
}

