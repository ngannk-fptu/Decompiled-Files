/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Classes;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$LineNumbers;
import com.google.inject.internal.util.$MapMaker;
import com.google.inject.internal.util.$SourceProvider;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class $StackTraceElements {
    static final Map<Class<?>, $LineNumbers> lineNumbersCache = new $MapMaker().weakKeys().softValues().makeComputingMap(new $Function<Class<?>, $LineNumbers>(){

        @Override
        public $LineNumbers apply(Class<?> key) {
            try {
                return new $LineNumbers(key);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    });

    public static Object forMember(Member member) {
        if (member == null) {
            return $SourceProvider.UNKNOWN_SOURCE;
        }
        Class<?> declaringClass = member.getDeclaringClass();
        $LineNumbers lineNumbers = lineNumbersCache.get(declaringClass);
        String fileName = lineNumbers.getSource();
        Integer lineNumberOrNull = lineNumbers.getLineNumber(member);
        int lineNumber = lineNumberOrNull == null ? lineNumbers.getFirstLine() : lineNumberOrNull.intValue();
        Class<? extends Member> memberType = $Classes.memberType(member);
        String memberName = memberType == Constructor.class ? "<init>" : member.getName();
        return new StackTraceElement(declaringClass.getName(), memberName, fileName, lineNumber);
    }

    public static Object forType(Class<?> implementation) {
        $LineNumbers lineNumbers = lineNumbersCache.get(implementation);
        int lineNumber = lineNumbers.getFirstLine();
        String fileName = lineNumbers.getSource();
        return new StackTraceElement(implementation.getName(), "class", fileName, lineNumber);
    }
}

