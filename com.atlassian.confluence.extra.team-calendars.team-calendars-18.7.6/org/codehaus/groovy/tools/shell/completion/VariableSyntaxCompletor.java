/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.IdentifierCompletor;

public class VariableSyntaxCompletor
implements IdentifierCompletor,
GroovyObject {
    private final Groovysh shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public VariableSyntaxCompletor(Groovysh shell) {
        MetaClass metaClass;
        CallSite[] callSiteArray = VariableSyntaxCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
    }

    @Override
    public boolean complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = VariableSyntaxCompletor.$getCallSiteArray();
        String prefix = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(callSiteArray[1].call(tokens)));
        Map vars = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(this.shell))), Map.class);
        boolean foundMatch = false;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            String varName = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(callSiteArray[6].call(vars)), Iterator.class);
            while (iterator.hasNext()) {
                boolean bl;
                varName = ShortTypeHandling.castToString(iterator.next());
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callStatic(VariableSyntaxCompletor.class, varName, prefix))) continue;
                if (callSiteArray[8].call((Object)vars, varName) instanceof MethodClosure) {
                    varName = ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[9].call((MethodClosure)ScriptBytecodeAdapter.castToType(callSiteArray[10].call((Object)vars, varName), MethodClosure.class)), 0) ? ShortTypeHandling.castToString(callSiteArray[11].call((Object)varName, "(")) : ShortTypeHandling.castToString(callSiteArray[12].call((Object)varName, "()"));
                }
                foundMatch = bl = true;
                callSiteArray[13].call(candidates, varName);
            }
        } else {
            String varName = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[14].call(callSiteArray[15].call(vars)), Iterator.class);
            while (iterator.hasNext()) {
                boolean bl;
                varName = ShortTypeHandling.castToString(iterator.next());
                if (!VariableSyntaxCompletor.acceptName(varName, prefix)) continue;
                if (callSiteArray[16].call((Object)vars, varName) instanceof MethodClosure) {
                    varName = ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[17].call((MethodClosure)ScriptBytecodeAdapter.castToType(callSiteArray[18].call((Object)vars, varName), MethodClosure.class)), 0) ? ShortTypeHandling.castToString(callSiteArray[19].call((Object)varName, "(")) : ShortTypeHandling.castToString(callSiteArray[20].call((Object)varName, "()"));
                }
                foundMatch = bl = true;
                callSiteArray[21].call(candidates, varName);
            }
        }
        return foundMatch;
    }

    private static boolean acceptName(String name, String prefix) {
        CallSite[] callSiteArray = VariableSyntaxCompletor.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (!DefaultTypeTransformation.booleanUnbox(prefix) || DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call((Object)name, prefix))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call((Object)name, "$")) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[24].call((Object)name, "_"));
        }
        return (!DefaultTypeTransformation.booleanUnbox(prefix) || DefaultTypeTransformation.booleanUnbox(callSiteArray[25].call((Object)name, prefix))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[26].call((Object)name, "$")) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call((Object)name, "_"));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != VariableSyntaxCompletor.class) {
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

    public final Groovysh getShell() {
        return this.shell;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "text";
        stringArray[1] = "last";
        stringArray[2] = "variables";
        stringArray[3] = "context";
        stringArray[4] = "interp";
        stringArray[5] = "iterator";
        stringArray[6] = "keySet";
        stringArray[7] = "acceptName";
        stringArray[8] = "get";
        stringArray[9] = "getMaximumNumberOfParameters";
        stringArray[10] = "get";
        stringArray[11] = "plus";
        stringArray[12] = "plus";
        stringArray[13] = "leftShift";
        stringArray[14] = "iterator";
        stringArray[15] = "keySet";
        stringArray[16] = "get";
        stringArray[17] = "getMaximumNumberOfParameters";
        stringArray[18] = "get";
        stringArray[19] = "plus";
        stringArray[20] = "plus";
        stringArray[21] = "leftShift";
        stringArray[22] = "startsWith";
        stringArray[23] = "contains";
        stringArray[24] = "startsWith";
        stringArray[25] = "startsWith";
        stringArray[26] = "contains";
        stringArray[27] = "startsWith";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        VariableSyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(VariableSyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = VariableSyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

