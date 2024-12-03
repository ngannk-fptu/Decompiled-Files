/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.ParseStatus;
import org.codehaus.groovy.tools.shell.Parser;
import org.codehaus.groovy.tools.shell.Parsing;
import org.codehaus.groovy.tools.shell.util.Logger;

public final class RigidParser
implements Parsing,
GroovyObject {
    private static final Pattern ANNOTATION_PATTERN;
    private static final String SCRIPT_FILENAME = "groovysh_parse";
    private final Logger log;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RigidParser() {
        MetaClass metaClass;
        CallSite[] callSiteArray = RigidParser.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public ParseStatus parse(Collection<String> buffer) {
        CallSite[] callSiteArray = RigidParser.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Collection<String> collection = buffer;
            valueRecorder.record(collection, 8);
            if (DefaultTypeTransformation.booleanUnbox(collection)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        String source = ShortTypeHandling.castToString(callSiteArray[2].call(buffer, callSiteArray[3].callGetProperty(Parser.class)));
        callSiteArray[4].call((Object)this.log, new GStringImpl(new Object[]{source}, new String[]{"Parsing: ", ""}));
        SourceUnit parser = null;
        Throwable error = null;
        Object object = callSiteArray[5].call(SourceUnit.class, SCRIPT_FILENAME, source, 1);
        parser = (SourceUnit)ScriptBytecodeAdapter.castToType(object, SourceUnit.class);
        callSiteArray[6].call(parser);
        callSiteArray[7].call((Object)this.log, "Parse complete");
        ParseStatus parseStatus = (ParseStatus)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(ParseStatus.class, callSiteArray[9].callGetProperty(ParseCode.class)), ParseStatus.class);
        try {
            return parseStatus;
        }
        catch (CompilationFailedException e) {
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(parser)), 1) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(parser))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[13].callStatic(RigidParser.class, callSiteArray[14].call(callSiteArray[15].call(buffer, -1)))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[16].callStatic(RigidParser.class, e, callSiteArray[17].call(callSiteArray[18].call(buffer, -1)))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[19].callStatic(RigidParser.class, source))) {
                        callSiteArray[20].call((Object)this.log, new GStringImpl(new Object[]{e}, new String[]{"Ignoring parse failure; might be valid: ", ""}));
                    } else {
                        CompilationFailedException compilationFailedException = e;
                        error = compilationFailedException;
                    }
                }
            } else if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[21].callGetProperty(callSiteArray[22].callGetProperty(parser)), 1) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(parser))) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[24].callStatic(RigidParser.class, callSiteArray[25].call(callSiteArray[26].call(buffer, -1)))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[27].callStatic(RigidParser.class, e, callSiteArray[28].call(callSiteArray[29].call(buffer, -1)))) || RigidParser.hasUnmatchedOpenBracketOrParen(source)) {
                    callSiteArray[30].call((Object)this.log, new GStringImpl(new Object[]{e}, new String[]{"Ignoring parse failure; might be valid: ", ""}));
                } else {
                    CompilationFailedException compilationFailedException = e;
                    error = compilationFailedException;
                }
            }
        }
        catch (Throwable e) {
            Throwable throwable;
            error = throwable = e;
        }
        if (DefaultTypeTransformation.booleanUnbox(error)) {
            callSiteArray[31].call((Object)this.log, new GStringImpl(new Object[]{error}, new String[]{"Parse error: ", ""}));
            return (ParseStatus)ScriptBytecodeAdapter.castToType(callSiteArray[32].callConstructor(ParseStatus.class, error), ParseStatus.class);
        }
        callSiteArray[33].call((Object)this.log, "Parse incomplete");
        return (ParseStatus)ScriptBytecodeAdapter.castToType(callSiteArray[34].callConstructor(ParseStatus.class, callSiteArray[35].callGetProperty(ParseCode.class)), ParseStatus.class);
    }

    public static boolean ignoreSyntaxErrorForLineEnding(String line) {
        CallSite[] callSiteArray = RigidParser.$getCallSiteArray();
        List lineEndings = ScriptBytecodeAdapter.createList(new Object[]{"{", "[", "(", ",", ".", "-", "+", "/", "*", "%", "&", "|", "?", "<", ">", "=", ":", "'''", "\"\"\"", "\\"});
        String lineEnding = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[36].call(lineEndings), Iterator.class);
        while (iterator.hasNext()) {
            lineEnding = ShortTypeHandling.castToString(iterator.next());
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[37].call((Object)line, lineEnding))) continue;
            return true;
        }
        return false;
    }

    public static boolean hasUnmatchedOpenBracketOrParen(String source) {
        CallSite[] callSiteArray = RigidParser.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(source)) {
            return false;
        }
        int parens = 0;
        int brackets = 0;
        Object ch = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[38].call(source), Iterator.class);
        while (iterator.hasNext()) {
            ch = iterator.next();
            Object var6_6 = ch;
            if (ScriptBytecodeAdapter.isCase(var6_6, "[")) {
                brackets = DefaultTypeTransformation.intUnbox(callSiteArray[39].call(brackets));
                continue;
            }
            if (ScriptBytecodeAdapter.isCase(var6_6, "]")) {
                brackets = DefaultTypeTransformation.intUnbox(callSiteArray[40].call(brackets));
                continue;
            }
            if (ScriptBytecodeAdapter.isCase(var6_6, "(")) {
                parens = DefaultTypeTransformation.intUnbox(callSiteArray[41].call(parens));
                continue;
            }
            if (!ScriptBytecodeAdapter.isCase(var6_6, ")")) continue;
            parens = DefaultTypeTransformation.intUnbox(callSiteArray[42].call(parens));
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return brackets > 0 || parens > 0;
        }
        return brackets > 0 || parens > 0;
    }

    public static boolean isAnnotationExpression(CompilationFailedException e, String line) {
        CallSite[] callSiteArray = RigidParser.$getCallSiteArray();
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(callSiteArray[44].call(e), "unexpected token: @")) && DefaultTypeTransformation.booleanUnbox(callSiteArray[45].call(callSiteArray[46].call((Object)ANNOTATION_PATTERN, line)));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RigidParser.class) {
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
        Object object = RigidParser.$getCallSiteArray()[47].call(Pattern.class, "^@[a-zA-Z_][a-zA-Z_0-9]*(.*)$");
        ANNOTATION_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
    }

    public static String getSCRIPT_FILENAME() {
        return SCRIPT_FILENAME;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "join";
        stringArray[3] = "NEWLINE";
        stringArray[4] = "debug";
        stringArray[5] = "create";
        stringArray[6] = "parse";
        stringArray[7] = "debug";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "COMPLETE";
        stringArray[10] = "errorCount";
        stringArray[11] = "errorCollector";
        stringArray[12] = "failedWithUnexpectedEOF";
        stringArray[13] = "ignoreSyntaxErrorForLineEnding";
        stringArray[14] = "trim";
        stringArray[15] = "getAt";
        stringArray[16] = "isAnnotationExpression";
        stringArray[17] = "trim";
        stringArray[18] = "getAt";
        stringArray[19] = "hasUnmatchedOpenBracketOrParen";
        stringArray[20] = "debug";
        stringArray[21] = "errorCount";
        stringArray[22] = "errorCollector";
        stringArray[23] = "failedWithUnexpectedEOF";
        stringArray[24] = "ignoreSyntaxErrorForLineEnding";
        stringArray[25] = "trim";
        stringArray[26] = "getAt";
        stringArray[27] = "isAnnotationExpression";
        stringArray[28] = "trim";
        stringArray[29] = "getAt";
        stringArray[30] = "debug";
        stringArray[31] = "debug";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "debug";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "INCOMPLETE";
        stringArray[36] = "iterator";
        stringArray[37] = "endsWith";
        stringArray[38] = "iterator";
        stringArray[39] = "next";
        stringArray[40] = "previous";
        stringArray[41] = "next";
        stringArray[42] = "previous";
        stringArray[43] = "contains";
        stringArray[44] = "getMessage";
        stringArray[45] = "find";
        stringArray[46] = "matcher";
        stringArray[47] = "compile";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[48];
        RigidParser.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RigidParser.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RigidParser.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

