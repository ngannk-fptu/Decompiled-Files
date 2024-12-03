/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginDependencies$Type
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginDependencies;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

final class DependentPlugins {
    private static final Set<PluginDependencies.Type> ALL_TYPES = ImmutableSet.of((Object)PluginDependencies.Type.MANDATORY, (Object)PluginDependencies.Type.OPTIONAL, (Object)PluginDependencies.Type.DYNAMIC);
    private final List<DependentPlugin> plugins;

    public DependentPlugins(Collection<String> rootPluginKeys, Iterable<Plugin> allEnabledPlugins, Set<PluginDependencies.Type> dependencyTypes) {
        if (dependencyTypes.isEmpty()) {
            throw new IllegalArgumentException("Dependency types must be provided");
        }
        PluginDependencies.Type leastSignificantType = this.getLeastSignificantType(dependencyTypes);
        Multimap<String, DependentPlugin> pluginsToAllDependents = this.buildPluginToItsDependants(allEnabledPlugins, leastSignificantType);
        ImmutableMap pluginKeyToPlugin = Maps.uniqueIndex(allEnabledPlugins, Plugin::getKey);
        Map<String, DependentPlugin> transitiveDependentsByName = this.calculateTransitivePluginDependencies(rootPluginKeys, dependencyTypes, pluginsToAllDependents, (Map<String, Plugin>)pluginKeyToPlugin);
        Map<String, Set<String>> workMap = this.constructWorkMap(pluginsToAllDependents, transitiveDependentsByName.values());
        this.plugins = this.getInDependencyOrder(workMap, transitiveDependentsByName);
    }

    private List<DependentPlugin> getInDependencyOrder(Map<String, Set<String>> workMap, Map<String, DependentPlugin> transitiveDependenciesByName) {
        ArrayList<DependentPlugin> pluginsInDependencyOrder = new ArrayList<DependentPlugin>();
        while (!workMap.isEmpty()) {
            List pluginsWithNoMoreDependencies = workMap.keySet().stream().filter(k -> ((Set)workMap.get(k)).isEmpty()).collect(Collectors.toList());
            if (pluginsWithNoMoreDependencies.isEmpty()) {
                String current = this.findPluginToCutTheCycle(workMap);
                this.removeFromWorkmap(workMap, current);
                DependentPlugin currentPlugin = transitiveDependenciesByName.get(current);
                if (currentPlugin == null) continue;
                pluginsInDependencyOrder.add(currentPlugin);
                continue;
            }
            for (String pluginWithNoMoreDependencies : pluginsWithNoMoreDependencies) {
                this.removeFromWorkmap(workMap, pluginWithNoMoreDependencies);
                DependentPlugin currentPlugin = transitiveDependenciesByName.get(pluginWithNoMoreDependencies);
                if (currentPlugin == null) continue;
                pluginsInDependencyOrder.add(currentPlugin);
            }
        }
        return pluginsInDependencyOrder;
    }

    private String findPluginToCutTheCycle(Map<String, Set<String>> workMap) {
        String next;
        HashSet<String> alreadySeen = new HashSet<String>();
        String current = next = workMap.keySet().iterator().next();
        while (alreadySeen.add(next)) {
            current = next;
            next = (String)workMap.get(current).stream().findFirst().orElseThrow(() -> new IllegalStateException("Each plugin has a dependency. This suggests there is a cyclic dependency, yet we could not find a cycle. The internal data structure is corrupted: " + workMap));
        }
        return current;
    }

    private void removeFromWorkmap(Map<String, Set<String>> workMap, String pluginKey) {
        workMap.remove(pluginKey);
        for (Set<String> plugins : workMap.values()) {
            plugins.remove(pluginKey);
        }
    }

    private Map<String, Set<String>> constructWorkMap(Multimap<String, DependentPlugin> pluginsToAllDependents, Collection<DependentPlugin> mostSignificantTransitiveDependencies) {
        HashMap<String, Set<String>> workMap = new HashMap<String, Set<String>>();
        for (DependentPlugin plugin : mostSignificantTransitiveDependencies) {
            Set dependentPlugins = pluginsToAllDependents.get((Object)plugin.getPlugin().getKey()).stream().map(DependentPlugin::getPlugin).map(Plugin::getKey).collect(Collectors.toSet());
            workMap.put(plugin.getPlugin().getKey(), dependentPlugins);
            for (String dependentPlugin : dependentPlugins) {
                workMap.putIfAbsent(dependentPlugin, Collections.emptySet());
            }
        }
        return workMap;
    }

    private Map<String, DependentPlugin> calculateTransitivePluginDependencies(Collection<String> rootPluginKeys, Set<PluginDependencies.Type> dependencyTypes, Multimap<String, DependentPlugin> allPluginDependencies, Map<String, Plugin> pluginKeyToPlugin) {
        HashMap<String, DependentPlugin> transitivePluginDependencies = new HashMap<String, DependentPlugin>();
        DependencyQueue dependenciesToExplore = new DependencyQueue();
        HashSet visited = Sets.newHashSet();
        for (String rootPluginKey : rootPluginKeys) {
            dependenciesToExplore.addLast(new CappedDep(rootPluginKey, PluginDependencies.Type.MANDATORY));
            for (PluginDependencies.Type type : PluginDependencies.Type.values()) {
                visited.add(new CappedDep(rootPluginKey, type));
            }
            Plugin rootPlugin = pluginKeyToPlugin.get(rootPluginKey);
            if (rootPlugin == null) continue;
            transitivePluginDependencies.put(rootPluginKey, new DependentPlugin(rootPlugin, PluginDependencies.Type.MANDATORY, true));
        }
        while (!dependenciesToExplore.isEmpty()) {
            CappedDep currentPlugin = dependenciesToExplore.removeFirst();
            for (DependentPlugin pluginWithDependencyType : allPluginDependencies.get((Object)currentPlugin.key)) {
                Plugin dependentPlugin;
                String dependentPluginKey;
                CappedDep newDep;
                PluginDependencies.Type dependencyType = currentPlugin.cap(pluginWithDependencyType.getDependencyType());
                if (!dependencyTypes.contains(dependencyType) || !visited.add(newDep = new CappedDep(dependentPluginKey = (dependentPlugin = pluginWithDependencyType.getPlugin()).getKey(), dependencyType))) continue;
                DependentPlugin existingDependencyType = (DependentPlugin)transitivePluginDependencies.get(dependentPluginKey);
                if (existingDependencyType == null || existingDependencyType.getDependencyType().lessSignificant(dependencyType)) {
                    transitivePluginDependencies.put(dependentPlugin.getKey(), new DependentPlugin(dependentPlugin, dependencyType, false));
                }
                dependenciesToExplore.addLast(newDep);
            }
        }
        return transitivePluginDependencies;
    }

