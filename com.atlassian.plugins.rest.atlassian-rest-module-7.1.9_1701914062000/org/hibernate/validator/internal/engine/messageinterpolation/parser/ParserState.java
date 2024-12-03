/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.TokenCollector;

public interface ParserState {
    public void terminate(TokenCollector var1) throws MessageDescriptorFormatException;

    public void handleNonMetaCharacter(char var1, TokenCollector var2) throws MessageDescriptorFormatException;

    public void handleBeginTerm(char var1, TokenCollector var2) throws MessageDescriptorFormatException;

    public void handleEndTerm(char var1, TokenCollector var2) throws MessageDescriptorFormatException;

    public void handleEscapeCharacter(char var1, TokenCollector var2) throws MessageDescriptorFormatException;

    public void handleELDesignator(char var1, TokenCollector var2) throws MessageDescriptorFormatException;
}

