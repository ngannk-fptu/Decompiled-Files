/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.Collection;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.ParseStatus;
import org.codehaus.groovy.tools.shell.Parser;
import org.codehaus.groovy.tools.shell.Parsing;
import org.codehaus.groovy.tools.shell.util.Logger;

public final class RelaxedParser
implements Parsing,
GroovyObject {
    private final Logger log;
    private SourceBuffer sourceBuffer;
    private String[] tokenNames;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RelaxedParser() {
        MetaClass metaClass;
        CallSite[] callSiteArray = RelaxedParser.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public ParseStatus parse(Collection<String> buffer) {
        var2_2 = RelaxedParser.$getCallSiteArray();
        var3_3 = new ValueRecorder();
        try {
            v0 = buffer;
            var3_3.record(v0, 8);
            if (DefaultTypeTransformation.booleanUnbox(v0)) {
                var3_3.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer", var3_3), null);
            }
        }
        catch (Throwable v1) {
            var3_3.clear();
            throw v1;
        }
        var4_4 = var2_2[2].callConstructor(SourceBuffer.class);
        this.sourceBuffer = (SourceBuffer)ScriptBytecodeAdapter.castToType(var4_4, SourceBuffer.class);
        source = var2_2[3].call(buffer, var2_2[4].callGetProperty(Parser.class));
        var2_2[5].call((Object)this.log, new GStringImpl(new Object[]{source}, new String[]{"Parsing: ", ""}));
        var2_2[6].callCurrent((GroovyObject)this, var2_2[7].callConstructor(UnicodeEscapingReader.class, var2_2[8].callConstructor(StringReader.class, source), this.sourceBuffer));
        var2_2[9].call((Object)this.log, "Parse complete");
        var6_6 = (ParseStatus)ScriptBytecodeAdapter.castToType(var2_2[10].callConstructor(ParseStatus.class, var2_2[11].callGetProperty(ParseCode.class)), ParseStatus.class);
        try {
            return var6_6;
        }
        catch (Exception e) {
            var8_8 = var2_2[12].call(e);
            if (!ScriptBytecodeAdapter.isCase(var8_8, TokenStreamException.class) && !ScriptBytecodeAdapter.isCase(var8_8, RecognitionException.class)) ** GOTO lbl43
            var2_2[13].call((Object)this.log, new GStringImpl(new Object[]{e, var2_2[14].callGetProperty(var2_2[15].call(e))}, new String[]{"Parse incomplete: ", " (", ")"}));
            var9_9 = (ParseStatus)ScriptBytecodeAdapter.castToType(var2_2[16].callConstructor(ParseStatus.class, var2_2[17].callGetProperty(ParseCode.class)), ParseStatus.class);
            return var9_9;
lbl43:
            // 1 sources

            var2_2[18].call((Object)this.log, new GStringImpl(new Object[]{e, var2_2[19].callGetProperty(var2_2[20].call(e))}, new String[]{"Parse error: ", " (", ")"}));
            var10_10 = (ParseStatus)ScriptBytecodeAdapter.castToType(var2_2[21].callConstructor(ParseStatus.class, e), ParseStatus.class);
            return var10_10;
        }
    }

    protected AST doParse(UnicodeEscapingReader reader) throws Exception {
        CallSite[] callSiteArray = RelaxedParser.$getCallSiteArray();
        GroovyLexer lexer = (GroovyLexer)ScriptBytecodeAdapter.castToType(callSiteArray[22].callConstructor(GroovyLexer.class, reader), GroovyLexer.class);
        callSiteArray[23].call((Object)reader, lexer);
        Object parser = callSiteArray[24].call(GroovyRecognizer.class, lexer);
        callSiteArray[25].call(parser, this.sourceBuffer);
        Object object = callSiteArray[26].callGetProperty(parser);
        this.tokenNames = (String[])ScriptBytecodeAdapter.castToType(object, String[].class);
        callSiteArray[27].call(parser);
        return (AST)ScriptBytecodeAdapter.castToType(callSiteArray[28].callGetProperty(parser), AST.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RelaxedParser.class) {
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
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "join";
        stringArray[4] = "NEWLINE";
        stringArray[5] = "debug";
        stringArray[6] = "doParse";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "debug";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "COMPLETE";
        stringArray[12] = "getClass";
        stringArray[13] = "debug";
        stringArray[14] = "name";
        stringArray[15] = "getClass";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "INCOMPLETE";
        stringArray[18] = "debug";
        stringArray[19] = "name";
        stringArray[20] = "getClass";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "setLexer";
        stringArray[24] = "make";
        stringArray[25] = "setSourceBuffer";
        stringArray[26] = "tokenNames";
        stringArray[27] = "compilationUnit";
        stringArray[28] = "AST";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[29];
        RelaxedParser.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RelaxedParser.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RelaxedParser.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

