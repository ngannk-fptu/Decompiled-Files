/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.Versioned
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDao
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.entity.AttachmentCheckMetadata;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.attachment.AttachmentPathService;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentContext;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentDto;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class MissingAttachmentChecker
implements Checker<MissingAttachmentContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MissingAttachmentChecker.class);
    private static final ForkJoinPool CHECK_RUNNER_POOL = new ForkJoinPool(Math.min((int)Math.ceil((double)Runtime.getRuntime().availableProcessors() / 2.0), 8));
    private static final int SPACE_BATCH_SIZE = 50;
    static final String MISSING_ATTACHMENT_FILE_PREFIX = "MissingAttachments";
    private static final String PAGE_ID_QUERY_PARAM = "pageId";
    private final AttachmentStore attachmentStore;
    private final AttachmentPathService attachmentPathService;
    private final AttachmentManager attachmentManager;
    private final String baseUrl;
    private final CheckResultFileManager checkResultFileManager;

    public MissingAttachmentChecker(AttachmentStore attachmentStore, AttachmentPathService attachmentPathService, SystemInformationService systemInformationService, CheckResultFileManager checkResultFileManager, AttachmentManager attachmentManager) {
        this.attachmentStore = attachmentStore;
        this.attachmentPathService = attachmentPathService;
        this.baseUrl = systemInformationService.getConfluenceInfo().getBaseUrl();
        this.checkResultFileManager = checkResultFileManager;
        this.attachmentManager = attachmentManager;
    }

    public CheckResult check(MissingAttachmentContext ctx) {
        log.info("Checking for missing attachments.");
        long start = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(ctx.spaceKeys)) {
            return new CheckResult(true);
        }
        try {
            List missingAttachments = (List)((ForkJoinTask)CHECK_RUNNER_POOL.submit(() -> ((Stream)Lists.partition(new ArrayList<String>(ctx.spaceKeys), (int)50).stream().parallel()).map(this::checkAttachmentsForSpaces).flatMap(Collection::stream).collect(Collectors.toList()))).get();
            long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
            log.info("Finished check for missing attachments in {} seconds", (Object)elapsedSeconds);
            if (missingAttachments.isEmpty()) {
                return new CheckResult(true, Collections.singletonMap("violationsCount", 0));
            }
            String path = this.checkResultFileManager.writeToJsonFile(MISSING_ATTACHMENT_FILE_PREFIX, missingAttachments);
            return new CheckResult(false, (Map)ImmutableMap.of((Object)"violationsCount", (Object)missingAttachments.size(), (Object)"path", (Object)path));
        }
        catch (Exception e) {
            log.error("Error executing missing attachments check.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    private List<MissingAttachmentDto> checkAttachmentsForSpaces(List<String> spaceKeys) {
        try (StatelessResults<AttachmentCheckMetadata> attachments = this.attachmentStore.getAttachmentsToCheck(spaceKeys);){
            List<MissingAttachmentDto> list = attachments.stream().flatMap(attachment -> this.checkIfMissing((AttachmentCheckMetadata)attachment).map(Stream::of).orElseGet(Stream::empty)).collect(Collectors.toList());
            return list;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Optional<MissingAttachmentDto> checkIfMissing(AttachmentCheckMetadata attachmentMetadata) {
        try {
            AttachmentDao attachmentDao = this.attachmentManager.getAttachmentDao();
            Attachment attachment = this.toConfluenceAttachmentStub(attachmentMetadata);
            try (InputStream ignored = attachmentDao.getAttachmentData(attachment);){
                Optional<MissingAttachmentDto> optional = Optional.empty();
                return optional;
            }
            catch (Exception ex) {
                return Optional.of(MissingAttachmentDto.builder().attachmentId(attachmentMetadata.getId()).pageId(attachmentMetadata.getContainerId()).spaceKey(attachmentMetadata.getSpaceKey()).name(attachmentMetadata.getTitle()).path(this.attachmentPathService.getAttachmentFilePath(attachmentMetadata)).url(this.getViewPageAttachment(attachmentMetadata.getContainerId())).build());
            }
        }
        catch (Exception e) {
            log.error("Error executing missing attachments check. attachment {id = {}, title = {}} ", new Object[]{attachmentMetadata.getId(), attachmentMetadata.getTitle(), e});
            throw e;
        }
    }

    private Attachment toConfluenceAttachmentStub(AttachmentCheckMetadata attachmentMetadata) {
        Attachment attachment = new Attachment();
        attachment.setId(attachmentMetadata.getId());
        attachment.setVersion(attachmentMetadata.getVersion());
        Page page = new Page();
        page.setId(attachmentMetadata.getContainerId());
        Space space = new Space(attachmentMetadata.getSpaceKey());
        space.setId(attachmentMetadata.getSpaceId());
        attachment.setSpace(space);
        page.setSpace(space);
        attachment.setContainer((ContentEntityObject)page);
        if (attachmentMetadata.getPreviousVersion() != null) {
            Attachment original = new Attachment();
            original.setId(attachmentMetadata.getPreviousVersion().longValue());
            attachment.setOriginalVersion((Versioned)original);
        }
        return attachment;
    }

    public static int retrieveMissingAttachmentsCount(Map<String, Object> details) {
        return Integer.parseInt(details.getOrDefault("violationsCount", 0).toString());
    }

    public String getViewPageAttachment(Long pageId) {
        return UriComponentsBuilder.fromHttpUrl((String)this.baseUrl).path("/pages/viewpageattachments.action").queryParam(PAGE_ID_QUERY_PARAM, new Object[]{pageId}).toUriString();
    }
}

