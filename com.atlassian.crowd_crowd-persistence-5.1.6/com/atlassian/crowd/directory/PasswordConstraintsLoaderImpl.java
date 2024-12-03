/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.PasswordScore
 *  com.atlassian.crowd.embedded.api.PasswordScoreService
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.PasswordConstraintsLoader;
import com.atlassian.crowd.directory.PasswordLengthConstraint;
import com.atlassian.crowd.directory.PasswordScoreConstraint;
import com.atlassian.crowd.directory.RegexConstraint;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.PasswordScore;
import com.atlassian.crowd.embedded.api.PasswordScoreService;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordConstraintsLoaderImpl
implements PasswordConstraintsLoader {
    private static final Logger logger = LoggerFactory.getLogger(PasswordConstraintsLoaderImpl.class);
    private final PasswordScoreService passwordScoreService;

    public PasswordConstraintsLoaderImpl(PasswordScoreService passwordScoreService) {
        this.passwordScoreService = passwordScoreService;
    }

    @Override
    public Set<PasswordConstraint> getFromDirectoryAttributes(long directoryId, Attributes attributes) {
        String minLength;
        PasswordScore minimumPasswordScore;
        ImmutableSet.Builder setBuilder = ImmutableSet.builder();
        String regex = attributes.getValue("password_regex");
        if (regex != null) {
            setBuilder.add((Object)new RegexConstraint(regex));
        }
        if ((minimumPasswordScore = this.getMinimumPasswordScore(directoryId, attributes)) != null) {
            setBuilder.add((Object)new PasswordScoreConstraint(minimumPasswordScore, this.passwordScoreService));
        }
        if ((minLength = attributes.getValue("password_minimum_length")) != null) {
            setBuilder.add((Object)new PasswordLengthConstraint(Integer.parseInt(minLength)));
        }
        return setBuilder.build();
    }

    @Nullable
    private PasswordScore getMinimumPasswordScore(long directoryId, Attributes attributes) {
        String scoreRanking = attributes.getValue("password_minimum_score");
        if (scoreRanking == null) {
            return null;
        }
        try {
            return PasswordScore.fromRanking((long)Long.parseLong(scoreRanking));
        }
        catch (IllegalArgumentException e) {
            logger.error("An invalid ranking for password score of {} was found in the database for directory {}", (Object)scoreRanking, (Object)directoryId);
            return null;
        }
    }
}

