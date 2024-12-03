/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ActionElement;
import antlr.Alternative;
import antlr.AlternativeBlock;
import antlr.AlternativeElement;
import antlr.BlockEndElement;
import antlr.BlockWithImpliedExitPath;
import antlr.CharFormatter;
import antlr.CharLiteralElement;
import antlr.CharRangeElement;
import antlr.CodeGenerator;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.JavaCharFormatter;
import antlr.LLkGrammarAnalyzer;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.OneOrMoreBlock;
import antlr.RuleBlock;
import antlr.RuleEndElement;
import antlr.RuleRefElement;
import antlr.RuleSymbol;
import antlr.StringLiteralElement;
import antlr.SynPredBlock;
import antlr.TokenRangeElement;
import antlr.TokenRefElement;
import antlr.Tool;
import antlr.TreeElement;
import antlr.TreeWalkerGrammar;
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;

public class LLkAnalyzer
implements LLkGrammarAnalyzer {
    public boolean DEBUG_ANALYZER = false;
    private AlternativeBlock currentBlock;
    protected Tool tool = null;
    protected Grammar grammar = null;
    protected boolean lexicalAnalysis = false;
    CharFormatter charFormatter = new JavaCharFormatter();

    public LLkAnalyzer(Tool tool) {
        this.tool = tool;
    }

    protected boolean altUsesWildcardDefault(Alternative alternative) {
        AlternativeElement alternativeElement = alternative.head;
        if (alternativeElement instanceof TreeElement && ((TreeElement)alternativeElement).root instanceof WildcardElement) {
            return true;
        }
        return alternativeElement instanceof WildcardElement && alternativeElement.next instanceof BlockEndElement;
    }

    public boolean deterministic(AlternativeBlock alternativeBlock) {
        int n = 1;
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(" + alternativeBlock + ")");
        }
        boolean bl = true;
        int n2 = alternativeBlock.alternatives.size();
        AlternativeBlock alternativeBlock2 = this.currentBlock;
        Object object = null;
        this.currentBlock = alternativeBlock;
        if (!(alternativeBlock.greedy || alternativeBlock instanceof OneOrMoreBlock || alternativeBlock instanceof ZeroOrMoreBlock)) {
            this.tool.warning("Being nongreedy only makes sense for (...)+ and (...)*", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
        }
        if (n2 == 1) {
            AlternativeElement alternativeElement = alternativeBlock.getAlternativeAt((int)0).head;
            this.currentBlock.alti = 0;
            alternativeBlock.getAlternativeAt((int)0).cache[1] = alternativeElement.look(1);
            alternativeBlock.getAlternativeAt((int)0).lookaheadDepth = 1;
            this.currentBlock = alternativeBlock2;
            return true;
        }
        for (int i = 0; i < n2 - 1; ++i) {
            this.currentBlock.alti = i;
            this.currentBlock.analysisAlt = i;
            this.currentBlock.altj = i + 1;
            for (int j = i + 1; j < n2; ++j) {
                Object object2;
                Object object3;
                boolean bl2;
                this.currentBlock.altj = j;
                if (this.DEBUG_ANALYZER) {
                    System.out.println("comparing " + i + " against alt " + j);
                }
                this.currentBlock.analysisAlt = j;
                n = 1;
                Lookahead[] lookaheadArray = new Lookahead[this.grammar.maxk + 1];
                do {
                    bl2 = false;
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("checking depth " + n + "<=" + this.grammar.maxk);
                    }
                    object3 = this.getAltLookahead(alternativeBlock, i, n);
                    object2 = this.getAltLookahead(alternativeBlock, j, n);
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("p is " + ((Lookahead)object3).toString(",", this.charFormatter, this.grammar));
                    }
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("q is " + ((Lookahead)object2).toString(",", this.charFormatter, this.grammar));
                    }
                    lookaheadArray[n] = ((Lookahead)object3).intersection((Lookahead)object2);
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("intersection at depth " + n + " is " + lookaheadArray[n].toString());
                    }
                    if (lookaheadArray[n].nil()) continue;
                    bl2 = true;
                    ++n;
                } while (bl2 && n <= this.grammar.maxk);
                object3 = alternativeBlock.getAlternativeAt(i);
                object2 = alternativeBlock.getAlternativeAt(j);
                if (bl2) {
                    bl = false;
                    ((Alternative)object3).lookaheadDepth = Integer.MAX_VALUE;
                    ((Alternative)object2).lookaheadDepth = Integer.MAX_VALUE;
                    if (((Alternative)object3).synPred != null) {
                        if (!this.DEBUG_ANALYZER) continue;
                        System.out.println("alt " + i + " has a syn pred");
                        continue;
                    }
                    if (((Alternative)object3).semPred != null) {
                        if (!this.DEBUG_ANALYZER) continue;
                        System.out.println("alt " + i + " has a sem pred");
                        continue;
                    }
                    if (this.altUsesWildcardDefault((Alternative)object2)) {
                        object = object2;
                        continue;
                    }
                    if (!alternativeBlock.warnWhenFollowAmbig && (((Alternative)object3).head instanceof BlockEndElement || ((Alternative)object2).head instanceof BlockEndElement) || !alternativeBlock.generateAmbigWarnings || alternativeBlock.greedySet && alternativeBlock.greedy && (((Alternative)object3).head instanceof BlockEndElement && !(((Alternative)object2).head instanceof BlockEndElement) || ((Alternative)object2).head instanceof BlockEndElement && !(((Alternative)object3).head instanceof BlockEndElement))) continue;
                    this.tool.errorHandler.warnAltAmbiguity(this.grammar, alternativeBlock, this.lexicalAnalysis, this.grammar.maxk, lookaheadArray, i, j);
                    continue;
                }
                ((Alternative)object3).lookaheadDepth = Math.max(((Alternative)object3).lookaheadDepth, n);
                ((Alternative)object2).lookaheadDepth = Math.max(((Alternative)object2).lookaheadDepth, n);
            }
        }
        this.currentBlock = alternativeBlock2;
        return bl;
    }

    public boolean deterministic(OneOrMoreBlock oneOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(...)+(" + oneOrMoreBlock + ")");
        }
        AlternativeBlock alternativeBlock = this.currentBlock;
        this.currentBlock = oneOrMoreBlock;
        boolean bl = this.deterministic((AlternativeBlock)oneOrMoreBlock);
        boolean bl2 = this.deterministicImpliedPath(oneOrMoreBlock);
        this.currentBlock = alternativeBlock;
        return bl2 && bl;
    }

    public boolean deterministic(ZeroOrMoreBlock zeroOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(...)*(" + zeroOrMoreBlock + ")");
        }
        AlternativeBlock alternativeBlock = this.currentBlock;
        this.currentBlock = zeroOrMoreBlock;
        boolean bl = this.deterministic((AlternativeBlock)zeroOrMoreBlock);
        boolean bl2 = this.deterministicImpliedPath(zeroOrMoreBlock);
        this.currentBlock = alternativeBlock;
        return bl2 && bl;
    }

    public boolean deterministicImpliedPath(BlockWithImpliedExitPath blockWithImpliedExitPath) {
        boolean bl = true;
        Vector vector = blockWithImpliedExitPath.getAlternatives();
        int n = vector.size();
        this.currentBlock.altj = -1;
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministicImpliedPath");
        }
        for (int i = 0; i < n; ++i) {
            Object object;
            boolean bl2;
            Alternative alternative = blockWithImpliedExitPath.getAlternativeAt(i);
            if (alternative.head instanceof BlockEndElement) {
                this.tool.warning("empty alternative makes no sense in (...)* or (...)+", this.grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
            }
            int n2 = 1;
            Lookahead[] lookaheadArray = new Lookahead[this.grammar.maxk + 1];
            do {
                Lookahead lookahead;
                bl2 = false;
                if (this.DEBUG_ANALYZER) {
                    System.out.println("checking depth " + n2 + "<=" + this.grammar.maxk);
                }
                blockWithImpliedExitPath.exitCache[n2] = lookahead = blockWithImpliedExitPath.next.look(n2);
                this.currentBlock.alti = i;
                object = this.getAltLookahead(blockWithImpliedExitPath, i, n2);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("follow is " + lookahead.toString(",", this.charFormatter, this.grammar));
                }
                if (this.DEBUG_ANALYZER) {
                    System.out.println("p is " + ((Lookahead)object).toString(",", this.charFormatter, this.grammar));
                }
                lookaheadArray[n2] = lookahead.intersection((Lookahead)object);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("intersection at depth " + n2 + " is " + lookaheadArray[n2]);
                }
                if (lookaheadArray[n2].nil()) continue;
                bl2 = true;
                ++n2;
            } while (bl2 && n2 <= this.grammar.maxk);
            if (bl2) {
                bl = false;
                alternative.lookaheadDepth = Integer.MAX_VALUE;
                blockWithImpliedExitPath.exitLookaheadDepth = Integer.MAX_VALUE;
                object = blockWithImpliedExitPath.getAlternativeAt(this.currentBlock.alti);
                if (!blockWithImpliedExitPath.warnWhenFollowAmbig || !blockWithImpliedExitPath.generateAmbigWarnings) continue;
                if (blockWithImpliedExitPath.greedy && blockWithImpliedExitPath.greedySet && !(((Alternative)object).head instanceof BlockEndElement)) {
                    if (!this.DEBUG_ANALYZER) continue;
                    System.out.println("greedy loop");
                    continue;
                }
                if (!blockWithImpliedExitPath.greedy && !(((Alternative)object).head instanceof BlockEndElement)) {
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("nongreedy loop");
                    }
                    if (LLkAnalyzer.lookaheadEquivForApproxAndFullAnalysis(blockWithImpliedExitPath.exitCache, this.grammar.maxk)) continue;
                    this.tool.warning(new String[]{"nongreedy block may exit incorrectly due", "\tto limitations of linear approximate lookahead (first k-1 sets", "\tin lookahead not singleton)."}, this.grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
                    continue;
                }
                this.tool.errorHandler.warnAltExitAmbiguity(this.grammar, blockWithImpliedExitPath, this.lexicalAnalysis, this.grammar.maxk, lookaheadArray, i);
                continue;
            }
            alternative.lookaheadDepth = Math.max(alternative.lookaheadDepth, n2);
            blockWithImpliedExitPath.exitLookaheadDepth = Math.max(blockWithImpliedExitPath.exitLookaheadDepth, n2);
        }
        return bl;
    }

    public Lookahead FOLLOW(int n, RuleEndElement ruleEndElement) {
        RuleBlock ruleBlock = (RuleBlock)ruleEndElement.block;
        String string = this.lexicalAnalysis ? CodeGenerator.encodeLexerRuleName(ruleBlock.getRuleName()) : ruleBlock.getRuleName();
        if (this.DEBUG_ANALYZER) {
            System.out.println("FOLLOW(" + n + "," + string + ")");
        }
        if (ruleEndElement.lock[n]) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("FOLLOW cycle to " + string);
            }
            return new Lookahead(string);
        }
        if (ruleEndElement.cache[n] != null) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("cache entry FOLLOW(" + n + ") for " + string + ": " + ruleEndElement.cache[n].toString(",", this.charFormatter, this.grammar));
            }
            if (ruleEndElement.cache[n].cycle == null) {
                return (Lookahead)ruleEndElement.cache[n].clone();
            }
            RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleEndElement.cache[n].cycle);
            RuleEndElement ruleEndElement2 = ruleSymbol.getBlock().endNode;
            if (ruleEndElement2.cache[n] == null) {
                return (Lookahead)ruleEndElement.cache[n].clone();
            }
            if (this.DEBUG_ANALYZER) {
                System.out.println("combining FOLLOW(" + n + ") for " + string + ": from " + ruleEndElement.cache[n].toString(",", this.charFormatter, this.grammar) + " with FOLLOW for " + ((RuleBlock)ruleEndElement2.block).getRuleName() + ": " + ruleEndElement2.cache[n].toString(",", this.charFormatter, this.grammar));
            }
            if (ruleEndElement2.cache[n].cycle == null) {
                ruleEndElement.cache[n].combineWith(ruleEndElement2.cache[n]);
                ruleEndElement.cache[n].cycle = null;
            } else {
                Lookahead lookahead = this.FOLLOW(n, ruleEndElement2);
                ruleEndElement.cache[n].combineWith(lookahead);
                ruleEndElement.cache[n].cycle = lookahead.cycle;
            }
            if (this.DEBUG_ANALYZER) {
                System.out.println("saving FOLLOW(" + n + ") for " + string + ": from " + ruleEndElement.cache[n].toString(",", this.charFormatter, this.grammar));
            }
            return (Lookahead)ruleEndElement.cache[n].clone();
        }
        ruleEndElement.lock[n] = true;
        Lookahead lookahead = new Lookahead();
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string);
        for (int i = 0; i < ruleSymbol.numReferences(); ++i) {
            RuleRefElement ruleRefElement = ruleSymbol.getReference(i);
            if (this.DEBUG_ANALYZER) {
                System.out.println("next[" + string + "] is " + ruleRefElement.next.toString());
            }
            Lookahead lookahead2 = ruleRefElement.next.look(n);
            if (this.DEBUG_ANALYZER) {
                System.out.println("FIRST of next[" + string + "] ptr is " + lookahead2.toString());
            }
            if (lookahead2.cycle != null && lookahead2.cycle.equals(string)) {
                lookahead2.cycle = null;
            }
            lookahead.combineWith(lookahead2);
            if (!this.DEBUG_ANALYZER) continue;
            System.out.println("combined FOLLOW[" + string + "] is " + lookahead.toString());
        }
        ruleEndElement.lock[n] = false;
        if (lookahead.fset.nil() && lookahead.cycle == null) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                lookahead.fset.add(3);
            } else if (this.grammar instanceof LexerGrammar) {
                lookahead.setEpsilon();
            } else {
                lookahead.fset.add(1);
            }
        }
        if (this.DEBUG_ANALYZER) {
            System.out.println("saving FOLLOW(" + n + ") for " + string + ": " + lookahead.toString(",", this.charFormatter, this.grammar));
        }
        ruleEndElement.cache[n] = (Lookahead)lookahead.clone();
        return lookahead;
    }

    private Lookahead getAltLookahead(AlternativeBlock alternativeBlock, int n, int n2) {
        Lookahead lookahead;
        Alternative alternative = alternativeBlock.getAlternativeAt(n);
        AlternativeElement alternativeElement = alternative.head;
        if (alternative.cache[n2] == null) {
            alternative.cache[n2] = lookahead = alternativeElement.look(n2);
        } else {
            lookahead = alternative.cache[n2];
        }
        return lookahead;
    }

    public Lookahead look(int n, ActionElement actionElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookAction(" + n + "," + actionElement + ")");
        }
        return actionElement.next.look(n);
    }

    public Lookahead look(int n, AlternativeBlock alternativeBlock) {
        Object object;
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookAltBlk(" + n + "," + alternativeBlock + ")");
        }
        AlternativeBlock alternativeBlock2 = this.currentBlock;
        this.currentBlock = alternativeBlock;
        Lookahead lookahead = new Lookahead();
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("alt " + i + " of " + alternativeBlock);
            }
            this.currentBlock.analysisAlt = i;
            object = alternativeBlock.getAlternativeAt(i);
            AlternativeElement alternativeElement = ((Alternative)object).head;
            if (this.DEBUG_ANALYZER && ((Alternative)object).head == ((Alternative)object).tail) {
                System.out.println("alt " + i + " is empty");
            }
            Lookahead lookahead2 = alternativeElement.look(n);
            lookahead.combineWith(lookahead2);
        }
        if (n == 1 && alternativeBlock.not && this.subruleCanBeInverted(alternativeBlock, this.lexicalAnalysis)) {
            if (this.lexicalAnalysis) {
                BitSet bitSet = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
                object = lookahead.fset.toArray();
                for (int i = 0; i < ((Object)object).length; ++i) {
                    bitSet.remove((int)object[i]);
                }
                lookahead.fset = bitSet;
            } else {
                lookahead.fset.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            }
        }
        this.currentBlock = alternativeBlock2;
        return lookahead;
    }

    public Lookahead look(int n, BlockEndElement blockEndElement) {
        Lookahead lookahead;
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookBlockEnd(" + n + ", " + blockEndElement.block + "); lock is " + blockEndElement.lock[n]);
        }
        if (blockEndElement.lock[n]) {
            return new Lookahead();
        }
        if (blockEndElement.block instanceof ZeroOrMoreBlock || blockEndElement.block instanceof OneOrMoreBlock) {
            blockEndElement.lock[n] = true;
            lookahead = this.look(n, blockEndElement.block);
            blockEndElement.lock[n] = false;
        } else {
            lookahead = new Lookahead();
        }
        if (blockEndElement.block instanceof TreeElement) {
            lookahead.combineWith(Lookahead.of(3));
        } else if (blockEndElement.block instanceof SynPredBlock) {
            lookahead.setEpsilon();
        } else {
            Lookahead lookahead2 = blockEndElement.block.next.look(n);
            lookahead.combineWith(lookahead2);
        }
        return lookahead;
    }

    public Lookahead look(int n, CharLiteralElement charLiteralElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookCharLiteral(" + n + "," + charLiteralElement + ")");
        }
        if (n > 1) {
            return charLiteralElement.next.look(n - 1);
        }
        if (this.lexicalAnalysis) {
            if (charLiteralElement.not) {
                BitSet bitSet = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
                if (this.DEBUG_ANALYZER) {
                    System.out.println("charVocab is " + bitSet.toString());
                }
                this.removeCompetingPredictionSets(bitSet, charLiteralElement);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("charVocab after removal of prior alt lookahead " + bitSet.toString());
                }
                bitSet.clear(charLiteralElement.getType());
                return new Lookahead(bitSet);
            }
            return Lookahead.of(charLiteralElement.getType());
        }
        this.tool.panic("Character literal reference found in parser");
        return Lookahead.of(charLiteralElement.getType());
    }

    public Lookahead look(int n, CharRangeElement charRangeElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookCharRange(" + n + "," + charRangeElement + ")");
        }
        if (n > 1) {
            return charRangeElement.next.look(n - 1);
        }
        BitSet bitSet = BitSet.of(charRangeElement.begin);
        for (int i = charRangeElement.begin + '\u0001'; i <= charRangeElement.end; ++i) {
            bitSet.add(i);
        }
        return new Lookahead(bitSet);
    }

    public Lookahead look(int n, GrammarAtom grammarAtom) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + n + "," + grammarAtom + "[" + grammarAtom.getType() + "])");
        }
        if (this.lexicalAnalysis) {
            this.tool.panic("token reference found in lexer");
        }
        if (n > 1) {
            return grammarAtom.next.look(n - 1);
        }
        Lookahead lookahead = Lookahead.of(grammarAtom.getType());
        if (grammarAtom.not) {
            int n2 = this.grammar.tokenManager.maxTokenType();
            lookahead.fset.notInPlace(4, n2);
            this.removeCompetingPredictionSets(lookahead.fset, grammarAtom);
        }
        return lookahead;
    }

    public Lookahead look(int n, OneOrMoreBlock oneOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look+" + n + "," + oneOrMoreBlock + ")");
        }
        Lookahead lookahead = this.look(n, (AlternativeBlock)oneOrMoreBlock);
        return lookahead;
    }

    public Lookahead look(int n, RuleBlock ruleBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleBlk(" + n + "," + ruleBlock + ")");
        }
        Lookahead lookahead = this.look(n, (AlternativeBlock)ruleBlock);
        return lookahead;
    }

    public Lookahead look(int n, RuleEndElement ruleEndElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleBlockEnd(" + n + "); noFOLLOW=" + ruleEndElement.noFOLLOW + "; lock is " + ruleEndElement.lock[n]);
        }
        if (ruleEndElement.noFOLLOW) {
            Lookahead lookahead = new Lookahead();
            lookahead.setEpsilon();
            lookahead.epsilonDepth = BitSet.of(n);
            return lookahead;
        }
        Lookahead lookahead = this.FOLLOW(n, ruleEndElement);
        return lookahead;
    }

    public Lookahead look(int n, RuleRefElement ruleRefElement) {
        RuleSymbol ruleSymbol;
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleRef(" + n + "," + ruleRefElement + ")");
        }
        if ((ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule)) == null || !ruleSymbol.defined) {
            this.tool.error("no definition of rule " + ruleRefElement.targetRule, this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            return new Lookahead();
        }
        RuleBlock ruleBlock = ruleSymbol.getBlock();
        RuleEndElement ruleEndElement = ruleBlock.endNode;
        boolean bl = ruleEndElement.noFOLLOW;
        ruleEndElement.noFOLLOW = true;
        Lookahead lookahead = this.look(n, ruleRefElement.targetRule);
        if (this.DEBUG_ANALYZER) {
            System.out.println("back from rule ref to " + ruleRefElement.targetRule);
        }
        ruleEndElement.noFOLLOW = bl;
        if (lookahead.cycle != null) {
            this.tool.error("infinite recursion to rule " + lookahead.cycle + " from rule " + ruleRefElement.enclosingRuleName, this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
        }
        if (lookahead.containsEpsilon()) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("rule ref to " + ruleRefElement.targetRule + " has eps, depth: " + lookahead.epsilonDepth);
            }
            lookahead.resetEpsilon();
            int[] nArray = lookahead.epsilonDepth.toArray();
            lookahead.epsilonDepth = null;
            for (int i = 0; i < nArray.length; ++i) {
                int n2 = n - (n - nArray[i]);
                Lookahead lookahead2 = ruleRefElement.next.look(n2);
                lookahead.combineWith(lookahead2);
            }
        }
        return lookahead;
    }

    public Lookahead look(int n, StringLiteralElement stringLiteralElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookStringLiteral(" + n + "," + stringLiteralElement + ")");
        }
        if (this.lexicalAnalysis) {
            if (n > stringLiteralElement.processedAtomText.length()) {
                return stringLiteralElement.next.look(n - stringLiteralElement.processedAtomText.length());
            }
            return Lookahead.of(stringLiteralElement.processedAtomText.charAt(n - 1));
        }
        if (n > 1) {
            return stringLiteralElement.next.look(n - 1);
        }
        Lookahead lookahead = Lookahead.of(stringLiteralElement.getType());
        if (stringLiteralElement.not) {
            int n2 = this.grammar.tokenManager.maxTokenType();
            lookahead.fset.notInPlace(4, n2);
        }
        return lookahead;
    }

    public Lookahead look(int n, SynPredBlock synPredBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look=>(" + n + "," + synPredBlock + ")");
        }
        return synPredBlock.next.look(n);
    }

    public Lookahead look(int n, TokenRangeElement tokenRangeElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookTokenRange(" + n + "," + tokenRangeElement + ")");
        }
        if (n > 1) {
            return tokenRangeElement.next.look(n - 1);
        }
        BitSet bitSet = BitSet.of(tokenRangeElement.begin);
        for (int i = tokenRangeElement.begin + 1; i <= tokenRangeElement.end; ++i) {
            bitSet.add(i);
        }
        return new Lookahead(bitSet);
    }

    public Lookahead look(int n, TreeElement treeElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + n + "," + treeElement.root + "[" + treeElement.root.getType() + "])");
        }
        if (n > 1) {
            return treeElement.next.look(n - 1);
        }
        Lookahead lookahead = null;
        if (treeElement.root instanceof WildcardElement) {
            lookahead = treeElement.root.look(1);
        } else {
            lookahead = Lookahead.of(treeElement.root.getType());
            if (treeElement.root.not) {
                int n2 = this.grammar.tokenManager.maxTokenType();
                lookahead.fset.notInPlace(4, n2);
            }
        }
        return lookahead;
    }

    public Lookahead look(int n, WildcardElement wildcardElement) {
        BitSet bitSet;
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + n + "," + wildcardElement + ")");
        }
        if (n > 1) {
            return wildcardElement.next.look(n - 1);
        }
        if (this.lexicalAnalysis) {
            bitSet = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
        } else {
            bitSet = new BitSet(1);
            int n2 = this.grammar.tokenManager.maxTokenType();
            bitSet.notInPlace(4, n2);
            if (this.DEBUG_ANALYZER) {
                System.out.println("look(" + n + "," + wildcardElement + ") after not: " + bitSet);
            }
        }
        return new Lookahead(bitSet);
    }

    public Lookahead look(int n, ZeroOrMoreBlock zeroOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look*(" + n + "," + zeroOrMoreBlock + ")");
        }
        Lookahead lookahead = this.look(n, (AlternativeBlock)zeroOrMoreBlock);
        Lookahead lookahead2 = zeroOrMoreBlock.next.look(n);
        lookahead.combineWith(lookahead2);
        return lookahead;
    }

    public Lookahead look(int n, String string) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleName(" + n + "," + string + ")");
        }
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string);
        RuleBlock ruleBlock = ruleSymbol.getBlock();
        if (ruleBlock.lock[n]) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("infinite recursion to rule " + ruleBlock.getRuleName());
            }
            return new Lookahead(string);
        }
        if (ruleBlock.cache[n] != null) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("found depth " + n + " result in FIRST " + string + " cache: " + ruleBlock.cache[n].toString(",", this.charFormatter, this.grammar));
            }
            return (Lookahead)ruleBlock.cache[n].clone();
        }
        ruleBlock.lock[n] = true;
        Lookahead lookahead = this.look(n, ruleBlock);
        ruleBlock.lock[n] = false;
        ruleBlock.cache[n] = (Lookahead)lookahead.clone();
        if (this.DEBUG_ANALYZER) {
            System.out.println("saving depth " + n + " result in FIRST " + string + " cache: " + ruleBlock.cache[n].toString(",", this.charFormatter, this.grammar));
        }
        return lookahead;
    }

    public static boolean lookaheadEquivForApproxAndFullAnalysis(Lookahead[] lookaheadArray, int n) {
        for (int i = 1; i <= n - 1; ++i) {
            BitSet bitSet = lookaheadArray[i].fset;
            if (bitSet.degree() <= 1) continue;
            return false;
        }
        return true;
    }

    private void removeCompetingPredictionSets(BitSet bitSet, AlternativeElement alternativeElement) {
        AlternativeElement alternativeElement2 = this.currentBlock.getAlternativeAt((int)this.currentBlock.analysisAlt).head;
        if (alternativeElement2 instanceof TreeElement ? ((TreeElement)alternativeElement2).root != alternativeElement : alternativeElement != alternativeElement2) {
            return;
        }
        for (int i = 0; i < this.currentBlock.analysisAlt; ++i) {
            AlternativeElement alternativeElement3 = this.currentBlock.getAlternativeAt((int)i).head;
            bitSet.subtractInPlace(alternativeElement3.look((int)1).fset);
        }
    }

    private void removeCompetingPredictionSetsFromWildcard(Lookahead[] lookaheadArray, AlternativeElement alternativeElement, int n) {
        for (int i = 1; i <= n; ++i) {
            for (int j = 0; j < this.currentBlock.analysisAlt; ++j) {
                AlternativeElement alternativeElement2 = this.currentBlock.getAlternativeAt((int)j).head;
                lookaheadArray[i].fset.subtractInPlace(alternativeElement2.look((int)i).fset);
            }
        }
    }

    private void reset() {
        this.grammar = null;
        this.DEBUG_ANALYZER = false;
        this.currentBlock = null;
        this.lexicalAnalysis = false;
    }

    public void setGrammar(Grammar grammar) {
        if (this.grammar != null) {
            this.reset();
        }
        this.grammar = grammar;
        this.lexicalAnalysis = this.grammar instanceof LexerGrammar;
        this.DEBUG_ANALYZER = this.grammar.analyzerDebug;
    }

    public boolean subruleCanBeInverted(AlternativeBlock alternativeBlock, boolean bl) {
        if (alternativeBlock instanceof ZeroOrMoreBlock || alternativeBlock instanceof OneOrMoreBlock || alternativeBlock instanceof SynPredBlock) {
            return false;
        }
        if (alternativeBlock.alternatives.size() == 0) {
            return false;
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (alternative.synPred != null || alternative.semPred != null || alternative.exceptionSpec != null) {
                return false;
            }
            AlternativeElement alternativeElement = alternative.head;
            if ((alternativeElement instanceof CharLiteralElement || alternativeElement instanceof TokenRefElement || alternativeElement instanceof CharRangeElement || alternativeElement instanceof TokenRangeElement || alternativeElement instanceof StringLiteralElement && !bl) && alternativeElement.next instanceof BlockEndElement && alternativeElement.getAutoGenType() == 1) continue;
            return false;
        }
        return true;
    }
}

