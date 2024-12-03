/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.TokenCollector;

public class EscapedState
implements ParserState {
    ParserState previousState;

    public EscapedState(ParserState previousState) {
        this.previousState = previousState;
    }

    @Override
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
    }

    @Override
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleEscapedCharacter(character, tokenCollector);
    }

    @Override
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleEscapedCharacter(character, tokenCollector);
    }

    @Override
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleEscapedCharacter(character, tokenCollector);
    }

    @Override
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleEscapedCharacter(character, tokenCollector);
    }

    @Override
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleEscapedCharacter(character, tokenCollector);
    }

    private void handleEscapedCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
        tokenCollector.transitionState(this.previousState);
    }
}

