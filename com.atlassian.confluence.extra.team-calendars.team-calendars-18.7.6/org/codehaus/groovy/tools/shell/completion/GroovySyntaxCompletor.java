/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import jline.console.completer.Completer;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.CommandRegistry;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.IdentifierCompletor;
import org.codehaus.groovy.tools.shell.completion.InfixKeywordSyntaxCompletor;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletor;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.transform.ImmutableASTTransformation;

public class GroovySyntaxCompletor
implements Completer,
GroovyObject {
    protected static final Logger LOG;
    private final Groovysh shell;
    private final List<IdentifierCompletor> identifierCompletors;
    private final IdentifierCompletor classnameCompletor;
    private final ReflectionCompletor reflectionCompletor;
    private final InfixKeywordSyntaxCompletor infixCompletor;
    private final Completer filenameCompletor;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GroovySyntaxCompletor(Groovysh shell, ReflectionCompletor reflectionCompletor, IdentifierCompletor classnameCompletor, List<IdentifierCompletor> identifierCompletors, Completer filenameCompletor) {
        MetaClass metaClass;
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
        IdentifierCompletor identifierCompletor = classnameCompletor;
        this.classnameCompletor = (IdentifierCompletor)ScriptBytecodeAdapter.castToType(identifierCompletor, IdentifierCompletor.class);
        List<IdentifierCompletor> list = identifierCompletors;
        this.identifierCompletors = (List)ScriptBytecodeAdapter.castToType(list, List.class);
        Object object = callSiteArray[0].callConstructor(InfixKeywordSyntaxCompletor.class);
        this.infixCompletor = (InfixKeywordSyntaxCompletor)ScriptBytecodeAdapter.castToType(object, InfixKeywordSyntaxCompletor.class);
        ReflectionCompletor reflectionCompletor2 = reflectionCompletor;
        this.reflectionCompletor = (ReflectionCompletor)ScriptBytecodeAdapter.castToType(reflectionCompletor2, ReflectionCompletor.class);
        Completer completer = filenameCompletor;
        this.filenameCompletor = (Completer)ScriptBytecodeAdapter.castToType(completer, Completer.class);
    }

    /*
     * Unable to fully structure code
     */
    public int complete(String bufferLine, int cursor, List<CharSequence> candidates) {
        block21: {
            var4_4 = GroovySyntaxCompletor.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(bufferLine) == false) {
                return -1;
            }
            if (DefaultTypeTransformation.booleanUnbox(var4_4[1].callStatic(GroovySyntaxCompletor.class, bufferLine, var4_4[2].callGetProperty(this.shell)))) {
                return -1;
            }
            tokens = ScriptBytecodeAdapter.createList(new Object[0]);
            if (!(DefaultTypeTransformation.booleanUnbox(var4_4[3].callStatic(GroovySyntaxCompletor.class, var4_4[4].call(bufferLine, 0, cursor), var4_4[5].call(var4_4[6].callGetProperty(this.shell)), tokens)) == false)) break block21;
            var6_6 = -1;
            try {
                return var6_6;
            }
            catch (InStringException ise) {
                completionStart = DefaultTypeTransformation.intUnbox(var4_4[7].call(var4_4[8].callGetProperty(ise), 1));
                fileResult = 0;
                if (!BytecodeInterface8.isOrigInt() || GroovySyntaxCompletor.__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    var10_10 = ScriptBytecodeAdapter.unaryPlus(var4_4[9].call(this.filenameCompletor, var4_4[10].call((Object)bufferLine, completionStart), var4_4[11].call((Object)cursor, completionStart), candidates));
                    fileResult = DefaultTypeTransformation.intUnbox(var10_10);
                } else {
                    var11_11 = ScriptBytecodeAdapter.unaryPlus(var4_4[12].call(this.filenameCompletor, var4_4[13].call((Object)bufferLine, completionStart), cursor - completionStart, candidates));
                    fileResult = DefaultTypeTransformation.intUnbox(var11_11);
                }
                if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !GroovySyntaxCompletor.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) ** GOTO lbl35
                if (!(fileResult >= 0)) ** GOTO lbl41
                var12_12 = DefaultTypeTransformation.intUnbox(var4_4[14].call((Object)completionStart, fileResult));
                return var12_12;
lbl35:
                // 1 sources

                if (!(fileResult >= 0)) ** GOTO lbl41
                var13_13 = completionStart + fileResult;
                return var13_13;
lbl41:
                // 2 sources

                var14_14 = -1;
                return var14_14;
            }
        }
        completionCase = (CompletionCase)ShortTypeHandling.castToEnum(var4_4[15].callStatic(GroovySyntaxCompletor.class, tokens), CompletionCase.class);
        if (ScriptBytecodeAdapter.compareEqual(completionCase, var4_4[16].callGetProperty(CompletionCase.class))) {
            return -1;
        }
        if (ScriptBytecodeAdapter.compareEqual(completionCase, var4_4[17].callGetProperty(CompletionCase.class))) {
            if (DefaultTypeTransformation.booleanUnbox(var4_4[18].call(this.infixCompletor, tokens, candidates))) {
                return DefaultTypeTransformation.intUnbox(var4_4[19].call(var4_4[20].callGetProperty(var4_4[21].call(tokens)), 1));
            }
            return -1;
        }
        if (ScriptBytecodeAdapter.compareEqual(completionCase, var4_4[22].callGetProperty(CompletionCase.class))) {
            if (DefaultTypeTransformation.booleanUnbox(var4_4[23].call(this.classnameCompletor, tokens, candidates))) {
                return DefaultTypeTransformation.intUnbox(var4_4[24].call(var4_4[25].callGetProperty(var4_4[26].call(tokens)), 1));
            }
            return -1;
        }
        result = 0;
        var18_18 = completionCase;
        if (ScriptBytecodeAdapter.isCase(var18_18, var4_4[27].callGetProperty(CompletionCase.class))) {
            var19_19 = var4_4[28].callCurrent(this, tokens, candidates);
            result = DefaultTypeTransformation.intUnbox(var19_19);
        } else if (ScriptBytecodeAdapter.isCase(var18_18, var4_4[29].callGetProperty(CompletionCase.class)) || ScriptBytecodeAdapter.isCase(var18_18, var4_4[30].callGetProperty(CompletionCase.class)) || ScriptBytecodeAdapter.isCase(var18_18, var4_4[31].callGetProperty(CompletionCase.class)) || ScriptBytecodeAdapter.isCase(var18_18, var4_4[32].callGetProperty(CompletionCase.class))) {
            var20_20 = var4_4[33].call(this.reflectionCompletor, tokens, candidates);
            result = DefaultTypeTransformation.intUnbox(var20_20);
        } else {
            throw (Throwable)var4_4[34].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{completionCase}, new String[]{"Unknown Completion case: ", ""}));
        }
        return result;
    }

    public static CompletionCase getCompletionCase(List<GroovySourceToken> tokens) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        GroovySourceToken currentToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(callSiteArray[35].call(tokens, -1), GroovySourceToken.class);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[36].callGetProperty(currentToken), callSiteArray[37].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[38].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[39].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                GroovySourceToken previousToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(callSiteArray[40].call(tokens, -2), GroovySourceToken.class);
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[41].callGetProperty(previousToken), callSiteArray[42].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[43].callGetProperty(previousToken), callSiteArray[44].callGetProperty(GroovyTokenTypes.class))) {
                    if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[45].call(tokens), 3)) {
                        return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[46].callGetProperty(CompletionCase.class), CompletionCase.class);
                    }
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[47].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[48].callGetProperty(previousToken), callSiteArray[49].callGetProperty(GroovyTokenTypes.class))) {
                    if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[50].call(tokens), 3)) {
                        return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[51].callGetProperty(CompletionCase.class), CompletionCase.class);
                    }
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[52].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                Object object = callSiteArray[53].callGetProperty(previousToken);
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[54].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[55].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[56].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[57].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[58].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[59].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[60].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[61].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[62].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[63].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[64].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[65].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[66].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[67].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[68].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[69].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[70].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[71].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[72].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[73].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[74].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[75].callGetProperty(GroovyTokenTypes.class))) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[76].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[77].callGetProperty(GroovyTokenTypes.class))) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[78].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[79].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[80].callGetProperty(currentToken), callSiteArray[81].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[82].callGetProperty(currentToken), callSiteArray[83].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[84].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[85].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[86].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[87].callGetProperty(currentToken), callSiteArray[88].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[89].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[90].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[91].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[92].callGetProperty(currentToken), callSiteArray[93].callGetProperty(GroovyTokenTypes.class))) {
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[94].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            callSiteArray[95].call((Object)LOG, callSiteArray[96].call((Object)"Untreated toke type: ", callSiteArray[97].callGetProperty(currentToken)));
        } else {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[98].callGetProperty(currentToken), callSiteArray[99].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[100].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[101].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                GroovySourceToken previousToken = (GroovySourceToken)ScriptBytecodeAdapter.castToType(callSiteArray[102].call(tokens, -2), GroovySourceToken.class);
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[103].callGetProperty(previousToken), callSiteArray[104].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[105].callGetProperty(previousToken), callSiteArray[106].callGetProperty(GroovyTokenTypes.class))) {
                    if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[107].call(tokens), 3)) {
                        return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[108].callGetProperty(CompletionCase.class), CompletionCase.class);
                    }
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[109].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[110].callGetProperty(previousToken), callSiteArray[111].callGetProperty(GroovyTokenTypes.class))) {
                    if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[112].call(tokens), 3)) {
                        return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[113].callGetProperty(CompletionCase.class), CompletionCase.class);
                    }
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[114].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                Object object = callSiteArray[115].callGetProperty(previousToken);
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[116].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[117].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[118].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[119].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[120].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[121].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[122].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[123].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[124].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[125].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[126].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[127].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[128].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[129].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[130].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[131].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[132].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[133].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[134].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[135].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[136].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.isCase(object, callSiteArray[137].callGetProperty(GroovyTokenTypes.class))) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[138].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[139].callGetProperty(GroovyTokenTypes.class))) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[140].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[141].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[142].callGetProperty(currentToken), callSiteArray[143].callGetProperty(GroovyTokenTypes.class)) || ScriptBytecodeAdapter.compareEqual(callSiteArray[144].callGetProperty(currentToken), callSiteArray[145].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[146].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[147].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[148].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[149].callGetProperty(currentToken), callSiteArray[150].callGetProperty(GroovyTokenTypes.class))) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[151].call(tokens), 1)) {
                    return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[152].callGetProperty(CompletionCase.class), CompletionCase.class);
                }
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[153].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[154].callGetProperty(currentToken), callSiteArray[155].callGetProperty(GroovyTokenTypes.class))) {
                return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[156].callGetProperty(CompletionCase.class), CompletionCase.class);
            }
            callSiteArray[157].call((Object)LOG, callSiteArray[158].call((Object)"Untreated toke type: ", callSiteArray[159].callGetProperty(currentToken)));
        }
        return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[160].callGetProperty(CompletionCase.class), CompletionCase.class);
    }

    public int completeIdentifier(List<GroovySourceToken> tokens, List<CharSequence> candidates) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        boolean foundMatches = false;
        IdentifierCompletor completor = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[161].call(this.identifierCompletors), Iterator.class);
        while (iterator.hasNext()) {
            completor = (IdentifierCompletor)ScriptBytecodeAdapter.castToType(iterator.next(), IdentifierCompletor.class);
            foundMatches = DefaultTypeTransformation.booleanUnbox(callSiteArray[162].call((Object)foundMatches, callSiteArray[163].call(completor, tokens, candidates)));
        }
        if (foundMatches) {
            return DefaultTypeTransformation.intUnbox(callSiteArray[164].call(callSiteArray[165].callGetProperty(callSiteArray[166].call(tokens)), 1));
        }
        return -1;
    }

    public static boolean isCommand(String bufferLine, CommandRegistry registry) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        int commandEnd = DefaultTypeTransformation.intUnbox(callSiteArray[167].call((Object)bufferLine, " "));
        if (ScriptBytecodeAdapter.compareNotEqual(commandEnd, -1)) {
            String commandTokenText = ShortTypeHandling.castToString(callSiteArray[168].call(bufferLine, 0, commandEnd));
            Object command = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[169].call(callSiteArray[170].call(registry)), Iterator.class);
            while (iterator.hasNext()) {
                command = iterator.next();
                if (!(ScriptBytecodeAdapter.compareEqual(commandTokenText, callSiteArray[171].callGetProperty(command)) || ScriptBytecodeAdapter.isCase(commandTokenText, callSiteArray[172].callGetProperty(command)))) continue;
                return true;
            }
        }
        return false;
    }

    public static GroovyLexer createGroovyLexer(String src) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        Reader unicodeReader = (Reader)ScriptBytecodeAdapter.castToType(callSiteArray[173].callConstructor(UnicodeEscapingReader.class, callSiteArray[174].callConstructor(StringReader.class, src), callSiteArray[175].callConstructor(SourceBuffer.class)), Reader.class);
        GroovyLexer lexer = (GroovyLexer)ScriptBytecodeAdapter.castToType(callSiteArray[176].callConstructor(GroovyLexer.class, unicodeReader), GroovyLexer.class);
        callSiteArray[177].call((Object)unicodeReader, lexer);
        return lexer;
    }

    /*
     * Exception decompiling
     */
    public static boolean tokenizeBuffer(String bufferLine, List<String> previousLines, List<GroovySourceToken> result) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [11[UNCONDITIONALDOLOOP]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public /* synthetic */ Object this$dist$invoke$1(String name, Object args) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(GroovySyntaxCompletor.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$1(String name, Object value) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, GroovySyntaxCompletor.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$1(String name) {
        CallSite[] callSiteArray = GroovySyntaxCompletor.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(GroovySyntaxCompletor.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GroovySyntaxCompletor.class) {
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
        Object object = GroovySyntaxCompletor.$getCallSiteArray()[220].call(Logger.class, GroovySyntaxCompletor.class);
        LOG = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "isCommand";
        stringArray[2] = "registry";
        stringArray[3] = "tokenizeBuffer";
        stringArray[4] = "substring";
        stringArray[5] = "current";
        stringArray[6] = "buffers";
        stringArray[7] = "plus";
        stringArray[8] = "column";
        stringArray[9] = "complete";
        stringArray[10] = "substring";
        stringArray[11] = "minus";
        stringArray[12] = "complete";
        stringArray[13] = "substring";
        stringArray[14] = "plus";
        stringArray[15] = "getCompletionCase";
        stringArray[16] = "NO_COMPLETION";
        stringArray[17] = "SECOND_IDENT";
        stringArray[18] = "complete";
        stringArray[19] = "minus";
        stringArray[20] = "column";
        stringArray[21] = "last";
        stringArray[22] = "INSTANCEOF";
        stringArray[23] = "complete";
        stringArray[24] = "minus";
        stringArray[25] = "column";
        stringArray[26] = "last";
        stringArray[27] = "NO_DOT_PREFIX";
        stringArray[28] = "completeIdentifier";
        stringArray[29] = "DOT_LAST";
        stringArray[30] = "PREFIX_AFTER_DOT";
        stringArray[31] = "SPREAD_DOT_LAST";
        stringArray[32] = "PREFIX_AFTER_SPREAD_DOT";
        stringArray[33] = "complete";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "getAt";
        stringArray[36] = "type";
        stringArray[37] = "IDENT";
        stringArray[38] = "size";
        stringArray[39] = "NO_DOT_PREFIX";
        stringArray[40] = "getAt";
        stringArray[41] = "type";
        stringArray[42] = "DOT";
        stringArray[43] = "type";
        stringArray[44] = "OPTIONAL_DOT";
        stringArray[45] = "size";
        stringArray[46] = "NO_COMPLETION";
        stringArray[47] = "PREFIX_AFTER_DOT";
        stringArray[48] = "type";
        stringArray[49] = "SPREAD_DOT";
        stringArray[50] = "size";
        stringArray[51] = "NO_COMPLETION";
        stringArray[52] = "PREFIX_AFTER_SPREAD_DOT";
        stringArray[53] = "type";
        stringArray[54] = "LITERAL_import";
        stringArray[55] = "LITERAL_class";
        stringArray[56] = "LITERAL_interface";
        stringArray[57] = "LITERAL_enum";
        stringArray[58] = "LITERAL_def";
        stringArray[59] = "LITERAL_void";
        stringArray[60] = "LITERAL_boolean";
        stringArray[61] = "LITERAL_byte";
        stringArray[62] = "LITERAL_char";
        stringArray[63] = "LITERAL_short";
        stringArray[64] = "LITERAL_int";
        stringArray[65] = "LITERAL_float";
        stringArray[66] = "LITERAL_long";
        stringArray[67] = "LITERAL_double";
        stringArray[68] = "LITERAL_package";
        stringArray[69] = "LITERAL_true";
        stringArray[70] = "LITERAL_false";
        stringArray[71] = "LITERAL_as";
        stringArray[72] = "LITERAL_this";
        stringArray[73] = "LITERAL_try";
        stringArray[74] = "LITERAL_finally";
        stringArray[75] = "LITERAL_catch";
        stringArray[76] = "NO_COMPLETION";
        stringArray[77] = "IDENT";
        stringArray[78] = "SECOND_IDENT";
        stringArray[79] = "NO_DOT_PREFIX";
        stringArray[80] = "type";
        stringArray[81] = "DOT";
        stringArray[82] = "type";
        stringArray[83] = "OPTIONAL_DOT";
        stringArray[84] = "size";
        stringArray[85] = "NO_COMPLETION";
        stringArray[86] = "DOT_LAST";
        stringArray[87] = "type";
        stringArray[88] = "SPREAD_DOT";
        stringArray[89] = "size";
        stringArray[90] = "NO_COMPLETION";
        stringArray[91] = "SPREAD_DOT_LAST";
        stringArray[92] = "type";
        stringArray[93] = "LITERAL_instanceof";
        stringArray[94] = "INSTANCEOF";
        stringArray[95] = "debug";
        stringArray[96] = "plus";
        stringArray[97] = "type";
        stringArray[98] = "type";
        stringArray[99] = "IDENT";
        stringArray[100] = "size";
        stringArray[101] = "NO_DOT_PREFIX";
        stringArray[102] = "getAt";
        stringArray[103] = "type";
        stringArray[104] = "DOT";
        stringArray[105] = "type";
        stringArray[106] = "OPTIONAL_DOT";
        stringArray[107] = "size";
        stringArray[108] = "NO_COMPLETION";
        stringArray[109] = "PREFIX_AFTER_DOT";
        stringArray[110] = "type";
        stringArray[111] = "SPREAD_DOT";
        stringArray[112] = "size";
        stringArray[113] = "NO_COMPLETION";
        stringArray[114] = "PREFIX_AFTER_SPREAD_DOT";
        stringArray[115] = "type";
        stringArray[116] = "LITERAL_import";
        stringArray[117] = "LITERAL_class";
        stringArray[118] = "LITERAL_interface";
        stringArray[119] = "LITERAL_enum";
        stringArray[120] = "LITERAL_def";
        stringArray[121] = "LITERAL_void";
        stringArray[122] = "LITERAL_boolean";
        stringArray[123] = "LITERAL_byte";
        stringArray[124] = "LITERAL_char";
        stringArray[125] = "LITERAL_short";
        stringArray[126] = "LITERAL_int";
        stringArray[127] = "LITERAL_float";
        stringArray[128] = "LITERAL_long";
        stringArray[129] = "LITERAL_double";
        stringArray[130] = "LITERAL_package";
        stringArray[131] = "LITERAL_true";
        stringArray[132] = "LITERAL_false";
        stringArray[133] = "LITERAL_as";
        stringArray[134] = "LITERAL_this";
        stringArray[135] = "LITERAL_try";
        stringArray[136] = "LITERAL_finally";
        stringArray[137] = "LITERAL_catch";
        stringArray[138] = "NO_COMPLETION";
        stringArray[139] = "IDENT";
        stringArray[140] = "SECOND_IDENT";
        stringArray[141] = "NO_DOT_PREFIX";
        stringArray[142] = "type";
        stringArray[143] = "DOT";
        stringArray[144] = "type";
        stringArray[145] = "OPTIONAL_DOT";
        stringArray[146] = "size";
        stringArray[147] = "NO_COMPLETION";
        stringArray[148] = "DOT_LAST";
        stringArray[149] = "type";
        stringArray[150] = "SPREAD_DOT";
        stringArray[151] = "size";
        stringArray[152] = "NO_COMPLETION";
        stringArray[153] = "SPREAD_DOT_LAST";
        stringArray[154] = "type";
        stringArray[155] = "LITERAL_instanceof";
        stringArray[156] = "INSTANCEOF";
        stringArray[157] = "debug";
        stringArray[158] = "plus";
        stringArray[159] = "type";
        stringArray[160] = "NO_COMPLETION";
        stringArray[161] = "iterator";
        stringArray[162] = "or";
        stringArray[163] = "complete";
        stringArray[164] = "minus";
        stringArray[165] = "column";
        stringArray[166] = "last";
        stringArray[167] = "indexOf";
        stringArray[168] = "substring";
        stringArray[169] = "iterator";
        stringArray[170] = "commands";
        stringArray[171] = "name";
        stringArray[172] = "aliases";
        stringArray[173] = "<$constructor$>";
        stringArray[174] = "<$constructor$>";
        stringArray[175] = "<$constructor$>";
        stringArray[176] = "<$constructor$>";
        stringArray[177] = "setLexer";
        stringArray[178] = "size";
        stringArray[179] = "<$constructor$>";
        stringArray[180] = "iterator";
        stringArray[181] = "append";
        stringArray[182] = "plus";
        stringArray[183] = "append";
        stringArray[184] = "createGroovyLexer";
        stringArray[185] = "toString";
        stringArray[186] = "createGroovyLexer";
        stringArray[187] = "size";
        stringArray[188] = "<$constructor$>";
        stringArray[189] = "iterator";
        stringArray[190] = "append";
        stringArray[191] = "plus";
        stringArray[192] = "append";
        stringArray[193] = "createGroovyLexer";
        stringArray[194] = "toString";
        stringArray[195] = "nextToken";
        stringArray[196] = "type";
        stringArray[197] = "EOF";
        stringArray[198] = "isEmpty";
        stringArray[199] = "line";
        stringArray[200] = "line";
        stringArray[201] = "last";
        stringArray[202] = "type";
        stringArray[203] = "STRING_CTOR_START";
        stringArray[204] = "leftShift";
        stringArray[205] = "substring";
        stringArray[206] = "minus";
        stringArray[207] = "columnLast";
        stringArray[208] = "length";
        stringArray[209] = "find";
        stringArray[210] = "charAt";
        stringArray[211] = "toString";
        stringArray[212] = "plus";
        stringArray[213] = "size";
        stringArray[214] = "line";
        stringArray[215] = "<$constructor$>";
        stringArray[216] = "minus";
        stringArray[217] = "plus";
        stringArray[218] = "columnLast";
        stringArray[219] = "empty";
        stringArray[220] = "create";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[221];
        GroovySyntaxCompletor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GroovySyntaxCompletor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GroovySyntaxCompletor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public static final class CompletionCase
    extends Enum<CompletionCase>
    implements GroovyObject {
        public static final /* enum */ CompletionCase SECOND_IDENT;
        public static final /* enum */ CompletionCase NO_COMPLETION;
        public static final /* enum */ CompletionCase DOT_LAST;
        public static final /* enum */ CompletionCase SPREAD_DOT_LAST;
        public static final /* enum */ CompletionCase PREFIX_AFTER_DOT;
        public static final /* enum */ CompletionCase PREFIX_AFTER_SPREAD_DOT;
        public static final /* enum */ CompletionCase NO_DOT_PREFIX;
        public static final /* enum */ CompletionCase INSTANCEOF;
        public static final CompletionCase MIN_VALUE;
        public static final CompletionCase MAX_VALUE;
        private static final /* synthetic */ CompletionCase[] $VALUES;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public CompletionCase(LinkedHashMap __namedArgs) {
            MetaClass metaClass;
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
            if (ScriptBytecodeAdapter.compareEqual(__namedArgs, null)) {
                throw (Throwable)callSiteArray[0].callConstructor(IllegalArgumentException.class, "One of the enum constants for enum org.codehaus.groovy.tools.shell.completion.GroovySyntaxCompletor$CompletionCase was initialized with null. Please use a non-null value or define your own constructor.");
            }
            callSiteArray[1].callStatic(ImmutableASTTransformation.class, this, __namedArgs);
        }

        public CompletionCase() {
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            this((LinkedHashMap)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(LinkedHashMap.class), LinkedHashMap.class));
        }

        public static final CompletionCase[] values() {
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            return (CompletionCase[])ScriptBytecodeAdapter.castToType($VALUES.clone(), CompletionCase[].class);
        }

        public /* synthetic */ CompletionCase next() {
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            Object ordinal = callSiteArray[3].call(callSiteArray[4].callCurrent(this));
            if (ScriptBytecodeAdapter.compareGreaterThanEqual(ordinal, callSiteArray[5].call($VALUES))) {
                Integer n = 0;
                ordinal = n;
            }
            return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[6].call((Object)$VALUES, ordinal), CompletionCase.class);
        }

        public /* synthetic */ CompletionCase previous() {
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            Object ordinal = callSiteArray[7].call(callSiteArray[8].callCurrent(this));
            if (ScriptBytecodeAdapter.compareLessThan(ordinal, 0)) {
                Object object;
                ordinal = object = callSiteArray[9].call(callSiteArray[10].call($VALUES), 1);
            }
            return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[11].call((Object)$VALUES, ordinal), CompletionCase.class);
        }

        public static CompletionCase valueOf(String name) {
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            return (CompletionCase)ShortTypeHandling.castToEnum(callSiteArray[12].callStatic(CompletionCase.class, CompletionCase.class, name), CompletionCase.class);
        }

        public static final /* synthetic */ CompletionCase $INIT(Object ... para) {
            CompletionCase completionCase;
            CallSite[] callSiteArray = CompletionCase.$getCallSiteArray();
            Object[] objectArray = ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{para}, new int[]{0});
            switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, CompletionCase.class)) {
                case -1348271900: {
                    CompletionCase completionCase2;
                    completionCase = completionCase2;
                    Object[] objectArray2 = objectArray;
                    completionCase2 = new CompletionCase();
                    break;
                }
                case -242181752: {
                    CompletionCase completionCase2;
                    completionCase = completionCase2;
                    Object[] objectArray2 = objectArray;
                    completionCase2 = new CompletionCase((LinkedHashMap)ScriptBytecodeAdapter.castToType(objectArray[2], LinkedHashMap.class));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
                }
            }
            return completionCase;
        }

        static {
            CompletionCase completionCase;
            CompletionCase completionCase2;
            Object object = CompletionCase.$getCallSiteArray()[13].callStatic(CompletionCase.class, "SECOND_IDENT", 0);
            SECOND_IDENT = (CompletionCase)ShortTypeHandling.castToEnum(object, CompletionCase.class);
            Object object2 = CompletionCase.$getCallSiteArray()[14].callStatic(CompletionCase.class, "NO_COMPLETION", 1);
            NO_COMPLETION = (CompletionCase)ShortTypeHandling.castToEnum(object2, CompletionCase.class);
            Object object3 = CompletionCase.$getCallSiteArray()[15].callStatic(CompletionCase.class, "DOT_LAST", 2);
            DOT_LAST = (CompletionCase)ShortTypeHandling.castToEnum(object3, CompletionCase.class);
            Object object4 = CompletionCase.$getCallSiteArray()[16].callStatic(CompletionCase.class, "SPREAD_DOT_LAST", 3);
            SPREAD_DOT_LAST = (CompletionCase)ShortTypeHandling.castToEnum(object4, CompletionCase.class);
            Object object5 = CompletionCase.$getCallSiteArray()[17].callStatic(CompletionCase.class, "PREFIX_AFTER_DOT", 4);
            PREFIX_AFTER_DOT = (CompletionCase)ShortTypeHandling.castToEnum(object5, CompletionCase.class);
            Object object6 = CompletionCase.$getCallSiteArray()[18].callStatic(CompletionCase.class, "PREFIX_AFTER_SPREAD_DOT", 5);
            PREFIX_AFTER_SPREAD_DOT = (CompletionCase)ShortTypeHandling.castToEnum(object6, CompletionCase.class);
            Object object7 = CompletionCase.$getCallSiteArray()[19].callStatic(CompletionCase.class, "NO_DOT_PREFIX", 6);
            NO_DOT_PREFIX = (CompletionCase)ShortTypeHandling.castToEnum(object7, CompletionCase.class);
            Object object8 = CompletionCase.$getCallSiteArray()[20].callStatic(CompletionCase.class, "INSTANCEOF", 7);
            INSTANCEOF = (CompletionCase)ShortTypeHandling.castToEnum(object8, CompletionCase.class);
            MIN_VALUE = completionCase2 = SECOND_IDENT;
            MAX_VALUE = completionCase = INSTANCEOF;
            CompletionCase[] completionCaseArray = new CompletionCase[]{SECOND_IDENT, NO_COMPLETION, DOT_LAST, SPREAD_DOT_LAST, PREFIX_AFTER_DOT, PREFIX_AFTER_SPREAD_DOT, NO_DOT_PREFIX, INSTANCEOF};
            $VALUES = completionCaseArray;
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != CompletionCase.class) {
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
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "checkPropNames";
            stringArray[2] = "<$constructor$>";
            stringArray[3] = "next";
            stringArray[4] = "ordinal";
            stringArray[5] = "size";
            stringArray[6] = "getAt";
            stringArray[7] = "previous";
            stringArray[8] = "ordinal";
            stringArray[9] = "minus";
            stringArray[10] = "size";
            stringArray[11] = "getAt";
            stringArray[12] = "valueOf";
            stringArray[13] = "$INIT";
            stringArray[14] = "$INIT";
            stringArray[15] = "$INIT";
            stringArray[16] = "$INIT";
            stringArray[17] = "$INIT";
            stringArray[18] = "$INIT";
            stringArray[19] = "$INIT";
            stringArray[20] = "$INIT";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[21];
            CompletionCase.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(CompletionCase.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = CompletionCase.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public static class InStringException
    extends Exception
    implements GroovyObject {
        private int column;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public InStringException(int column) {
            MetaClass metaClass;
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
            int n = column;
            this.column = DefaultTypeTransformation.intUnbox(n);
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(InStringException.class, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(InStringException.class, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(InStringException.class, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = InStringException.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(InStringException.class, GroovySyntaxCompletor.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != InStringException.class) {
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

        public int getColumn() {
            return this.column;
        }

        public void setColumn(int n) {
            this.column = n;
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[]{};
            return new CallSiteArray(InStringException.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = InStringException.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

