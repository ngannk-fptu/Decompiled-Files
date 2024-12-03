/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.IdentifierCompletor;

public class CustomClassSyntaxCompletor
implements IdentifierCompletor,
GroovyObject {
    private final Groovysh shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CustomClassSyntaxCompletor(Groovysh shell) {
        MetaClass metaClass;
        CallSite[] callSiteArray = CustomClassSyntaxCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
    }

    @Override
    public boolean complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = CustomClassSyntaxCompletor.$getCallSiteArray();
        String prefix = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(callSiteArray[1].call(tokens)));
        boolean foundMatch = false;
        Class[] classes = (Class[])ScriptBytecodeAdapter.castToType(callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(this.shell))), Class[].class);
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[5].call(classes), 0)) {
            List classnames = (List)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.getPropertySpreadSafe(CustomClassSyntaxCompletor.class, classes, "name"), List.class);
            String varName = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(classnames), Iterator.class);
            while (iterator.hasNext()) {
                boolean bl;
                varName = ShortTypeHandling.castToString(iterator.next());
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)varName, prefix))) continue;
                callSiteArray[8].call(candidates, varName);
                foundMatch = bl = true;
            }
        }
        return foundMatch;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CustomClassSyntaxCompletor.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "text";
        stringArray[1] = "last";
        stringArray[2] = "loadedClasses";
        stringArray[3] = "classLoader";
        stringArray[4] = "interp";
        stringArray[5] = "size";
        stringArray[6] = "iterator";
        stringArray[7] = "startsWith";
        stringArray[8] = "leftShift";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        CustomClassSyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CustomClassSyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CustomClassSyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

