/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.cron.ErrorCode
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.scheduler.caesium.cron.parser;

import com.atlassian.scheduler.caesium.cron.parser.CronLexer;
import com.atlassian.scheduler.caesium.cron.parser.TokenType;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.cron.ErrorCode;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

class NameResolver {
    private static final ImmutableMap<String, Integer> MONTH_NAMES = ImmutableMap.builder().put((Object)"JAN", (Object)1).put((Object)"FEB", (Object)2).put((Object)"MAR", (Object)3).put((Object)"APR", (Object)4).put((Object)"MAY", (Object)5).put((Object)"JUN", (Object)6).put((Object)"JUL", (Object)7).put((Object)"AUG", (Object)8).put((Object)"SEP", (Object)9).put((Object)"OCT", (Object)10).put((Object)"NOV", (Object)11).put((Object)"DEC", (Object)12).build();
    private static final ImmutableMap<String, Integer> DAY_OF_WEEK_NAMES = ImmutableMap.builder().put((Object)"SUN", (Object)1).put((Object)"MON", (Object)2).put((Object)"TUE", (Object)3).put((Object)"WED", (Object)4).put((Object)"THU", (Object)5).put((Object)"FRI", (Object)6).put((Object)"SAT", (Object)7).build();
    static final NameResolver MONTH = new NameResolver(MONTH_NAMES, ErrorCode.INVALID_NAME_MONTH);
    static final NameResolver DAY_OF_WEEK = new NameResolver(DAY_OF_WEEK_NAMES, ErrorCode.INVALID_NAME_DAY_OF_WEEK);
    private final Map<String, Integer> names;
    private final ErrorCode errorCode;

    private NameResolver(ImmutableMap<String, Integer> names, ErrorCode errorCode) {
        this.names = names;
        this.errorCode = errorCode;
    }

    final int resolveName(CronLexer.Token token) throws CronSyntaxException {
        if (token.getType() != TokenType.NAME) {
            throw new IllegalArgumentException("Called name resolver with something that wasn't a NAME: " + token);
        }
        String name = token.getText();
        Integer value = this.names.get(name);
        if (value == null) {
            throw CronSyntaxException.builder().cronExpression(token.getCronExpression()).errorCode(this.errorCode).errorOffset(token.getStart()).value(name).build();
        }
        return value;
    }
}

