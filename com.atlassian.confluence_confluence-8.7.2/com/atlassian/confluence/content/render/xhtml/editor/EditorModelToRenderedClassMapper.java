/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.xhtml.api.Link;
import org.apache.commons.lang3.StringUtils;

public class EditorModelToRenderedClassMapper
implements ModelToRenderedClassMapper {
    private final ModelToRenderedClassMapper viewMapper;

    public EditorModelToRenderedClassMapper(ModelToRenderedClassMapper viewMapper) {
        this.viewMapper = viewMapper;
    }

    @Override
    public String getRenderedClass(Link link) {
        String viewClasses = this.viewMapper.getRenderedClass(link);
        if (link.getDestinationResourceIdentifier() instanceof UrlResourceIdentifier) {
            return viewClasses;
        }
        Object editorClasses = "confluence-link";
        if (StringUtils.isNotBlank((CharSequence)viewClasses)) {
            editorClasses = (String)editorClasses + " " + viewClasses;
        }
        return editorClasses;
    }
}

