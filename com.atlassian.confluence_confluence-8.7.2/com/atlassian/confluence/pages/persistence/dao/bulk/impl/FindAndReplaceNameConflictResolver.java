/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;
import com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class FindAndReplaceNameConflictResolver
implements PageNameConflictResolver {
    private static final int MAX_RETRY = 10;
    private String replaceString;
    private final Pattern pattern;

    public FindAndReplaceNameConflictResolver(String searchString, String replaceString) {
        Preconditions.checkNotNull((Object)searchString);
        Preconditions.checkArgument((boolean)StringUtils.isNotEmpty((CharSequence)searchString));
        Preconditions.checkNotNull((Object)replaceString);
        this.replaceString = replaceString;
        this.pattern = Pattern.compile(Pattern.quote(searchString), 2);
    }

    @Override
    public boolean couldProvideNewName() {
        return true;
    }

    @Override
    public int getMaxRetryNumber() {
        return 10;
    }

    @Override
    public String resolveConflict(int currentRetryNumber, String originalName) {
        return this.pattern.matcher(originalName).replaceAll(this.replaceString);
    }
}

