/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.compiler;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.compiler.OpMapVector;
import org.apache.xpath.compiler.XPathParser;

class Lexer {
    private Compiler m_compiler;
    PrefixResolver m_namespaceContext;
    XPathParser m_processor;
    static final int TARGETEXTRA = 10000;
    private int[] m_patternMap = new int[100];
    private int m_patternMapSize;

    Lexer(Compiler compiler, PrefixResolver resolver, XPathParser xpathProcessor) {
        this.m_compiler = compiler;
        this.m_namespaceContext = resolver;
        this.m_processor = xpathProcessor;
    }

    void tokenize(String pat) throws TransformerException {
        this.tokenize(pat, null);
    }

    void tokenize(String pat, Vector targetStrings) throws TransformerException {
        this.m_compiler.m_currentPattern = pat;
        this.m_patternMapSize = 0;
        int initTokQueueSize = (pat.length() < 500 ? pat.length() : 500) * 5;
        this.m_compiler.m_opMap = new OpMapVector(initTokQueueSize, 2500, 1);
        int nChars = pat.length();
        int startSubstring = -1;
        int posOfNSSep = -1;
        boolean isStartOfPat = true;
        boolean isAttrName = false;
        boolean isNum = false;
        int nesting = 0;
        block9: for (int i = 0; i < nChars; ++i) {
            char c = pat.charAt(i);
            switch (c) {
                case '\"': {
                    if (startSubstring != -1) {
                        isNum = false;
                        isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                        isAttrName = false;
                        if (-1 != posOfNSSep) {
                            posOfNSSep = this.mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        } else {
                            this.addToTokenQueue(pat.substring(startSubstring, i));
                        }
                    }
                    startSubstring = i++;
                    while (i < nChars && (c = pat.charAt(i)) != '\"') {
                        ++i;
                    }
                    if (c == '\"' && i < nChars) {
                        this.addToTokenQueue(pat.substring(startSubstring, i + 1));
                        startSubstring = -1;
                        continue block9;
                    }
                    this.m_processor.error("ER_EXPECTED_DOUBLE_QUOTE", null);
                    continue block9;
                }
                case '\'': {
                    if (startSubstring != -1) {
                        isNum = false;
                        isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                        isAttrName = false;
                        if (-1 != posOfNSSep) {
                            posOfNSSep = this.mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        } else {
                            this.addToTokenQueue(pat.substring(startSubstring, i));
                        }
                    }
                    startSubstring = i++;
                    while (i < nChars && (c = pat.charAt(i)) != '\'') {
                        ++i;
                    }
                    if (c == '\'' && i < nChars) {
                        this.addToTokenQueue(pat.substring(startSubstring, i + 1));
                        startSubstring = -1;
                        continue block9;
                    }
                    this.m_processor.error("ER_EXPECTED_SINGLE_QUOTE", null);
                    continue block9;
                }
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    if (startSubstring == -1) continue block9;
                    isNum = false;
                    isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                    isAttrName = false;
                    if (-1 != posOfNSSep) {
                        posOfNSSep = this.mapNSTokens(pat, startSubstring, posOfNSSep, i);
                    } else {
                        this.addToTokenQueue(pat.substring(startSubstring, i));
                    }
                    startSubstring = -1;
                    continue block9;
                }
                case '@': {
                    isAttrName = true;
                }
                case '-': {
                    if ('-' == c) {
                        if (!isNum && startSubstring != -1) continue block9;
                        isNum = false;
                    }
                }
                case '!': 
                case '$': 
                case '(': 
                case ')': 
                case '*': 
                case '+': 
                case ',': 
                case '/': 
                case '<': 
                case '=': 
                case '>': 
                case '[': 
                case '\\': 
                case ']': 
                case '^': 
                case '|': {
                    if (startSubstring != -1) {
                        isNum = false;
                        isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                        isAttrName = false;
                        if (-1 != posOfNSSep) {
                            posOfNSSep = this.mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        } else {
                            this.addToTokenQueue(pat.substring(startSubstring, i));
                        }
                        startSubstring = -1;
                    } else if ('/' == c && isStartOfPat) {
                        isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                    } else if ('*' == c) {
                        isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
                        isAttrName = false;
                    }
                    if (0 == nesting && '|' == c) {
                        if (null != targetStrings) {
                            this.recordTokenString(targetStrings);
                        }
                        isStartOfPat = true;
                    }
                    if (')' == c || ']' == c) {
                        --nesting;
                    } else if ('(' == c || '[' == c) {
                        ++nesting;
                    }
                    this.addToTokenQueue(pat.substring(i, i + 1));
                    continue block9;
                }
                case ':': {
                    if (i > 0) {
                        if (posOfNSSep == i - 1) {
                            if (startSubstring != -1 && startSubstring < i - 1) {
                                this.addToTokenQueue(pat.substring(startSubstring, i - 1));
                            }
                            isNum = false;
                            isAttrName = false;
                            startSubstring = -1;
                            posOfNSSep = -1;
                            this.addToTokenQueue(pat.substring(i - 1, i + 1));
                            continue block9;
                        }
                        posOfNSSep = i;
                    }
                }
                default: {
                    if (-1 == startSubstring) {
                        startSubstring = i;
                        isNum = Character.isDigit(c);
                        continue block9;
                    }
                    if (!isNum) continue block9;
                    isNum = Character.isDigit(c);
                }
            }
        }
        if (startSubstring != -1) {
            isNum = false;
            isStartOfPat = this.mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            if (-1 != posOfNSSep || this.m_namespaceContext != null && this.m_namespaceContext.handlesNullPrefixes()) {
                posOfNSSep = this.mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
            } else {
                this.addToTokenQueue(pat.substring(startSubstring, nChars));
            }
        }
        if (0 == this.m_compiler.getTokenQueueSize()) {
            this.m_processor.error("ER_EMPTY_EXPRESSION", null);
        } else if (null != targetStrings) {
            this.recordTokenString(targetStrings);
        }
        this.m_processor.m_queueMark = 0;
    }

    private boolean mapPatternElemPos(int nesting, boolean isStart, boolean isAttrName) {
        if (0 == nesting) {
            if (this.m_patternMapSize >= this.m_patternMap.length) {
                int[] patternMap = this.m_patternMap;
                int len = this.m_patternMap.length;
                this.m_patternMap = new int[this.m_patternMapSize + 100];
                System.arraycopy(patternMap, 0, this.m_patternMap, 0, len);
            }
            if (!isStart) {
                int n = this.m_patternMapSize - 1;
                this.m_patternMap[n] = this.m_patternMap[n] - 10000;
            }
            this.m_patternMap[this.m_patternMapSize] = this.m_compiler.getTokenQueueSize() - (isAttrName ? 1 : 0) + 10000;
            ++this.m_patternMapSize;
            isStart = false;
        }
        return isStart;
    }

    private int getTokenQueuePosFromMap(int i) {
        int pos = this.m_patternMap[i];
        return pos >= 10000 ? pos - 10000 : pos;
    }

    private final void resetTokenMark(int mark) {
        int qsz = this.m_compiler.getTokenQueueSize();
        int n = mark > 0 ? (mark <= qsz ? mark - 1 : mark) : (this.m_processor.m_queueMark = 0);
        if (this.m_processor.m_queueMark < qsz) {
            this.m_processor.m_token = (String)this.m_compiler.getTokenQueue().elementAt(this.m_processor.m_queueMark++);
            this.m_processor.m_tokenChar = this.m_processor.m_token.charAt(0);
        } else {
            this.m_processor.m_token = null;
            this.m_processor.m_tokenChar = '\u0000';
        }
    }

    final int getKeywordToken(String key) {
        int tok;
        try {
            Integer itok = (Integer)Keywords.getKeyWord(key);
            tok = null != itok ? itok : 0;
        }
        catch (NullPointerException npe) {
            tok = 0;
        }
        catch (ClassCastException cce) {
            tok = 0;
        }
        return tok;
    }

    private void recordTokenString(Vector targetStrings) {
        int tokPos = this.getTokenQueuePosFromMap(this.m_patternMapSize - 1);
        this.resetTokenMark(tokPos + 1);
        if (this.m_processor.lookahead('(', 1)) {
            int tok = this.getKeywordToken(this.m_processor.m_token);
            switch (tok) {
                case 1030: {
                    targetStrings.addElement("#comment");
                    break;
                }
                case 1031: {
                    targetStrings.addElement("#text");
                    break;
                }
                case 1033: {
                    targetStrings.addElement("*");
                    break;
                }
                case 35: {
                    targetStrings.addElement("/");
                    break;
                }
                case 36: {
                    targetStrings.addElement("*");
                    break;
                }
                case 1032: {
                    targetStrings.addElement("*");
                    break;
                }
                default: {
                    targetStrings.addElement("*");
                    break;
                }
            }
        } else {
            if (this.m_processor.tokenIs('@')) {
                this.resetTokenMark(++tokPos + 1);
            }
            if (this.m_processor.lookahead(':', 1)) {
                tokPos += 2;
            }
            targetStrings.addElement(this.m_compiler.getTokenQueue().elementAt(tokPos));
        }
    }

    private final void addToTokenQueue(String s) {
        this.m_compiler.getTokenQueue().addElement(s);
    }

    private int mapNSTokens(String pat, int startSubstring, int posOfNSSep, int posOfScan) throws TransformerException {
        String uName;
        String prefix;
        block9: {
            prefix = "";
            if (startSubstring >= 0 && posOfNSSep >= 0) {
                prefix = pat.substring(startSubstring, posOfNSSep);
            }
            if (null != this.m_namespaceContext && !prefix.equals("*") && !prefix.equals("xmlns")) {
                try {
                    if (prefix.length() > 0) {
                        uName = this.m_namespaceContext.getNamespaceForPrefix(prefix);
                        break block9;
                    }
                    uName = this.m_namespaceContext.getNamespaceForPrefix(prefix);
                }
                catch (ClassCastException cce) {
                    uName = this.m_namespaceContext.getNamespaceForPrefix(prefix);
                }
            } else {
                uName = prefix;
            }
        }
        if (null != uName && uName.length() > 0) {
            this.addToTokenQueue(uName);
            this.addToTokenQueue(":");
            String s = pat.substring(posOfNSSep + 1, posOfScan);
            if (s.length() > 0) {
                this.addToTokenQueue(s);
            }
        } else {
            this.m_processor.errorForDOM3("ER_PREFIX_MUST_RESOLVE", new String[]{prefix});
        }
        return -1;
    }
}

