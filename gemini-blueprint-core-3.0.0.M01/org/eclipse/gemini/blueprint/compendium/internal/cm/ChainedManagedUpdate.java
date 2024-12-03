/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.util.Map;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.springframework.util.ObjectUtils;

class ChainedManagedUpdate
implements UpdateCallback {
    private final UpdateCallback[] callbacks;

    ChainedManagedUpdate(UpdateCallback[] callbacks) {
        this.callbacks = ObjectUtils.isEmpty((Object[])callbacks) ? new UpdateCallback[]{} : callbacks;
    }

    @Override
    public void update(Object instance, Map properties) {
        for (UpdateCallback callback : this.callbacks) {
            callback.update(instance, properties);
        }
    }
}

