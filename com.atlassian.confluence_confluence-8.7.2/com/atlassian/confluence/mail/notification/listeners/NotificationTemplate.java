/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  io.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.ResettableLazyReference;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;

@Deprecated
public enum NotificationTemplate {
    ADG("adg.email");

    private String darkFeatureKeyPrefix;
    private static Supplier<Maybe<DarkFeaturesManager>> darkFeaturesManagerSupplier;

    private NotificationTemplate(String darkFeatureKeyPrefix) {
        this.darkFeatureKeyPrefix = darkFeatureKeyPrefix;
    }

    public boolean isEnabled(String templateIdentifier) {
        Maybe<DarkFeaturesManager> darkFeaturesManager = darkFeaturesManagerSupplier.get();
        if (darkFeaturesManager.isEmpty()) {
            return false;
        }
        DarkFeatures darkFeatures = ((DarkFeaturesManager)darkFeaturesManager.get()).getSiteDarkFeatures();
        return !darkFeatures.isFeatureEnabled(this.darkFeatureKeyPrefix + ".disable") && !darkFeatures.isFeatureEnabled(this.darkFeatureKeyPrefix + "." + templateIdentifier + ".disable");
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

