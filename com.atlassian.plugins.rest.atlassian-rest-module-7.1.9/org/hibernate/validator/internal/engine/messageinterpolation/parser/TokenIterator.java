/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.Token;

public class TokenIterator {
    private final List<Token> tokenList;
    private int currentPosition;
    private Token currentToken;
    private boolean allInterpolationTermsProcessed;
    private boolean currentTokenAvailable;

    public TokenIterator(List<Token> tokens) {
        this.tokenList = new ArrayList<Token>(tokens);
    }

    public boolean hasMoreInterpolationTerms() throws MessageDescriptorFormatException {
        while (this.currentPosition < this.tokenList.size()) {
            this.currentToken = this.tokenList.get(this.currentPosition);
            ++this.currentPosition;
            if (!this.currentToken.isParameter()) continue;
            this.currentTokenAvailable = true;
            return true;
        }
        this.allInterpolationTermsProcessed = true;
        return false;
    }

    public String nextInterpolationTerm() {
        if (!this.currentTokenAvailable) {
            throw new IllegalStateException("Trying to call #nextInterpolationTerm without calling #hasMoreInterpolationTerms");
        }
        this.currentTokenAvailable = false;
        return this.currentToken.getTokenValue();
    }

    public void replaceCurrentInterpolationTerm(String replacement) {
        Token token = new Token(replacement);
        token.terminate();
        this.tokenList.set(this.currentPosition - 1, token);
    }

    public String getInterpolatedMessage() {
        if (!this.allInterpolationTermsProcessed) {
            throw new IllegalStateException("Not all interpolation terms have been processed yet.");
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (Token token : this.tokenList) {
            messageBuilder.append(token.getTokenValue());
        }
        return messageBuilder.toString();
    }
}

