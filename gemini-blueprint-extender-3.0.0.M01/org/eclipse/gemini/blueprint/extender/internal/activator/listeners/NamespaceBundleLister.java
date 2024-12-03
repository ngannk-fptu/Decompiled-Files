/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleEvent
 */
package org.eclipse.gemini.blueprint.extender.internal.activator.listeners;

import org.eclipse.gemini.blueprint.extender.internal.activator.NamespaceHandlerActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.listeners.BaseListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

public class NamespaceBundleLister
extends BaseListener {
    private final boolean resolved;
    private final NamespaceHandlerActivator namespaceHandlerActivator;

    public NamespaceBundleLister(boolean resolvedBundles, NamespaceHandlerActivator namespaceHandlerActivator) {
        this.resolved = resolvedBundles;
        this.namespaceHandlerActivator = namespaceHandlerActivator;
    }

    @Override
    protected void handleEvent(BundleEvent event) {
        Bundle bundle = event.getBundle();
        switch (event.getType()) {
            case 32: {
                if (!this.resolved) break;
                this.namespaceHandlerActivator.maybeAddNamespaceHandlerFor(bundle, false);
                break;
            }
            case 512: {
                if (this.resolved) break;
                this.push(bundle);
                this.namespaceHandlerActivator.maybeAddNamespaceHandlerFor(bundle, true);
                break;
            }
            case 2: {
                if (this.resolved || this.pop(bundle)) break;
                this.namespaceHandlerActivator.maybeAddNamespaceHandlerFor(bundle, false);
                break;
            }
            case 4: {
                this.pop(bundle);
                this.namespaceHandlerActivator.maybeRemoveNameSpaceHandlerFor(bundle);
                break;
            }
        }
    }
}

