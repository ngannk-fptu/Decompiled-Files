/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.LexerSharedInputState;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CurlyCountingGroovyLexer
extends GroovyLexer
implements GroovyObject {
    private Object endReached;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected CurlyCountingGroovyLexer(Reader reader) {
        MetaClass metaClass;
        CallSite[] callSiteArray = CurlyCountingGroovyLexer.$getCallSiteArray();
        Object[] objectArray = new Object[]{reader};
        CurlyCountingGroovyLexer curlyCountingGroovyLexer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, GroovyLexer.class)) {
            case -324182139: {
                Object[] objectArray2 = objectArray;
                super((Reader)ScriptBytecodeAdapter.castToType(objectArray[0], Reader.class));
                break;
            }
            case -35777578: {
                Object[] objectArray2 = objectArray;
                super((LexerSharedInputState)ScriptBytecodeAdapter.castToType(objectArray[0], LexerSharedInputState.class));
                break;
            }
            case 1828999620: {
                Object[] objectArray2 = objectArray;
                super((InputBuffer)ScriptBytecodeAdapter.castToType(objectArray[0], InputBuffer.class));
                break;
            }
            case 1898076734: {
                Object[] objectArray2 = objectArray;
                super((InputStream)ScriptBytecodeAdapter.castToType(objectArray[0], InputStream.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
        boolean bl = false;
        this.endReached = bl;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static CurlyCountingGroovyLexer createGroovyLexer(String src) {
        CallSite[] callSiteArray = CurlyCountingGroovyLexer.$getCallSiteArray();
        Reader unicodeReader = (Reader)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(UnicodeEscapingReader.class, callSiteArray[1].callConstructor(StringReader.class, callSiteArray[2].call(src)), callSiteArray[3].callConstructor(SourceBuffer.class)), Reader.class);
        CurlyCountingGroovyLexer lexer = (CurlyCountingGroovyLexer)ScriptBytecodeAdapter.castToType(callSiteArray[4].callConstructor(CurlyCountingGroovyLexer.class, unicodeReader), CurlyCountingGroovyLexer.class);
        callSiteArray[5].call((Object)unicodeReader, lexer);
        return lexer;
    }

    public int getParenLevel() {
        CallSite[] callSiteArray = CurlyCountingGroovyLexer.$getCallSiteArray();
        return DefaultTypeTransformation.intUnbox(callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this)));
    }

    @Override
    public void uponEOF() {
        CallSite[] callSiteArray = CurlyCountingGroovyLexer.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuper0(GroovyLexer.class, this, "uponEOF");
        boolean bl = true;
        this.endReached = bl;
    }

    public List<GroovySourceToken> toList() {
        CallSite[] callSiteArray = CurlyCountingGroovyLexer.$getCallSiteArray();
        List tokens = ScriptBytecodeAdapter.createList(new Object[0]);
        GroovySourceToken token = null;
        while (!DefaultTypeTransformation.booleanUnbox(this.endReached)) {
            GroovySourceToken groovySourceToken;
            token = groovySourceToken = (GroovySourceToken)ScriptBytecodeAdapter.asType(callSiteArray[8].callCurrent(this), GroovySourceToken.class);
            callSiteArray[9].call((Object)tokens, token);
            if (!ScriptBytecodeAdapter.compareEqual(callSiteArray[10].callGetProperty(token), callSiteArray[11].callGetProperty(GroovyTokenTypes.class))) continue;
            break;
        }
        return tokens;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CurlyCountingGroovyLexer.class) {
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

    public /* synthetic */ void super$2$uponEOF() {
        super.uponEOF();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "toString";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "setLexer";
        stringArray[6] = "size";
        stringArray[7] = "parenLevelStack";
        stringArray[8] = "nextToken";
        stringArray[9] = "add";
        stringArray[10] = "type";
        stringArray[11] = "EOF";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        CurlyCountingGroovyLexer.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CurlyCountingGroovyLexer.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CurlyCountingGroovyLexer.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

