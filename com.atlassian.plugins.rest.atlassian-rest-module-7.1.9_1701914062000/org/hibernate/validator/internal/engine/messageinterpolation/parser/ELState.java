/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.EscapedState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.InterpolationTermState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.TokenCollector;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ELState
implements ParserState {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken('$');
        tokenCollector.terminateToken();
    }

    @Override
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken('$');
        tokenCollector.appendToToken(character);
        tokenCollector.terminateToken();
        tokenCollector.transitionState(new MessageState());
    }

    @Override
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
        tokenCollector.appendToToken('$');
        tokenCollector.appendToToken(character);
        tokenCollector.makeELToken();
        tokenCollector.transitionState(new InterpolationTermState());
    }

    @Override
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        throw LOG.getUnbalancedBeginEndParameterException(tokenCollector.getOriginalMessageDescriptor(), character);
    }

    @Override
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken('$');
        tokenCollector.appendToToken(character);
        MessageState stateAfterEscape = new MessageState();
        tokenCollector.transitionState(new EscapedState(stateAfterEscape));
    }

    @Override
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        this.handleNonMetaCharacter(character, tokenCollector);
    }
}

