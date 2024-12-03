/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.extender.internal.activator.NamespaceHandlerActivator;

public class BlueprintNamespaceHandlerActivator
extends NamespaceHandlerActivator {
    @Override
    protected String getManagedBundleExtenderVersionHeader() {
        return "BlueprintExtender-Version";
    }
}

