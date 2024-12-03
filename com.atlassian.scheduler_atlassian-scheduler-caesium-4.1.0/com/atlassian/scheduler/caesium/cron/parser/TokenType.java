/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.parser;

enum TokenType {
    NUMBER,
    COMMA,
    HYPHEN,
    ASTERISK,
    SLASH,
    HASH,
    FLAG_L,
    FLAG_W,
    NAME,
    QUESTION_MARK,
    WHITESPACE{

        @Override
        boolean isFieldSeparator() {
            return true;
        }
    }
    ,
    NOTHING{

        @Override
        boolean isFieldSeparator() {
            return true;
        }
    }
    ,
    INVALID;


    boolean isFieldSeparator() {
        return false;
    }
}

