/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.MapMaker
 */
package com.google.inject.internal.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;
import com.google.inject.internal.util.Classes;
import com.google.inject.internal.util.LineNumbers;
import com.google.inject.internal.util.SourceProvider;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackTraceElements {
    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    private static final InMemoryStackTraceElement[] EMPTY_INMEMORY_STACK_TRACE = new InMemoryStackTraceElement[0];
    static final LoadingCache<Class<?>, LineNumbers> lineNumbersCache = CacheBuilder.newBuilder().weakKeys().softValues().build(new CacheLoader<Class<?>, LineNumbers>(){

        public LineNumbers load(Class<?> key) {
            try {
                return new LineNumbers(key);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    });
    private static Map<Object, Object> cache = new MapMaker().makeMap();
    private static final String UNKNOWN_SOURCE = "Unknown Source";

    public static Object forMember(Member member) {
        if (member == null) {
            return SourceProvider.UNKNOWN_SOURCE;
        }
        Class<?> declaringClass = member.getDeclaringClass();
        LineNumbers lineNumbers = (LineNumbers)lineNumbersCache.getUnchecked(declaringClass);
        String fileName = lineNumbers.getSource();
        Integer lineNumberOrNull = lineNumbers.getLineNumber(member);
        int lineNumber = lineNumberOrNull == null ? lineNumbers.getFirstLine() : lineNumberOrNull.intValue();
        Class<? extends Member> memberType = Classes.memberType(member);
        String memberName = memberType == Constructor.class ? "<init>" : member.getName();
        return new StackTraceElement(declaringClass.getName(), memberName, fileName, lineNumber);
    }

    public static Object forType(Class<?> implementation) {
        LineNumbers lineNumbers = (LineNumbers)lineNumbersCache.getUnchecked(implementation);
        int lineNumber = lineNumbers.getFirstLine();
        String fileName = lineNumbers.getSource();
        return new StackTraceElement(implementation.getName(), "class", fileName, lineNumber);
    }

    public static void clearCache() {
        cache.clear();
    }

    public static InMemoryStackTraceElement[] convertToInMemoryStackTraceElement(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements.length == 0) {
            return EMPTY_INMEMORY_STACK_TRACE;
        }
        InMemoryStackTraceElement[] inMemoryStackTraceElements = new InMemoryStackTraceElement[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; ++i) {
            inMemoryStackTraceElements[i] = StackTraceElements.weakIntern(new InMemoryStackTraceElement(stackTraceElements[i]));
        }
        return inMemoryStackTraceElements;
    }

    public static StackTraceElement[] convertToStackTraceElement(InMemoryStackTraceElement[] inMemoryStackTraceElements) {
        if (inMemoryStackTraceElements.length == 0) {
            return EMPTY_STACK_TRACE;
        }
        StackTraceElement[] stackTraceElements = new StackTraceElement[inMemoryStackTraceElements.length];
        for (int i = 0; i < inMemoryStackTraceElements.length; ++i) {
            String declaringClass = inMemoryStackTraceElements[i].getClassName();
            String methodName = inMemoryStackTraceElements[i].getMethodName();
            int lineNumber = inMemoryStackTraceElements[i].getLineNumber();
            stackTraceElements[i] = new StackTraceElement(declaringClass, methodName, UNKNOWN_SOURCE, lineNumber);
        }
        return stackTraceElements;
    }

    private static InMemoryStackTraceElement weakIntern(InMemoryStackTraceElement inMemoryStackTraceElement) {
        InMemoryStackTraceElement cached = (InMemoryStackTraceElement)cache.get(inMemoryStackTraceElement);
        if (cached != null) {
            return cached;
        }
        inMemoryStackTraceElement = new InMemoryStackTraceElement(StackTraceElements.weakIntern(inMemoryStackTraceElement.getClassName()), StackTraceElements.weakIntern(inMemoryStackTraceElement.getMethodName()), inMemoryStackTraceElement.getLineNumber());
        cache.put(inMemoryStackTraceElement, inMemoryStackTraceElement);
        return inMemoryStackTraceElement;
    }

    private static String weakIntern(String s) {
        String cached = (String)cache.get(s);
        if (cached != null) {
            return cached;
        }
        cache.put(s, s);
        return s;
    }

    public static class InMemoryStackTraceElement {
        private String declaringClass;
        private String methodName;
        private int lineNumber;

        InMemoryStackTraceElement(StackTraceElement ste) {
            this(ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
        }

        InMemoryStackTraceElement(String declaringClass, String methodName, int lineNumber) {
            this.declaringClass = declaringClass;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
        }

        String getClassName() {
            return this.declaringClass;
        }

        String getMethodName() {
            return this.methodName;
        }

        int getLineNumber() {
            return this.lineNumber;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof InMemoryStackTraceElement)) {
                return false;
            }
            InMemoryStackTraceElement e = (InMemoryStackTraceElement)obj;
            return e.declaringClass.equals(this.declaringClass) && e.lineNumber == this.lineNumber && this.methodName.equals(e.methodName);
        }

        public int hashCode() {
            int result = 31 * this.declaringClass.hashCode() + this.methodName.hashCode();
            result = 31 * result + this.lineNumber;
            return result;
        }

        public String toString() {
            return this.declaringClass + "." + this.methodName + "(" + this.lineNumber + ")";
        }
    }
}

