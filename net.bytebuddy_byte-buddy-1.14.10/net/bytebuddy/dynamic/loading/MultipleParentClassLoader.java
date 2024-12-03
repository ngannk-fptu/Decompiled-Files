/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.loading;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultipleParentClassLoader
extends InjectionClassLoader {
    private final List<? extends ClassLoader> parents;

    @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Must be invoked from targeting class loader type.")
    private static void doRegisterAsParallelCapable() {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
            method.setAccessible(true);
            method.invoke(null, new Object[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public MultipleParentClassLoader(List<? extends ClassLoader> parents) {
        this(ClassLoadingStrategy.BOOTSTRAP_LOADER, parents);
    }

    public MultipleParentClassLoader(@MaybeNull ClassLoader parent, List<? extends ClassLoader> parents) {
        this(parent, parents, true);
    }

    public MultipleParentClassLoader(@MaybeNull ClassLoader parent, List<? extends ClassLoader> parents, boolean sealed) {
        super(parent, sealed);
        this.parents = parents;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (ClassLoader classLoader : this.parents) {
            try {
                Class<?> type = classLoader.loadClass(name);
                if (resolve) {
                    this.resolveClass(type);
                }
                return type;
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        for (ClassLoader classLoader : this.parents) {
            URL url = classLoader.getResource(name);
            if (url == null) continue;
            return url;
        }
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        ArrayList<Enumeration<URL>> enumerations = new ArrayList<Enumeration<URL>>(this.parents.size() + 1);
        for (ClassLoader classLoader : this.parents) {
            enumerations.add(classLoader.getResources(name));
        }
        enumerations.add(super.getResources(name));
        return new CompoundEnumeration(enumerations);
    }

    @Override
    protected Map<String, Class<?>> doDefineClasses(Map<String, byte[]> typeDefinitions) {
        HashMap types = new HashMap();
        for (Map.Entry<String, byte[]> entry : typeDefinitions.entrySet()) {
            types.put(entry.getKey(), this.defineClass(entry.getKey(), entry.getValue(), 0, entry.getValue().length));
        }
        return types;
    }

    static {
        MultipleParentClassLoader.doRegisterAsParallelCapable();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Builder {
        private final boolean sealed;
        private final List<? extends ClassLoader> classLoaders;

        public Builder() {
            this(true);
        }

        public Builder(boolean sealed) {
            this(Collections.emptyList(), sealed);
        }

        private Builder(List<? extends ClassLoader> classLoaders, boolean sealed) {
            this.classLoaders = classLoaders;
            this.sealed = sealed;
        }

        public Builder append(Class<?> ... type) {
            return this.append((Collection<? extends Class<?>>)Arrays.asList(type));
        }

        public Builder append(Collection<? extends Class<?>> types) {
            ArrayList<ClassLoader> classLoaders = new ArrayList<ClassLoader>(types.size());
            for (Class<?> type : types) {
                classLoaders.add(type.getClassLoader());
            }
            return this.append((List<? extends ClassLoader>)classLoaders);
        }

        public Builder append(ClassLoader ... classLoader) {
            return this.append(Arrays.asList(classLoader));
        }

        public Builder append(List<? extends ClassLoader> classLoaders) {
            ArrayList<? extends ClassLoader> filtered = new ArrayList<ClassLoader>(this.classLoaders.size() + classLoaders.size());
            filtered.addAll(this.classLoaders);
            HashSet<? extends ClassLoader> registered = new HashSet<ClassLoader>(this.classLoaders);
            for (ClassLoader classLoader : classLoaders) {
                if (classLoader == null || !registered.add(classLoader)) continue;
                filtered.add(classLoader);
            }
            return new Builder(filtered, this.sealed);
        }

        public Builder appendMostSpecific(Class<?> ... type) {
            return this.appendMostSpecific((Collection<? extends Class<?>>)Arrays.asList(type));
        }

        public Builder appendMostSpecific(Collection<? extends Class<?>> types) {
            ArrayList<ClassLoader> classLoaders = new ArrayList<ClassLoader>(types.size());
            for (Class<?> type : types) {
                classLoaders.add(type.getClassLoader());
            }
            return this.appendMostSpecific((List<? extends ClassLoader>)classLoaders);
        }

        public Builder appendMostSpecific(ClassLoader ... classLoader) {
            return this.appendMostSpecific(Arrays.asList(classLoader));
        }

        /*
         * WARNING - void declaration
         */
        public Builder appendMostSpecific(List<? extends ClassLoader> classLoaders) {
            ArrayList<? extends ClassLoader> filtered = new ArrayList<ClassLoader>(this.classLoaders.size() + classLoaders.size());
            filtered.addAll(this.classLoaders);
            block0: for (ClassLoader classLoader : classLoaders) {
                if (classLoader == null) continue;
                ClassLoader candidate = classLoader;
                do {
                    Iterator iterator = filtered.iterator();
                    while (iterator.hasNext()) {
                        ClassLoader classLoader2 = (ClassLoader)iterator.next();
                        if (!classLoader2.equals(candidate)) continue;
                        iterator.remove();
                    }
                } while ((candidate = candidate.getParent()) != null);
                block3: for (ClassLoader classLoader3 : filtered) {
                    void var7_10;
                    while (!var7_10.equals(classLoader)) {
                        ClassLoader classLoader4 = var7_10.getParent();
                        if (classLoader4 != null) continue;
                        continue block3;
                    }
                    continue block0;
                }
                filtered.add(classLoader);
            }
            return new Builder(filtered, this.sealed);
        }

        public Builder filter(ElementMatcher<? super ClassLoader> matcher) {
            ArrayList<ClassLoader> classLoaders = new ArrayList<ClassLoader>(this.classLoaders.size());
            for (ClassLoader classLoader : this.classLoaders) {
                if (!matcher.matches(classLoader)) continue;
                classLoaders.add(classLoader);
            }
            return new Builder(classLoaders, this.sealed);
        }

        public ClassLoader build() {
            return this.classLoaders.size() == 1 ? this.classLoaders.get(0) : this.doBuild(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        }

        public ClassLoader build(ClassLoader parent) {
            return this.classLoaders.isEmpty() || this.classLoaders.size() == 1 && this.classLoaders.contains(parent) ? parent : this.filter(ElementMatchers.not(ElementMatchers.is(parent))).doBuild(parent);
        }

        @SuppressFBWarnings(value={"DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
        private ClassLoader doBuild(@MaybeNull ClassLoader parent) {
            return new MultipleParentClassLoader(parent, this.classLoaders, this.sealed);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            if (this.sealed != ((Builder)object).sealed) {
                return false;
            }
            return ((Object)this.classLoaders).equals(((Builder)object).classLoaders);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sealed) * 31 + ((Object)this.classLoaders).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class CompoundEnumeration
    implements Enumeration<URL> {
        private static final int FIRST = 0;
        private final List<Enumeration<URL>> enumerations;
        @UnknownNull
        private Enumeration<URL> current;

        protected CompoundEnumeration(List<Enumeration<URL>> enumerations) {
            this.enumerations = enumerations;
        }

        @Override
        public boolean hasMoreElements() {
            if (this.current != null && this.current.hasMoreElements()) {
                return true;
            }
            if (!this.enumerations.isEmpty()) {
                this.current = this.enumerations.remove(0);
                return this.hasMoreElements();
            }
            return false;
        }

        @Override
        @SuppressFBWarnings(value={"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"}, justification="Null reference is avoided by element check.")
        public URL nextElement() {
            if (this.hasMoreElements()) {
                return this.current.nextElement();
            }
            throw new NoSuchElementException();
        }
    }
}

