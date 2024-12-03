/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.joda.time.LocalDate
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import java.time.LocalDate;
import java.util.Map;

@ExperimentalApi
public interface ContentService {
    public static final String DEFAULT_EXPANSIONS = "body.storage,history,space,container.history,container.version,version,ancestors";

    public ContentFinder find(Expansion ... var1);

    public Content create(Content var1) throws ServiceException;

    public Content create(Content var1, Expansion ... var2) throws ServiceException;

    public Content update(Content var1) throws ServiceException;

    public void delete(Content var1) throws ServiceException;

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateDelete(Content var1);

        public ValidationResult validateCreate(Content var1) throws ServiceException;
    }

    public static interface ContentFetcher
    extends SingleContentFetcher {
        public PageResponse<Content> fetchMany(ContentType var1, PageRequest var2) throws ServiceException;

        public Map<ContentType, PageResponse<Content>> fetchMappedByContentType(PageRequest var1) throws ServiceException;
    }

    public static interface SingleContentFetcher
    extends SingleFetcher<Content> {
    }

    public static interface ParameterContentFinder
    extends ContentFetcher {
        public ParameterContentFinder withSpace(Space ... var1);

        public ParameterContentFinder withType(ContentType ... var1);

        @Deprecated
        default public ParameterContentFinder withCreatedDate(org.joda.time.LocalDate time) {
            return this.withCreatedDate(JodaTimeUtils.convert(time));
        }

        public ParameterContentFinder withCreatedDate(LocalDate var1);

        public ParameterContentFinder withTitle(String var1);

        public ParameterContentFinder withContainer(Container var1);

        public ParameterContentFinder withId(ContentId var1, ContentId ... var2);

        public ParameterContentFinder withId(Iterable<ContentId> var1);

        public ContentFinder withStatus(ContentStatus ... var1);

        public ContentFinder withStatus(Iterable<ContentStatus> var1);

        public ContentFinder withAnyStatus();
    }

    public static interface ContentFinder
    extends ParameterContentFinder {
        public SingleContentFetcher withId(ContentId var1);

        public SingleContentFetcher withIdAndVersion(ContentId var1, int var2);

        public SingleContentFetcher withLocator(ContentLocator var1);
    }
}

