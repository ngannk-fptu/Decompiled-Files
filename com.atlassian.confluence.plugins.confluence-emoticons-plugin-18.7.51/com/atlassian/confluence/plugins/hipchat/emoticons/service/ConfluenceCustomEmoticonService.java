/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.SavableAttachment
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterators
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.ConfluenceCustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonException;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonGenerateThumbnailException;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonValidationException;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.ThumbnailManager;
import com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.ThumbnailSize;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Component
public class ConfluenceCustomEmoticonService
implements CustomEmoticonService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceCustomEmoticonService.class);
    private static final long DEFAULT_MAX_ALLOWED_UPLOADS = 2000L;
    public static final String MAX_ALLOWED_UPLOADS_ENV_VAR_NAME = "com.atlassian.confluence.plugins.emoticons.max.allowed.uploads";
    public static final long MAX_ALLOWED_UPLOADS = Long.getLong("com.atlassian.confluence.plugins.emoticons.max.allowed.uploads", 2000L);
    public static final String EMOTICON_SHORTCUT_KEY = "emoticon-shortcut";
    protected static final String LOCK_NAME = "custom-emoji-creation-lock";
    private final CustomContentManager contentManager;
    private final AttachmentManager attachmentManager;
    private final ContextPathHolder contextPathHolder;
    private final ClusterLockService clusterLockService;
    private final ThumbnailManager thumbnailManager;
    private final TransactionalHostContextAccessor transactionalHostContextAccessor;
    private static final long MAX_SHORTCUT_LENGTH = 50L;
    private static final long MAX_NAME_LENGTH = 50L;
    private static final String SHORTCUT_ALREADY_EXISTS = "SHORTCUT_ALREADY_EXISTS";
    private static final String UPLOAD_LIMIT_REACHED = "UPLOAD_LIMIT_REACHED";
    private static final String SHORTCUT_MISSING = "SHORTCUT_MISSING";
    private static final String SHORTCUT_TOO_LONG = "SHORTCUT_TOO_LONG";
    private static final String NAME_MISSING = "NAME_MISSING";
    private static final String NAME_TOO_LONG = "NAME_TOO_LONG";
    private static final String INVALID_INPUT_DISALLOWED_CHARS = "INVALID_INPUT_DISALLOWED_CHARS";
    private static final String INVALID_INPUT_XSS = "INVALID_INPUT_XSS";
    public static final Set<String> DisallowedCharsSet = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(":", "!", "@", "#", "%", "^", "&", "*", "(", ")")));
    private final Predicate<CustomContentEntityObject> doesEntityObjectHaveAttachmentsWithUrl = contentEntityObject -> {
        boolean doesEntityHaveAttachments;
        boolean bl = doesEntityHaveAttachments = !contentEntityObject.getAttachments().isEmpty();
        if (doesEntityHaveAttachments) {
            if (((Attachment)contentEntityObject.getAttachments().stream().findFirst().get()).getDownloadPath().isEmpty()) {
                log.debug("Emoticon with content ID {} has attachment with no URL", (Object)contentEntityObject.getContentId());
                return false;
            }
        } else {
            log.debug("Emoticon with content ID {} has no attachments", (Object)contentEntityObject.getContentId());
        }
        return doesEntityHaveAttachments;
    };

    public ConfluenceCustomEmoticonService(@ComponentImport CustomContentManager contentManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport ContextPathHolder contextPathHolder, @ComponentImport ClusterLockService clusterLockService, @ComponentImport TransactionalHostContextAccessor transactionalHostContextAccessor, ThumbnailManager thumbnailManager) {
        this.contentManager = contentManager;
        this.attachmentManager = attachmentManager;
        this.contextPathHolder = contextPathHolder;
        this.clusterLockService = clusterLockService;
        this.thumbnailManager = thumbnailManager;
        this.transactionalHostContextAccessor = transactionalHostContextAccessor;
    }

    protected void validateBeforeSave(CustomEmoticon emoticon) throws EmoticonValidationException {
        long numberOfExistingUploadedEmojis = StreamSupport.stream(this.list().spliterator(), false).count();
        if (numberOfExistingUploadedEmojis >= MAX_ALLOWED_UPLOADS) {
            throw new EmoticonValidationException(UPLOAD_LIMIT_REACHED);
        }
        if (this.findByShortcut(emoticon.getShortcut()).stream().findFirst().isPresent()) {
            throw new EmoticonValidationException(SHORTCUT_ALREADY_EXISTS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CustomEmoticon create(CustomEmoticon emoticon, SavableAttachment savableAttachment) throws EmoticonException {
        this.validateEmoticon(emoticon);
        ClusterLock lock = this.clusterLockService.getLockForName(LOCK_NAME);
        try {
            this.validateBeforeSave(emoticon);
            List<SavableAttachment> attachmentList = this.generateThumbnails(savableAttachment);
            attachmentList.stream().forEach(attachment -> attachment.getAttachment().setHidden(true));
            if (lock.tryLock(1L, TimeUnit.SECONDS)) {
                try {
                    this.validateBeforeSave(emoticon);
                    this.saveEmojiContentKeyAndSetAttachmentContainer(emoticon, attachmentList);
                }
                finally {
                    lock.unlock();
                }
            } else {
                throw new EmoticonException("Lock timed out: custom-emoji-creation-lock");
            }
            this.saveAttachment(attachmentList);
        }
        catch (InterruptedException e) {
            throw new EmoticonException("Emoticon lock interrupted", e);
        }
        catch (EmoticonException ex) {
            throw ex;
        }
        return emoticon;
    }

    private List<SavableAttachment> generateThumbnails(SavableAttachment savableAttachment) throws EmoticonException {
        ArrayList<SavableAttachment> attachmentList = new ArrayList<SavableAttachment>();
        attachmentList.add(savableAttachment);
        try {
            Collection<SavableAttachment> thumbnails = this.thumbnailManager.generateThumbnails(savableAttachment);
            attachmentList.addAll(thumbnails);
        }
        catch (ConversionException ex) {
            throw new EmoticonGenerateThumbnailException((Exception)((Object)ex));
        }
        return attachmentList;
    }

    private void validateEmoticon(CustomEmoticon emoticon) throws EmoticonValidationException {
        if (emoticon.getShortcut().isEmpty()) {
            throw new EmoticonValidationException(SHORTCUT_MISSING);
        }
        if ((long)emoticon.getShortcut().length() > 50L) {
            throw new EmoticonValidationException(SHORTCUT_TOO_LONG);
        }
        if (emoticon.getName().isEmpty()) {
            throw new EmoticonValidationException(NAME_MISSING);
        }
        if ((long)emoticon.getName().length() > 50L) {
            throw new EmoticonValidationException(NAME_TOO_LONG);
        }
        if (ConfluenceCustomEmoticonService.isContainsDisallowedChars(emoticon.getShortcut(), false) || ConfluenceCustomEmoticonService.isContainsDisallowedChars(emoticon.getName(), true)) {
            throw new EmoticonValidationException(INVALID_INPUT_DISALLOWED_CHARS);
        }
        if (!emoticon.getShortcut().equals(HtmlUtil.htmlEncode((String)emoticon.getShortcut())) || !emoticon.getName().equals(HtmlUtil.htmlEncode((String)emoticon.getName()))) {
            throw new EmoticonValidationException(INVALID_INPUT_XSS);
        }
    }

    public static boolean isContainsDisallowedChars(String val, boolean allowEmptySpace) throws EmoticonValidationException {
        if (val == null) {
            return false;
        }
        for (String letter : val.split("")) {
            if (DisallowedCharsSet.contains(letter)) {
                return true;
            }
            if (allowEmptySpace || !letter.isBlank()) continue;
            return true;
        }
        return false;
    }

    private void saveEmojiContentKeyAndSetAttachmentContainer(CustomEmoticon emoticon, List<SavableAttachment> savableAttachments) {
        CustomContentEntityObject content = this.contentManager.newPluginContentEntityObject("com.atlassian.confluence.plugins.confluence-emoticons-plugin:custom-emoticon");
        content.setTitle(Objects.requireNonNull(emoticon.getName()));
        content.getProperties().setStringProperty(EMOTICON_SHORTCUT_KEY, Objects.requireNonNull(emoticon.getShortcut()));
        this.contentManager.saveContentEntity((ContentEntityObject)content, DefaultSaveContext.SUPPRESS_NOTIFICATIONS);
        savableAttachments.stream().forEach(attachment -> attachment.getAttachment().setContainer((ContentEntityObject)content));
    }

    private void saveAttachment(List<SavableAttachment> savableAttachments) throws EmoticonException {
        try {
            for (SavableAttachment savableAttachment : savableAttachments) {
                this.attachmentManager.saveAttachments(Collections.singletonList(savableAttachment));
            }
        }
        catch (IOException e) {
            throw new EmoticonException("Emoji creation failed");
        }
    }

    @Override
    public Iterable<CustomEmoticon> list() {
        Iterator<CustomContentEntityObject> emoticonEntities = this.fetchCustomEmoticonEntities();
        ArrayList<CustomEmoticon> emoticons = new ArrayList<CustomEmoticon>();
        Iterators.filter(emoticonEntities, this.doesEntityObjectHaveAttachmentsWithUrl).forEachRemaining(emoticonEntity -> emoticons.add(new CustomEmoticonCEOAdapter((ContentEntityObject)emoticonEntity)));
        return emoticons;
    }

    @Override
    public Collection<CustomEmoticon> findByShortcut(String ... shortcuts) {
        return this.fetchCustomEmoticonEntityByShortcut(shortcuts).stream().map(x$0 -> new CustomEmoticonCEOAdapter((ContentEntityObject)x$0)).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> findIDByShortcut(String ... shortcuts) {
        HashMap<String, Long> shortcutToIDMap = new HashMap<String, Long>();
        Collection<CustomContentEntityObject> customContentEntityObjects = this.fetchCustomEmoticonEntityByShortcut(shortcuts);
        customContentEntityObjects.stream().forEach(cceo -> {
            CustomEmoticonCEOAdapter customEmoticonCEOAdapter = new CustomEmoticonCEOAdapter((ContentEntityObject)cceo);
            shortcutToIDMap.putIfAbsent(customEmoticonCEOAdapter.getShortcut(), customEmoticonCEOAdapter.getId());
        });
        return shortcutToIDMap;
    }

    @Override
    public void delete(String shortcut) {
        this.fetchCustomEmoticonEntityByShortcut(shortcut).stream().findFirst().ifPresent(arg_0 -> ((CustomContentManager)this.contentManager).removeContentEntity(arg_0));
    }

    @Override
    public void cleanupInvalidEmoticon() {
        AtomicInteger startIndex = new AtomicInteger(0);
        int maxResult = 20;
        AtomicBoolean shouldFetchMore = new AtomicBoolean(true);
        while (shouldFetchMore.get()) {
            try {
                this.transactionalHostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> {
                    List invalidEmoticonList = this.contentManager.queryForList(new ContentQuery("custom-emoticon.findInvalid", new Object[0]), startIndex.intValue(), 20);
                    if (invalidEmoticonList == null || invalidEmoticonList.size() == 0) {
                        log.debug("Could not find any invalid custom emoji. We will stop clean up");
                        shouldFetchMore.set(false);
                        return null;
                    }
                    if (invalidEmoticonList.size() < 20) {
                        log.debug("Last batch of invalid custom emoji contain {} item", (Object)invalidEmoticonList.size());
                        shouldFetchMore.set(false);
                    }
                    log.debug("Processing {} invalid custom emoji", (Object)invalidEmoticonList.size());
                    startIndex.set(startIndex.intValue() + 20);
                    invalidEmoticonList.stream().forEach(arg_0 -> ((CustomContentManager)this.contentManager).removeContentEntity(arg_0));
                    return null;
                });
            }
            catch (Exception ex) {
                log.warn("There is an exception happen while clean up invalid emoji", (Throwable)ex);
                shouldFetchMore.set(false);
            }
        }
    }

    private String prepareRelativeDownloadURL(Attachment attachment) {
        return attachment == null ? "" : this.contextPathHolder.getContextPath() + attachment.getDownloadPath();
    }

    private Iterator<CustomContentEntityObject> fetchCustomEmoticonEntities() {
        return this.contentManager.findByQuery(new ContentQuery("custom-emoticon.findAllEmoticons", new Object[0]), 0, Integer.MAX_VALUE);
    }

    private Collection<CustomContentEntityObject> fetchCustomEmoticonEntityByShortcut(String ... shortcuts) {
        return this.contentManager.queryForList(new ContentQuery("custom-emoticon.findByShortcut", new Object[]{Arrays.asList(shortcuts)}));
    }

    private class CustomEmoticonCEOAdapter
    extends ConfluenceCustomEmoticon {
        CustomEmoticonCEOAdapter(ContentEntityObject emoticonEntity) {
            super(emoticonEntity.getId(), emoticonEntity.getProperties().getStringProperty(ConfluenceCustomEmoticonService.EMOTICON_SHORTCUT_KEY), emoticonEntity.getTitle(), ConfluenceCustomEmoticonService.this.prepareRelativeDownloadURL((Attachment)emoticonEntity.getAttachments().stream().findFirst().get()), emoticonEntity.getCreator() != null ? emoticonEntity.getCreator().getName() : null, emoticonEntity.getCreationDate());
            Stream attachmentStream = emoticonEntity.getAttachments().stream();
            Optional<Attachment> smallAttachmentOptional = attachmentStream.filter(attachment -> attachment.getFileName().contains(ThumbnailSize.SMALL.getSize())).findFirst();
            smallAttachmentOptional.ifPresent(smallAttachment -> this.setURL(ConfluenceCustomEmoticonService.this.prepareRelativeDownloadURL((Attachment)smallAttachment)));
        }
    }
}

