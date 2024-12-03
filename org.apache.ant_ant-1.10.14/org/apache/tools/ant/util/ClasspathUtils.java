/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class ClasspathUtils {
    public static final String REUSE_LOADER_REF = "ant.reuse.loader";

    public static ClassLoader getClassLoaderForPath(Project p, Reference ref) {
        return ClasspathUtils.getClassLoaderForPath(p, ref, false);
    }

    public static ClassLoader getClassLoaderForPath(Project p, Reference ref, boolean reverseLoader) {
        String pathId = ref.getRefId();
        Object path = p.getReference(pathId);
        if (!(path instanceof Path)) {
            throw new BuildException("The specified classpathref %s does not reference a Path.", pathId);
        }
        String loaderId = "ant.loader." + pathId;
        return ClasspathUtils.getClassLoaderForPath(p, (Path)path, loaderId, reverseLoader);
    }

    public static ClassLoader getClassLoaderForPath(Project p, Path path, String loaderId) {
        return ClasspathUtils.getClassLoaderForPath(p, path, loaderId, false);
    }

    public static ClassLoader getClassLoaderForPath(Project p, Path path, String loaderId, boolean reverseLoader) {
        return ClasspathUtils.getClassLoaderForPath(p, path, loaderId, reverseLoader, ClasspathUtils.isMagicPropertySet(p));
    }

    public static ClassLoader getClassLoaderForPath(Project p, Path path, String loaderId, boolean reverseLoader, boolean reuseLoader) {
        ClassLoader cl = null;
        if (loaderId != null && reuseLoader) {
            Object reusedLoader = p.getReference(loaderId);
            if (reusedLoader != null && !(reusedLoader instanceof ClassLoader)) {
                throw new BuildException("The specified loader id %s does not reference a class loader", loaderId);
            }
            cl = (ClassLoader)reusedLoader;
        }
        if (cl == null) {
            cl = ClasspathUtils.getUniqueClassLoaderForPath(p, path, reverseLoader);
            if (loaderId != null && reuseLoader) {
                p.addReference(loaderId, cl);
            }
        }
        return cl;
    }

    public static ClassLoader getUniqueClassLoaderForPath(Project p, Path path, boolean reverseLoader) {
        AntClassLoader acl = p.createClassLoader(path);
        if (reverseLoader) {
            acl.setParentFirst(false);
            acl.addJavaLibraries();
        }
        return acl;
    }

    public static Object newInstance(String className, ClassLoader userDefinedLoader) {
        return ClasspathUtils.newInstance(className, userDefinedLoader, Object.class);
    }

    public static <T> T newInstance(String className, ClassLoader userDefinedLoader, Class<T> expectedType) {
        try {
            Class<?> clazz = Class.forName(className, true, userDefinedLoader);
            if (Modifier.isAbstract(clazz.getModifiers())) {
                throw new BuildException("Abstract class " + className);
            }
            Object o = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            if (!expectedType.isInstance(o)) {
                throw new BuildException("Class of unexpected Type: %s expected : %s", className, expectedType);
            }
            return (T)o;
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("Class not found: " + className, e);
        }
        catch (InstantiationException e) {
            throw new BuildException("Could not instantiate " + className + ". Specified class should have a no argument constructor.", e);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new BuildException("Could not instantiate " + className + ". Specified class should have a public constructor.", e);
        }
        catch (LinkageError e) {
            throw new BuildException("Class " + className + " could not be loaded because of an invalid dependency.", e);
        }
    }

    public static Delegate getDelegate(ProjectComponent component) {
        return new Delegate(component);
    }

    private static boolean isMagicPropertySet(Project p) {
        return p.getProperty(REUSE_LOADER_REF) != null;
    }

    private ClasspathUtils() {
    }

    public static class Delegate {
        private final ProjectComponent component;
        private Path classpath;
        private String classpathId;
        private String className;
        private String loaderId;
        private boolean reverseLoader = false;

        Delegate(ProjectComponent component) {
            this.component = component;
        }

        public void setClasspath(Path classpath) {
            if (this.classpath == null) {
                this.classpath = classpath;
            } else {
                this.classpath.append(classpath);
            }
        }

        public Path createClasspath() {
            if (this.classpath == null) {
                this.classpath = new Path(this.component.getProject());
            }
            return this.classpath.createPath();
        }

        public void setClassname(String fcqn) {
            this.className = fcqn;
        }

        public void setClasspathref(Reference r) {
            this.classpathId = r.getRefId();
            this.createClasspath().setRefid(r);
        }

        public void setReverseLoader(boolean reverseLoader) {
            this.reverseLoader = reverseLoader;
        }

        public void setLoaderRef(Reference r) {
            this.loaderId = r.getRefId();
        }

        public ClassLoader getClassLoader() {
            return ClasspathUtils.getClassLoaderForPath(this.getContextProject(), this.classpath, this.getClassLoadId(), this.reverseLoader, this.loaderId != null || ClasspathUtils.isMagicPropertySet(this.getContextProject()));
        }

        private Project getContextProject() {
            return this.component.getProject();
        }

        public String getClassLoadId() {
            if (this.loaderId == null && this.classpathId != null) {
                return "ant.loader." + this.classpathId;
            }
            return this.loaderId;
        }

        public Object newInstance() {
            return ClasspathUtils.newInstance(this.className, this.getClassLoader());
        }

        public Path getClasspath() {
            return this.classpath;
        }

        public boolean isReverseLoader() {
            return this.reverseLoader;
        }
    }
}

