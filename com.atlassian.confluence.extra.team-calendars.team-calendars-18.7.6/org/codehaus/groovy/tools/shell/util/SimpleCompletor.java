/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import jline.console.completer.Completer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class SimpleCompletor
implements Completer,
GroovyObject {
    private SortedSet<String> candidates;
    private String delimiter;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SimpleCompletor(String ... candidates) {
        MetaClass metaClass;
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[0].callCurrent((GroovyObject)this, (Object)candidates);
        } else {
            this.setCandidateStrings(candidates);
        }
    }

    public SimpleCompletor() {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        Object[] objectArray = new Object[]{new String[0]};
        SimpleCompletor simpleCompletor = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, SimpleCompletor.class)) {
            case 39797: {
                Object[] objectArray2 = objectArray;
                simpleCompletor();
                break;
            }
            case 842122122: {
                Object[] objectArray2 = objectArray;
                simpleCompletor((String[])DefaultTypeTransformation.castToVargsArray(objectArray, 0, String[].class));
                break;
            }
            case 1692375985: {
                Object[] objectArray2 = objectArray;
                simpleCompletor((Closure)ScriptBytecodeAdapter.castToType(objectArray[0], Closure.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public SimpleCompletor(Closure loader) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        this();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Closure closure = loader;
            valueRecorder.record(closure, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(closure, null);
            valueRecorder.record(bl, 15);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert loader != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Object obj = callSiteArray[1].call(loader);
        List list = null;
        if (obj instanceof List) {
            List list2;
            list = list2 = (List)ScriptBytecodeAdapter.castToType(obj, List.class);
        }
        if (ScriptBytecodeAdapter.compareEqual(list, null)) {
            throw (Throwable)callSiteArray[2].callConstructor(IllegalStateException.class, callSiteArray[3].call((Object)"The loader closure did not return a list of candidates; found: ", obj));
        }
        Iterator iter = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(list), Iterator.class);
        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(iter))) {
            callSiteArray[6].callCurrent((GroovyObject)this, callSiteArray[7].call(InvokerHelper.class, callSiteArray[8].call(iter)));
        }
    }

    public void add(String candidate) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[9].callCurrent((GroovyObject)this, candidate);
        } else {
            this.addCandidateString(candidate);
        }
    }

    public Object leftShift(String s) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[10].callCurrent((GroovyObject)this, s);
        } else {
            this.add(s);
        }
        return null;
    }

    public int complete(String buffer, int cursor, List<CharSequence> clist) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        String start = ScriptBytecodeAdapter.compareEqual(buffer, null) ? "" : buffer;
        SortedSet matches = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[11].call(callSiteArray[12].callCurrent(this), start);
            matches = (SortedSet)ScriptBytecodeAdapter.castToType(object, SortedSet.class);
        } else {
            Object object = callSiteArray[13].call(this.getCandidates(), start);
            matches = (SortedSet)ScriptBytecodeAdapter.castToType(object, SortedSet.class);
        }
        Iterator i = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[14].call(matches), Iterator.class);
        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(i))) {
            String can = ShortTypeHandling.castToString(callSiteArray[16].call(i));
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[17].call((Object)can, start))) break;
            String delim = this.delimiter;
            if (ScriptBytecodeAdapter.compareNotEqual(delim, null)) {
                int index = DefaultTypeTransformation.intUnbox(callSiteArray[18].call(can, delim, cursor));
                if (ScriptBytecodeAdapter.compareNotEqual(index, -1)) {
                    Object object = callSiteArray[19].call(can, 0, callSiteArray[20].call((Object)index, 1));
                    can = ShortTypeHandling.castToString(object);
                }
            }
            callSiteArray[21].call(clist, can);
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[22].call(clist), 1)) {
            callSiteArray[23].call(clist, 0, callSiteArray[24].call((Object)ShortTypeHandling.castToString(callSiteArray[25].call(clist, 0)), " "));
        }
        return ScriptBytecodeAdapter.compareEqual(callSiteArray[26].call(clist), 0) ? Integer.valueOf(-1) : Integer.valueOf(0);
    }

    public void setCandidates(SortedSet<String> candidates) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        SortedSet<String> sortedSet = candidates;
        this.candidates = (SortedSet)ScriptBytecodeAdapter.castToType(sortedSet, SortedSet.class);
    }

    public SortedSet<String> getCandidates() {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        return (SortedSet)ScriptBytecodeAdapter.castToType(callSiteArray[27].call(Collections.class, this.candidates), SortedSet.class);
    }

    public void setCandidateStrings(String ... strings) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        callSiteArray[28].callCurrent((GroovyObject)this, callSiteArray[29].callConstructor(TreeSet.class, callSiteArray[30].call(Arrays.class, (Object)strings)));
    }

    public void addCandidateString(String string) {
        CallSite[] callSiteArray = SimpleCompletor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(string, null)) {
            callSiteArray[31].call(this.candidates, string);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SimpleCompletor.class) {
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

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String string) {
        this.delimiter = string;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "setCandidateStrings";
        stringArray[1] = "call";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "plus";
        stringArray[4] = "iterator";
        stringArray[5] = "hasNext";
        stringArray[6] = "add";
        stringArray[7] = "toString";
        stringArray[8] = "next";
        stringArray[9] = "addCandidateString";
        stringArray[10] = "add";
        stringArray[11] = "tailSet";
        stringArray[12] = "getCandidates";
        stringArray[13] = "tailSet";
        stringArray[14] = "iterator";
        stringArray[15] = "hasNext";
        stringArray[16] = "next";
        stringArray[17] = "startsWith";
        stringArray[18] = "indexOf";
        stringArray[19] = "substring";
        stringArray[20] = "plus";
        stringArray[21] = "add";
        stringArray[22] = "size";
        stringArray[23] = "set";
        stringArray[24] = "plus";
        stringArray[25] = "get";
        stringArray[26] = "size";
        stringArray[27] = "unmodifiableSortedSet";
        stringArray[28] = "setCandidates";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "asList";
        stringArray[31] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[32];
        SimpleCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SimpleCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SimpleCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

