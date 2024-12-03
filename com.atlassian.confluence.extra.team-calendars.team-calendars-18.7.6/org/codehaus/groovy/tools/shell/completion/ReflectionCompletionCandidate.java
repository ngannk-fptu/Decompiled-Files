/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;

public class ReflectionCompletionCandidate
implements Comparable<ReflectionCompletionCandidate>,
GroovyObject {
    private final String value;
    private final List<String> jAnsiCodes;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;

    public ReflectionCompletionCandidate(String value, String ... jAnsiCodes) {
        String string;
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.value = string = value;
        ArrayList<Object> arrayList = new ArrayList<Object>(Arrays.asList((Object[])ScriptBytecodeAdapter.castToType(jAnsiCodes, Object[].class)));
        this.jAnsiCodes = arrayList;
    }

    public String getValue() {
        return this.value;
    }

    public List<String> getjAnsiCodes() {
        return this.jAnsiCodes;
    }

    @Override
    public int compareTo(ReflectionCompletionCandidate o) {
        boolean hasBracket = this.value.contains("(");
        boolean otherBracket = o.getValue().contains("(");
        if (ScriptBytecodeAdapter.compareEqual(hasBracket, otherBracket)) {
            return this.value.compareTo(o.getValue());
        }
        if (hasBracket && !otherBracket) {
            return -1;
        }
        return 1;
    }

    public String toString() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(this.getClass(), o.getClass())) {
            return false;
        }
        ReflectionCompletionCandidate that = (ReflectionCompletionCandidate)ScriptBytecodeAdapter.castToType(o, ReflectionCompletionCandidate.class);
        return ScriptBytecodeAdapter.compareEqual(this.value, that.getValue());
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ReflectionCompletionCandidate.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }
}

