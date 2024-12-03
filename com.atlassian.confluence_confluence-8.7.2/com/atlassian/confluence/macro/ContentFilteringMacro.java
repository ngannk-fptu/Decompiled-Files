/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.MaxResultsParameter;
import com.atlassian.confluence.macro.params.SearchSortParameter;
import com.atlassian.confluence.macro.query.params.AuthorParameter;
import com.atlassian.confluence.macro.query.params.ContentTypeParameter;
import com.atlassian.confluence.macro.query.params.LabelParameter;
import com.atlassian.confluence.macro.query.params.SpaceKeyParameter;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public abstract class ContentFilteringMacro
extends BaseMacro {
    public static final String DEFAULT_MAX_RESULTS = "15";
    public static final String DEFAULT_SPACE_KEY = "@self";
    protected final LabelParameter labelParam = new LabelParameter();
    protected final ContentTypeParameter contentTypeParam = new ContentTypeParameter();
    protected final SpaceKeyParameter spaceKeyParam = new SpaceKeyParameter();
    protected final AuthorParameter authorParam = new AuthorParameter();
    protected final MaxResultsParameter maxResultsParam = new MaxResultsParameter();
    protected final SearchSortParameter sortParam = new SearchSortParameter();
    protected SearchManager searchManager;

    public ContentFilteringMacro() {
        this.labelParam.setValidate(true);
        this.spaceKeyParam.setValidate(true);
    }

    public final String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        if (!(renderContext instanceof PageContext)) {
            throw new MacroException("This macro is only usable in Confluence.");
        }
        MacroExecutionContext ctx = new MacroExecutionContext(params, body, (PageContext)renderContext);
        return this.execute(ctx);
    }

    protected abstract String execute(MacroExecutionContext var1) throws MacroException;

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
}

