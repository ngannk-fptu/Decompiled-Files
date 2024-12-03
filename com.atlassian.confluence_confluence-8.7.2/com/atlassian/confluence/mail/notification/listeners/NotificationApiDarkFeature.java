/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Option
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;

@Deprecated
public enum NotificationApiDarkFeature {
    NotificationPluginApi("notification.plugin.api.enabled");

    private static Supplier<Maybe<DarkFeaturesManager>> darkFeaturesManagerSupplier;
    private final String key;

    private NotificationApiDarkFeature(String key) {
        this.key = key;
    }

    public boolean isEnabled(Object event) {
        Preconditions.checkNotNull((Object)event);
        Maybe<DarkFeaturesManager> maybeDarkFeaturesManager = darkFeaturesManagerSupplier.get();
        if (maybeDarkFeaturesManager.isEmpty()) {
            return false;
        }
        DarkFeatures darkFeatures = ((DarkFeaturesManager)maybeDarkFeaturesManager.get()).getSiteDarkFeatures();
        return darkFeatures.isFeatureEnabled(this.key) || darkFeatures.isFeatureEnabled(this.key + "." + event.getClass().getName());
    }

    static {
        darkFeaturesManagerSupplier = new Supplier<Maybe<DarkFeaturesManager>>(){
            private final ResettableLazyReference<Reference<DarkFeaturesManager>> innerReference = new ResettableLazyReference<Reference<DarkFeaturesManager>>(){

                protected Reference<DarkFeaturesManager> create() {
                    return new WeakReference<DarkFeaturesManager>((DarkFeaturesManager)ContainerManager.getComponent((String)"darkFeaturesManager"));
                }
            };

            @Override
            public Maybe<DarkFeaturesManager> get() {
                DarkFeaturesManager darkFeaturesManager = (DarkFeaturesManager)((Reference)this.innerReference.get()).get();
                if (darkFeaturesManager == null) {
                    this.innerReference.reset();
                    return Option.none();
                }
                return Option.some((Object)darkFeaturesManager);
            }
        };
    }
}

