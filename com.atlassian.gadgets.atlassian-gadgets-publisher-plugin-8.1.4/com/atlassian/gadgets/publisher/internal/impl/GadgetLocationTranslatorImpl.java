/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.plugins.GadgetLocationTranslator
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec$Key
 *  com.atlassian.gadgets.util.GadgetSpecUrlBuilder
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker$Customizer
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.plugins.GadgetLocationTranslator;
import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.publisher.GadgetModuleDescriptor;
import com.atlassian.gadgets.util.GadgetSpecUrlBuilder;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService
@Component
public class GadgetLocationTranslatorImpl
implements GadgetLocationTranslator {
    private PluginModuleTracker<PluginGadgetSpec, GadgetModuleDescriptor> gadgetModuleTracker;
    private GadgetSpecUrlBuilder urlBuilder;

    @Autowired
    public GadgetLocationTranslatorImpl(GadgetSpecUrlBuilder urlBuilder, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager) {
        this.gadgetModuleTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, GadgetModuleDescriptor.class, (PluginModuleTracker.Customizer)new GadgetModuleWithPublishLocationCustomizer());
        this.urlBuilder = urlBuilder;
    }

    private URI buildGadgetSpecUri(GadgetModuleDescriptor gadgetModuleDescriptor) {
        PluginGadgetSpec gadget = gadgetModuleDescriptor.getModule();
        return URI.create(this.urlBuilder.buildGadgetSpecUrl(gadget.getPluginKey(), gadget.getModuleKey(), gadget.getLocation()));
    }

    private Option<GadgetModuleDescriptor> find(PluginGadgetSpec.Key key) {
        return Iterables.findFirst((Iterable)this.gadgetModuleTracker.getModuleDescriptors(), this.findByGadgetSpecPublishKeyPredicate(key));
    }

    private Predicate<GadgetModuleDescriptor> findByGadgetSpecPublishKeyPredicate(final PluginGadgetSpec.Key key) {
        return new Predicate<GadgetModuleDescriptor>(){

            @Override
            public boolean test(GadgetModuleDescriptor gadgetModuleDescriptor) {
                String customPublishLocation = gadgetModuleDescriptor.getModule().getPublishLocation();
                if (customPublishLocation == null) {
                    return false;
                }
                return key.equals((Object)GadgetLocationTranslatorImpl.this.urlBuilder.parseGadgetSpecUrl(GadgetLocationTranslatorImpl.this.urlBuilder.appendBaseGadgetSpecUrl(customPublishLocation)));
            }
        };
    }

    public PluginGadgetSpec.Key translate(PluginGadgetSpec.Key gadgetSpecKey) {
        Option<GadgetModuleDescriptor> gadget = this.find(gadgetSpecKey);
        return gadget.isEmpty() ? gadgetSpecKey : ((GadgetModuleDescriptor)((Object)gadget.get())).getModule().getKey();
    }

    public URI translate(URI gadgetSpecUri) {
        try {
            PluginGadgetSpec.Key gadgetSpecKey = this.urlBuilder.parseGadgetSpecUrl(gadgetSpecUri.toASCIIString());
            Option<GadgetModuleDescriptor> gadget = this.find(gadgetSpecKey);
            return gadget.isEmpty() ? gadgetSpecUri : this.buildGadgetSpecUri((GadgetModuleDescriptor)((Object)gadget.get()));
        }
        catch (GadgetSpecUriNotAllowedException e) {
            return gadgetSpecUri;
        }
    }

    private static class GadgetModuleWithPublishLocationCustomizer
    implements PluginModuleTracker.Customizer<PluginGadgetSpec, GadgetModuleDescriptor> {
        private GadgetModuleWithPublishLocationCustomizer() {
        }

        public GadgetModuleDescriptor adding(GadgetModuleDescriptor descriptor) {
            return descriptor.getModule().getPublishLocation() == null ? null : descriptor;
        }

        public void removed(GadgetModuleDescriptor descriptor) {
        }
    }
}

