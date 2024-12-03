/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.apt.util.JrtFileSystem;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;

public class ModuleLocationHandler {
    Map<JavaFileManager.Location, LocationContainer> containers = new HashMap<JavaFileManager.Location, LocationContainer>();

    ModuleLocationHandler() {
    }

    public void newSystemLocation(JavaFileManager.Location loc, ClasspathJrt cp) throws IOException {
        SystemLocationContainer systemLocationWrapper = new SystemLocationContainer((JavaFileManager.Location)StandardLocation.SYSTEM_MODULES, cp);
        this.containers.put(loc, systemLocationWrapper);
    }

    public void newSystemLocation(JavaFileManager.Location loc, JrtFileSystem jrt) throws IOException {
        SystemLocationContainer systemLocationWrapper = new SystemLocationContainer((JavaFileManager.Location)StandardLocation.SYSTEM_MODULES, jrt);
        this.containers.put(loc, systemLocationWrapper);
    }

    public LocationWrapper getLocation(JavaFileManager.Location loc, String moduleName) {
        LocationContainer forwarder;
        if (loc instanceof LocationWrapper) {
            loc = ((LocationWrapper)loc).loc;
        }
        if ((forwarder = this.containers.get(loc)) != null) {
            return forwarder.get(moduleName);
        }
        return null;
    }

    public JavaFileManager.Location getLocation(JavaFileManager.Location loc, Path path) {
        LocationContainer forwarder = this.containers.get(loc);
        if (forwarder != null) {
            return forwarder.get(path);
        }
        return null;
    }

    public LocationContainer getLocation(JavaFileManager.Location location) {
        return this.containers.get(location);
    }

    public void setLocation(JavaFileManager.Location location, Iterable<? extends Path> paths) {
        LocationContainer container = this.containers.get(location);
        if (container == null) {
            container = new LocationContainer(location);
            this.containers.put(location, container);
        }
        container.setPaths(paths);
    }

    public void setLocation(JavaFileManager.Location location, String moduleName, Iterable<? extends Path> paths) {
        LocationWrapper wrapper = null;
        LocationContainer container = this.containers.get(location);
        if (container != null) {
            wrapper = container.get(moduleName);
        } else {
            container = new LocationContainer(location);
            this.containers.put(location, container);
        }
        if (wrapper == null) {
            if (moduleName.equals("")) {
                wrapper = new LocationWrapper(location, location.isOutputLocation(), paths);
            } else {
                wrapper = new ModuleLocationWrapper(location, moduleName, location.isOutputLocation(), paths);
                for (Path path : paths) {
                    container.put(path, wrapper);
                }
            }
        } else {
            wrapper.setPaths(paths);
        }
        container.put(moduleName, wrapper);
    }

    public Iterable<Set<JavaFileManager.Location>> listLocationsForModules(JavaFileManager.Location location) {
        LocationContainer locationContainer = this.containers.get(location);
        if (locationContainer == null) {
            return Collections.emptyList();
        }
        HashSet<LocationWrapper> set = new HashSet<LocationWrapper>(locationContainer.locationNames.values());
        List<Set<JavaFileManager.Location>> singletonList = Collections.singletonList(set);
        return singletonList;
    }

    public void close() {
        Collection<LocationContainer> values = this.containers.values();
        for (LocationContainer locationContainer : values) {
            locationContainer.clear();
        }
    }

    class LocationContainer
    extends LocationWrapper {
        Map<String, LocationWrapper> locationNames;
        Map<Path, LocationWrapper> locationPaths;

        LocationContainer(JavaFileManager.Location loc) {
            this.loc = loc;
            this.locationNames = new HashMap<String, LocationWrapper>();
            this.locationPaths = new HashMap<Path, LocationWrapper>();
        }

        LocationWrapper get(String moduleName) {
            return this.locationNames.get(moduleName);
        }

        void put(String moduleName, LocationWrapper impl) {
            this.locationNames.put(moduleName, impl);
            this.paths = null;
        }

        void put(Path path, LocationWrapper impl) {
            this.locationPaths.put(path, impl);
            this.paths = null;
        }

        JavaFileManager.Location get(Path path) {
            return this.locationPaths.get(path);
        }

        @Override
        void setPaths(Iterable<? extends Path> paths) {
            super.setPaths(paths);
            this.clear();
        }

        @Override
        Iterable<? extends Path> getPaths() {
            if (this.paths != null) {
                return this.paths;
            }
            return this.locationPaths.keySet();
        }

        public void clear() {
            this.locationNames.clear();
            this.locationPaths.clear();
        }
    }

    class LocationWrapper
    implements JavaFileManager.Location {
        JavaFileManager.Location loc;
        boolean output;
        List<? extends Path> paths;

        LocationWrapper() {
        }

        public LocationWrapper(JavaFileManager.Location loc, boolean output, Iterable<? extends Path> paths) {
            this.loc = loc;
            this.output = output;
            this.setPaths(paths);
        }

        @Override
        public String getName() {
            return this.loc.getName();
        }

        @Override
        public boolean isOutputLocation() {
            return this.output;
        }

        Iterable<? extends Path> getPaths() {
            return this.paths;
        }

        void setPaths(Iterable<? extends Path> paths) {
            if (paths == null) {
                this.paths = null;
            } else {
                ArrayList<Path> newPaths = new ArrayList<Path>();
                for (Path path : paths) {
                    newPaths.add(path);
                }
                this.paths = Collections.unmodifiableList(newPaths);
            }
        }

        public String toString() {
            return String.valueOf(this.loc.toString()) + "[]";
        }
    }

    class ModuleLocationWrapper
    extends LocationWrapper {
        String modName;

        public ModuleLocationWrapper(JavaFileManager.Location loc, String mod, boolean output, Iterable<? extends Path> paths) {
            super(loc, output, paths);
            this.modName = mod;
        }

        @Override
        public String getName() {
            return String.valueOf(this.loc.getName()) + "[" + this.modName + "]";
        }

        @Override
        public boolean isOutputLocation() {
            return this.output;
        }

        @Override
        Iterable<? extends Path> getPaths() {
            return this.paths;
        }

        @Override
        public String toString() {
            return String.valueOf(this.loc.toString()) + "[" + this.modName + "]";
        }
    }

    class SystemLocationContainer
    extends LocationContainer {
        public SystemLocationContainer(JavaFileManager.Location loc, JrtFileSystem jrt) throws IOException {
            super(loc);
            jrt.initialize();
            HashMap<String, Path> modulePathMap = jrt.modulePathMap;
            Set<String> keySet = modulePathMap.keySet();
            for (String mod : keySet) {
                Path path = jrt.file.toPath();
                ModuleLocationWrapper wrapper = new ModuleLocationWrapper(loc, mod, false, Collections.singletonList(path));
                this.locationNames.put(mod, wrapper);
                this.locationPaths.put(path, wrapper);
            }
        }

        public SystemLocationContainer(JavaFileManager.Location loc, ClasspathJrt cp) throws IOException {
            this(loc, new JrtFileSystem(cp.file));
        }
    }
}

