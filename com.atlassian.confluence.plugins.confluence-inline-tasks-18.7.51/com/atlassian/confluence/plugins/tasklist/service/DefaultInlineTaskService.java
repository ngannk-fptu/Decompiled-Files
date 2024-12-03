/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.diffs.MergeResult
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.TasksFinderViaSearchIndex;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.TaskReportIndexPersistedStateService;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskResponse;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.service.TaskPaginationService;
import com.atlassian.confluence.plugins.tasklist.transformer.TaskVisitor;
import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import com.atlassian.confluence.plugins.tasklist.transformer.xml.ParsingContext;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultInlineTaskService
implements InlineTaskService {
    private static final Logger log = LoggerFactory.getLogger(DefaultInlineTaskService.class);
    private static final int MAX_LIMIT_FOR_REQUEST = 5000;
    private final PageManager pageManager;
    private final DraftManager draftManager;
    private final XmlEventReaderFactory readerFactory;
    private final XMLEventFactory xmlEventFactory;
    private final XmlOutputFactory writerFactory;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final PermissionManager permissionManager;
    private final ClusterLockService lockFactory;
    private final InlineTaskDao inlineTaskDao;
    private final PaginationService paginationService;
    private final UserAccessor userAccessor;
    private final DarkFeatureManager darkFeatureManager;
    private final TasksFinderViaSearchIndex tasksFinderViaSearchIndex;
    private final TaskReportIndexPersistedStateService taskReportIndexPersistedStateService;
    private final Supplier<Cache<String, String>> lastModifiedTasks;
    private static final String CACHE_NAME = DefaultInlineTaskService.class.getName() + ".lastModifiedTasks";
    private static final String NOTIFICATION_BATCH_DARKFEATURE = "notification.batch";
    private static final String INLINE_TASK_COMPLETED_KEY = "inline.task.completed";
    private static final String INLINE_TASK_UNCOMPLETED_KEY = "inline.task.uncompleted";
    private static final String USE_DATABASE_FOR_TASK_REPORT_MACRO_DARK_FEATURE_NAME = "confluence.task-report.use-database-for-reports";

    @Autowired
    public DefaultInlineTaskService(PageManager pageManager, DraftManager draftManager, @ConfluenceImport ClusterLockService lockFactory, XmlEventReaderFactory inFactory, XMLEventFactoryProvider xmlEventFactory, @Qualifier(value="xmlOutputFactory") XmlOutputFactory outFactory, @Qualifier(value="i18NBeanFactory") I18NBeanFactory i18nfactory, LocaleManager lm, PermissionManager pm, InlineTaskDao inlineTaskDao, PaginationService paginationService, UserAccessor userAccessor, @ConfluenceImport CacheManager cacheFactory, DarkFeatureManager darkFeatureManager, TasksFinderViaSearchIndex tasksFinderViaSearchIndex, TaskReportIndexPersistedStateService taskReportIndexPersistedStateService) {
        this.pageManager = pageManager;
        this.draftManager = draftManager;
        this.readerFactory = inFactory;
        this.inlineTaskDao = inlineTaskDao;
        this.paginationService = paginationService;
        this.userAccessor = userAccessor;
        this.darkFeatureManager = darkFeatureManager;
        this.xmlEventFactory = xmlEventFactory.getXmlEventFactory();
        this.writerFactory = outFactory;
        this.i18NBeanFactory = i18nfactory;
        this.localeManager = lm;
        this.permissionManager = pm;
        this.lockFactory = lockFactory;
        this.lastModifiedTasks = Lazy.supplier(() -> cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(10L, TimeUnit.SECONDS).build()));
        this.tasksFinderViaSearchIndex = tasksFinderViaSearchIndex;
        this.taskReportIndexPersistedStateService = taskReportIndexPersistedStateService;
    }

    @Override
    public InlineTaskResponse setTaskStatus(ContentEntityObject cob, final String taskId, final TaskStatus newStatus, PageUpdateTrigger trigger) throws NotPermittedException {
        final AtomicBoolean found = new AtomicBoolean(false);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)cob)) {
            throw new NotPermittedException("Object not found, or you lack sufficient permission to view/edit the entity");
        }
        if (cob == null) {
            throw new IllegalArgumentException("Invalid contentId, or not sufficient permissions");
        }
        StringReader reader = new StringReader(cob.getBodyAsString());
        DefaultConversionContext ctx = new DefaultConversionContext((RenderContext)cob.toPageContext());
        try {
            String xmlBody = TaskVisitor.transformTask(this.readerFactory, this.writerFactory, this.xmlEventFactory, reader, (ConversionContext)ctx, 0L, new TaskVisitor.VisitTask(){

                @Override
                public boolean consumeTaskIfHandled(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
                    while (xmlReader.hasNext()) {
                        if (xmlReader.peek().isStartElement() && StorageInlineTaskConstants.TASK_ELEMENT.equals(xmlReader.peek().asStartElement().getName())) {
                            boolean foundMatch = false;
                            xmlWriter.add(xmlReader.nextEvent());
                            while (xmlReader.hasNext()) {
                                XMLEvent nextEvent = xmlReader.peek();
                                if (nextEvent.isStartElement() && StorageInlineTaskConstants.TASK_ID_ELEMENT.equals(nextEvent.asStartElement().getName())) {
                                    foundMatch = this.handleIdAndCheckIfMatch(xmlReader, xmlWriter);
                                    continue;
                                }
                                if (nextEvent.isStartElement() && StorageInlineTaskConstants.TASK_STATUS_ELEMENT.equals(nextEvent.asStartElement().getName())) {
                                    this.handleStatus(context, xmlReader, xmlWriter, foundMatch);
                                    continue;
                                }
                                xmlWriter.add(xmlReader.nextEvent());
                            }
                            return true;
                        }
                        XMLEvent event = xmlReader.nextEvent();
                        xmlWriter.add(event);
                        if (!event.isEndElement() || !StorageInlineTaskConstants.TASK_ELEMENT.equals(event.asEndElement().getName())) continue;
                        break;
                    }
                    return true;
                }

                private boolean handleIdAndCheckIfMatch(XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
                    boolean foundMatch = false;
                    xmlWriter.add(xmlReader.nextEvent());
                    XMLEvent taskIdEvent = xmlReader.nextEvent();
                    if (taskIdEvent.isCharacters() && taskIdEvent.asCharacters().getData().equals(taskId)) {
                        foundMatch = true;
                        found.set(true);
                    }
                    xmlWriter.add(taskIdEvent);
                    xmlWriter.add(xmlReader.nextEvent());
                    return foundMatch;
                }

                private void handleStatus(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter, boolean matchFound) throws XMLStreamException {
                    String taskStatus;
                    xmlWriter.add(xmlReader.nextEvent());
                    XMLEvent taskStatusEvent = xmlReader.nextEvent();
                    if (matchFound && !(taskStatus = taskStatusEvent.asCharacters().getData()).equals(this.storageEquivalent(newStatus))) {
                        taskStatusEvent = context.getEventFactory().createCharacters(this.storageEquivalent(newStatus));
                    }
                    xmlWriter.add(taskStatusEvent);
                    xmlWriter.add(xmlReader.nextEvent());
                }

                private String storageEquivalent(TaskStatus status) {
                    if (status == TaskStatus.CHECKED) {
                        return StorageInlineTaskConstants.TASK_STATUS_COMPLETE;
                    }
                    return StorageInlineTaskConstants.TASK_STATUS_INCOMPLETE;
                }
            });
            if (found.get()) {
                I18NBean bean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
                String versionComment = this.darkFeatureManager.isFeatureEnabledForAllUsers(NOTIFICATION_BATCH_DARKFEATURE) ? null : (newStatus.equals((Object)TaskStatus.CHECKED) ? bean.getText(INLINE_TASK_COMPLETED_KEY) : bean.getText(INLINE_TASK_UNCOMPLETED_KEY));
                return this.updatePage(cob, taskId, xmlBody, versionComment, trigger);
            }
        }
        catch (XhtmlException e) {
            log.error(e.toString(), (Throwable)e);
            throw new RuntimeException("Internal error", e);
        }
        catch (CloneNotSupportedException e) {
            log.error(e.toString(), (Throwable)e);
        }
        return InlineTaskResponse.TASK_NOT_FOUND;
    }

    @Override
    public Task find(long globalId) {
        return this.inlineTaskDao.find(globalId);
    }

    @Override
    public Task find(long contentId, long id) {
        return this.inlineTaskDao.find(contentId, id);
    }

    @Override
    public Set<Long> findTaskIdsByContentId(long contentId) {
        return this.inlineTaskDao.findTaskIdsByContentId(contentId);
    }

    @Override
    public Task create(Task task) {
        return this.inlineTaskDao.create(task);
    }

    @Override
    public Task update(Task task, String performerName, boolean hasStatusChanged, boolean hasBodyChanged) {
        Task updatedTask = this.updateTask(task);
        if (updatedTask == null) {
            return null;
        }
        if (hasStatusChanged) {
            updatedTask = task.getStatus() == TaskStatus.CHECKED ? new Task.Builder(updatedTask).withCompleteUser(performerName).withCompleteDate(new Date()).build() : new Task.Builder(updatedTask).withCompleteUser(null).withCompleteDate(null).withUpdateDate(new Date()).build();
        }
        if (hasBodyChanged) {
            updatedTask = new Task.Builder(updatedTask).withUpdateDate(new Date()).build();
        }
        return this.inlineTaskDao.update(updatedTask);
    }

    @Override
    public void delete(long globalId) {
        this.inlineTaskDao.delete(globalId);
    }

    @Override
    public void delete(long contentId, long id) {
        this.inlineTaskDao.delete(contentId, id);
    }

    private Task updateTask(Task task) {
        Task persistedTask = this.inlineTaskDao.find(task.getContentId(), task.getId());
        if (persistedTask != null) {
            return new Task.Builder(persistedTask).withContentId(task.getContentId()).withStatus(task.getStatus()).withTitle(task.getTitle()).withBody(task.getBody()).withAssignee(task.getAssignee()).withDueDate(task.getDueDate()).build();
        }
        return null;
    }

    @Override
    public void delete(Task task) {
        this.inlineTaskDao.delete(task.getContentId(), task.getId());
    }

    @Override
    public void deleteBySpaceId(long spaceId) {
        this.inlineTaskDao.deleteBySpaceId(spaceId);
    }

    @Override
    public PageResponse<Task> searchTasks(SearchTaskParameters params) {
        if (this.shouldUseSearchMechanismForTaskReports()) {
            log.debug("Tasks will be retrieved from the search index.");
            return this.getTasksFromSearchIndex(params);
        }
        log.debug("Tasks will be retrieved from the database.");
        return this.searchTasksWithRegularPermissions(params);
    }

    private boolean shouldUseSearchMechanismForTaskReports() {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request != null && StringUtils.isNotEmpty((CharSequence)request.getParameter("use-database"))) {
            return false;
        }
        if (!this.taskReportIndexPersistedStateService.isIndexReady()) {
            log.debug("Search index cannot be used because search index is not ready.");
            return false;
        }
        return this.darkFeatureManager.isEnabledForAllUsers(USE_DATABASE_FOR_TASK_REPORT_MACRO_DARK_FEATURE_NAME).orElse(false) == false;
    }

    private PageResponse<Task> searchTasksWithRegularPermissions(SearchTaskParameters params) {
        int startPage = params.getPageIndex() * params.getPageSize();
        int endPage = startPage + params.getPageSize() * params.getDisplayedPages();
        SimplePageRequest pageRequest = new SimplePageRequest(startPage, endPage);
        PageResponse<Task> response = this.inlineTaskDao.searchTask(params, new TaskPaginationService(this.permissionManager, this.pageManager, this.paginationService, this.userAccessor), (PageRequest)pageRequest);
        int totalPages = 7;
        if (!response.hasMore()) {
            int additionalPages = (int)Math.ceil((double)response.size() / (double)params.getPageSize());
            totalPages = params.getPageIndex() + additionalPages;
        }
        params.setTotalPages(totalPages);
        return response;
    }

    private PageResponse<Task> getTasksFromSearchIndex(SearchTaskParameters params) {
        try {
            int startPage = params.getPageIndex() * params.getPageSize();
            int endPage = startPage + params.getPageSize() * params.getDisplayedPages();
            SimplePageRequest pageRequest = new SimplePageRequest(startPage, endPage);
            List<Task> tasks = this.tasksFinderViaSearchIndex.find(params, pageRequest.getStart(), pageRequest.getLimit() - pageRequest.getStart());
            PageResponse<Task> response = this.createPageResponse(tasks, (PageRequest)pageRequest);
            Integer totalPages = 7;
            if (!response.hasMore()) {
                int additionalPages = (int)Math.ceil((double)response.size() / (double)params.getPageSize());
                totalPages = params.getPageIndex() + additionalPages;
            }
            params.setTotalPages(totalPages);
            return response;
        }
        catch (InvalidSearchException e) {
            throw new IllegalStateException(e);
        }
    }

    private PageResponse<Task> createPageResponse(List<Task> tasks, PageRequest pageRequest) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)5000);
        if (tasks == null || tasks.isEmpty()) {
            return PageResponseImpl.from(Collections.emptyList(), (boolean)false).pageRequest(limitedRequest).build();
        }
        List<Task> subList = tasks.subList(0, Math.min(limitedRequest.getLimit() - limitedRequest.getStart(), tasks.size()));
        boolean hasMore = tasks.size() > limitedRequest.getLimit() - limitedRequest.getStart();
        return PageResponseImpl.from(subList, (boolean)hasMore).pageRequest(pageRequest).build();
    }

    @Override
    public long countAllTasks() {
        return this.inlineTaskDao.countAll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InlineTaskResponse updatePage(ContentEntityObject page, String taskId, String newBody, String versionComment, PageUpdateTrigger trigger) throws CloneNotSupportedException {
        Draft draft = new Draft();
        draft.setPageId(page.getIdAsString());
        draft.setPageVersion(page.getVersion());
        draft.setBodyAsString(newBody);
        while (true) {
            block7: {
                ClusterLock lock = this.lockFactory.getLockForName("InlineTaskService.updatePage." + page.getIdAsString());
                lock.lock();
                try {
                    this.pageManager.refreshContentEntity(page);
                    if (this.draftManager.isMergeRequired(draft) && !this.isTaskIdInCache(taskId, page)) break block7;
                    ContentEntityObject originalPage = (ContentEntityObject)page.clone();
                    try {
                        ((Cache)this.lastModifiedTasks.get()).put((Object)page.getIdAsString(), (Object)taskId);
                    }
                    catch (RuntimeException failEx) {
                        log.warn("Cache put failed.", (Throwable)failEx);
                    }
                    page.setVersionComment(versionComment);
                    page.setBodyAsString(draft.getBodyAsString());
                    this.pageManager.saveContentEntity(page, originalPage, (SaveContext)new DefaultSaveContext(false, true, false, trigger));
                    InlineTaskResponse inlineTaskResponse = InlineTaskResponse.SUCCESS;
                    return inlineTaskResponse;
                }
                finally {
                    lock.unlock();
                }
            }
            MergeResult mergeResult = this.draftManager.mergeContent(draft);
            if (mergeResult.hasConflicts()) {
                return InlineTaskResponse.MERGE_CONFLICT;
            }
            draft.setPageVersion(page.getVersion());
            draft.setBodyAsString(mergeResult.getMergedContent());
        }
    }

    private boolean isTaskIdInCache(String taskId, ContentEntityObject page) {
        try {
            String cachedValue = (String)((Cache)this.lastModifiedTasks.get()).get((Object)page.getIdAsString());
            return StringUtils.equals((CharSequence)taskId, (CharSequence)cachedValue);
        }
        catch (RuntimeException failEx) {
            log.warn("Cache get failed.", (Throwable)failEx);
            return false;
        }
    }

    private ConfluenceUser getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

