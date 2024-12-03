/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTermType;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ELState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.EscapedState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.InterpolationTermState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.TokenCollector;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class MessageState
implements ParserState {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
    }

    @Override
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
    }

    @Override
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
        tokenCollector.appendToToken(character);
        if (tokenCollector.getInterpolationType().equals((Object)InterpolationTermType.PARAMETER)) {
            tokenCollector.makeParameterToken();
        }
        tokenCollector.transitionState(new InterpolationTermState());
    }

    @Override
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        throw LOG.getUnbalancedBeginEndParameterException(tokenCollector.getOriginalMessageDescriptor(), character);
    }

    @Override
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
        tokenCollector.transitionState(new EscapedState(this));
    }

    @Override
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        if (tokenCollector.getInterpolationType().equals((Object)InterpolationTermType.PARAMETER)) {
            this.handleNonMetaCharacter(character, tokenCollector);
        } else {
            tokenCollector.transitionState(new ELState());
        }
    }
}

