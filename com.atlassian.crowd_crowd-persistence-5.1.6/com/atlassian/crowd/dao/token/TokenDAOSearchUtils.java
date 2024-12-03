/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.model.token.Token
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.TokenTermKeys
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.model.token.Token;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.TokenTermKeys;

public class TokenDAOSearchUtils {
    public static boolean tokenMatchesSearchRestriction(Token token, SearchRestriction searchRestriction) {
        if (searchRestriction instanceof NullRestriction) {
            return true;
        }
        if (searchRestriction instanceof PropertyRestriction) {
            return TokenDAOSearchUtils.tokenMatchesTermRestriction(token, (PropertyRestriction)searchRestriction);
        }
        if (searchRestriction instanceof BooleanRestriction) {
            return TokenDAOSearchUtils.tokenMatchesMultiTermRestriction(token, (BooleanRestriction)searchRestriction);
        }
        throw new IllegalArgumentException("SearchRestriction unsupported: " + searchRestriction.getClass());
    }

    private static boolean tokenMatchesMultiTermRestriction(Token token, BooleanRestriction multiRestriction) {
        if (multiRestriction.getBooleanLogic() == BooleanRestriction.BooleanLogic.AND) {
            boolean match = true;
            for (SearchRestriction restriction : multiRestriction.getRestrictions()) {
                if (TokenDAOSearchUtils.tokenMatchesSearchRestriction(token, restriction)) continue;
                match = false;
                break;
            }
            return match;
        }
        if (multiRestriction.getBooleanLogic() == BooleanRestriction.BooleanLogic.OR) {
            boolean match = false;
            for (SearchRestriction restriction : multiRestriction.getRestrictions()) {
                if (!TokenDAOSearchUtils.tokenMatchesSearchRestriction(token, restriction)) continue;
                match = true;
                break;
            }
            return match;
        }
        throw new IllegalArgumentException("BooleanLogic unsupported: " + multiRestriction.getBooleanLogic().getClass());
    }

    private static boolean tokenMatchesTermRestriction(Token token, PropertyRestriction<?> restriction) {
        if (restriction.getProperty().equals(TokenTermKeys.NAME)) {
            String value = (String)restriction.getValue();
            switch (restriction.getMatchMode()) {
                case STARTS_WITH: {
                    return token.getName().startsWith(value);
                }
                case ENDS_WITH: {
                    return token.getName().endsWith(value);
                }
                case CONTAINS: {
                    return token.getName().contains(value);
                }
            }
            return token.getName().equals(value);
        }
        if (restriction.getProperty().equals(TokenTermKeys.LAST_ACCESSED_TIME)) {
            Long value = (Long)restriction.getValue();
            switch (restriction.getMatchMode()) {
                case GREATER_THAN: {
                    return token.getLastAccessedTime() > value;
                }
                case GREATER_THAN_OR_EQUAL: {
                    return token.getLastAccessedTime() >= value;
                }
                case LESS_THAN: {
                    return token.getLastAccessedTime() < value;
                }
                case LESS_THAN_OR_EQUAL: {
                    return token.getLastAccessedTime() <= value;
                }
            }
            return token.getLastAccessedTime() == value.longValue();
        }
        if (restriction.getProperty().equals(TokenTermKeys.DIRECTORY_ID)) {
            Long value = (Long)restriction.getValue();
            switch (restriction.getMatchMode()) {
                case GREATER_THAN: {
                    return token.getDirectoryId() > value;
                }
                case GREATER_THAN_OR_EQUAL: {
                    return token.getDirectoryId() >= value;
                }
                case LESS_THAN: {
                    return token.getDirectoryId() < value;
                }
                case LESS_THAN_OR_EQUAL: {
                    return token.getDirectoryId() <= value;
                }
            }
            return token.getDirectoryId() == value.longValue();
        }
        if (restriction.getProperty().equals(TokenTermKeys.RANDOM_NUMBER)) {
            Long value = (Long)restriction.getValue();
            switch (restriction.getMatchMode()) {
                case GREATER_THAN: {
                    return token.getRandomNumber() > value;
                }
                case GREATER_THAN_OR_EQUAL: {
                    return token.getRandomNumber() >= value;
                }
                case LESS_THAN: {
                    return token.getRandomNumber() < value;
                }
                case LESS_THAN_OR_EQUAL: {
                    return token.getRandomNumber() <= value;
                }
            }
            return token.getRandomNumber() == value.longValue();
        }
        throw new IllegalArgumentException("ProperyRestriction unsupported: " + restriction.getClass());
    }
}

