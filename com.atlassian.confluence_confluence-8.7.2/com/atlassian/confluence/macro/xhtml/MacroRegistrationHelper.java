/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.atlassian.renderer.v2.macro.Macro
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.event.events.admin.PluginMacroRegisteredEvent;
import com.atlassian.confluence.event.events.admin.PluginMacroUnregisteredEvent;
import com.atlassian.confluence.macro.LazyLoadingMacroWrapper;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.UserMacroPluginMacroManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.renderer.v2.macro.Macro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MacroRegistrationHelper {
    private static final Logger log = LoggerFactory.getLogger(UserMacroPluginMacroManager.class);
    private final DescriptorChecker checker;
    private final MacroManager macroManager;
    private final EventPublisher eventPublisher;

    MacroRegistrationHelper(Class<?> requiredModuleDescriptor, MacroManager macroManager, EventPublisher eventPublisher) {
        this.checker = new ClassDescriptorChecker(requiredModuleDescriptor);
        this.macroManager = macroManager;
        this.eventPublisher = eventPublisher;
    }

    MacroRegistrationHelper(ModuleDescriptorPredicate<Macro> predicate, MacroManager macroManager, EventPublisher eventPublisher) {
        this.checker = new ModuleDescriptorPredicateChecker<Macro>(predicate);
        this.macroManager = macroManager;
        this.eventPublisher = eventPublisher;
    }

    void pluginModuleEnabled(ModuleDescriptor<?> moduleDescriptor) {
        if (!this.checker.isValidDescriptor(moduleDescriptor)) {
            return;
        }
        log.info("Enabling a macro from the plugin {}", (Object)moduleDescriptor.getCompleteKey());
        this.macroManager.registerMacro(moduleDescriptor.getName(), new LazyLoadingMacroWrapper(this.macroManager.createLazyMacroReference(moduleDescriptor)));
        this.eventPublisher.publish((Object)new PluginMacroRegisteredEvent(moduleDescriptor.getName(), this));
    }

    void pluginModuleDisabled(ModuleDescriptor<?> moduleDescriptor) {
        if (!this.checker.isValidDescriptor(moduleDescriptor)) {
            return;
        }
        log.info("Disabling a user macro from the plugin {}.", (Object)moduleDescriptor.getCompleteKey());
        this.macroManager.unregisterMacro(moduleDescriptor.getName());
        this.eventPublisher.publish((Object)new PluginMacroUnregisteredEvent(moduleDescriptor.getName(), this));
    }

    private static class ModuleDescriptorPredicateChecker<T>
    implements DescriptorChecker {
        private final ModuleDescriptorPredicate<T> predicate;

        ModuleDescriptorPredicateChecker(ModuleDescriptorPredicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean isValidDescriptor(ModuleDescriptor<?> moduleDescriptor) {
            ModuleDescriptor<?> m = moduleDescriptor;
            return this.predicate.matches(m);
        }
    }

    private static class ClassDescriptorChecker
    implements DescriptorChecker {
        private final Class<?> requiredModuleDescriptor;

        ClassDescriptorChecker(Class<?> requiredModuleDescriptor) {
            this.requiredModuleDescriptor = requiredModuleDescriptor;
        }

        @Override
        public boolean isValidDescriptor(ModuleDescriptor<?> moduleDescriptor) {
            return this.requiredModuleDescriptor.isInstance(moduleDescriptor);
        }
    }

    private static interface DescriptorChecker {
        public boolean isValidDescriptor(ModuleDescriptor<?> var1);
    }
}

