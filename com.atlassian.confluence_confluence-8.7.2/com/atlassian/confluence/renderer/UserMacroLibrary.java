/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.util.concurrent.Suppliers
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.renderer;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.admin.PluginMacroRegisteredEvent;
import com.atlassian.confluence.event.events.admin.PluginMacroUnregisteredEvent;
import com.atlassian.confluence.event.events.admin.UserMacroAddedEvent;
import com.atlassian.confluence.event.events.admin.UserMacroRemovedEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.renderer.MacroManager;
import com.atlassian.confluence.renderer.MacroParameterStringParser;
import com.atlassian.confluence.renderer.UserMacroConfig;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.Suppliers;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class UserMacroLibrary
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(UserMacroLibrary.class);
    public static final String LIBRARY_KEY = "confluence.user";
    private static final String PARAM_LINE_PREFIX = "## @param ";
    private static final String NO_PARAMS_LINE_PREFIX = "## @noparams";
    private final Set<String> hiddenMacros = new CopyOnWriteArraySet<String>();
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;
    private final Supplier<MacroManager> macroManagerSupplier;

    public UserMacroLibrary(BandanaManager bandanaManager, EventPublisher eventPublisher, Supplier<MacroManager> macroManagerSupplier) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.macroManagerSupplier = macroManagerSupplier;
    }

    @Deprecated
    public UserMacroLibrary(BandanaManager bandanaManager, EventPublisher eventPublisher, TenantRegistry tenantRegistry) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.macroManagerSupplier = new LazyComponentReference("macroManager");
    }

    @Deprecated
    public UserMacroLibrary(BandanaManager bandanaManager, EventPublisher eventPublisher, TenantRegistry tenantRegistry, MacroManager macroManager) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.macroManagerSupplier = Suppliers.memoize((Object)macroManager);
    }

    @Deprecated(forRemoval=true)
    public UserMacroLibrary(BandanaManager bandanaManager, EventPublisher eventPublisher, TenantRegistry tenantRegistry, Supplier<MacroManager> macroManagerSupplier) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
        this.macroManagerSupplier = macroManagerSupplier;
    }

    public void addUpdateMacro(UserMacroConfig userMacroConfig) {
        String name = userMacroConfig.getName();
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("User macro does not have a name: " + userMacroConfig);
        }
        userMacroConfig.setParameters(this.createMacroParameters(userMacroConfig));
        Map<String, UserMacroConfig> userMacros = this.getBandanaMacros();
        userMacros.put(name, userMacroConfig);
        this.updateBandana(userMacros);
        this.eventPublisher.publish((Object)new UserMacroAddedEvent(this));
    }

    private List<MacroParameter> createMacroParameters(UserMacroConfig userMacroConfig) {
        ArrayList<MacroParameter> parameters = null;
        String name = userMacroConfig.getName();
        String template = userMacroConfig.getTemplate();
        if (StringUtils.isNotBlank((CharSequence)template)) {
            String[] templateLines;
            for (String templateLine : templateLines = template.split("\n")) {
                if ((templateLine = templateLine.trim()).startsWith(PARAM_LINE_PREFIX)) {
                    MacroParameter macroParameter;
                    String paramLine = templateLine.substring(PARAM_LINE_PREFIX.length()).trim();
                    if (parameters == null) {
                        parameters = new ArrayList();
                    }
                    if ((macroParameter = MacroParameterStringParser.extractParameter("_-user-macro-_", name, paramLine)) == null) continue;
                    parameters.add(macroParameter);
                    continue;
                }
                if (!templateLine.startsWith(NO_PARAMS_LINE_PREFIX)) continue;
                parameters = new ArrayList<MacroParameter>();
                break;
            }
        }
        if (parameters == null) {
            log.warn("User macro '{}' does not have any parameters. It will not appear in the macro browser. Please visit http://confluence.atlassian.com/x/UolYDQ for further information on how to define parameters", (Object)name);
        }
        return parameters;
    }

    public boolean hasMacro(String name) {
        return this.getMacro(name) != null;
    }

    public UserMacroConfig getMacro(String name) {
        name = name.toLowerCase();
        UserMacroConfig storedConfig = this.getAvailableMacros().get(name);
        return storedConfig;
    }

    public void removeMacro(String name) {
        Map<String, UserMacroConfig> userMacros = this.getBandanaMacros();
        userMacros.remove(name.toLowerCase());
        this.updateBandana(userMacros);
        this.hiddenMacros.remove(name);
        this.eventPublisher.publish((Object)new UserMacroRemovedEvent(this));
    }

    public SortedSet<String> getMacroNames() {
        return new TreeSet<String>(this.getAvailableMacros().keySet());
    }

    public Map<String, UserMacroConfig> getMacros() {
        return this.getAvailableMacros();
    }

    private Map<String, UserMacroConfig> getBandanaMacros() {
        Map result = (Map)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.user.macros");
        if (result == null) {
            return new HashMap<String, UserMacroConfig>();
        }
        return result;
    }

    private Map<String, UserMacroConfig> getAvailableMacros() {
        Map<String, UserMacroConfig> userMacros = this.getBandanaMacros();
        if (this.hiddenMacros.isEmpty()) {
            return userMacros;
        }
        userMacros = new HashMap<String, UserMacroConfig>(userMacros);
        for (String hidden : this.hiddenMacros) {
            userMacros.remove(hidden);
        }
        return userMacros;
    }

    private void updateBandana(Map<String, UserMacroConfig> userMacros) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.user.macros", userMacros);
    }

    @EventListener
    public void pluginMacroRegistered(PluginMacroRegisteredEvent event) {
        Map<String, UserMacroConfig> userMacros = this.getBandanaMacros();
        if (userMacros.containsKey(event.getMacroName())) {
            log.warn("The user macro '{}' shares the same name with an existing plugin macro. The user macro will be hidden.", (Object)event.getMacroName());
            this.hiddenMacros.add(event.getMacroName());
        }
    }

    @EventListener
    public void pluginMacroUnregistered(PluginMacroUnregisteredEvent event) {
        if (this.hiddenMacros.contains(event.getMacroName())) {
            this.hiddenMacros.remove(event.getMacroName());
        }
    }

    @EventListener
    public void pluginSystemStarted(PluginFrameworkStartedEvent event) {
        Map<String, Macro> macros = ((MacroManager)this.macroManagerSupplier.get()).getMacros();
        Map<String, UserMacroConfig> userMacros = this.getBandanaMacros();
        this.hiddenMacros.addAll((Collection<String>)Sets.intersection(macros.keySet(), userMacros.keySet()));
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }
}

