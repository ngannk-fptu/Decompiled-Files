/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ModuleRef;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ModuleFinder {
    private List<ModuleRef> systemModuleRefs;
    private List<ModuleRef> nonSystemModuleRefs;
    private boolean forceScanJavaClassPath;
    private final ReflectionUtils reflectionUtils;

    public List<ModuleRef> getSystemModuleRefs() {
        return this.systemModuleRefs;
    }

    public List<ModuleRef> getNonSystemModuleRefs() {
        return this.nonSystemModuleRefs;
    }

    public boolean forceScanJavaClassPath() {
        return this.forceScanJavaClassPath;
    }

    private void findLayerOrder(Object layer, Set<Object> layerVisited, Set<Object> parentLayers, Deque<Object> layerOrderOut) {
        if (layerVisited.add(layer)) {
            List parents = (List)this.reflectionUtils.invokeMethod(true, layer, "parents");
            if (parents != null) {
                parentLayers.addAll(parents);
                for (Object parent : parents) {
                    this.findLayerOrder(parent, layerVisited, parentLayers, layerOrderOut);
                }
            }
            layerOrderOut.push(layer);
        }
    }

    private List<ModuleRef> findModuleRefs(LinkedHashSet<Object> layers, ScanSpec scanSpec, LogNode log) {
        ArrayList layerOrderFinal;
        if (layers.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayDeque<Object> layerOrder = new ArrayDeque<Object>();
        HashSet<Object> parentLayers = new HashSet<Object>();
        for (Object e : layers) {
            if (e == null) continue;
            this.findLayerOrder(e, new HashSet<Object>(), parentLayers, layerOrder);
        }
        if (scanSpec.addedModuleLayers != null) {
            for (Object object : scanSpec.addedModuleLayers) {
                if (object == null) continue;
                this.findLayerOrder(object, new HashSet<Object>(), parentLayers, layerOrder);
            }
        }
        if (scanSpec.ignoreParentModuleLayers) {
            layerOrderFinal = new ArrayList();
            for (Object e : layerOrder) {
                if (parentLayers.contains(e)) continue;
                layerOrderFinal.add(e);
            }
        } else {
            layerOrderFinal = new ArrayList(layerOrder);
        }
        HashSet<Object> hashSet = new HashSet<Object>();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Object layer : layerOrderFinal) {
            Set modules;
            Object configuration = this.reflectionUtils.invokeMethod(true, layer, "configuration");
            if (configuration == null || (modules = (Set)this.reflectionUtils.invokeMethod(true, configuration, "modules")) == null) continue;
            ArrayList<ModuleRef> modulesInLayer = new ArrayList<ModuleRef>();
            for (Object module : modules) {
                Object moduleReference = this.reflectionUtils.invokeMethod(true, module, "reference");
                if (moduleReference == null || !hashSet.add(moduleReference)) continue;
                try {
                    modulesInLayer.add(new ModuleRef(moduleReference, layer, this.reflectionUtils));
                }
                catch (IllegalArgumentException e) {
                    if (log == null) continue;
                    log.log("Exception while creating ModuleRef for module " + moduleReference, e);
                }
            }
            CollectionUtils.sortIfNotEmpty(modulesInLayer);
            linkedHashSet.addAll(modulesInLayer);
        }
        return new ArrayList<ModuleRef>(linkedHashSet);
    }

    private List<ModuleRef> findModuleRefsFromCallstack(Class<?>[] callStack, ScanSpec scanSpec, boolean scanNonSystemModules, LogNode log) {
        LinkedHashSet<Object> layers = new LinkedHashSet<Object>();
        if (callStack != null) {
            for (Class<?> stackFrameClass : callStack) {
                Object module = this.reflectionUtils.invokeMethod(false, stackFrameClass, "getModule");
                if (module == null) continue;
                Object layer = this.reflectionUtils.invokeMethod(true, module, "getLayer");
                if (layer != null) {
                    layers.add(layer);
                    continue;
                }
                if (!scanNonSystemModules) continue;
                this.forceScanJavaClassPath = true;
            }
        }
        Class<?> moduleLayerClass = null;
        try {
            moduleLayerClass = Class.forName("java.lang.ModuleLayer");
        }
        catch (ClassNotFoundException | LinkageError throwable) {
            // empty catch block
        }
        if (moduleLayerClass != null) {
            Object bootLayer = this.reflectionUtils.invokeStaticMethod(false, moduleLayerClass, "boot");
            if (bootLayer != null) {
                layers.add(bootLayer);
            } else if (scanNonSystemModules) {
                this.forceScanJavaClassPath = true;
            }
        }
        return this.findModuleRefs(layers, scanSpec, log);
    }

    public ModuleFinder(Class<?>[] callStack, ScanSpec scanSpec, boolean scanNonSystemModules, boolean scanSystemModules, ReflectionUtils reflectionUtils, LogNode log) {
        this.reflectionUtils = reflectionUtils;
        List<ModuleRef> allModuleRefsList = null;
        if (scanSpec.overrideModuleLayers == null) {
            if (callStack != null && callStack.length > 0) {
                allModuleRefsList = this.findModuleRefsFromCallstack(callStack, scanSpec, scanNonSystemModules, log);
            }
        } else {
            if (log != null) {
                LogNode subLog = log.log("Overriding module layers");
                for (Object moduleLayer : scanSpec.overrideModuleLayers) {
                    subLog.log(moduleLayer.toString());
                }
            }
            allModuleRefsList = this.findModuleRefs(new LinkedHashSet<Object>(scanSpec.overrideModuleLayers), scanSpec, log);
        }
        if (allModuleRefsList != null) {
            this.systemModuleRefs = new ArrayList<ModuleRef>();
            this.nonSystemModuleRefs = new ArrayList<ModuleRef>();
            for (ModuleRef moduleRef : allModuleRefsList) {
                if (moduleRef == null) continue;
                boolean isSystemModule = moduleRef.isSystemModule();
                if (isSystemModule && scanSystemModules) {
                    this.systemModuleRefs.add(moduleRef);
                    continue;
                }
                if (isSystemModule || !scanNonSystemModules) continue;
                this.nonSystemModuleRefs.add(moduleRef);
            }
        }
        if (log != null) {
            if (scanSystemModules) {
                LogNode sysSubLog = log.log("System modules found:");
                if (this.systemModuleRefs != null && !this.systemModuleRefs.isEmpty()) {
                    for (ModuleRef moduleRef : this.systemModuleRefs) {
                        sysSubLog.log(moduleRef.toString());
                    }
                } else {
                    sysSubLog.log("[None]");
                }
            } else {
                log.log("Scanning of system modules is not enabled");
            }
            if (scanNonSystemModules) {
                LogNode nonSysSubLog = log.log("Non-system modules found:");
                if (this.nonSystemModuleRefs != null && !this.nonSystemModuleRefs.isEmpty()) {
                    for (ModuleRef moduleRef : this.nonSystemModuleRefs) {
                        nonSysSubLog.log(moduleRef.toString());
                    }
                } else {
                    nonSysSubLog.log("[None]");
                }
            } else {
                log.log("Scanning of non-system modules is not enabled");
            }
        }
    }
}

