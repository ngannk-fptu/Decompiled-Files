/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.http.HttpMethod
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.list;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.embedded.admin.DirectoryContextHelper;
import com.atlassian.crowd.embedded.admin.list.DirectoryListItem;
import com.atlassian.crowd.embedded.admin.list.ListItemPosition;
import com.atlassian.crowd.embedded.admin.plugin.InternalDirectoryOptionsModuleDescriptor;
import com.atlassian.crowd.embedded.admin.plugin.NewDirectoryTypeResolver;
import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.admin.util.MapBuilder;
import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.PermissionOption;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/directories/**"})
public final class DirectoriesController {
    private static final String TYPE_KEY_PREFIX = "embedded.crowd.directory.type.";
    private static final Logger log = LoggerFactory.getLogger(DirectoriesController.class);
    private static final String LIST_DIRECTORIES_VIEW = "list-directories";
    private final CrowdDirectoryService crowdDirectoryService;
    private final UserManager userManager;
    private final TransactionTemplate transactionTemplate;
    private final DirectoryContextHelper directoryContextHelper;
    private final ApplicationProperties applicationProperties;
    private final LDAPPropertiesMapper ldapPropertiesMapper;
    private final HtmlEncoder htmlEncoder;
    private final PluginAccessor pluginAccessor;
    private final NewDirectoryTypeResolver newDirectoryTypeResolver;
    private final LocaleResolver localeResolver;
    private final TimeZoneManager timeZoneManager;

    @Autowired
    public DirectoriesController(CrowdDirectoryService crowdDirectoryService, UserManager userManager, TransactionTemplate transactionTemplate, DirectoryContextHelper directoryContextHelper, ApplicationProperties applicationProperties, LDAPPropertiesMapper ldapPropertiesMapper, HtmlEncoder htmlEncoder, PluginAccessor pluginAccessor, NewDirectoryTypeResolver newDirectoryTypeResolver, LocaleResolver localeResolver, TimeZoneManager timeZoneManager) {
        this.crowdDirectoryService = Objects.requireNonNull(crowdDirectoryService, "crowdDirectoryService Can't be null");
        this.userManager = Objects.requireNonNull(userManager, "userManager Can't be null");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate Can't be null");
        this.directoryContextHelper = Objects.requireNonNull(directoryContextHelper, "directoryContextHelper Can't be null");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties Can't be null");
        this.ldapPropertiesMapper = Objects.requireNonNull(ldapPropertiesMapper, "ldapPropertiesMapper Can't be null");
        this.htmlEncoder = Objects.requireNonNull(htmlEncoder, "htmlEncoder Can't be null");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor Can't be null");
        this.newDirectoryTypeResolver = Objects.requireNonNull(newDirectoryTypeResolver, "newDirectoryTypeResolver Can't be null");
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver Can't be null");
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager, "timeZoneManager Can't be null");
    }

    @RequestMapping(value={"list"}, method={RequestMethod.GET})
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView(LIST_DIRECTORIES_VIEW, this.getReferenceData(request));
    }

    @RequestMapping(value={"edit"}, method={RequestMethod.GET})
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            switch (directory.getType()) {
                case INTERNAL: {
                    return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/configure/internal/", MapBuilder.build("directoryId", directory.getId()));
                }
                case CROWD: {
                    return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/configure/crowd/", MapBuilder.build("directoryId", directory.getId()));
                }
                case DELEGATING: {
                    return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/configure/delegatingldap/", MapBuilder.build("directoryId", directory.getId()));
                }
                case CUSTOM: {
                    if (!"com.atlassian.confluence.user.crowd.jira.JiraJdbcRemoteDirectory".equals(directory.getImplementationClass())) break;
                    return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/configure/jirajdbc/", MapBuilder.build("directoryId", directory.getId()));
                }
            }
            return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/configure/ldap/", MapBuilder.build("directoryId", directory.getId()));
        }
        catch (DirectoryNotFoundException e) {
            return this.directoryNotFound(request);
        }
    }

    @RequestMapping(value={"disable"}, method={RequestMethod.POST})
    public ModelAndView disable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.directoryContextHelper.isContextUserFromDirectory(request)) {
            return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.current.directory.cannot.disable.remove", new Serializable[0]));
        }
        return this.withDirectoryInTransaction(request, new DirectoryOperation(){

            @Override
            public void withDirectory(Directory directory) {
                ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder((Directory)directory);
                builder.setActive(false);
                Directory updatedDirectory = builder.toDirectory();
                DirectoriesController.this.crowdDirectoryService.updateDirectory(updatedDirectory);
                log.info("User directory disabled: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            }
        });
    }

    @RequestMapping(value={"remove"}, method={RequestMethod.POST})
    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Directory directory = this.directoryContextHelper.getDirectory(request);
        if (this.directoryContextHelper.isContextUserFromDirectory(directory, request)) {
            return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.current.directory.cannot.disable.remove", new Serializable[0]));
        }
        switch (directory.getType()) {
            case INTERNAL: {
                return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.internal.directory.cannot.remove", new Serializable[0]));
            }
        }
        return this.withDirectoryInTransaction(request, new DirectoryOperation(){

            @Override
            public void withDirectory(Directory directory) {
                try {
                    DirectoriesController.this.crowdDirectoryService.removeDirectory(directory.getId().longValue());
                }
                catch (DirectoryCurrentlySynchronisingException e) {
                    throw new DirectoryOperationException((Throwable)e);
                }
                log.info("User directory removed: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            }
        });
    }

    @RequestMapping(value={"enable"}, method={RequestMethod.POST})
    public ModelAndView enable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.withDirectoryInTransaction(request, new DirectoryOperation(){

            @Override
            public void withDirectory(Directory directory) {
                ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder((Directory)directory);
                builder.setActive(true);
                Directory updatedDirectory = builder.toDirectory();
                DirectoriesController.this.crowdDirectoryService.updateDirectory(updatedDirectory);
                log.info("User directory enabled: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            }
        });
    }

    @RequestMapping(value={"moveUp"}, method={RequestMethod.POST})
    public ModelAndView moveUp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.withDirectoryInTransaction(request, new DirectoryOperation(){

            @Override
            public void withDirectory(Directory directory) {
                List directoryIds = DirectoriesController.this.getDirectoryIds();
                int currentIndex = directoryIds.indexOf(directory.getId());
                DirectoriesController.this.crowdDirectoryService.setDirectoryPosition(directory.getId().longValue(), currentIndex > 0 ? currentIndex - 1 : 0);
                if (!DirectoriesController.this.userManager.isSystemAdmin(DirectoriesController.this.userManager.getRemoteUserKey())) {
                    DirectoriesController.this.crowdDirectoryService.setDirectoryPosition(directory.getId().longValue(), currentIndex);
                    throw new DirectoryOperationNotPermittedException("Current user would have lost system admin privileges if directory was moved.", SimpleMessage.instance("embedded.crowd.internal.directory.cannot.reorder", new Serializable[0]));
                }
                log.info("User directory moved up: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            }
        });
    }

    @RequestMapping(value={"moveDown"}, method={RequestMethod.POST})
    public ModelAndView moveDown(HttpServletRequest request, HttpServletResponse response) {
        return this.withDirectoryInTransaction(request, new DirectoryOperation(){

            @Override
            public void withDirectory(Directory directory) {
                List directoryIds = DirectoriesController.this.getDirectoryIds();
                int currentIndex = directoryIds.indexOf(directory.getId());
                int maxIndex = directoryIds.size() - 1;
                DirectoriesController.this.crowdDirectoryService.setDirectoryPosition(directory.getId().longValue(), currentIndex < maxIndex ? currentIndex + 1 : maxIndex);
                if (!DirectoriesController.this.userManager.isSystemAdmin(DirectoriesController.this.userManager.getRemoteUserKey())) {
                    DirectoriesController.this.crowdDirectoryService.setDirectoryPosition(directory.getId().longValue(), currentIndex);
                    throw new DirectoryOperationNotPermittedException("Current user would have lost system admin privileges if directory was moved.", SimpleMessage.instance("embedded.crowd.internal.directory.cannot.reorder", new Serializable[0]));
                }
                log.info("User directory moved down: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            }
        });
    }

    @RequestMapping(value={"sync"}, method={RequestMethod.POST})
    public ModelAndView sync(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Directory directory = this.directoryContextHelper.getDirectory(request);
        log.info("User directory synchronisation requested: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
        this.crowdDirectoryService.synchroniseDirectory(directory.getId().longValue());
        return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/directories/list?highlightDirectoryId=" + directory.getId());
    }

    private Map<String, Object> getReferenceData(HttpServletRequest request) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("directories", this.getDirectoryListItems(request));
        model.put("newDirectoryTypes", this.newDirectoryTypeResolver.getEnabledNewDirectoryTypes(this.getApplicationType(this.applicationProperties.getDisplayName())));
        model.put("highlightDirectoryId", request.getParameter("highlightDirectoryId"));
        model.put("context", model);
        model.put("req", request);
        model.put("htmlEncoder", this.htmlEncoder);
        return model;
    }

    private List<Long> getDirectoryIds() {
        List directories = this.crowdDirectoryService.findAllDirectories();
        ArrayList<Long> ids = new ArrayList<Long>(directories.size());
        for (Directory directory : directories) {
            ids.add(directory.getId());
        }
        return ids;
    }

    private ModelAndView withDirectoryInTransaction(HttpServletRequest request, final DirectoryOperation operation) {
        Directory directory;
        try {
            directory = this.directoryContextHelper.getDirectory(request);
            this.transactionTemplate.execute(new TransactionCallback(){

                public Object doInTransaction() {
                    operation.withDirectory(directory);
                    return null;
                }
            });
        }
        catch (DirectoryNotFoundException e) {
            log.error("Directory not found: ", (Throwable)e);
            return this.directoryNotFound(request);
        }
        catch (DirectoryOperationException e) {
            log.error("The directory operation failed: ", (Throwable)e);
            if (e.getCause() instanceof DirectoryCurrentlySynchronisingException) {
                return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.directory.not.removable.during.sync", new Serializable[0]));
            }
            String error = this.htmlEncoder.encode(e.getMessage());
            return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.directory.operation.error", new Serializable[]{error}));
        }
        catch (DirectoryOperationNotPermittedException e) {
            Message message = e.getI18nMessage();
            if (message != null) {
                return this.directoryInError(request, message);
            }
            String error = this.htmlEncoder.encode(e.getMessage());
            return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.directory.operation.error", new Serializable[]{error}));
        }
        return new ModelAndView("redirect:/plugins/servlet/embedded-crowd/directories/list?highlightDirectoryId=" + directory.getId());
    }

    private ModelAndView directoryInError(HttpServletRequest request, Message message) {
        Map<String, Object> model = this.getReferenceData(request);
        model.put("errors", Collections.singleton(message));
        return new ModelAndView(LIST_DIRECTORIES_VIEW, model);
    }

    private ModelAndView directoryNotFound(HttpServletRequest request) {
        return this.directoryInError(request, SimpleMessage.instance("embedded.crowd.directory.not.found", new Serializable[0]));
    }

    @VisibleForTesting
    protected List<DirectoryListItem> getDirectoryListItems(HttpServletRequest request) {
        ArrayList<DirectoryListItem> directoryListItems = new ArrayList<DirectoryListItem>();
        List directories = this.crowdDirectoryService.findAllDirectories();
        boolean isInternalDirectoryEditable = this.isInternalDirectoryEditable();
        Locale locale = this.localeResolver.getLocale();
        TimeZone timeZone = this.timeZoneManager.getUserTimeZone();
        for (int i = 0; i < directories.size(); ++i) {
            Directory directory = (Directory)directories.get(i);
            ListItemPosition position = new ListItemPosition(i, directories.size());
            DirectorySynchronisationInformation syncInfo = this.crowdDirectoryService.getDirectorySynchronisationInformation(directory.getId().longValue());
            boolean userFromDirectory = this.directoryContextHelper.isContextUserFromDirectory(directory, request);
            boolean showLoggedIntoWarning = this.shouldShowLoggedIntoWarning(directory, userFromDirectory, isInternalDirectoryEditable);
            Set<Operation> allowedOperations = this.getAvailableOperations(directory, userFromDirectory, isInternalDirectoryEditable);
            DirectoryListItem directoryListItem = new DirectoryListItem(directory, allowedOperations, showLoggedIntoWarning, this.getTypeName(directory), position, syncInfo, locale, timeZone);
            directoryListItems.add(directoryListItem);
        }
        return directoryListItems;
    }

    @VisibleForTesting
    protected Set<Operation> getAvailableOperations(Directory directory, boolean isContextUserFromDirectory, boolean internalDirectoryEditable) {
        HashSet operations = Sets.newHashSet();
        if (directory.getType() == DirectoryType.INTERNAL) {
            if (internalDirectoryEditable) {
                operations.add(Operation.EDIT);
            }
            if (!isContextUserFromDirectory) {
                operations.add(directory.isActive() ? Operation.DISABLE : Operation.ENABLE);
            }
        } else {
            if (!directory.isActive()) {
                operations.add(Operation.REMOVE);
            }
            operations.add(Operation.TROUBLESHOOT);
            if (!isContextUserFromDirectory) {
                operations.add(Operation.EDIT);
                operations.add(directory.isActive() ? Operation.DISABLE : Operation.ENABLE);
            }
        }
        return operations.isEmpty() ? operations : EnumSet.copyOf(operations);
    }

    @VisibleForTesting
    protected boolean isInternalDirectoryEditable() {
        List optionsDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(InternalDirectoryOptionsModuleDescriptor.class);
        if (optionsDescriptors.isEmpty()) {
            return true;
        }
        for (InternalDirectoryOptionsModuleDescriptor optionsDescriptor : optionsDescriptors) {
            if (!optionsDescriptor.isEditable()) continue;
            return true;
        }
        return false;
    }

    @VisibleForTesting
    boolean shouldShowLoggedIntoWarning(Directory directory, boolean isContextUserFromDirectory, boolean internalDirectoryEditable) {
        return isContextUserFromDirectory && (directory.getType() != DirectoryType.INTERNAL || internalDirectoryEditable);
    }

    private Message getTypeName(Directory directory) {
        DirectoryType directoryType = directory.getType();
        switch (directoryType) {
            case CONNECTOR: {
                String implemntationName = this.getNameForImplementation(directory.getImplementationClass());
                String name = implemntationName == null ? directoryType.name() : implemntationName;
                PermissionOption permissionOption = PermissionOption.fromAllowedOperations((Set)directory.getAllowedOperations());
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name() + "." + permissionOption.name(), new Serializable[]{name});
            }
            case CUSTOM: {
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name() + this.getClassNameOnly(directory.getImplementationClass()), new Serializable[0]);
            }
            case DELEGATING: {
                String implementationClass = directory.getValue("crowd.delegated.directory.type");
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name(), new Serializable[]{this.getNameForImplementation(implementationClass)});
            }
        }
        return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name(), new Serializable[0]);
    }

    private String getNameForImplementation(String implementationClass) {
        Map implementations = this.ldapPropertiesMapper.getImplementations();
        for (Map.Entry entry : implementations.entrySet()) {
            if (!((String)entry.getValue()).equals(implementationClass)) continue;
            return (String)entry.getKey();
        }
        return null;
    }

    private String getClassNameOnly(String implementationClass) {
        return implementationClass.substring(implementationClass.lastIndexOf("."));
    }

    public ApplicationType getApplicationType(String applicationName) {
        try {
            return ApplicationType.valueOf((String)applicationName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return ApplicationType.GENERIC_APPLICATION;
        }
    }

    public static enum Operation {
        ENABLE("enable", HttpMethod.POST),
        DISABLE("disable", HttpMethod.POST),
        REMOVE("remove", HttpMethod.POST),
        EDIT("edit", HttpMethod.GET),
        TROUBLESHOOT("troubleshoot", HttpMethod.GET);

        private static final String LABEL_PREFIX = "embedded.crowd.operation.";
        private static final String URL_PREFIX = "/plugins/servlet/embedded-crowd/directories/";
        private final String methodName;
        private HttpMethod httpMethod;

        private Operation(String methodName, HttpMethod httpMethod) {
            this.methodName = methodName;
            this.httpMethod = httpMethod;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public HttpMethod getHttpMethod() {
            return this.httpMethod;
        }

        public String getUrl(Directory directory) {
            return URL_PREFIX + this.methodName + "?" + "directoryId" + "=" + directory.getId();
        }

        public Message getMessage() {
            return SimpleMessage.instance(LABEL_PREFIX + this.name(), new Serializable[0]);
        }
    }

    public final class DirectoryOperationNotPermittedException
    extends RuntimeException {
        private Message message;

        public DirectoryOperationNotPermittedException(String englishMessage, Message i18nMessage) {
            super(englishMessage);
            this.message = i18nMessage;
        }

        public Message getI18nMessage() {
            return this.message;
        }
    }

    public final class DirectoryOperationException
    extends RuntimeException {
        public DirectoryOperationException(String message) {
            super(message);
        }

        public DirectoryOperationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DirectoryOperationException(Throwable cause) {
            super(cause);
        }
    }

    private static interface DirectoryOperation {
        public void withDirectory(Directory var1) throws DirectoryOperationException;
    }
}

