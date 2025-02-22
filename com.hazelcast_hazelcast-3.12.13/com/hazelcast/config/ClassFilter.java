/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassFilter {
    private static final String PROPERTY_CLASSNAME_LIMIT = "hazelcast.serialization.filter.classname.limit";
    private static final int CLASSNAME_LIMIT = Integer.getInteger("hazelcast.serialization.filter.classname.limit", 10000);
    private static final ILogger LOGGER = Logger.getLogger(ClassFilter.class);
    private final Set<String> classes;
    private final Set<String> packages;
    private final Set<String> prefixes;
    private AtomicBoolean warningLogged = new AtomicBoolean();

    public ClassFilter() {
        this.classes = Collections.newSetFromMap(new ConcurrentHashMap());
        this.packages = Collections.newSetFromMap(new ConcurrentHashMap());
        this.prefixes = Collections.newSetFromMap(new ConcurrentHashMap());
    }

    public ClassFilter(ClassFilter filter) {
        this.classes = Collections.newSetFromMap(new ConcurrentHashMap());
        this.classes.addAll(filter.classes);
        this.packages = Collections.newSetFromMap(new ConcurrentHashMap());
        this.packages.addAll(filter.packages);
        this.prefixes = Collections.newSetFromMap(new ConcurrentHashMap());
        this.prefixes.addAll(filter.prefixes);
        this.warningLogged = new AtomicBoolean(filter.warningLogged.get());
    }

    public Set<String> getClasses() {
        return Collections.unmodifiableSet(this.classes);
    }

    public Set<String> getPackages() {
        return Collections.unmodifiableSet(this.packages);
    }

    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(this.prefixes);
    }

    public ClassFilter addClasses(String ... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            this.classes.add(name);
        }
        return this;
    }

    public ClassFilter setClasses(Collection<String> names) {
        Preconditions.checkNotNull(names);
        this.classes.clear();
        this.classes.addAll(names);
        return this;
    }

    public ClassFilter addPackages(String ... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            this.packages.add(name);
        }
        return this;
    }

    public ClassFilter setPackages(Collection<String> names) {
        Preconditions.checkNotNull(names);
        this.packages.clear();
        this.packages.addAll(names);
        return this;
    }

    public ClassFilter addPrefixes(String ... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            this.prefixes.add(name);
        }
        return this;
    }

    public ClassFilter setPrefixes(Collection<String> names) {
        Preconditions.checkNotNull(names);
        this.prefixes.clear();
        this.prefixes.addAll(names);
        return this;
    }

    public boolean isEmpty() {
        return this.classes.isEmpty() && this.packages.isEmpty() && this.prefixes.isEmpty();
    }

    public boolean isListed(String className) {
        int dotPosition;
        if (this.classes.contains(className)) {
            return true;
        }
        if (!this.packages.isEmpty() && (dotPosition = className.lastIndexOf(".")) > 0 && this.checkPackage(className, className.substring(0, dotPosition))) {
            return true;
        }
        return this.checkPrefixes(className);
    }

    private boolean checkPackage(String className, String packageName) {
        if (this.packages.contains(packageName)) {
            this.cacheClassname(className);
            return true;
        }
        return false;
    }

    private void cacheClassname(String className) {
        if (this.classes.size() < CLASSNAME_LIMIT) {
            this.classes.add(className);
        } else if (this.warningLogged.compareAndSet(false, true)) {
            LOGGER.warning(String.format("The class names collection size reached its limit. Optimizations for package names checks will not optimize next usages. You can control the class names collection size limit by setting system property '%s'. Actual value is %d.", PROPERTY_CLASSNAME_LIMIT, CLASSNAME_LIMIT));
        }
    }

    private boolean checkPrefixes(String className) {
        for (String prefix : this.prefixes) {
            if (!className.startsWith(prefix)) continue;
            this.cacheClassname(className);
            return true;
        }
        return false;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.classes.hashCode();
        result = 31 * result + this.packages.hashCode();
        result = 31 * result + this.prefixes.hashCode();
        result = 31 * result + (this.warningLogged.get() ? 0 : 1);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ClassFilter other = (ClassFilter)obj;
        return this.classes.equals(other.classes) && this.packages.equals(other.packages) && this.prefixes.equals(other.prefixes) && this.warningLogged.get() == other.warningLogged.get();
    }

    public String toString() {
        return "ClassFilter{classes=" + this.classes + ", packages=" + this.packages + ", prefixes=" + this.prefixes + "}";
    }
}

