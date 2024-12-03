/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="class")
public class ClassTool
extends SafeConfig {
    public static final String INSPECT_KEY = "inspect";
    public static final String SHOW_DEPRECATED_KEY = "showDeprecated";
    protected Log log;
    protected Class type;
    protected List<MethodSub> methods;
    protected List<ConstructorSub> constructors;
    protected List<FieldSub> fields;
    private boolean showDeprecated = false;

    public ClassTool() {
        this.setType(Object.class);
    }

    protected ClassTool(ClassTool tool, Class type) {
        this.setType(type);
        if (tool == null) {
            throw new IllegalArgumentException("parent tool must not be null");
        }
        this.log = tool.log;
        this.showDeprecated = tool.showDeprecated;
        this.setSafeMode(tool.isSafeMode());
        this.setLockConfig(tool.isConfigLocked());
    }

    @Override
    protected void configure(ValueParser values) {
        this.log = (Log)values.getValue("log");
        this.showDeprecated = values.getBoolean(SHOW_DEPRECATED_KEY, this.showDeprecated);
        String classname = values.getString(INSPECT_KEY);
        if (classname != null) {
            this.setType(this.toClass(classname));
        }
    }

    private Class toClass(String name) {
        try {
            return ClassUtils.getClass(name);
        }
        catch (Exception e) {
            if (this.log != null) {
                this.log.error((Object)("Could not load Class for " + name));
            }
            return null;
        }
    }

    protected void setType(Class type) {
        if (type == null) {
            throw new IllegalArgumentException("target type is null or invalid");
        }
        this.type = type;
    }

    protected static boolean isDeprecated(AnnotatedElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    public boolean getShowDeprecated() {
        return this.showDeprecated;
    }

    public Class getType() {
        return this.type;
    }

    public ClassTool inspect(String name) {
        if (name == null) {
            return null;
        }
        return this.inspect(this.toClass(name));
    }

    public ClassTool inspect(Object obj) {
        if (obj == null) {
            return null;
        }
        return this.inspect(obj.getClass());
    }

    public ClassTool getSuper() {
        Class sup = this.getType().getSuperclass();
        if (sup == null) {
            return null;
        }
        return this.inspect(sup);
    }

    public ClassTool inspect(Class type) {
        if (type == null) {
            return null;
        }
        ClassTool tool = new ClassTool(this, type);
        if (this.isSafeMode() && !tool.isPublic()) {
            return null;
        }
        return tool;
    }

    public String getPackage() {
        return this.getType().getPackage().getName();
    }

    public String getName() {
        return this.getType().getSimpleName();
    }

    public String getFullName() {
        return this.getType().getName();
    }

    public boolean supportsNewInstance() {
        try {
            this.type.newInstance();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean isDeprecated() {
        return ClassTool.isDeprecated(this.getType());
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.getType().getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.getType().getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.getType().getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.getType().getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.getType().getModifiers());
    }

    public boolean isInterface() {
        return Modifier.isInterface(this.getType().getModifiers());
    }

    public boolean isStrict() {
        return Modifier.isStrict(this.getType().getModifiers());
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.getType().getModifiers());
    }

    public List<MethodSub> getMethods() {
        if (this.methods == null) {
            Method[] declared = this.getType().getDeclaredMethods();
            ArrayList<MethodSub> subs = new ArrayList<MethodSub>(declared.length);
            for (Method method : declared) {
                MethodSub sub = new MethodSub(method);
                if (this.isSafeMode() && !sub.isPublic() || !this.showDeprecated && sub.isDeprecated()) continue;
                subs.add(sub);
            }
            Collections.sort(subs);
            this.methods = Collections.unmodifiableList(subs);
        }
        return this.methods;
    }

    public List<ConstructorSub> getConstructors() {
        if (this.constructors == null) {
            Constructor<?>[] declared = this.getType().getDeclaredConstructors();
            ArrayList<ConstructorSub> subs = new ArrayList<ConstructorSub>(declared.length);
            for (Constructor<?> constructor : declared) {
                ConstructorSub sub = new ConstructorSub(constructor);
                if (this.isSafeMode() && !sub.isPublic() || !this.showDeprecated && sub.isDeprecated()) continue;
                subs.add(sub);
            }
            Collections.sort(subs);
            this.constructors = Collections.unmodifiableList(subs);
        }
        return this.constructors;
    }

    public List<FieldSub> getFields() {
        if (this.fields == null) {
            Field[] declared = this.getType().getDeclaredFields();
            ArrayList<FieldSub> subs = new ArrayList<FieldSub>(declared.length);
            for (Field field : declared) {
                FieldSub sub = new FieldSub(field);
                if (this.isSafeMode() && !sub.isPublic() || !this.showDeprecated && sub.isDeprecated()) continue;
                subs.add(sub);
            }
            Collections.sort(subs);
            this.fields = Collections.unmodifiableList(subs);
        }
        return this.fields;
    }

    public Set<Class> getTypes() {
        HashSet<Class> types = new HashSet<Class>();
        for (MethodSub method : this.getMethods()) {
            if (this.isSafeMode() && !method.isPublic()) continue;
            if (!method.isVoid()) {
                this.addType(types, method.getReturns());
            }
            for (Class type : method.getParameters()) {
                this.addType(types, type);
            }
        }
        for (ConstructorSub constructor : this.getConstructors()) {
            if (this.isSafeMode() && !constructor.isPublic()) continue;
            for (Class type : constructor.getParameters()) {
                this.addType(types, type);
            }
        }
        for (FieldSub field : this.getFields()) {
            if (this.isSafeMode() && !field.isPublic()) continue;
            this.addType(types, field.getType());
        }
        return types;
    }

    private void addType(Set<Class> types, Class type) {
        if (type.isArray()) {
            type = type.getComponentType();
        }
        if (!type.isPrimitive()) {
            types.add(type);
        }
    }

    public List<Annotation> getAnnotations() {
        return Arrays.asList(this.getType().getAnnotations());
    }

    public String toString() {
        return this.getType().toString();
    }

    public static abstract class Sub<T extends Sub>
    implements Comparable<T> {
        protected abstract AnnotatedElement getElement();

        protected abstract int getModifiers();

        protected abstract String getSubType();

        public abstract String getName();

        public abstract String getUniqueName();

        public abstract String getJavadocRef();

        public List<Annotation> getAnnotations() {
            return Arrays.asList(this.getElement().getAnnotations());
        }

        public boolean isDeprecated() {
            return ClassTool.isDeprecated(this.getElement());
        }

        public boolean isPublic() {
            return Modifier.isPublic(this.getModifiers());
        }

        public boolean isProtected() {
            return Modifier.isProtected(this.getModifiers());
        }

        public boolean isPrivate() {
            return Modifier.isPrivate(this.getModifiers());
        }

        public boolean isStatic() {
            return Modifier.isStatic(this.getModifiers());
        }

        public boolean isFinal() {
            return Modifier.isFinal(this.getModifiers());
        }

        public boolean isInterface() {
            return Modifier.isInterface(this.getModifiers());
        }

        public boolean isNative() {
            return Modifier.isNative(this.getModifiers());
        }

        public boolean isStrict() {
            return Modifier.isStrict(this.getModifiers());
        }

        public boolean isSynchronized() {
            return Modifier.isSynchronized(this.getModifiers());
        }

        public boolean isTransient() {
            return Modifier.isTransient(this.getModifiers());
        }

        public boolean isVolatile() {
            return Modifier.isVolatile(this.getModifiers());
        }

        public boolean isAbstract() {
            return Modifier.isAbstract(this.getModifiers());
        }

        @Override
        public int compareTo(T that) {
            return this.getUniqueName().compareTo(((Sub)that).getUniqueName());
        }

        public int hashCode() {
            return this.getUniqueName().hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof Sub) {
                Sub that = (Sub)obj;
                return this.getUniqueName().equals(that.getUniqueName());
            }
            return false;
        }

        public String toString() {
            return this.getSubType() + ' ' + this.getJavadocRef();
        }
    }

    public static abstract class CallableSub<T extends CallableSub>
    extends Sub<T> {
        protected String uniqueName;
        protected String javadocRef;
        protected String signature;

        public abstract Class[] getParameters();

        public abstract boolean isVarArgs();

        public boolean takesParameters() {
            return this.getParameterCount() > 0;
        }

        public int getParameterCount() {
            return this.getParameters().length;
        }

        @Override
        public String getUniqueName() {
            if (this.uniqueName == null) {
                Class[] params = this.getParameters();
                if (params.length == 0) {
                    this.uniqueName = this.getName();
                } else {
                    StringBuilder out = new StringBuilder(30);
                    out.append(this.getName());
                    out.append('_');
                    for (int i = 0; i < params.length; ++i) {
                        Class param = params[i];
                        if (param.isArray()) {
                            out.append(param.getComponentType().getSimpleName());
                            if (i == params.length - 1 && this.isVarArgs()) {
                                out.append("VarArgs");
                                continue;
                            }
                            out.append("Array");
                            continue;
                        }
                        out.append(param.getSimpleName());
                    }
                    this.uniqueName = out.toString();
                }
            }
            return this.uniqueName;
        }

        public String getSignature() {
            if (this.signature == null) {
                this.signature = this.signature(false);
            }
            return this.signature;
        }

        @Override
        public String getJavadocRef() {
            if (this.javadocRef == null) {
                this.javadocRef = this.signature(true);
            }
            return this.javadocRef;
        }

        protected String signature(boolean fullNames) {
            Class[] params = this.getParameters();
            if (params.length == 0) {
                return this.getName() + "()";
            }
            StringBuilder out = new StringBuilder(30);
            out.append(this.getName());
            out.append('(');
            boolean first = true;
            for (int i = 0; i < params.length; ++i) {
                Class param = params[i];
                if (first) {
                    first = false;
                } else {
                    out.append(',');
                }
                if (param.isArray()) {
                    if (fullNames) {
                        out.append(param.getComponentType().getName());
                    } else {
                        out.append(param.getComponentType().getSimpleName());
                    }
                    if (i == params.length - 1 && this.isVarArgs()) {
                        out.append("...");
                        continue;
                    }
                    out.append("[]");
                    continue;
                }
                if (fullNames) {
                    out.append(param.getName());
                    continue;
                }
                out.append(param.getSimpleName());
            }
            out.append(')');
            return out.toString();
        }
    }

    public static class MethodSub
    extends CallableSub<MethodSub> {
        protected Method method;

        public MethodSub(Method method) {
            this.method = method;
        }

        @Override
        protected AnnotatedElement getElement() {
            return this.method;
        }

        @Override
        public String getName() {
            return this.method.getName();
        }

        public String getPropertyName() {
            String name = this.getName();
            switch (this.getParameterCount()) {
                case 0: {
                    if (name.startsWith("get") && name.length() > 3) {
                        return this.uncapitalize(name.substring(3, name.length()));
                    }
                    if (!name.startsWith("is") || name.length() <= 2) break;
                    return this.uncapitalize(name.substring(2, name.length()));
                }
                case 1: {
                    if (!name.startsWith("set") || name.length() <= 3) break;
                    return this.uncapitalize(name.substring(3, name.length()));
                }
            }
            return null;
        }

        private String uncapitalize(String string) {
            if (string.length() > 1) {
                StringBuilder out = new StringBuilder(string.length());
                out.append(string.substring(0, 1).toLowerCase());
                out.append(string.substring(1, string.length()));
                return out.toString();
            }
            return string.toLowerCase();
        }

        @Override
        public boolean isVarArgs() {
            return this.method.isVarArgs();
        }

        public boolean isVoid() {
            return this.getReturns() == Void.TYPE;
        }

        public Class getReturns() {
            return this.method.getReturnType();
        }

        @Override
        public Class[] getParameters() {
            return this.method.getParameterTypes();
        }

        @Override
        protected int getModifiers() {
            return this.method.getModifiers();
        }

        @Override
        protected String getSubType() {
            return "method";
        }
    }

    public static class ConstructorSub
    extends CallableSub<ConstructorSub> {
        protected Constructor constructor;

        public ConstructorSub(Constructor constructor) {
            this.constructor = constructor;
        }

        @Override
        protected AnnotatedElement getElement() {
            return this.constructor;
        }

        @Override
        public String getName() {
            return this.constructor.getDeclaringClass().getSimpleName();
        }

        @Override
        public Class[] getParameters() {
            return this.constructor.getParameterTypes();
        }

        @Override
        public boolean isVarArgs() {
            return this.constructor.isVarArgs();
        }

        @Override
        protected int getModifiers() {
            return this.constructor.getModifiers();
        }

        @Override
        protected String getSubType() {
            return "constructor";
        }
    }

    public static class FieldSub
    extends Sub<FieldSub> {
        protected Field field;

        public FieldSub(Field field) {
            this.field = field;
        }

        @Override
        protected AnnotatedElement getElement() {
            return this.field;
        }

        @Override
        public String getName() {
            return this.field.getName();
        }

        @Override
        public String getUniqueName() {
            return this.field.getName();
        }

        @Override
        public String getJavadocRef() {
            return this.field.getName();
        }

        public Class getType() {
            return this.field.getType();
        }

        public Object getStaticValue() {
            if (this.isStatic()) {
                try {
                    return this.field.get(null);
                }
                catch (IllegalAccessException illegalAccessException) {
                    // empty catch block
                }
            }
            return null;
        }

        @Override
        protected int getModifiers() {
            return this.field.getModifiers();
        }

        @Override
        protected String getSubType() {
            return "field";
        }
    }
}

