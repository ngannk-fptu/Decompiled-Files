/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.TypeAdapter;

public class AntTypeDefinition {
    private String name;
    private Class<?> clazz;
    private Class<?> adapterClass;
    private Class<?> adaptToClass;
    private String className;
    private ClassLoader classLoader;
    private boolean restrict = false;

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public boolean isRestrict() {
        return this.restrict;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setClass(Class<?> clazz) {
        this.clazz = clazz;
        if (clazz == null) {
            return;
        }
        this.classLoader = this.classLoader == null ? clazz.getClassLoader() : this.classLoader;
        this.className = this.className == null ? clazz.getName() : this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setAdapterClass(Class<?> adapterClass) {
        this.adapterClass = adapterClass;
    }

    public void setAdaptToClass(Class<?> adaptToClass) {
        this.adaptToClass = adaptToClass;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Class<?> getExposedClass(Project project) {
        Class<?> z;
        if (this.adaptToClass != null && ((z = this.getTypeClass(project)) == null || this.adaptToClass.isAssignableFrom(z))) {
            return z;
        }
        return this.adapterClass == null ? this.getTypeClass(project) : this.adapterClass;
    }

    public Class<?> getTypeClass(Project project) {
        try {
            return this.innerGetTypeClass();
        }
        catch (NoClassDefFoundError ncdfe) {
            project.log("Could not load a dependent class (" + ncdfe.getMessage() + ") for type " + this.name, 4);
        }
        catch (ClassNotFoundException cnfe) {
            project.log("Could not load class (" + this.className + ") for type " + this.name, 4);
        }
        return null;
    }

    public Class<?> innerGetTypeClass() throws ClassNotFoundException {
        if (this.clazz != null) {
            return this.clazz;
        }
        this.clazz = this.classLoader == null ? Class.forName(this.className) : this.classLoader.loadClass(this.className);
        return this.clazz;
    }

    public Object create(Project project) {
        return this.icreate(project);
    }

    private Object icreate(Project project) {
        Class<?> c = this.getTypeClass(project);
        if (c == null) {
            return null;
        }
        Object o = this.createAndSet(project, c);
        if (this.adapterClass == null || this.adaptToClass != null && this.adaptToClass.isAssignableFrom(o.getClass())) {
            return o;
        }
        TypeAdapter adapterObject = (TypeAdapter)this.createAndSet(project, this.adapterClass);
        adapterObject.setProxy(o);
        return adapterObject;
    }

    public void checkClass(Project project) {
        if (this.clazz == null) {
            this.clazz = this.getTypeClass(project);
            if (this.clazz == null) {
                throw new BuildException("Unable to create class for " + this.getName());
            }
        }
        if (!(this.adapterClass == null || this.adaptToClass != null && this.adaptToClass.isAssignableFrom(this.clazz))) {
            TypeAdapter adapter = (TypeAdapter)this.createAndSet(project, this.adapterClass);
            adapter.checkProxyClass(this.clazz);
        }
    }

    private Object createAndSet(Project project, Class<?> c) {
        try {
            return this.innerCreateAndSet(c, project);
        }
        catch (InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            throw new BuildException("Could not create type " + this.name + " due to " + t, t);
        }
        catch (NoClassDefFoundError ncdfe) {
            String msg = "Type " + this.name + ": A class needed by class " + c + " cannot be found: " + ncdfe.getMessage();
            throw new BuildException(msg, ncdfe);
        }
        catch (NoSuchMethodException nsme) {
            throw new BuildException("Could not create type " + this.name + " as the class " + c + " has no compatible constructor");
        }
        catch (InstantiationException nsme) {
            throw new BuildException("Could not create type " + this.name + " as the class " + c + " is abstract");
        }
        catch (IllegalAccessException e) {
            throw new BuildException("Could not create type " + this.name + " as the constructor " + c + " is not accessible");
        }
        catch (Throwable t) {
            throw new BuildException("Could not create type " + this.name + " due to " + t, t);
        }
    }

    public <T> T innerCreateAndSet(Class<T> newclass, Project project) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object[] objectArray;
        Constructor<T> ctor;
        boolean noArg = false;
        try {
            ctor = newclass.getConstructor(new Class[0]);
            noArg = true;
        }
        catch (NoSuchMethodException nse) {
            ctor = newclass.getConstructor(Project.class);
            noArg = false;
        }
        if (noArg) {
            objectArray = new Object[]{};
        } else {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = project;
        }
        T o = ctor.newInstance(objectArray);
        project.setProjectReference(o);
        return o;
    }

    public boolean sameDefinition(AntTypeDefinition other, Project project) {
        return other != null && other.getClass() == this.getClass() && other.getTypeClass(project).equals(this.getTypeClass(project)) && other.getExposedClass(project).equals(this.getExposedClass(project)) && other.restrict == this.restrict && other.adapterClass == this.adapterClass && other.adaptToClass == this.adaptToClass;
    }

    public boolean similarDefinition(AntTypeDefinition other, Project project) {
        ClassLoader newLoader;
        if (!(other != null && this.getClass() == other.getClass() && this.getClassName().equals(other.getClassName()) && this.extractClassname(this.adapterClass).equals(this.extractClassname(other.adapterClass)) && this.extractClassname(this.adaptToClass).equals(this.extractClassname(other.adaptToClass)) && this.restrict == other.restrict)) {
            return false;
        }
        ClassLoader oldLoader = other.getClassLoader();
        return oldLoader == (newLoader = this.getClassLoader()) || oldLoader instanceof AntClassLoader && newLoader instanceof AntClassLoader && ((AntClassLoader)oldLoader).getClasspath().equals(((AntClassLoader)newLoader).getClasspath());
    }

    private String extractClassname(Class<?> c) {
        return c == null ? "<null>" : c.getName();
    }
}

