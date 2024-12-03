/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;

public class MethodRankHelper {
    public static final int DL_SUBSTITUTION = 10;
    public static final int DL_DELETE = 10;
    public static final int DL_TRANSPOSITION = 5;
    public static final int DL_CASE = 5;
    public static final int MAX_RECOMENDATIONS = 5;
    public static final int MAX_METHOD_SCORE = 50;
    public static final int MAX_CONSTRUCTOR_SCORE = 20;
    public static final int MAX_FIELD_SCORE = 30;
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static String getMethodSuggestionString(String methodName, Class type, Object[] arguments) {
        Class[] argumentClasses;
        List<Pair<Class, Class>> conflictClasses;
        ClassInfo ci = ClassInfo.getClassInfo(type);
        ArrayList<MetaMethod> methods = new ArrayList<MetaMethod>(ci.getMetaClass().getMethods());
        methods.addAll(ci.getMetaClass().getMetaMethods());
        List<MetaMethod> sugg = MethodRankHelper.rankMethods(methodName, arguments, methods);
        StringBuilder sb = new StringBuilder();
        if (!sugg.isEmpty()) {
            sb.append("\nPossible solutions: ");
            for (int i = 0; i < sugg.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(sugg.get(i).getName()).append("(");
                sb.append(MethodRankHelper.listParameterNames(sugg.get(i).getParameterTypes()));
                sb.append(")");
            }
        }
        if (!(conflictClasses = MethodRankHelper.getConflictClasses(sugg, argumentClasses = MethodRankHelper.getArgumentClasses(arguments))).isEmpty()) {
            sb.append("\nThe following classes appear as argument class and as parameter class, ");
            sb.append("but are defined by different class loader:\n");
            boolean first = true;
            for (Pair<Class, Class> pair : conflictClasses) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                sb.append(((Class)((Pair)pair).u).getName()).append(" (defined by '");
                sb.append(((Class)((Pair)pair).u).getClassLoader());
                sb.append("' and '");
                sb.append(((Class)((Pair)pair).v).getClassLoader());
                sb.append("')");
            }
            sb.append("\nIf one of the method suggestions matches the method you wanted to call, ");
            sb.append("\nthen check your class loader setup.");
        }
        return sb.toString();
    }

    private static List<Pair<Class, Class>> getConflictClasses(List<MetaMethod> sugg, Class[] argumentClasses) {
        LinkedList<Pair<Class, Class>> ret = new LinkedList<Pair<Class, Class>>();
        HashSet<Class> recordedClasses = new HashSet<Class>();
        for (MetaMethod method : sugg) {
            Class[] para;
            for (Class aPara : para = method.getNativeParameterTypes()) {
                if (recordedClasses.contains(aPara)) continue;
                for (Class argumentClass : argumentClasses) {
                    if (argumentClass == null || argumentClass == aPara || !argumentClass.getName().equals(aPara.getName())) continue;
                    ret.add(new Pair<Class, Class>(argumentClass, aPara));
                }
                recordedClasses.add(aPara);
            }
        }
        return ret;
    }

    private static Class[] getArgumentClasses(Object[] arguments) {
        Class[] argumentClasses = new Class[arguments.length];
        for (int i = 0; i < argumentClasses.length; ++i) {
            Object arg = arguments[i];
            if (arg == null) continue;
            argumentClasses[i] = arg.getClass();
        }
        return argumentClasses;
    }

    public static String getConstructorSuggestionString(Class type, Object[] arguments) {
        Constructor[] sugg = MethodRankHelper.rankConstructors(arguments, type.getConstructors());
        if (sugg.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("\nPossible solutions: ");
            for (int i = 0; i < sugg.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(type.getName()).append("(");
                sb.append(MethodRankHelper.listParameterNames(sugg[i].getParameterTypes()));
                sb.append(")");
            }
            return sb.toString();
        }
        return "";
    }

    public static String getPropertySuggestionString(String fieldName, Class type) {
        ClassInfo ci = ClassInfo.getClassInfo(type);
        List<MetaProperty> fi = ci.getMetaClass().getProperties();
        ArrayList<RankableField> rf = new ArrayList<RankableField>(fi.size());
        StringBuilder sb = new StringBuilder();
        sb.append("\nPossible solutions: ");
        for (MetaProperty mp : fi) {
            rf.add(new RankableField(fieldName, mp));
        }
        Collections.sort(rf);
        int i = 0;
        for (RankableField f : rf) {
            if (i > 5 || f.score > 30) break;
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(f.f.getName());
            ++i;
        }
        return i > 0 ? sb.toString() : "";
    }

    private static String listParameterNames(Class[] cachedClasses) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cachedClasses.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(cachedClasses[i].getName());
        }
        return sb.toString();
    }

    private static String listParameterNames(CachedClass[] cachedClasses) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cachedClasses.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(cachedClasses[i].getName());
        }
        return sb.toString();
    }

    private static List<MetaMethod> rankMethods(String name, Object[] original, List<MetaMethod> methods) {
        ArrayList<RankableMethod> rm = new ArrayList<RankableMethod>(methods.size());
        if (original == null) {
            original = EMPTY_OBJECT_ARRAY;
        }
        Class[] ta = new Class[original.length];
        Class<NullObject> nullC = NullObject.class;
        for (int i = 0; i < original.length; ++i) {
            ta[i] = original[i] == null ? nullC : original[i].getClass();
        }
        for (MetaMethod m : methods) {
            rm.add(new RankableMethod(name, ta, m));
        }
        Collections.sort(rm);
        ArrayList<MetaMethod> l = new ArrayList<MetaMethod>(rm.size());
        for (RankableMethod m : rm) {
            if (l.size() > 5 || m.score > 50) break;
            l.add(m.m);
        }
        return l;
    }

    private static Constructor[] rankConstructors(Object[] original, Constructor[] candidates) {
        int i;
        Object[] rc = new RankableConstructor[candidates.length];
        Class[] ta = new Class[original.length];
        Class<NullObject> nullC = NullObject.class;
        for (i = 0; i < original.length; ++i) {
            ta[i] = original[i] == null ? nullC : original[i].getClass();
        }
        for (i = 0; i < candidates.length; ++i) {
            rc[i] = new RankableConstructor(ta, candidates[i]);
        }
        Arrays.sort(rc);
        ArrayList<Constructor> l = new ArrayList<Constructor>();
        for (int index = 0; l.size() < 5 && index < rc.length && ((RankableConstructor)rc[index]).score < 20; ++index) {
            l.add(((RankableConstructor)rc[index]).c);
        }
        return l.toArray(new Constructor[l.size()]);
    }

    protected static Class boxVar(Class c) {
        if (Boolean.TYPE.equals(c)) {
            return Boolean.class;
        }
        if (Character.TYPE.equals(c)) {
            return Character.class;
        }
        if (Byte.TYPE.equals(c)) {
            return Byte.class;
        }
        if (Double.TYPE.equals(c)) {
            return Double.class;
        }
        if (Float.TYPE.equals(c)) {
            return Float.class;
        }
        if (Integer.TYPE.equals(c)) {
            return Integer.class;
        }
        if (Long.TYPE.equals(c)) {
            return Long.class;
        }
        if (Short.TYPE.equals(c)) {
            return Short.class;
        }
        return c;
    }

    public static int delDistance(CharSequence s, CharSequence t) {
        int i;
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[][] vals = new int[3][n + 1];
        for (i = 0; i <= n; ++i) {
            vals[1][i] = i * 10;
        }
        for (int j = 1; j <= m; ++j) {
            char t_j = t.charAt(j - 1);
            vals[0][0] = j * 10;
            for (i = 1; i <= n; ++i) {
                char s_i = s.charAt(i - 1);
                int cost = Character.isLowerCase(s_i) ^ Character.isLowerCase(t_j) ? (MethodRankHelper.caselessCompare(s_i, t_j) ? 5 : 10) : (s_i == t_j ? 0 : 10);
                vals[0][i] = Math.min(Math.min(vals[0][i - 1] + 10, vals[1][i] + 10), vals[1][i - 1] + cost);
                if (i <= 1 || j <= 1) continue;
                cost = Character.isLowerCase(s_i) ^ Character.isLowerCase(t.charAt(j - 2)) ? 5 : 0;
                int n2 = cost = Character.isLowerCase(s.charAt(i - 2)) ^ Character.isLowerCase(t_j) ? cost + 5 : cost;
                if (!MethodRankHelper.caselessCompare(s_i, t.charAt(j - 2)) || !MethodRankHelper.caselessCompare(s.charAt(i - 2), t_j)) continue;
                vals[0][i] = Math.min(vals[0][i], vals[2][i - 2] + 5 + cost);
            }
            int[] _d = vals[2];
            vals[2] = vals[1];
            vals[1] = vals[0];
            vals[0] = _d;
        }
        return vals[1][n];
    }

    private static boolean caselessCompare(char a, char b) {
        return Character.toLowerCase(a) == Character.toLowerCase(b);
    }

    public static int damerauLevenshteinDistance(Object[] s, Object[] t) {
        int i;
        if (s == null || t == null) {
            throw new IllegalArgumentException("Arrays must not be null");
        }
        int n = s.length;
        int m = t.length;
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[][] vals = new int[3][n + 1];
        for (i = 0; i <= n; ++i) {
            vals[1][i] = i * 10;
        }
        for (int j = 1; j <= m; ++j) {
            Object t_j = t[j - 1];
            vals[0][0] = j * 10;
            for (i = 1; i <= n; ++i) {
                int cost = s[i - 1].equals(t_j) ? 0 : 10;
                vals[0][i] = Math.min(Math.min(vals[0][i - 1] + 10, vals[1][i] + 10), vals[1][i - 1] + cost);
                if (i <= 1 || j <= 1 || !s[i - 1].equals(t[j - 2]) || !s[i - 2].equals(t_j)) continue;
                vals[0][i] = Math.min(vals[0][i], vals[2][i - 2] + 5);
            }
            int[] _d = vals[2];
            vals[2] = vals[1];
            vals[1] = vals[0];
            vals[0] = _d;
        }
        return vals[1][n];
    }

    private static class NullObject {
        private NullObject() {
        }
    }

    private static final class RankableField
    implements Comparable {
        final MetaProperty f;
        final Integer score;

        public RankableField(String name, MetaProperty mp) {
            this.f = mp;
            this.score = MethodRankHelper.delDistance(name, mp.getName());
        }

        public int compareTo(Object o) {
            RankableField co = (RankableField)o;
            return this.score.compareTo(co.score);
        }
    }

    private static final class RankableConstructor
    implements Comparable {
        final Constructor c;
        final Integer score;

        public RankableConstructor(Class[] argumentTypes, Constructor c) {
            this.c = c;
            Object[] cArgs = new Class[c.getParameterTypes().length];
            for (int i = 0; i < cArgs.length; ++i) {
                cArgs[i] = MethodRankHelper.boxVar(c.getParameterTypes()[i]);
            }
            this.score = MethodRankHelper.damerauLevenshteinDistance(argumentTypes, cArgs);
        }

        public int compareTo(Object o) {
            RankableConstructor co = (RankableConstructor)o;
            return this.score.compareTo(co.score);
        }
    }

    private static final class RankableMethod
    implements Comparable {
        final MetaMethod m;
        final Integer score;

        public RankableMethod(String name, Class[] argumentTypes, MetaMethod m2) {
            this.m = m2;
            int nameDist = MethodRankHelper.delDistance(name, m2.getName());
            Object[] mArgs = new Class[m2.getParameterTypes().length];
            for (int i = 0; i < mArgs.length; ++i) {
                mArgs[i] = MethodRankHelper.boxVar(m2.getParameterTypes()[i].getTheClass());
            }
            int argDist = MethodRankHelper.damerauLevenshteinDistance(argumentTypes, mArgs);
            this.score = nameDist + argDist;
        }

        public int compareTo(Object o) {
            RankableMethod mo = (RankableMethod)o;
            return this.score.compareTo(mo.score);
        }
    }

    private static final class Pair<U, V> {
        private U u;
        private V v;

        public Pair(U u, V v) {
            this.u = u;
            this.v = v;
        }
    }
}

