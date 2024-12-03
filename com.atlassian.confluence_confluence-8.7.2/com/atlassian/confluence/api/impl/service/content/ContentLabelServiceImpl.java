/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.api.service.content.ContentLabelService$Validator
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.model.validation.CoreValidationResultFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentLabelServiceImpl
implements ContentLabelService {
    private ContentService contentService;
    private LabelsService labelsService;
    private ContentEntityManagerInternal contentEntityManager;

    public ContentLabelServiceImpl(ContentService contentService, LabelsService labelsService, ContentEntityManagerInternal contentEntityManager) {
        this.contentService = contentService;
        this.labelsService = labelsService;
        this.contentEntityManager = contentEntityManager;
    }

    public PageResponse<com.atlassian.confluence.api.model.content.Label> getLabels(ContentId contentId, Collection<Label.Prefix> prefixes, PageRequest pageRequest) throws NotFoundException {
        Content content = this.getContentOrNotFound(contentId);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Labelable labelable = this.getLabelable(content);
        Iterable<Label> labels = this.filterLabels(prefixes, user, labelable.getLabels());
        Iterable transformed = StreamSupport.stream(labels.spliterator(), false).map(input -> new com.atlassian.confluence.api.model.content.Label(input.getNamespace().getPrefix(), input.getName(), String.valueOf(input.getId()))).collect(Collectors.toList());
        return this.paginate(pageRequest, transformed);
    }

    public PageResponse<com.atlassian.confluence.api.model.content.Label> addLabels(ContentId contentId, Iterable<com.atlassian.confluence.api.model.content.Label> labels) throws ServiceException {
        Content labelable = this.getContentOrNotFound(contentId);
        AddLabelsCommand command = this.getAddLabelsCommand(labelable, labels);
        ValidationResult result = this.validator().validateLabelsCommand(command);
        result.throwIfNotSuccessful("Could not add labels to content with id " + contentId);
        command.execute();
        return this.getLabels(contentId, Arrays.asList(Label.Prefix.values()), (PageRequest)new SimplePageRequest(0, PaginationLimits.labels()));
    }

    @Deprecated
    public void removeLabel(ContentId contentId, String label) throws ServiceException {
        this.removeLabel(contentId, com.atlassian.confluence.api.model.content.Label.builder((String)label).build());
    }

    public final void removeLabel(@NonNull ContentId contentId, @NonNull com.atlassian.confluence.api.model.content.Label label) {
        Content content = this.getContentOrNotFound(contentId);
        Labelable labelable = this.getLabelable(content);
        RemoveLabelCommand command = this.labelsService.newRemoveLabelCommand(labelable.getLabels().stream().filter(item -> Objects.equals(label.getLabel(), item.getName()) && Objects.equals(label.getPrefix(), item.getNamespace().getPrefix())).findFirst().orElseThrow(() -> new NotFoundException("No label found : " + label.serialise())), (User)AuthenticatedUserThreadLocal.get(), contentId.asLong());
        ValidationResult result = this.validator().validateLabelsCommand(command);
        result.throwIfNotSuccessful("Could not delete label from content with id " + contentId);
        command.execute();
    }

    private Content getContentOrNotFound(ContentId contentId) throws NotFoundException {
        return (Content)this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{ContentStatus.CURRENT, ContentStatus.DRAFT}).withId(contentId).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No content found with id : " + contentId)));
    }

    private PageResponse<com.atlassian.confluence.api.model.content.Label> paginate(PageRequest pageRequest, Iterable<com.atlassian.confluence.api.model.content.Label> labels) {
        int startPage;
        LimitedRequest from = LimitedRequestImpl.create((PageRequest)pageRequest, (int)PaginationLimits.labels());
        Iterable labelPages = Iterables.partition(labels, (int)from.getLimit());
        Iterator page = Iterables.skip((Iterable)labelPages, (int)(startPage = from.getStart() / from.getLimit())).iterator();
        if (page.hasNext()) {
            return PageResponseImpl.from((Iterable)((Iterable)page.next()), (boolean)page.hasNext()).pageRequest(from).build();
        }
        return PageResponseImpl.empty((boolean)false, (LimitedRequest)from);
    }

    private Iterable<Label> filterLabels(Collection<Label.Prefix> prefixes, User user, List<Label> labels) {
        Predicate prefixPredicate = input -> prefixes.isEmpty() || prefixes.contains(Label.Prefix.valueOf((String)input.getNamespace().getPrefix()));
        return labels.stream().filter(LabelUtil.labelPredicate(user, false)).filter(arg_0 -> ((Predicate)prefixPredicate).apply(arg_0)).collect(Collectors.toList());
    }

    private Labelable getLabelable(Content content) {
        return this.contentEntityManager.getById(content.getId());
    }

    private AddLabelsCommand getAddLabelsCommand(Content labelable, Iterable<com.atlassian.confluence.api.model.content.Label> labels) {
        String labelsString = new LabelHelper().concatenate(labels);
        return this.labelsService.newAddLabelCommand(labelsString, AuthenticatedUserThreadLocal.get(), labelable.getId().asLong(), labelable.getType().getType());
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    private static class LabelHelper {
        private LabelHelper() {
        }

        String concatenate(Iterable<com.atlassian.confluence.api.model.content.Label> labels) {
            StringBuilder fullLabel = new StringBuilder();
            for (com.atlassian.confluence.api.model.content.Label label : labels) {
                String prefix = StringUtils.isEmpty((CharSequence)label.getPrefix()) ? "" : label.getPrefix() + ":";
                fullLabel.append(prefix).append(label.getLabel()).append(" ");
            }
            return fullLabel.toString().trim();
        }
    }

    class ValidatorImpl
    implements ContentLabelService.Validator {
        ValidatorImpl() {
        }

        public ValidationResult validateAddLabels(ContentId contentId, com.atlassian.confluence.api.model.content.Label ... labels) {
            Optional labelable = ContentLabelServiceImpl.this.contentService.find(new Expansion[0]).withId(contentId).fetch();
            if (!labelable.isPresent()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return this.validateLabelsCommand(ContentLabelServiceImpl.this.getAddLabelsCommand((Content)labelable.get(), Arrays.asList(labels)));
        }

        ValidationResult validateLabelsCommand(ServiceCommand command) throws ServiceException {
            return CoreValidationResultFactory.create(command.isAuthorized(), command.getValidationErrors());
        }
    }
}

