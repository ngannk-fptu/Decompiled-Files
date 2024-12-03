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

public class KeywordSyntaxCompletor
implements IdentifierCompletor,
GroovyObject {
    private static final String[] KEYWORDS;
    private static final String[] VALUE_KEYWORDS;
    private static final String[] SPECIAL_FUNCTIONS;
    private static final String[] DEFAULT_METHODS;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public KeywordSyntaxCompletor() {
        MetaClass metaClass;
        CallSite[] callSiteArray = KeywordSyntaxCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean complete(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = KeywordSyntaxCompletor.$getCallSiteArray();
        String prefix = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(callSiteArray[1].call(tokens)));
        boolean foundMatch = false;
        String varName = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[2].call(KEYWORDS), Iterator.class);
        while (iterator.hasNext()) {
            boolean bl;
            varName = ShortTypeHandling.castToString(iterator.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Object)varName, prefix))) continue;
            callSiteArray[4].call(candidates, callSiteArray[5].call((Object)varName, " "));
            foundMatch = bl = true;
        }
        String varName2 = null;
        Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(VALUE_KEYWORDS), Iterator.class);
        while (iterator2.hasNext()) {
            boolean bl;
            varName2 = ShortTypeHandling.castToString(iterator2.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)varName2, prefix))) continue;
            callSiteArray[8].call(candidates, varName2);
            foundMatch = bl = true;
        }
        String varName3 = null;
        Iterator iterator3 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[9].call(SPECIAL_FUNCTIONS), Iterator.class);
        while (iterator3.hasNext()) {
            boolean bl;
            varName3 = ShortTypeHandling.castToString(iterator3.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call((Object)varName3, prefix))) continue;
            callSiteArray[11].call(candidates, varName3);
            foundMatch = bl = true;
        }
        String varName4 = null;
        Iterator iterator4 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(DEFAULT_METHODS), Iterator.class);
        while (iterator4.hasNext()) {
            boolean bl;
            varName4 = ShortTypeHandling.castToString(iterator4.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call((Object)varName4, prefix))) continue;
            callSiteArray[14].call(candidates, varName4);
            foundMatch = bl = true;
        }
        return foundMatch;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != KeywordSyntaxCompletor.class) {
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
        String[] stringArray = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "char", "class", "continue", "def", "default", "do", "double", "else", "enum", "final", "float", "int", "interface", "long", "new", "private", "protected", "public", "return", "short", "static", "synchronized", "throw", "throws", "transient", "void", "volatile"};
        KEYWORDS = stringArray;
        String[] stringArray2 = new String[]{"true", "false", "this", "super", "null"};
        VALUE_KEYWORDS = stringArray2;
        String[] stringArray3 = new String[]{"catch (", "finally {", "for (", "if (", "switch (", "try {", "while ("};
        SPECIAL_FUNCTIONS = stringArray3;
        String[] stringArray4 = new String[]{"use (", "print ", "println ", "printf ", "sprintf "};
        DEFAULT_METHODS = stringArray4;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "text";
        stringArray[1] = "last";
        stringArray[2] = "iterator";
        stringArray[3] = "startsWith";
        stringArray[4] = "leftShift";
        stringArray[5] = "plus";
        stringArray[6] = "iterator";
        stringArray[7] = "startsWith";
        stringArray[8] = "leftShift";
        stringArray[9] = "iterator";
        stringArray[10] = "startsWith";
        stringArray[11] = "leftShift";
        stringArray[12] = "iterator";
        stringArray[13] = "startsWith";
        stringArray[14] = "leftShift";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        KeywordSyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(KeywordSyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = KeywordSyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

