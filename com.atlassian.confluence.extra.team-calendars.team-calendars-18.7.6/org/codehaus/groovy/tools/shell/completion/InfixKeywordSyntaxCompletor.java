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
import org.codehaus.groovy.tools.shell.completion.IdentifierCompletor;

public class InfixKeywordSyntaxCompletor
implements IdentifierCompletor,
GroovyObject {
    private static final String[] INFIX_KEYWORDS;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public InfixKeywordSyntaxCompletor() {
        MetaClass metaClass;
        CallSite[] callSiteArray = InfixKeywordSyntaxCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = InfixKeywordSyntaxCompletor.$getCallSiteArray();
        String prefix = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(callSiteArray[1].call(tokens)));
        boolean foundMatch = false;
        String varName = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[2].call(INFIX_KEYWORDS), Iterator.class);
        while (iterator.hasNext()) {
            boolean bl;
            varName = ShortTypeHandling.castToString(iterator.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Object)varName, prefix))) continue;
            callSiteArray[4].call(candidates, varName);
            foundMatch = bl = true;
        }
        return foundMatch;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != InfixKeywordSyntaxCompletor.class) {
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

    static {
        String[] stringArray = new String[]{"in", "instanceof", "extends", "implements"};
        INFIX_KEYWORDS = stringArray;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "text";
        stringArray[1] = "last";
        stringArray[2] = "iterator";
        stringArray[3] = "startsWith";
        stringArray[4] = "leftShift";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        InfixKeywordSyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(InfixKeywordSyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = InfixKeywordSyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

