/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.ArrayList;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.patterns.BasicToken;
import org.aspectj.weaver.patterns.IToken;
import org.aspectj.weaver.patterns.ITokenSource;

public class BasicTokenSource
implements ITokenSource {
    private int index = 0;
    private IToken[] tokens;
    private ISourceContext sourceContext;

    public BasicTokenSource(IToken[] tokens, ISourceContext sourceContext) {
        this.tokens = tokens;
        this.sourceContext = sourceContext;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int newIndex) {
        this.index = newIndex;
    }

    @Override
    public IToken next() {
        try {
            return this.tokens[this.index++];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return IToken.EOF;
        }
    }

    @Override
    public IToken peek() {
        try {
            return this.tokens[this.index];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return IToken.EOF;
        }
    }

    @Override
    public IToken peek(int offset) {
        try {
            return this.tokens[this.index + offset];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return IToken.EOF;
        }
    }

    public String toString() {
        IToken t;
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int i = 0; i < this.tokens.length && (t = this.tokens[i]) != null; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(t.toString());
        }
        buf.append("]");
        return buf.toString();
    }

    public static ITokenSource makeTokenSource(String input, ISourceContext context) {
        char[] chars = input.toCharArray();
        int i = 0;
        ArrayList<BasicToken> tokens = new ArrayList<BasicToken>();
        block8: while (i < chars.length) {
            char ch = chars[i++];
            switch (ch) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block8;
                }
                case '!': 
                case '(': 
                case ')': 
                case '*': 
                case '+': 
                case ',': 
                case ':': 
                case '<': 
                case '=': 
                case '>': 
                case '?': 
                case '@': 
                case '[': 
                case ']': {
                    tokens.add(BasicToken.makeOperator(BasicTokenSource.makeString(ch), i - 1, i - 1));
                    continue block8;
                }
                case '.': {
                    if (i + 2 <= chars.length) {
                        char nextChar1 = chars[i];
                        char nextChar2 = chars[i + 1];
                        if (ch == nextChar1 && ch == nextChar2) {
                            tokens.add(BasicToken.makeIdentifier("...", i - 1, i + 1));
                            i += 2;
                            continue block8;
                        }
                        tokens.add(BasicToken.makeOperator(BasicTokenSource.makeString(ch), i - 1, i - 1));
                        continue block8;
                    }
                    tokens.add(BasicToken.makeOperator(BasicTokenSource.makeString(ch), i - 1, i - 1));
                    continue block8;
                }
                case '&': {
                    if (i + 1 <= chars.length && chars[i] != '&') {
                        tokens.add(BasicToken.makeOperator(BasicTokenSource.makeString(ch), i - 1, i - 1));
                        continue block8;
                    }
                }
                case '|': {
                    char nextChar;
                    if (i == chars.length) {
                        throw new BCException("bad " + ch);
                    }
                    if ((nextChar = chars[i++]) == ch) {
                        tokens.add(BasicToken.makeOperator(BasicTokenSource.makeString(ch, 2), i - 2, i - 1));
                        continue block8;
                    }
                    throw new RuntimeException("bad " + ch);
                }
                case '\"': {
                    int start0 = i - 1;
                    while (i < chars.length && chars[i] != '\"') {
                        ++i;
                    }
                    tokens.add(BasicToken.makeLiteral(new String(chars, start0 + 1, ++i - start0 - 2), "string", start0, i - 1));
                    continue block8;
                }
            }
            int start = i - 1;
            while (i < chars.length && Character.isJavaIdentifierPart(chars[i])) {
                ++i;
            }
            tokens.add(BasicToken.makeIdentifier(new String(chars, start, i - start), start, i - 1));
        }
        return new BasicTokenSource(tokens.toArray(new IToken[tokens.size()]), context);
    }

    private static String makeString(char ch) {
        return Character.toString(ch);
    }

    private static String makeString(char ch, int count) {
        char[] chars = new char[count];
        for (int i = 0; i < count; ++i) {
            chars[i] = ch;
        }
        return new String(chars);
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.sourceContext;
    }

    public void setSourceContext(ISourceContext context) {
        this.sourceContext = context;
    }

    @Override
    public boolean hasMoreTokens() {
        return this.index < this.tokens.length;
    }
}

