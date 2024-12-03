/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.plugin;

import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.plugins.edgeindex.plugin.EdgeTypeModuleDescriptor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={EdgeTypeRepository.class})
@Component(value="edgeTypeRepository")
public class PluggableEdgeTypeRepository
implements EdgeTypeRepository {
    private final PluginAccessor pluginAccessor;
    private final PluginEventManager pluginEventManager;
    private volatile Map<String, EdgeType> edgeTypeByKey;
    private final AtomicBoolean refresh = new AtomicBoolean(true);

    @Autowired
    public PluggableEdgeTypeRepository(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginEventManager = pluginEventManager;
        this.loadEdgeIndexTypes();
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.pluginEventManager.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.pluginEventManager.unregister((Object)this);
    }

    @Override
    public Option<EdgeType> getEdgeIndexTypeByKey(String key) {
        this.refreshEdgeTypes();
        return Option.option((Object)this.edgeTypeByKey.get(key));
    }

    @Override
    public Collection<EdgeType> getEdgeIndexTypes() {
        this.refreshEdgeTypes();
        ArrayList<EdgeType> weightedEdgeTypes = new ArrayList<EdgeType>(this.edgeTypeByKey.values());
        weightedEdgeTypes.sort((o1, o2) -> {
            int w1 = o1.getEdgeUiSupport().getWeight();
            int w2 = o2.getEdgeUiSupport().getWeight();
            return Integer.compare(w1, w2);
        });
        return weightedEdgeTypes;
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (event.getModule() instanceof EdgeTypeModuleDescriptor) {
            this.refresh.set(true);
        }
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        if (event.getModule() instanceof EdgeTypeModuleDescriptor) {
            this.refresh.set(true);
        }
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        if (event.getPlugin().getModuleDescriptorsByModuleClass(EdgeTypeModuleDescriptor.class).size() > 0) {
            this.refresh.set(true);
        }
    }

    private void loadEdgeIndexTypes() {
        List descriptorsByClass = this.pluginAccessor.getEnabledModuleDescriptorsByClass(EdgeTypeModuleDescriptor.class);
        HashMap<String, EdgeType> edgeTypeByKey = new HashMap<String, EdgeType>(descriptorsByClass.size());
        for (EdgeTypeModuleDescriptor edgeTypeModuleDescriptor : descriptorsByClass) {
            EdgeType edgeType = edgeTypeModuleDescriptor.getModule();
            edgeTypeByKey.put(edgeType.getKey(), edgeType);
        }
        this.edgeTypeByKey = Collections.unmodifiableMap(edgeTypeByKey);
    }

    private void refreshEdgeTypes() {
        while (this.refresh.compareAndSet(true, false)) {
            this.loadEdgeIndexTypes();
        }
    }
}

