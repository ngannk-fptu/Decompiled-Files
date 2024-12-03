/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.FieldHandler
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.fields.UserFullnameFieldHandler;
import com.atlassian.confluence.plugins.cql.fields.UserKeyFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.FieldHandler;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSubFieldFactoryImpl
implements UserSubFieldFactory {
    private final SearchManager searchManager;
    private final PredefinedSearchBuilder searchBuilder;

    @Autowired
    public UserSubFieldFactoryImpl(@ComponentImport SearchManager searchManager, @ComponentImport PredefinedSearchBuilder searchBuilder) {
        this.searchManager = searchManager;
        this.searchBuilder = searchBuilder;
    }

    public List<FieldHandler> createUserSubfields(AbstractUserFieldHandler userFieldHandlerDelegate) {
        return ImmutableList.of((Object)((Object)new UserFullnameFieldHandler(userFieldHandlerDelegate, this.searchManager, this.searchBuilder)), (Object)((Object)new UserKeyFieldHandler(userFieldHandlerDelegate)));
    }
}

