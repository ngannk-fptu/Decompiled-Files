/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$ParameterTemplateFinder
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$TemplateFinder
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.factory;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.plugins.createcontent.factory.FinderFactory;
import com.atlassian.confluence.plugins.createcontent.factory.TransactionWrappingFinder;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateFinderFactory
implements FinderFactory {
    private final TransactionalHostContextAccessor hostContextAccessor;

    @Autowired
    public TemplateFinderFactory(@ComponentImport TransactionalHostContextAccessor hostContextAccessor) {
        this.hostContextAccessor = hostContextAccessor;
    }

    @Override
    public ContentTemplateService.TemplateFinder createFinder(ContentTemplateService service, Expansion ... expansions) {
        TemplateFinderImpl rawFinder = new TemplateFinderImpl(service, expansions);
        return new TransactionWrappingContentFinder(rawFinder, this.hostContextAccessor);
    }

    private static class TransactionWrappingContentFinder
    extends TransactionWrappingFinder<ContentTemplate>
    implements ContentTemplateService.TemplateFinder {
        private final TemplateFinderImpl delegate;

        public TransactionWrappingContentFinder(TemplateFinderImpl delegate, TransactionalHostContextAccessor hostContextAccessor) {
            super(delegate, delegate, hostContextAccessor);
            this.delegate = delegate;
        }

        public SingleFetcher<ContentTemplate> withId(ContentTemplateId contentTemplateId) {
            this.delegate.withId(contentTemplateId);
            return this;
        }

        public ContentTemplateService.ParameterTemplateFinder withType(ContentTemplateType contentTemplateType) {
            this.delegate.withType(contentTemplateType);
            return this;
        }

        public ContentTemplateService.ParameterTemplateFinder withSpace(Space space) {
            this.delegate.withSpace(space);
            return this;
        }
    }

    private class TemplateFinderImpl
    implements SingleFetcher<ContentTemplate>,
    ContentTemplateService.TemplateFinder {
        private final ContentTemplateService contentTemplateService;
        private final Expansion[] expansions;
        private Optional<Space> space = Optional.empty();
        private ContentTemplateId contentTemplateId;
        private ContentTemplateType contentTemplateType;

        public TemplateFinderImpl(ContentTemplateService contentTemplateService, Expansion ... expansions) {
            this.expansions = expansions;
            this.contentTemplateService = contentTemplateService;
        }

        public SingleFetcher<ContentTemplate> withId(ContentTemplateId contentTemplateId) {
            Preconditions.checkNotNull((Object)contentTemplateId);
            this.contentTemplateId = contentTemplateId;
            return this;
        }

        public ContentTemplateService.ParameterTemplateFinder withType(ContentTemplateType contentTemplateType) {
            Preconditions.checkNotNull((Object)contentTemplateType);
            this.contentTemplateType = contentTemplateType;
            return this;
        }

        public ContentTemplateService.ParameterTemplateFinder withSpace(Space space) {
            Preconditions.checkNotNull((Object)space);
            this.space = Optional.of(space);
            return this;
        }

        public PageResponse<ContentTemplate> fetchMany(PageRequest pageRequest) {
            return this.contentTemplateService.getTemplates(this.contentTemplateType, this.space, pageRequest, this.expansions);
        }

        public Option<ContentTemplate> fetchOne() {
            return Option.option((Object)((ContentTemplate)this.fetchOrNull()));
        }

        public Optional<ContentTemplate> fetch() {
            if (this.contentTemplateId != null) {
                return Optional.ofNullable(this.contentTemplateService.getTemplate(this.contentTemplateId, this.expansions));
            }
            return this.fetchMany(SimplePageRequest.ONE).getResults().stream().findFirst();
        }

        public ContentTemplate fetchOneOrNull() {
            return (ContentTemplate)this.fetchOne().getOrNull();
        }
    }
}

