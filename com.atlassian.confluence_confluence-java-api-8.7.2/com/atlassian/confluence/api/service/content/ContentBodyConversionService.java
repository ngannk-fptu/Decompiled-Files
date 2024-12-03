/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;

@ExperimentalApi
public interface ContentBodyConversionService {
    public ContentBody convertBody(Content var1, ContentRepresentation var2, Expansion ... var3) throws ServiceException;

    public ContentBody convertBody(Content var1, ContentRepresentation var2) throws ServiceException;

    public ContentBody convert(ContentBody var1, ContentRepresentation var2, Expansion ... var3) throws ServiceException;

    public ContentBody convert(ContentBody var1, ContentRepresentation var2) throws ServiceException;

    @Deprecated
    default public Option<ContentBody> getBodyToConvert(Content content, ContentRepresentation toFormat) {
        return FugueConversionUtil.toComOption(this.selectBodyForRepresentation(content, toFormat));
    }

    public Optional<ContentBody> selectBodyForRepresentation(Content var1, ContentRepresentation var2);
}

