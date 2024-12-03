/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping;

public class MappingExpressionException
extends RuntimeException {
    private final int indexOfException;
    private final String friendlyMessage;

    public MappingExpressionException(String message, String friendlyMessage, int indexOfException) {
        super(message);
        this.friendlyMessage = friendlyMessage;
        this.indexOfException = indexOfException;
    }

    public MappingExpressionException(String message, int indexOfException) {
        this(message, message, indexOfException);
    }

    public String getFriendlyMessage() {
        return this.friendlyMessage;
    }

    public int getIndexOfException() {
        return this.indexOfException;
    }

    public static MappingExpressionException bracketAlreadyOpened(int invalidBracketIndex) {
        return new MappingExpressionException(String.format("Invalid opening bracket at index %d: a bracket is already opened", invalidBracketIndex), "The variable has already been open. Remove redundant brackets.", invalidBracketIndex);
    }

    public static MappingExpressionException bracketAlreadyClosed(int invalidBracketIndex) {
        return new MappingExpressionException(String.format("Invalid closing bracket at index %d: a bracket is already closed", invalidBracketIndex), "The variable has already been closed. Remove redundant brackets.", invalidBracketIndex);
    }

    public static MappingExpressionException missingClosingBracket(int openingBracketIndex) {
        return new MappingExpressionException(String.format("Invalid expression: closing bracket is missing for opening bracket at %d", openingBracketIndex), "The closing bracket is missing. Make sure all variables have been closed.", openingBracketIndex);
    }

    public static MappingExpressionException emptyVariable(int emptyVariableIndex) {
        return new MappingExpressionException(String.format("Invalid substitution variable starting at index %d: substitution variables may not be empty", emptyVariableIndex - 1), "Empty variable. Enter at least one character for your variable.", emptyVariableIndex - 1);
    }

    public static MappingExpressionException invalidOpeningBracket(int openingBracketIndex) {
        return new MappingExpressionException(String.format("Invalid opening bracket at index %d: bracket is not preceded by '$'", openingBracketIndex), "Incorrect opening bracket. Make sure that all opening brackets are preceded by '$', e.g. \"${name}\"", openingBracketIndex);
    }
}

