/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ASdebug.ASDebugStream;
import antlr.ASdebug.IASDebugStream;
import antlr.ASdebug.TokenOffsetInfo;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamRetryException;
import antlr.collections.Stack;
import antlr.collections.impl.LList;
import java.util.Hashtable;

public class TokenStreamSelector
implements TokenStream,
IASDebugStream {
    protected Hashtable inputStreamNames;
    protected TokenStream input;
    protected Stack streamStack = new LList();

    public TokenStreamSelector() {
        this.inputStreamNames = new Hashtable();
    }

    public void addInputStream(TokenStream tokenStream, String string) {
        this.inputStreamNames.put(string, tokenStream);
    }

    public TokenStream getCurrentStream() {
        return this.input;
    }

    public TokenStream getStream(String string) {
        TokenStream tokenStream = (TokenStream)this.inputStreamNames.get(string);
        if (tokenStream == null) {
            throw new IllegalArgumentException("TokenStream " + string + " not found");
        }
        return tokenStream;
    }

    public Token nextToken() throws TokenStreamException {
        while (true) {
            try {
                return this.input.nextToken();
            }
            catch (TokenStreamRetryException tokenStreamRetryException) {
                continue;
            }
            break;
        }
    }

    public TokenStream pop() {
        TokenStream tokenStream = (TokenStream)this.streamStack.pop();
        this.select(tokenStream);
        return tokenStream;
    }

    public void push(TokenStream tokenStream) {
        this.streamStack.push(this.input);
        this.select(tokenStream);
    }

    public void push(String string) {
        this.streamStack.push(this.input);
        this.select(string);
    }

    public void retry() throws TokenStreamRetryException {
        throw new TokenStreamRetryException();
    }

    public void select(TokenStream tokenStream) {
        this.input = tokenStream;
    }

    public void select(String string) throws IllegalArgumentException {
        this.input = this.getStream(string);
    }

    public String getEntireText() {
        return ASDebugStream.getEntireText(this.input);
    }

    public TokenOffsetInfo getOffsetInfo(Token token) {
        return ASDebugStream.getOffsetInfo(this.input, token);
    }
}

