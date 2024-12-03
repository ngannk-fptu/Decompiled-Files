/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Alternative;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.BlockWithImpliedExitPath;
import groovyjarjarantlr.CharFormatter;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.JavaCharFormatter;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.RuleBlock;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.ToolErrorHandler;

class DefaultToolErrorHandler
implements ToolErrorHandler {
    private final Tool antlrTool;
    CharFormatter javaCharFormatter = new JavaCharFormatter();

    DefaultToolErrorHandler(Tool tool) {
        this.antlrTool = tool;
    }

    private void dumpSets(String[] stringArray, int n, Grammar grammar, boolean bl, int n2, Lookahead[] lookaheadArray) {
        StringBuffer stringBuffer = new StringBuffer(100);
        for (int i = 1; i <= n2; ++i) {
            stringBuffer.append("k==").append(i).append(':');
            if (bl) {
                String string = lookaheadArray[i].fset.toStringWithRanges(",", this.javaCharFormatter);
                if (lookaheadArray[i].containsEpsilon()) {
                    stringBuffer.append("<end-of-token>");
                    if (string.length() > 0) {
                        stringBuffer.append(',');
                    }
                }
                stringBuffer.append(string);
            } else {
                stringBuffer.append(lookaheadArray[i].fset.toString(",", grammar.tokenManager.getVocabulary()));
            }
            stringArray[n++] = stringBuffer.toString();
            stringBuffer.setLength(0);
        }
    }

    public void warnAltAmbiguity(Grammar grammar, AlternativeBlock alternativeBlock, boolean bl, int n, Lookahead[] lookaheadArray, int n2, int n3) {
        String[] stringArray;
        StringBuffer stringBuffer = new StringBuffer(100);
        if (alternativeBlock instanceof RuleBlock && ((RuleBlock)alternativeBlock).isLexerAutoGenRule()) {
            stringArray = alternativeBlock.getAlternativeAt(n2);
            Alternative alternative = alternativeBlock.getAlternativeAt(n3);
            RuleRefElement ruleRefElement = (RuleRefElement)stringArray.head;
            RuleRefElement ruleRefElement2 = (RuleRefElement)alternative.head;
            String string = CodeGenerator.reverseLexerRuleName(ruleRefElement.targetRule);
            String string2 = CodeGenerator.reverseLexerRuleName(ruleRefElement2.targetRule);
            stringBuffer.append("lexical nondeterminism between rules ");
            stringBuffer.append(string).append(" and ").append(string2).append(" upon");
        } else {
            if (bl) {
                stringBuffer.append("lexical ");
            }
            stringBuffer.append("nondeterminism between alts ");
            stringBuffer.append(n2 + 1).append(" and ");
            stringBuffer.append(n3 + 1).append(" of block upon");
        }
        stringArray = new String[n + 1];
        stringArray[0] = stringBuffer.toString();
        this.dumpSets(stringArray, 1, grammar, bl, n, lookaheadArray);
        this.antlrTool.warning(stringArray, grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
    }

    public void warnAltExitAmbiguity(Grammar grammar, BlockWithImpliedExitPath blockWithImpliedExitPath, boolean bl, int n, Lookahead[] lookaheadArray, int n2) {
        String[] stringArray = new String[n + 2];
        stringArray[0] = (bl ? "lexical " : "") + "nondeterminism upon";
        this.dumpSets(stringArray, 1, grammar, bl, n, lookaheadArray);
        stringArray[n + 1] = "between alt " + (n2 + 1) + " and exit branch of block";
        this.antlrTool.warning(stringArray, grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
    }
}