    private Multimap<String, DependentPlugin> buildPluginToItsDependants(Iterable<Plugin> allEnabledPlugins, PluginDependencies.Type leastSignificantType) {
        ArrayListMultimap dependencies = ArrayListMultimap.create();
        for (Plugin p : allEnabledPlugins) {
            for (Map.Entry keyTypes : p.getDependencies().getTypesByPluginKey().entrySet()) {
                for (PluginDependencies.Type type : (SortedSet)keyTypes.getValue()) {
                    if (type.lessSignificant(leastSignificantType)) continue;
                    dependencies.put(keyTypes.getKey(), (Object)new DependentPlugin(p, type, false));
                }
            }
        }
        return dependencies;
    }

    private PluginDependencies.Type getLeastSignificantType(Set<PluginDependencies.Type> dependencyTypes) {
        PluginDependencies.Type leastSignificantType = PluginDependencies.Type.MANDATORY;
        for (PluginDependencies.Type type : dependencyTypes) {
            if (!type.lessSignificant(leastSignificantType)) continue;
            leastSignificantType = type;
        }
        return leastSignificantType;
    }

    public List<String> toStringList() {
        return this.toStringList(ALL_TYPES);
    }

    public List<String> toStringList(Set<PluginDependencies.Type> dependencyTypes) {
        return this.plugins.stream().filter(dp -> !dp.isRoot()).filter(dp -> dependencyTypes.contains(dp.getDependencyType())).map(dp -> dp.getPlugin().getKey() + "(" + dp.getDependencyType() + ")").collect(Collectors.toList());
    }

    public List<Plugin> getPlugins(boolean includeRoots) {
        return this.getPluginsByTypes(ALL_TYPES, includeRoots);
    }

    public List<Plugin> getPluginsByTypes(Set<PluginDependencies.Type> dependencyTypes, boolean includeRoots) {
        Preconditions.checkArgument((!includeRoots || dependencyTypes.contains(PluginDependencies.Type.MANDATORY) ? 1 : 0) != 0, (Object)("Roots always have dependency type " + PluginDependencies.Type.MANDATORY + ". Cannot ask for includeRoots=true and not have " + PluginDependencies.Type.MANDATORY + " in dependencyTypes (" + dependencyTypes + ")."));
        return this.plugins.stream().filter(p -> includeRoots || !p.isRoot()).filter(p -> dependencyTypes.contains(p.getDependencyType())).map(DependentPlugin::getPlugin).collect(Collectors.toList());
    }

    private static class DependencyQueue {
        private final Deque<CappedDep> queue = new ArrayDeque<CappedDep>();

        private DependencyQueue() {
        }

        CappedDep removeFirst() {
            return this.queue.removeFirst();
        }

        boolean isEmpty() {
            return this.queue.isEmpty();
        }

        void addLast(CappedDep newDep) {
            boolean addToQueue = true;
            Iterator<CappedDep> iter = this.queue.iterator();
            while (iter.hasNext()) {
                CappedDep current = iter.next();
                if (!current.key.equals(newDep.key)) continue;
                addToQueue = current.cap.lessSignificant(newDep.cap);
                if (!addToQueue) break;
                iter.remove();
                break;
            }
            if (addToQueue) {
                this.queue.addLast(newDep);
            }
        }
    }

    private static class CappedDep {
        @Nonnull
        final String key;
        @Nonnull
        final PluginDependencies.Type cap;

        CappedDep(String key, PluginDependencies.Type cap) {
            this.key = key;
            this.cap = cap;
        }

        PluginDependencies.Type cap(PluginDependencies.Type depType) {
            return depType.lessSignificant(this.cap) ? depType : this.cap;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CappedDep cappedDep = (CappedDep)o;
            if (!this.key.equals(cappedDep.key)) {
                return false;
            }
            return this.cap == cappedDep.cap;
        }

        public int hashCode() {
            int result = this.key.hashCode();
            result = 31 * result + this.cap.hashCode();
            return result;
        }
    }

    private static class DependentPlugin {
        private final Plugin plugin;
        private final PluginDependencies.Type dependencyType;
        private final boolean isRoot;

        DependentPlugin(Plugin plugin, PluginDependencies.Type dependencyType, boolean isRoot) {
            this.plugin = plugin;
            this.dependencyType = dependencyType;
            this.isRoot = isRoot;
        }

        Plugin getPlugin() {
            return this.plugin;
        }

        PluginDependencies.Type getDependencyType() {
            return this.dependencyType;
        }

        boolean isRoot() {
            return this.isRoot;
        }

        public String toString() {
            return "DependentPlugin{plugin=" + this.plugin + ", dependencyType=" + this.dependencyType + ", isRoot=" + this.isRoot + '}';
        }
    }
}

