/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.type;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeBindings {
    private static final JavaType[] NO_TYPES = new JavaType[0];
    public static final JavaType UNBOUND = new SimpleType(Object.class);
    protected final TypeFactory _typeFactory;
    protected final JavaType _contextType;
    protected final Class<?> _contextClass;
    protected Map<String, JavaType> _bindings;
    protected HashSet<String> _placeholders;
    private final TypeBindings _parentBindings;

    public TypeBindings(TypeFactory typeFactory, Class<?> cc) {
        this(typeFactory, null, cc, null);
    }

    public TypeBindings(TypeFactory typeFactory, JavaType type) {
        this(typeFactory, null, type.getRawClass(), type);
    }

    public TypeBindings childInstance() {
        return new TypeBindings(this._typeFactory, this, this._contextClass, this._contextType);
    }

    private TypeBindings(TypeFactory tf, TypeBindings parent, Class<?> cc, JavaType type) {
        this._typeFactory = tf;
        this._parentBindings = parent;
        this._contextClass = cc;
        this._contextType = type;
    }

    public JavaType resolveType(Class<?> cls) {
        return this._typeFactory._constructType(cls, this);
    }

    public JavaType resolveType(Type type) {
        return this._typeFactory._constructType(type, this);
    }

    public int getBindingCount() {
        if (this._bindings == null) {
            this._resolve();
        }
        return this._bindings.size();
    }

    public JavaType findType(String name) {
        Class<?> enclosing;
        JavaType t;
        if (this._bindings == null) {
            this._resolve();
        }
        if ((t = this._bindings.get(name)) != null) {
            return t;
        }
        if (this._placeholders != null && this._placeholders.contains(name)) {
            return UNBOUND;
        }
        if (this._parentBindings != null) {
            return this._parentBindings.findType(name);
        }
        if (this._contextClass != null && (enclosing = this._contextClass.getEnclosingClass()) != null && !Modifier.isStatic(this._contextClass.getModifiers())) {
            return UNBOUND;
        }
        String className = this._contextClass != null ? this._contextClass.getName() : (this._contextType != null ? this._contextType.toString() : "UNKNOWN");
        throw new IllegalArgumentException("Type variable '" + name + "' can not be resolved (with context of class " + className + ")");
    }

    public void addBinding(String name, JavaType type) {
        if (this._bindings == null || this._bindings.size() == 0) {
            this._bindings = new LinkedHashMap<String, JavaType>();
        }
        this._bindings.put(name, type);
    }

    public JavaType[] typesAsArray() {
        if (this._bindings == null) {
            this._resolve();
        }
        if (this._bindings.size() == 0) {
            return NO_TYPES;
        }
        return this._bindings.values().toArray(new JavaType[this._bindings.size()]);
    }

    protected void _resolve() {
        int count;
        this._resolveBindings(this._contextClass);
        if (this._contextType != null && (count = this._contextType.containedTypeCount()) > 0) {
            if (this._bindings == null) {
                this._bindings = new LinkedHashMap<String, JavaType>();
            }
            for (int i = 0; i < count; ++i) {
                String name = this._contextType.containedTypeName(i);
                JavaType type = this._contextType.containedType(i);
                this._bindings.put(name, type);
            }
        }
        if (this._bindings == null) {
            this._bindings = Collections.emptyMap();
        }
    }

    public void _addPlaceholder(String name) {
        if (this._placeholders == null) {
            this._placeholders = new HashSet();
        }
        this._placeholders.add(name);
    }

    protected void _resolveBindings(Type t) {
        Class raw;
        if (t == null) {
            return;
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            Type[] args = pt.getActualTypeArguments();
            if (args != null && args.length > 0) {
                Class rawType = (Class)pt.getRawType();
                TypeVariable<Class<T>>[] vars = rawType.getTypeParameters();
                if (vars.length != args.length) {
                    throw new IllegalArgumentException("Strange parametrized type (in class " + rawType.getName() + "): number of type arguments != number of type parameters (" + args.length + " vs " + vars.length + ")");
                }
                int len = args.length;
                for (int i = 0; i < len; ++i) {
                    TypeVariable var = vars[i];
                    String name = var.getName();
                    if (this._bindings == null) {
                        this._bindings = new LinkedHashMap<String, JavaType>();
                    } else if (this._bindings.containsKey(name)) continue;
                    this._addPlaceholder(name);
                    this._bindings.put(name, this._typeFactory._constructType(args[i], this));
                }
            }
            raw = (Class)pt.getRawType();
        } else if (t instanceof Class) {
            TypeVariable<Class<T>>[] vars;
            raw = (Class)t;
            Class<?> decl = raw.getDeclaringClass();
            if (decl != null && !decl.isAssignableFrom(raw)) {
                this._resolveBindings(raw.getDeclaringClass());
            }
            if ((vars = raw.getTypeParameters()) != null && vars.length > 0) {
                JavaType[] typeParams = null;
                if (this._contextType != null && raw.isAssignableFrom(this._contextType.getRawClass())) {
                    typeParams = this._typeFactory.findTypeParameters(this._contextType, raw);
                }
                for (int i = 0; i < vars.length; ++i) {
                    TypeVariable var = vars[i];
                    String name = var.getName();
                    Type varType = var.getBounds()[0];
                    if (varType == null) continue;
                    if (this._bindings == null) {
                        this._bindings = new LinkedHashMap<String, JavaType>();
                    } else if (this._bindings.containsKey(name)) continue;
                    this._addPlaceholder(name);
                    if (typeParams != null) {
                        this._bindings.put(name, typeParams[i]);
                        continue;
                    }
                    this._bindings.put(name, this._typeFactory._constructType(varType, this));
                }
            }
        } else {
            return;
        }
        this._resolveBindings(raw.getGenericSuperclass());
        for (Type intType : raw.getGenericInterfaces()) {
            this._resolveBindings(intType);
        }
    }

    public String toString() {
        if (this._bindings == null) {
            this._resolve();
        }
        StringBuilder sb = new StringBuilder("[TypeBindings for ");
        if (this._contextType != null) {
            sb.append(this._contextType.toString());
        } else {
            sb.append(this._contextClass.getName());
        }
        sb.append(": ").append(this._bindings).append("]");
        return sb.toString();
    }
}

