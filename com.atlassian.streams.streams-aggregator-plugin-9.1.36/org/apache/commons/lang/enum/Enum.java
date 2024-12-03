/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.enum;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

public abstract class Enum
implements Comparable,
Serializable {
    private static final long serialVersionUID = -487045951170455942L;
    private static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));
    private static Map cEnumClasses = new WeakHashMap();
    private final String iName;
    private final transient int iHashCode;
    protected transient String iToString = null;
    static /* synthetic */ Class class$org$apache$commons$lang$enum$Enum;
    static /* synthetic */ Class class$org$apache$commons$lang$enum$ValuedEnum;

    protected Enum(String name) {
        this.init(name);
        this.iName = name;
        this.iHashCode = 7 + this.getEnumClass().hashCode() + 3 * name.hashCode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init(String name) {
        Entry entry;
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The Enum name must not be empty or null");
        }
        Class enumClass = this.getEnumClass();
        if (enumClass == null) {
            throw new IllegalArgumentException("getEnumClass() must not be null");
        }
        boolean ok = false;
        for (Class<?> cls = this.getClass(); cls != null && cls != (class$org$apache$commons$lang$enum$Enum == null ? Enum.class$("org.apache.commons.lang.enum.Enum") : class$org$apache$commons$lang$enum$Enum) && cls != (class$org$apache$commons$lang$enum$ValuedEnum == null ? Enum.class$("org.apache.commons.lang.enum.ValuedEnum") : class$org$apache$commons$lang$enum$ValuedEnum); cls = cls.getSuperclass()) {
            if (cls != enumClass) continue;
            ok = true;
            break;
        }
        if (!ok) {
            throw new IllegalArgumentException("getEnumClass() must return a superclass of this class");
        }
        Class clazz = class$org$apache$commons$lang$enum$Enum == null ? (class$org$apache$commons$lang$enum$Enum = Enum.class$("org.apache.commons.lang.enum.Enum")) : class$org$apache$commons$lang$enum$Enum;
        synchronized (clazz) {
            entry = (Entry)cEnumClasses.get(enumClass);
            if (entry == null) {
                entry = Enum.createEntry(enumClass);
                WeakHashMap<Class, Entry> myMap = new WeakHashMap<Class, Entry>();
                myMap.putAll(cEnumClasses);
                myMap.put(enumClass, entry);
                cEnumClasses = myMap;
            }
        }
        if (entry.map.containsKey(name)) {
            throw new IllegalArgumentException("The Enum name must be unique, '" + name + "' has already been added");
        }
        entry.map.put(name, this);
        entry.list.add(this);
    }

    protected Object readResolve() {
        Entry entry = (Entry)cEnumClasses.get(this.getEnumClass());
        if (entry == null) {
            return null;
        }
        return entry.map.get(this.getName());
    }

    protected static Enum getEnum(Class enumClass, String name) {
        Entry entry = Enum.getEntry(enumClass);
        if (entry == null) {
            return null;
        }
        return (Enum)entry.map.get(name);
    }

    protected static Map getEnumMap(Class enumClass) {
        Entry entry = Enum.getEntry(enumClass);
        if (entry == null) {
            return EMPTY_MAP;
        }
        return entry.unmodifiableMap;
    }

    protected static List getEnumList(Class enumClass) {
        Entry entry = Enum.getEntry(enumClass);
        if (entry == null) {
            return Collections.EMPTY_LIST;
        }
        return entry.unmodifiableList;
    }

    protected static Iterator iterator(Class enumClass) {
        return Enum.getEnumList(enumClass).iterator();
    }

    private static Entry getEntry(Class enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        if (!(class$org$apache$commons$lang$enum$Enum == null ? (class$org$apache$commons$lang$enum$Enum = Enum.class$("org.apache.commons.lang.enum.Enum")) : class$org$apache$commons$lang$enum$Enum).isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("The Class must be a subclass of Enum");
        }
        Entry entry = (Entry)cEnumClasses.get(enumClass);
        return entry;
    }

    private static Entry createEntry(Class enumClass) {
        Entry entry = new Entry();
        for (Class cls = enumClass.getSuperclass(); cls != null && cls != (class$org$apache$commons$lang$enum$Enum == null ? Enum.class$("org.apache.commons.lang.enum.Enum") : class$org$apache$commons$lang$enum$Enum) && cls != (class$org$apache$commons$lang$enum$ValuedEnum == null ? Enum.class$("org.apache.commons.lang.enum.ValuedEnum") : class$org$apache$commons$lang$enum$ValuedEnum); cls = cls.getSuperclass()) {
            Entry loopEntry = (Entry)cEnumClasses.get(cls);
            if (loopEntry == null) continue;
            entry.list.addAll(loopEntry.list);
            entry.map.putAll(loopEntry.map);
            break;
        }
        return entry;
    }

    public final String getName() {
        return this.iName;
    }

    public Class getEnumClass() {
        return this.getClass();
    }

    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() == this.getClass()) {
            return this.iName.equals(((Enum)other).iName);
        }
        if (!other.getClass().getName().equals(this.getClass().getName())) {
            return false;
        }
        return this.iName.equals(this.getNameInOtherClassLoader(other));
    }

    public final int hashCode() {
        return this.iHashCode;
    }

    public int compareTo(Object other) {
        if (other == this) {
            return 0;
        }
        if (other.getClass() != this.getClass()) {
            if (other.getClass().getName().equals(this.getClass().getName())) {
                return this.iName.compareTo(this.getNameInOtherClassLoader(other));
            }
            throw new ClassCastException("Different enum class '" + ClassUtils.getShortClassName(other.getClass()) + "'");
        }
        return this.iName.compareTo(((Enum)other).iName);
    }

    private String getNameInOtherClassLoader(Object other) {
        try {
            Method mth = other.getClass().getMethod("getName", null);
            String name = (String)mth.invoke(other, null);
            return name;
        }
        catch (NoSuchMethodException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        throw new IllegalStateException("This should not happen");
    }

    public String toString() {
        if (this.iToString == null) {
            String shortName = ClassUtils.getShortClassName(this.getEnumClass());
            this.iToString = shortName + "[" + this.getName() + "]";
        }
        return this.iToString;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class Entry {
        final Map map = new HashMap();
        final Map unmodifiableMap = Collections.unmodifiableMap(this.map);
        final List list = new ArrayList(25);
        final List unmodifiableList = Collections.unmodifiableList(this.list);

        protected Entry() {
        }
    }
}

