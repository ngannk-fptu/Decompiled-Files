/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.SortedSet;
import java.util.TreeSet;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.util.SimpleCompletor;

public class InspectCommandCompletor
extends SimpleCompletor {
    private final Binding binding;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public InspectCommandCompletor(Binding binding) {
        CallSite[] callSiteArray = InspectCommandCompletor.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Binding binding2 = binding;
            valueRecorder.record(binding2, 8);
            if (DefaultTypeTransformation.booleanUnbox(binding2)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert binding", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Binding binding3 = binding;
        this.binding = (Binding)ScriptBytecodeAdapter.castToType(binding3, Binding.class);
    }

    @Override
    public SortedSet<String> getCandidates() {
        CallSite[] callSiteArray = InspectCommandCompletor.$getCallSiteArray();
        Reference<SortedSet> set = new Reference<SortedSet>((SortedSet)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(TreeSet.class), SortedSet.class));
        public class _getCandidates_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference set;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getCandidates_closure1(Object _outerInstance, Object _thisObject, Reference set) {
                Reference reference;
                CallSite[] callSiteArray = _getCandidates_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.set = reference = set;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getCandidates_closure1.$getCallSiteArray();
                return callSiteArray[0].call(this.set.get(), it);
            }

            public SortedSet getSet() {
                CallSite[] callSiteArray = _getCandidates_closure1.$getCallSiteArray();
                return (SortedSet)ScriptBytecodeAdapter.castToType(this.set.get(), SortedSet.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getCandidates_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getCandidates_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "leftShift";
                return new CallSiteArray(_getCandidates_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getCandidates_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this.binding)), new _getCandidates_closure1(this, this, set));
        return set.get();
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != InspectCommandCompletor.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ SortedSet super$2$getCandidates() {
        return super.getCandidates();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "each";
        stringArray[2] = "keySet";
        stringArray[3] = "variables";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        InspectCommandCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(InspectCommandCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = InspectCommandCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

