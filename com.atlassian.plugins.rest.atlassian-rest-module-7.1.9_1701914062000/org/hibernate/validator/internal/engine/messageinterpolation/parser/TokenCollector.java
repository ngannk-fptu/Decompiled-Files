/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.util.Collections;
import java.util.List;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTermType;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.Token;
import org.hibernate.validator.internal.util.CollectionHelper;

public class TokenCollector {
    private final String originalMessageDescriptor;
    private final InterpolationTermType interpolationTermType;
    private final List<Token> tokenList;
    private ParserState currentParserState;
    private int currentPosition;
    private Token currentToken;

    public TokenCollector(String originalMessageDescriptor, InterpolationTermType interpolationTermType) throws MessageDescriptorFormatException {
        this.originalMessageDescriptor = originalMessageDescriptor;
        this.interpolationTermType = interpolationTermType;
        this.currentParserState = new MessageState();
        this.tokenList = CollectionHelper.newArrayList();
        this.parse();
    }

    public void terminateToken() {
        if (this.currentToken == null) {
            return;
        }
        this.currentToken.terminate();
        this.tokenList.add(this.currentToken);
        this.currentToken = null;
    }

    public void appendToToken(char character) {
        if (this.currentToken == null) {
            this.currentToken = new Token(character);
        } else {
            this.currentToken.append(character);
        }
    }

    public void makeParameterToken() {
        this.currentToken.makeParameterToken();
    }

    public void makeELToken() {
        this.currentToken.makeELToken();
    }

    private void next() throws MessageDescriptorFormatException {
        if (this.currentPosition == this.originalMessageDescriptor.length()) {
            this.currentParserState.terminate(this);
            ++this.currentPosition;
            return;
        }
        char currentCharacter = this.originalMessageDescriptor.charAt(this.currentPosition);
        ++this.currentPosition;
        switch (currentCharacter) {
            case '{': {
                this.currentParserState.handleBeginTerm(currentCharacter, this);
                break;
            }
            case '}': {
                this.currentParserState.handleEndTerm(currentCharacter, this);
                break;
            }
            case '$': {
                this.currentParserState.handleELDesignator(currentCharacter, this);
                break;
            }
            case '\\': {
                this.currentParserState.handleEscapeCharacter(currentCharacter, this);
                break;
            }
            default: {
                this.currentParserState.handleNonMetaCharacter(currentCharacter, this);
            }
        }
    }

    public final void parse() throws MessageDescriptorFormatException {
        while (this.currentPosition <= this.originalMessageDescriptor.length()) {
            this.next();
        }
    }

    public void transitionState(ParserState newState) {
        this.currentParserState = newState;
    }

    public InterpolationTermType getInterpolationType() {
        return this.interpolationTermType;
    }

    public List<Token> getTokenList() {
        return Collections.unmodifiableList(this.tokenList);
    }

    public String getOriginalMessageDescriptor() {
        return this.originalMessageDescriptor;
    }
}

