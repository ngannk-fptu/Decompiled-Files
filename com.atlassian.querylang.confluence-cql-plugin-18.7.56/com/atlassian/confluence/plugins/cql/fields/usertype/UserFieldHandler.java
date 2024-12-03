/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.query.SearchQuery
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Strings
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields.usertype;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory;
import com.atlassian.confluence.plugins.cql.v2search.query.UserQuery;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.SearchQuery;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public final class UserFieldHandler
extends AbstractUserFieldHandler
implements TextFieldHandler {
    private static final String FIELD_NAME = "user";

    public UserFieldHandler(UserSubFieldFactory subFieldFactory) {
        super(FIELD_NAME, subFieldFactory);
    }

    public SearchQuery build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        TextFieldHandler handler = (TextFieldHandler)this.getSubfieldHandlersAsMap().get("fullname");
        return handler.build(expressionData, value);
    }

    protected com.atlassian.confluence.search.v2.SearchQuery createUserQuery(String username) {
        return this.createUserQuery(null, username);
    }

    public com.atlassian.confluence.search.v2.SearchQuery createUserQuery(UserKey key, String username) {
        if (key != null) {
            return new UserQuery(key.getStringValue(), UserQuery.UserQueryType.USERKEY);
        }
        if (!Strings.isNullOrEmpty((String)username)) {
            return new UserQuery(username, UserQuery.UserQueryType.USERNAME);
        }
        throw new NullPointerException("One of key or username need to not be provided to createUserQuery");
    }

    protected String getIndexFieldName() {
        return SearchFieldNames.USER_KEY;
    }
}

