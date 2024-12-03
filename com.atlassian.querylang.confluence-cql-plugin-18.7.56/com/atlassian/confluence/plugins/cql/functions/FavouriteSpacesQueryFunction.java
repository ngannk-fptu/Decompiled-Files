/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.cql.functions;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.List;

public class FavouriteSpacesQueryFunction
extends CQLMultiValueQueryFunction {
    private LabelManager labelManager;

    public FavouriteSpacesQueryFunction(@ComponentImport LabelManager labelManager) {
        super("favouriteSpaces");
        this.labelManager = labelManager;
    }

    public int paramCount() {
        return 0;
    }

    public Iterable<String> invoke(List<String> params, CQLEvaluationContext context) {
        ArrayList<String> favouriteSpaceKeys = new ArrayList<String>();
        if (context.getCurrentUser().isEmpty()) {
            return favouriteSpaceKeys;
        }
        List favouriteSpaces = this.labelManager.getFavouriteSpaces((String)context.getCurrentUser().get());
        for (Space space : favouriteSpaces) {
            favouriteSpaceKeys.add(space.getKey());
        }
        return favouriteSpaceKeys;
    }
}

