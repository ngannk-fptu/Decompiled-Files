/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;
import org.aspectj.runtime.reflect.StringMaker;

abstract class SignatureImpl
implements Signature {
    private static boolean useCache = true;
    int modifiers = -1;
    String name;
    String declaringTypeName;
    Class declaringType;
    Cache stringCache;
    private String stringRep;
    ClassLoader lookupClassLoader = null;
    static final char SEP = '-';
    static String[] EMPTY_STRING_ARRAY = new String[0];
    static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    static final String INNER_SEP = ":";

    SignatureImpl(int modifiers, String name, Class declaringType) {
        this.modifiers = modifiers;
        this.name = name;
        this.declaringType = declaringType;
    }

    protected abstract String createToString(StringMaker var1);

    String toString(StringMaker sm) {
        String result = null;
        if (useCache) {
            if (this.stringCache == null) {
                try {
                    this.stringCache = new CacheImpl();
                }
                catch (Throwable t) {
                    useCache = false;
                }
            } else {
                result = this.stringCache.get(sm.cacheOffset);
            }
        }
        if (result == null) {
            result = this.createToString(sm);
        }
        if (useCache) {
            this.stringCache.set(sm.cacheOffset, result);
        }
        return result;
    }

    @Override
    public final String toString() {
        return this.toString(StringMaker.middleStringMaker);
    }

    @Override
    public final String toShortString() {
        return this.toString(StringMaker.shortStringMaker);
    }

    @Override
    public final String toLongString() {
        return this.toString(StringMaker.longStringMaker);
    }

    @Override
    public int getModifiers() {
        if (this.modifiers == -1) {
            this.modifiers = this.extractInt(0);
        }
        return this.modifiers;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            this.name = this.extractString(1);
        }
        return this.name;
    }

    @Override
    public Class getDeclaringType() {
        if (this.declaringType == null) {
            this.declaringType = this.extractType(2);
        }
        return this.declaringType;
    }

    @Override
    public String getDeclaringTypeName() {
        if (this.declaringTypeName == null) {
            this.declaringTypeName = this.getDeclaringType().getName();
        }
        return this.declaringTypeName;
    }

    String fullTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (type.isArray()) {
            return this.fullTypeName(type.getComponentType()) + "[]";
        }
        return type.getName().replace('$', '.');
    }

    String stripPackageName(String name) {
        int dot = name.lastIndexOf(46);
        if (dot == -1) {
            return name;
        }
        return name.substring(dot + 1);
    }

    String shortTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (type.isArray()) {
            return this.shortTypeName(type.getComponentType()) + "[]";
        }
        return this.stripPackageName(type.getName()).replace('$', '.');
    }

    void addFullTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(this.fullTypeName(types[i]));
        }
    }

    void addShortTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(this.shortTypeName(types[i]));
        }
    }

    void addTypeArray(StringBuffer buf, Class[] types) {
        this.addFullTypeNames(buf, types);
    }

    public void setLookupClassLoader(ClassLoader loader) {
        this.lookupClassLoader = loader;
    }

    private ClassLoader getLookupClassLoader() {
        if (this.lookupClassLoader == null) {
            this.lookupClassLoader = this.getClass().getClassLoader();
        }
        return this.lookupClassLoader;
    }

    public SignatureImpl(String stringRep) {
        this.stringRep = stringRep;
    }

    String extractString(int n) {
        int startIndex = 0;
        int endIndex = this.stringRep.indexOf(45);
        while (n-- > 0) {
            startIndex = endIndex + 1;
            endIndex = this.stringRep.indexOf(45, startIndex);
        }
        if (endIndex == -1) {
            endIndex = this.stringRep.length();
        }
        return this.stringRep.substring(startIndex, endIndex);
    }

    int extractInt(int n) {
        String s = this.extractString(n);
        return Integer.parseInt(s, 16);
    }

    Class extractType(int n) {
        String s = this.extractString(n);
        return Factory.makeClass(s, this.getLookupClassLoader());
    }

    String[] extractStrings(int n) {
        String s = this.extractString(n);
        StringTokenizer st = new StringTokenizer(s, INNER_SEP);
        int N = st.countTokens();
        String[] ret = new String[N];
        for (int i = 0; i < N; ++i) {
            ret[i] = st.nextToken();
        }
        return ret;
    }

    Class[] extractTypes(int n) {
        String s = this.extractString(n);
        StringTokenizer st = new StringTokenizer(s, INNER_SEP);
        int N = st.countTokens();
        Class[] ret = new Class[N];
        for (int i = 0; i < N; ++i) {
            ret[i] = Factory.makeClass(st.nextToken(), this.getLookupClassLoader());
        }
        return ret;
    }

    static void setUseCache(boolean b) {
        useCache = b;
    }

    static boolean getUseCache() {
        return useCache;
    }

    private static final class CacheImpl
    implements Cache {
        private SoftReference toStringCacheRef;

        public CacheImpl() {
            this.makeCache();
        }

        @Override
        public String get(int cacheOffset) {
            String[] cachedArray = this.array();
            if (cachedArray == null) {
                return null;
            }
            return cachedArray[cacheOffset];
        }

        @Override
        public void set(int cacheOffset, String result) {
            String[] cachedArray = this.array();
            if (cachedArray == null) {
                cachedArray = this.makeCache();
            }
            cachedArray[cacheOffset] = result;
        }

        private String[] array() {
            return (String[])this.toStringCacheRef.get();
        }

        private String[] makeCache() {
            String[] array = new String[3];
            this.toStringCacheRef = new SoftReference<String[]>(array);
            return array;
        }
    }

    private static interface Cache {
        public String get(int var1);

        public void set(int var1, String var2);
    }
}

