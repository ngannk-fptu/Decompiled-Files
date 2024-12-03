/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.AttributeValuesHolder
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.avatar.AvatarReference$BlobAvatar
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.GroupQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.atlassian.crowd.util.BoundedCount
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.crowd.util.UserUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.CommunicationException
 *  org.springframework.ldap.NameNotFoundException
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.control.PagedResultsCookie
 *  org.springframework.ldap.control.PagedResultsDirContextProcessor
 *  org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
 *  org.springframework.ldap.core.ContextMapper
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.core.DirContextProcessor
 *  org.springframework.ldap.core.LdapTemplate
 *  org.springframework.ldap.core.support.AggregateDirContextProcessor
 *  org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager
 *  org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.AttributeValuesHolder;
import com.atlassian.crowd.directory.LDAPDirectory;
import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.NamedLdapEntity;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapperImpl;
import com.atlassian.crowd.directory.ldap.SpringLdapTemplateWrapper;
import com.atlassian.crowd.directory.ldap.control.RequestControlsTrackingDirContextProcessor;
import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.directory.ldap.mapper.AttributeToContextCallbackHandler;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.GroupContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.JpegPhotoContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapperConfig;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPGroupAttributesMapper;
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPUserAttributesMapper;
import com.atlassian.crowd.directory.ldap.name.Converter;
import com.atlassian.crowd.directory.ldap.name.GenericConverter;
import com.atlassian.crowd.directory.ldap.name.SearchDN;
import com.atlassian.crowd.directory.ldap.util.DNStandardiser;
import com.atlassian.crowd.directory.ldap.util.DirectoryAttributeRetriever;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.ldap.LDAPQuery;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.search.ldap.NullResultException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.GroupQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.atlassian.crowd.util.BoundedCount;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.crowd.util.UserUtils;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class SpringLDAPConnector
implements LDAPDirectory {
    public static final int DEFAULT_PAGE_SIZE = 999;
    private static final Logger logger = LoggerFactory.getLogger(SpringLDAPConnector.class);
    private volatile long directoryId;
    protected volatile AttributeValuesHolder attributes;
    protected volatile Supplier<SpringLdapTemplateWrapper> ldapTemplate;
    protected volatile Supplier<ContextSource> contextSource;
    protected volatile Converter nameConverter;
    protected volatile SearchDN searchDN;
    protected volatile LDAPPropertiesMapper ldapPropertiesMapper;
    protected volatile Supplier<ContextSourceTransactionManager> contextSourceTransactionManager;
    protected final LDAPQueryTranslater ldapQueryTranslater;
    protected final EventPublisher eventPublisher;
    private final InstanceFactory instanceFactory;
    private final LdapContextSourceProvider ldapContextSourceProvider;
    private static final DirContextProcessor DO_NOTHING_DIR_CONTEXT_PROCESSOR = new DirContextProcessor(){

        public void preProcess(DirContext ctx) throws NamingException {
        }

        public void postProcess(DirContext ctx) throws NamingException {
        }
    };

    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        MembershipQuery membershipQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(groupName).startingAt(0).returningAtMost(querySizeHint);
        return BoundedCount.fromCountedItemsAndLimit((long)this.searchGroupRelationships(membershipQuery).size(), (long)querySizeHint);
    }

    public SpringLDAPConnector(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        this.ldapQueryTranslater = ldapQueryTranslater;
        this.eventPublisher = eventPublisher;
        this.instanceFactory = instanceFactory;
        this.ldapContextSourceProvider = ldapContextSourceProvider;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long id) {
        this.directoryId = id;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new AttributeValuesHolder(attributes);
        this.ldapPropertiesMapper = (LDAPPropertiesMapper)this.instanceFactory.getInstance(LDAPPropertiesMapperImpl.class);
        this.setLdapPropertiesMapperAttributes(attributes);
        this.contextSource = Suppliers.memoize(() -> new TransactionAwareContextSourceProxy(this.getContextSource(this.directoryId, this.ldapPropertiesMapper, this.getBaseEnvironmentProperties())));
        this.contextSourceTransactionManager = Suppliers.memoize(() -> {
            ContextSourceTransactionManager c = new ContextSourceTransactionManager();
            c.setContextSource(this.contextSource.get());
            return c;
        });
        this.ldapTemplate = Suppliers.memoize(() -> {
            SpringLdapTemplateWrapper l = new SpringLdapTemplateWrapper(new LdapTemplate(this.contextSource.get()));
            if (!this.ldapPropertiesMapper.isReferral()) {
                l.setIgnorePartialResultException(true);
            }
            return l;
        });
        this.nameConverter = new GenericConverter();
        this.searchDN = new SearchDN(this.ldapPropertiesMapper, this.nameConverter);
    }

    private ContextSource getContextSource(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> baseEnv) {
        if (directoryId <= 0L) {
            return this.ldapContextSourceProvider.createContextSource(ldapPropertiesMapper, baseEnv);
        }
        return this.ldapContextSourceProvider.getPooledContextSource(directoryId, ldapPropertiesMapper, baseEnv);
    }

    protected void setLdapPropertiesMapperAttributes(Map<String, String> attributes) {
        this.ldapPropertiesMapper.setAttributes(attributes);
    }

    public ContextSource getContextSource() {
        return this.contextSource.get();
    }

    public LDAPPropertiesMapper getLdapPropertiesMapper() {
        return this.ldapPropertiesMapper;
    }

    public Set<String> getValues(String name) {
        return this.attributes.getValues(name);
    }

    public String getValue(String name) {
        return this.attributes.getValue(name);
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public long getAttributeAsLong(String name, long defaultValue) {
        return this.attributes.getAttributeAsLong(name, defaultValue);
    }

    public boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        return this.attributes.getAttributeAsBoolean(name, defaultValue);
    }

    public Set<String> getKeys() {
        return this.attributes.getKeys();
    }

    public SearchDN getSearchDN() {
        return this.searchDN;
    }

    static final SearchControls copyOf(SearchControls sc) {
        return new SearchControls(sc.getSearchScope(), sc.getCountLimit(), sc.getTimeLimit(), sc.getReturningAttributes(), sc.getReturningObjFlag(), sc.getDerefLinkFlag());
    }

    private static final String[] toArray(Collection<String> s) {
        if (s != null) {
            return s.toArray(new String[s.size()]);
        }
        return null;
    }

    protected SearchControls getSearchControls(ContextMapperWithRequiredAttributes<?> mapper, int scope) {
        SearchControls sc = new SearchControls();
        sc.setSearchScope(scope);
        sc.setReturningObjFlag(false);
        String[] returningAttributes = SpringLDAPConnector.toArray(mapper.getRequiredLdapAttributes());
        if (returningAttributes != null) {
            sc.setReturningAttributes(returningAttributes);
        }
        return sc;
    }

    protected Map<String, Object> getBaseEnvironmentProperties() {
        return this.ldapPropertiesMapper.getEnvironment();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected CollectingNameClassPairCallbackHandler pageSearchResults(Name baseDN, String filter, ContextMapper contextMapper, SearchControls searchControls, DirContextProcessor ldapRequestControls, int maxResults) throws OperationFailedException {
        AttributeToContextCallbackHandler attributeToContextCallbackHandler;
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(0);
        TransactionStatus status = this.contextSourceTransactionManager.get().getTransaction((TransactionDefinition)transactionDefinition);
        try {
            int pagingSize = this.ldapPropertiesMapper.getPagedResultsSize();
            PagedResultsDirContextProcessor pagedResultsControl = new PagedResultsDirContextProcessor(pagingSize);
            if (logger.isDebugEnabled()) {
                logger.debug("Paged results are enabled with a paging size of: " + pagingSize);
            }
            AttributeToContextCallbackHandler handler = new AttributeToContextCallbackHandler(contextMapper);
            byte[] cookieBytes = null;
            do {
                AggregateDirContextProcessor aggregateDirContextProcessor = new AggregateDirContextProcessor();
                aggregateDirContextProcessor.addDirContextProcessor((DirContextProcessor)new RequestControlsTrackingDirContextProcessor());
                aggregateDirContextProcessor.addDirContextProcessor((DirContextProcessor)pagedResultsControl);
                if (ldapRequestControls != null) {
                    aggregateDirContextProcessor.addDirContextProcessor(ldapRequestControls);
                }
                this.ldapTemplate.get().search(baseDN, filter, searchControls, handler, (DirContextProcessor)aggregateDirContextProcessor);
                PagedResultsCookie cookie = pagedResultsControl.getCookie();
                byte[] byArray = cookieBytes = cookie == null ? null : cookie.getCookie();
                if (logger.isDebugEnabled()) {
                    logger.debug("Results fetched so far: {}", (Object)handler.getList().size());
                    logger.debug("Has more results: {} ", (Object)pagedResultsControl.hasMore());
                    logger.debug("Cookie length: {} ", cookieBytes == null ? null : Integer.valueOf(cookieBytes.length));
                }
                pagedResultsControl = new PagedResultsDirContextProcessor(pagingSize, cookie);
            } while (cookieBytes != null && cookieBytes.length != 0 && (handler.getList().size() < maxResults || maxResults == -1));
            attributeToContextCallbackHandler = handler;
        }
        catch (Throwable throwable) {
            try {
                this.contextSourceTransactionManager.get().commit(status);
                throw throwable;
            }
            catch (TransactionException e) {
                throw new OperationFailedException((Throwable)e);
            }
            catch (NamingException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        this.contextSourceTransactionManager.get().commit(status);
        return attributeToContextCallbackHandler;
    }

    protected <T> List<T> searchEntities(Name baseDN, String filter, ContextMapperWithRequiredAttributes<T> contextMapper, int startIndex, int maxResults) throws OperationFailedException {
        return this.searchEntitiesWithRequestControls(baseDN, filter, contextMapper, this.getSearchControls(contextMapper, 2), null, startIndex, maxResults);
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input, filter is encoded in all calls")
    protected <T> List<T> searchEntitiesWithRequestControls(Name baseDN, String filter, ContextMapperWithRequiredAttributes<T> contextMapper, SearchControls searchControls, DirContextProcessor ldapRequestControls, int startIndex, int maxResults) throws OperationFailedException {
        List results;
        searchControls = SpringLDAPConnector.copyOf(searchControls);
        searchControls.setTimeLimit(this.ldapPropertiesMapper.getSearchTimeLimit());
        if (this.ldapPropertiesMapper.isPagedResultsControl()) {
            CollectingNameClassPairCallbackHandler handler = this.pageSearchResults(baseDN, filter, contextMapper, searchControls, ldapRequestControls, startIndex + maxResults);
            results = handler.getList();
        } else {
            try {
                DirContextProcessor processor = (DirContextProcessor)MoreObjects.firstNonNull((Object)ldapRequestControls, (Object)DO_NOTHING_DIR_CONTEXT_PROCESSOR);
                if (maxResults != -1) {
                    int limit = startIndex + maxResults;
                    if (searchControls.getCountLimit() == 0L) {
                        searchControls.setCountLimit(limit);
                    }
                    results = this.ldapTemplate.get().searchWithLimitedResults(baseDN, filter, searchControls, contextMapper, processor, limit);
                } else {
                    results = this.ldapTemplate.get().search(baseDN, filter, searchControls, contextMapper, processor);
                }
            }
            catch (NamingException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        if (contextMapper instanceof GroupContextMapper) {
            results = this.postprocessGroups(results);
        }
        return SearchResultsUtil.constrainResults(results, (int)startIndex, (int)maxResults);
    }

    protected static ModificationItem createModificationItem(String directoryAttributeName, String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return null;
        }
        if (oldValue == null) {
            return new ModificationItem(1, new BasicAttribute(directoryAttributeName, DirectoryAttributeRetriever.toSaveableLDAPValue(newValue)));
        }
        if (!oldValue.equals(newValue)) {
            return new ModificationItem(2, new BasicAttribute(directoryAttributeName, DirectoryAttributeRetriever.toSaveableLDAPValue(newValue)));
        }
        return null;
    }

    ContextMapperWithRequiredAttributes<LDAPUserWithAttributes> getUserContextMapper(UserContextMapperConfig config) {
        return new UserContextMapper(this.getDirectoryId(), this.ldapPropertiesMapper, this.getCustomUserAttributeMappers(config));
    }

    protected List<AttributeMapper> getCustomUserAttributeMappers(UserContextMapperConfig config) {
        return Collections.emptyList();
    }

    public ContextMapperWithRequiredAttributes<LDAPGroupWithAttributes> getGroupContextMapper(GroupType groupType, boolean withAllAttributes) {
        Validate.notNull((Object)groupType, (String)"group type cannot be null", (Object[])new Object[0]);
        return new GroupContextMapper(this.getDirectoryId(), groupType, this.ldapPropertiesMapper, withAllAttributes ? this.getCustomGroupAttributeMappers() : this.getRequiredCustomGroupAttributeMappers());
    }

    protected List<AttributeMapper> getCustomGroupAttributeMappers() {
        return this.getRequiredCustomGroupAttributeMappers();
    }

    protected List<AttributeMapper> getRequiredCustomGroupAttributeMappers() {
        return Collections.emptyList();
    }

    public LDAPUserWithAttributes findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.findUserWithAttributesByName(name);
    }

    public LDAPUserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(com.atlassian.crowd.model.user.User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatching((Object)name)).returningAtMost(1);
        List<LDAPUserWithAttributes> users = this.searchUserObjects(query, this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()));
        if (users.isEmpty()) {
            throw new UserNotFoundException(name);
        }
        return users.get(0);
    }

    public LDAPUserWithAttributes findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        Validate.notNull((Object)externalId, (String)"externalId argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(com.atlassian.crowd.model.user.User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.EXTERNAL_ID).exactlyMatching((Object)externalId)).returningAtMost(1);
        List<LDAPUserWithAttributes> users = this.searchUserObjects(query, this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()));
        if (users.isEmpty()) {
            UserNotFoundException.throwNotFoundByExternalId((String)externalId);
        }
        return users.get(0);
    }

    public <T> List<T> searchUserObjects(EntityQuery<?> query, ContextMapperWithRequiredAttributes<T> mapper) throws OperationFailedException, IllegalArgumentException {
        List results;
        if (query == null) {
            throw new IllegalArgumentException("user search can only evaluate non-null EntityQueries for Entity.USER");
        }
        if (query.getEntityDescriptor().getEntityType() != Entity.USER) {
            throw new IllegalArgumentException("user search can only evaluate EntityQueries for Entity.USER");
        }
        LdapName baseDN = this.searchDN.getUser();
        try {
            LDAPQuery ldapQuery = this.ldapQueryTranslater.asLDAPFilter(query, this.ldapPropertiesMapper);
            String filter = ldapQuery.encode();
            logger.debug("Performing user search: baseDN = " + baseDN + " - filter = " + filter + " in directory " + this.directoryId);
            results = this.searchEntities(baseDN, filter, mapper, query.getStartIndex(), query.getMaxResults());
        }
        catch (NullResultException e) {
            results = Collections.emptyList();
        }
        return results;
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        Validate.notEmpty((CharSequence)name, (String)"name argument cannot be null or empty", (Object[])new Object[0]);
        LDAPUserWithAttributes user = this.findUserByName(name);
        try {
            this.ldapTemplate.get().unbind(this.asLdapUserName(user.getDn(), name));
        }
        catch (NamingException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void updateUserCredential(String name, PasswordCredential credential) throws InvalidCredentialException, UserNotFoundException, OperationFailedException {
        Validate.notEmpty((CharSequence)name, (String)"name argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notNull((Object)credential, (String)"credential argument cannot be null", (Object[])new Object[0]);
        if (credential.getCredential() == null) {
            throw new InvalidCredentialException("Credential's value must not be null");
        }
        ModificationItem[] mods = new ModificationItem[1];
        Object encodedCredential = this.getCredentialEncoder().encodeCredential(credential);
        mods[0] = new ModificationItem(2, new BasicAttribute(this.ldapPropertiesMapper.getUserPasswordAttribute(), encodedCredential));
        LdapName userDn = this.asLdapUserName(this.findUserByName(name).getDn(), name);
        try {
            this.ldapTemplate.get().modifyAttributes(userDn, mods);
        }
        catch (NamingException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public com.atlassian.crowd.model.user.User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, OperationFailedException {
        throw new OperationNotSupportedException("User renaming is not supported for LDAP directories.");
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("Custom user attributes are not yet supported for LDAP directories");
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("Custom user attributes are not yet supported for LDAP directories");
    }

    protected Attributes getNewUserAttributes(com.atlassian.crowd.model.user.User user, PasswordCredential credential) throws InvalidCredentialException, NamingException {
        LDAPUserAttributesMapper mapper = new LDAPUserAttributesMapper(this.getDirectoryId(), this.ldapPropertiesMapper);
        Attributes attributes = mapper.mapAttributesFromUser(user);
        if (credential != null && credential.getCredential() != null) {
            Object encodedCredential = this.getCredentialEncoder().encodeCredential(credential);
            attributes.put(this.ldapPropertiesMapper.getUserPasswordAttribute(), encodedCredential);
        }
        this.getNewUserDirectorySpecificAttributes(user, attributes);
        return attributes;
    }

    protected void getNewUserDirectorySpecificAttributes(com.atlassian.crowd.model.user.User user, Attributes attributes) {
    }

    public LDAPUserWithAttributes addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        return this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    public LDAPUserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        Validate.notNull((Object)user, (String)"user cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)"user.name cannot be null", (Object[])new Object[0]);
        try {
            LdapName dn = this.nameConverter.getName(this.ldapPropertiesMapper.getUserNameRdnAttribute(), user.getName(), this.searchDN.getUser());
            Attributes attrs = this.getNewUserAttributes((com.atlassian.crowd.model.user.User)user, credential);
            this.ldapTemplate.get().bind(dn, null, attrs);
            return this.findEntityByDN(this.getStandardisedDN(dn), LDAPUserWithAttributes.class);
        }
        catch (NamingException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
        catch (InvalidNameException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
        catch (GroupNotFoundException e) {
            throw new AssertionError((Object)"Should not throw a GroupNotFoundException");
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    protected void addDefaultSnToUserAttributes(Attributes attrs, String defaultSnValue) {
        this.addDefaultValueToUserAttributesForAttribute(this.ldapPropertiesMapper.getUserLastNameAttribute(), attrs, defaultSnValue);
    }

    protected void addDefaultValueToUserAttributesForAttribute(String attributeName, Attributes attrs, String defaultValue) {
        if (attrs == null) {
            return;
        }
        Attribute userAttribute = attrs.get(attributeName);
        if (userAttribute == null) {
            attrs.put(new BasicAttribute(attributeName, defaultValue));
        }
    }

    @Override
    public <T extends LDAPDirectoryEntity> T findEntityByDN(String dn, Class<T> entityClass) throws UserNotFoundException, GroupNotFoundException, OperationFailedException {
        dn = this.standardiseDN(dn);
        if (com.atlassian.crowd.model.user.User.class.isAssignableFrom(entityClass)) {
            return this.findEntityByDN(dn, this.getStandardisedDN(this.searchDN.getUser()), this.ldapPropertiesMapper.getUserFilter(), this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()), entityClass);
        }
        if (Group.class.isAssignableFrom(entityClass)) {
            T groupEntity = this.findEntityByDN(dn, this.getStandardisedDN(this.searchDN.getGroup()), this.ldapPropertiesMapper.getGroupFilter(), this.getGroupContextMapper(GroupType.GROUP, true), entityClass);
            return (T)this.postprocessGroups(Collections.singletonList((LDAPGroupWithAttributes)groupEntity)).get(0);
        }
        throw new IllegalArgumentException("Class " + entityClass.getCanonicalName() + " is not assignable from " + com.atlassian.crowd.model.user.User.class.getCanonicalName() + " or " + Group.class.getCanonicalName());
    }

    protected <T extends LDAPDirectoryEntity> RuntimeException typedEntityNotFoundException(String name, Class<T> entityClass) throws UserNotFoundException, GroupNotFoundException {
        if (com.atlassian.crowd.model.user.User.class.isAssignableFrom(entityClass)) {
            throw new UserNotFoundException(name);
        }
        if (Group.class.isAssignableFrom(entityClass)) {
            throw new GroupNotFoundException(name);
        }
        throw new IllegalArgumentException("Class " + entityClass.getCanonicalName() + " is not assignable from " + com.atlassian.crowd.model.user.User.class.getCanonicalName() + " or " + Group.class.getCanonicalName());
    }

    protected <T extends LDAPDirectoryEntity> T findEntityByDN(String dn, String baseDN, String filter, ContextMapperWithRequiredAttributes contextMapper, Class<T> entityClass) throws UserNotFoundException, GroupNotFoundException, OperationFailedException {
        if (StringUtils.isBlank((CharSequence)dn)) {
            throw this.typedEntityNotFoundException("Blank DN", entityClass);
        }
        if (dn.endsWith(baseDN)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing search at DN: <" + dn + "> with filter: <" + filter + ">");
            }
            List entities = null;
            try {
                SearchControls searchControls = this.getSearchControls(contextMapper, 0);
                searchControls.setTimeLimit(this.ldapPropertiesMapper.getSearchTimeLimit());
                entities = this.ldapTemplate.get().search(this.asLdapName(dn, "DN: " + dn, entityClass), filter, searchControls, contextMapper);
            }
            catch (NameNotFoundException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Search failed", (Throwable)e);
                }
            }
            catch (NamingException e) {
                throw new OperationFailedException((Throwable)e);
            }
            if (entities != null && !entities.isEmpty()) {
                return (T)((LDAPDirectoryEntity)entities.get(0));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Entity DN <" + dn + "> does not exist or does not match filter <" + filter + ">");
            }
            throw this.typedEntityNotFoundException("DN: " + dn, entityClass);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Entity DN <" + dn + "> is outside the entity base DN subtree scope <" + baseDN + ">");
        }
        throw this.typedEntityNotFoundException("DN: " + dn, entityClass);
    }

    public com.atlassian.crowd.model.user.User updateUser(UserTemplate user) throws UserNotFoundException, OperationFailedException {
        List<ModificationItem> modificationItems;
        Validate.notNull((Object)user, (String)"user cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)user.getName()), (String)"user cannot have blank user name", (Object[])new Object[0]);
        com.atlassian.crowd.model.user.User populatedUser = UserUtils.populateNames((com.atlassian.crowd.model.user.User)user);
        LDAPUserWithAttributes currentUser = this.findUserByName(user.getName());
        String ldapUserObjectType = this.ldapPropertiesMapper.getUserObjectClass();
        if (("inetOrgPerson".equalsIgnoreCase(ldapUserObjectType) || "user".equalsIgnoreCase(ldapUserObjectType)) && !(modificationItems = this.getUserModificationItems(populatedUser, currentUser)).isEmpty()) {
            try {
                this.ldapTemplate.get().modifyAttributes(this.asLdapUserName(currentUser.getDn(), user.getName()), modificationItems.toArray(new ModificationItem[modificationItems.size()]));
            }
            catch (NamingException e) {
                throw new OperationFailedException(String.format("Update user failed with the following modification items %s", modificationItems), (Throwable)e);
            }
        }
        try {
            return (com.atlassian.crowd.model.user.User)this.findEntityByDN(currentUser.getDn(), LDAPUserWithAttributes.class);
        }
        catch (GroupNotFoundException e) {
            throw new AssertionError((Object)"Should not throw a GroupNotFoundException");
        }
    }

    protected List<ModificationItem> getUserModificationItems(com.atlassian.crowd.model.user.User userTemplate, LDAPUserWithAttributes currentUser) {
        ModificationItem displayNameMod;
        ModificationItem givenNameMod;
        ModificationItem mailMod;
        ArrayList<ModificationItem> modificationItems = new ArrayList<ModificationItem>();
        ModificationItem snMod = SpringLDAPConnector.createModificationItem(this.ldapPropertiesMapper.getUserLastNameAttribute(), currentUser.getLastName(), userTemplate.getLastName());
        if (snMod != null) {
            modificationItems.add(snMod);
        }
        if ((mailMod = SpringLDAPConnector.createModificationItem(this.ldapPropertiesMapper.getUserEmailAttribute(), currentUser.getEmailAddress(), userTemplate.getEmailAddress())) != null) {
            modificationItems.add(mailMod);
        }
        if ((givenNameMod = SpringLDAPConnector.createModificationItem(this.ldapPropertiesMapper.getUserFirstNameAttribute(), currentUser.getFirstName(), userTemplate.getFirstName())) != null) {
            modificationItems.add(givenNameMod);
        }
        if ((displayNameMod = SpringLDAPConnector.createModificationItem(this.ldapPropertiesMapper.getUserDisplayNameAttribute(), currentUser.getDisplayName(), userTemplate.getDisplayName())) != null) {
            modificationItems.add(displayNameMod);
        }
        return modificationItems;
    }

    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        if (query.getReturnType() == String.class) {
            ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getUserNameAttribute());
            List<String> namedLdapEntities = NamedLdapEntity.namesOf(this.searchUserObjects(query, mapper));
            return namedLdapEntities;
        }
        if (query.getReturnType() == com.atlassian.crowd.model.user.User.class) {
            return this.searchUserObjects(query, this.getUserContextMapper(UserContextMapperConfig.Builder.withRequiredAttributes().build()));
        }
        return this.searchUserObjects(query, this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()));
    }

    public com.atlassian.crowd.model.user.User authenticate(String name, PasswordCredential credential) throws InvalidAuthenticationException, UserNotFoundException, OperationFailedException {
        if (credential == null || StringUtils.isBlank((CharSequence)credential.getCredential())) {
            throw new InvalidAuthenticationException("You cannot authenticate with a blank password");
        }
        if (credential.isEncryptedCredential()) {
            throw new InvalidAuthenticationException("You cannot authenticate with an encrypted PasswordCredential");
        }
        LDAPUserWithAttributes user = this.findUserByName(name);
        logger.debug("Authenticating user '{}' with DN '{}' in directory {}", new Object[]{name, user.getDn(), this.directoryId});
        try {
            ContextSource ctxSource = this.ldapContextSourceProvider.createMinimalContextSource(user.getDn(), credential.getCredential(), this.ldapPropertiesMapper, this.getBaseEnvironmentProperties());
            DirContext ctxt = ctxSource.getReadWriteContext();
            ctxt.close();
        }
        catch (CommunicationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (NamingException e) {
            throw InvalidAuthenticationException.newInstanceWithNameAndDescriptionFromCause((String)name, (Throwable)e);
        }
        catch (Exception e) {
            throw new InvalidAuthenticationException(name, (Throwable)e);
        }
        return user;
    }

    public LDAPGroupWithAttributes findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.findGroupWithAttributesByName(name);
    }

    public LDAPGroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).exactlyMatching((Object)name)).returningAtMost(1);
        try {
            return (LDAPGroupWithAttributes)Iterables.getOnlyElement(this.searchGroupObjects(query, this.getGroupContextMapper(GroupType.GROUP, true)));
        }
        catch (NoSuchElementException e) {
            throw new GroupNotFoundException(name);
        }
    }

    protected LDAPGroupWithAttributes findGroupByNameAndType(String name, GroupType groupType) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group((GroupType)groupType)).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).exactlyMatching((Object)name)).returningAtMost(1);
        ContextMapperWithRequiredAttributes<LDAPGroupWithAttributes> mapper = groupType == null ? this.getGroupContextMapper(GroupType.GROUP, true) : this.getGroupContextMapper(groupType, true);
        try {
            return (LDAPGroupWithAttributes)Iterables.getOnlyElement(this.searchGroupObjects(query, mapper));
        }
        catch (NoSuchElementException e) {
            throw new GroupNotFoundException(name);
        }
    }

    protected <T> List<T> searchGroupObjectsOfSpecifiedGroupType(EntityQuery<?> query, ContextMapperWithRequiredAttributes<T> mapper) throws OperationFailedException {
        List results;
        GroupType groupType = query.getEntityDescriptor().getGroupType();
        if (!GroupType.GROUP.equals((Object)groupType)) {
            if (GroupType.LEGACY_ROLE.equals((Object)groupType)) {
                return Collections.emptyList();
            }
            throw new IllegalArgumentException("Cannot search for groups of type: " + groupType);
        }
        LdapName baseDN = this.searchDN.getGroup();
        try {
            LDAPQuery ldapQuery = this.ldapQueryTranslater.asLDAPFilter(query, this.ldapPropertiesMapper);
            String filter = ldapQuery.encode();
            logger.debug("Performing group search: baseDN = " + baseDN + " - filter = " + filter);
            results = this.searchEntities(baseDN, filter, mapper, query.getStartIndex(), query.getMaxResults());
        }
        catch (NullResultException e) {
            results = Collections.emptyList();
        }
        return results;
    }

    public <T> List<T> searchGroupObjects(EntityQuery<?> query, ContextMapperWithRequiredAttributes<T> mapper) throws OperationFailedException {
        Validate.notNull(query, (String)"query argument cannot be null", (Object[])new Object[0]);
        if (query.getEntityDescriptor().getEntityType() != Entity.GROUP) {
            throw new IllegalArgumentException("group search can only evaluate EntityQueries for Entity.GROUP");
        }
        GroupType groupType = query.getEntityDescriptor().getGroupType();
        if (groupType == null) {
            int groupStartIndex = query.getStartIndex();
            int groupMaxResults = query.getMaxResults();
            GroupQuery groupQuery = new GroupQuery(Group.class, GroupType.GROUP, query.getSearchRestriction(), groupStartIndex, groupMaxResults);
            ImmutableList results = ImmutableList.copyOf(this.searchGroupObjectsOfSpecifiedGroupType((EntityQuery<?>)groupQuery, mapper));
            return SearchResultsUtil.constrainResults((List)results, (int)query.getStartIndex(), (int)query.getMaxResults());
        }
        return this.searchGroupObjectsOfSpecifiedGroupType(query, mapper);
    }

    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        Validate.notNull(query, (String)"query argument cannot be null", (Object[])new Object[0]);
        GroupType groupType = query.getEntityDescriptor().getGroupType();
        if (groupType == GroupType.LEGACY_ROLE) {
            return Collections.emptyList();
        }
        if (groupType != null && groupType != GroupType.GROUP) {
            throw new IllegalArgumentException("group search can only evaluate EntityQueries for GroupType.GROUP");
        }
        if (query.getReturnType() == String.class) {
            ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getGroupNameAttribute());
            List<String> namedLdapEntities = NamedLdapEntity.namesOf(this.searchGroupObjects(query, mapper));
            return namedLdapEntities;
        }
        if (query.getReturnType() == Group.class) {
            ContextMapperWithRequiredAttributes<LDAPGroupWithAttributes> mapper = this.getGroupContextMapper(GroupType.GROUP, false);
            return this.searchGroupObjects(query, mapper);
        }
        ContextMapperWithRequiredAttributes<LDAPGroupWithAttributes> mapper = this.getGroupContextMapper(GroupType.GROUP, true);
        return this.searchGroupObjects(query, mapper);
    }

    protected List<LDAPGroupWithAttributes> postprocessGroups(List<LDAPGroupWithAttributes> groups) throws OperationFailedException {
        return groups;
    }

    protected Attributes getNewGroupAttributes(Group group) throws NamingException {
        LDAPGroupAttributesMapper mapper = new LDAPGroupAttributesMapper(this.getDirectoryId(), group.getType(), this.ldapPropertiesMapper);
        Attributes attributes = mapper.mapAttributesFromGroup(group);
        this.getNewGroupDirectorySpecificAttributes(group, attributes);
        String defaultContainerMemberDN = this.getInitialGroupMemberDN();
        if (defaultContainerMemberDN != null) {
            attributes.put(new BasicAttribute(this.ldapPropertiesMapper.getGroupMemberAttribute(), defaultContainerMemberDN));
        }
        return attributes;
    }

    protected void getNewGroupDirectorySpecificAttributes(Group group, Attributes attributes) {
    }

    protected String getInitialGroupMemberDN() {
        return "";
    }

    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        Validate.notNull((Object)group, (String)"group cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)group.getName()), (String)"group cannot have blank group name", (Object[])new Object[0]);
        if (this.groupExists((Group)group)) {
            throw new InvalidGroupException((Group)group, "Group already exists");
        }
        if (group.getType() != GroupType.GROUP) {
            throw new InvalidGroupException((Group)group, "group.type must be GroupType.GROUP");
        }
        LdapName baseDN = this.searchDN.getGroup();
        String nameAttribute = this.ldapPropertiesMapper.getGroupNameAttribute();
        try {
            LdapName dn = this.nameConverter.getName(nameAttribute, group.getName(), baseDN);
            Attributes groupAttributes = this.getNewGroupAttributes((Group)group);
            this.ldapTemplate.get().bind(dn, null, groupAttributes);
            return (Group)this.findEntityByDN(this.getStandardisedDN(dn), LDAPGroupWithAttributes.class);
        }
        catch (UserNotFoundException e) {
            throw new AssertionError((Object)"Should not throw UserNotFoundException");
        }
        catch (GroupNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (NamingException e) {
            throw new InvalidGroupException((Group)group, e.getMessage(), (Throwable)e);
        }
        catch (InvalidNameException e) {
            throw new InvalidGroupException((Group)group, e.getMessage(), (Throwable)e);
        }
    }

    public Group updateGroup(GroupTemplate group) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)group, (String)"group cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)group.getName()), (String)"group cannot have blank group name", (Object[])new Object[0]);
        LDAPGroupWithAttributes currentGroup = this.findGroupByName(group.getName());
        if (currentGroup.getType() != group.getType()) {
            throw new OperationNotSupportedException("Cannot modify the GroupType for an LDAP group");
        }
        ArrayList<ModificationItem> modificationItems = new ArrayList<ModificationItem>();
        String descriptionAttribute = group.getType() == GroupType.GROUP ? this.ldapPropertiesMapper.getGroupDescriptionAttribute() : this.ldapPropertiesMapper.getRoleDescriptionAttribute();
        ModificationItem descriptionMod = SpringLDAPConnector.createModificationItem(descriptionAttribute, currentGroup.getDescription(), group.getDescription());
        if (descriptionMod != null) {
            modificationItems.add(descriptionMod);
        }
        if (!modificationItems.isEmpty()) {
            try {
                this.ldapTemplate.get().modifyAttributes(this.asLdapGroupName(currentGroup.getDn(), group.getName()), modificationItems.toArray(new ModificationItem[modificationItems.size()]));
            }
            catch (NamingException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        try {
            return (Group)this.findEntityByDN(currentGroup.getDn(), LDAPGroupWithAttributes.class);
        }
        catch (UserNotFoundException e) {
            throw new AssertionError((Object)"Should not throw UserNotFoundException.");
        }
    }

    public void removeGroup(String name) throws GroupNotFoundException, OperationFailedException {
        Validate.notEmpty((CharSequence)name, (String)"name argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(name);
        try {
            this.ldapTemplate.get().unbind(this.asLdapGroupName(group.getDn(), name));
        }
        catch (NamingException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException("Group renaming is not yet supported for LDAP directories");
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("Custom group attributes are not yet supported for LDAP directories");
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("Custom group attributes are not yet supported for LDAP directories");
    }

    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        Iterable<T> results;
        Validate.notNull(query, (String)"query argument cannot be null", (Object[])new Object[0]);
        if (query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.GROUP && query.getEntityToMatch().getEntityType() != query.getEntityToReturn().getEntityType()) {
            throw new IllegalArgumentException("Cannot search for group relationships of mismatching GroupTypes: attempted to match <" + query.getEntityToMatch().getEntityType() + "> and return <" + query.getEntityToReturn().getEntityType() + ">");
        }
        if (query.getSearchRestriction() != NullRestrictionImpl.INSTANCE) {
            throw new IllegalArgumentException("LDAP-based membership queries do not support search restrictions.");
        }
        if (query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.USER) {
            GroupType groupType = query.getEntityToMatch().getGroupType();
            if (groupType == null) {
                MembershipQuery groupQuery = QueryBuilder.createMembershipQuery((int)query.getMaxResults(), (int)query.getStartIndex(), (boolean)query.isFindChildren(), (EntityDescriptor)query.getEntityToReturn(), (Class)query.getReturnType(), (EntityDescriptor)query.getEntityToMatch(), (String)query.getEntityNameToMatch());
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(groupQuery);
            } else {
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(query);
            }
        } else if (query.getEntityToMatch().getEntityType() == Entity.USER && query.getEntityToReturn().getEntityType() == Entity.GROUP) {
            GroupType groupType = query.getEntityToReturn().getGroupType();
            if (groupType == null) {
                MembershipQuery groupQuery = QueryBuilder.createMembershipQuery((int)query.getMaxResults(), (int)query.getStartIndex(), (boolean)query.isFindChildren(), (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP), (Class)query.getReturnType(), (EntityDescriptor)query.getEntityToMatch(), (String)query.getEntityNameToMatch());
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(groupQuery);
            } else {
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(query);
            }
        } else if (query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.GROUP) {
            GroupType groupTypeToReturn;
            GroupType groupTypeToMatch = query.getEntityToMatch().getGroupType();
            if (groupTypeToMatch != (groupTypeToReturn = query.getEntityToReturn().getGroupType())) {
                throw new IllegalArgumentException("Cannot search for group relationships of mismatching GroupTypes: attempted to match <" + groupTypeToMatch + "> and return <" + groupTypeToReturn + ">");
            }
            if (groupTypeToReturn == null) {
                MembershipQuery groupQuery = QueryBuilder.createMembershipQuery((int)query.getMaxResults(), (int)query.getStartIndex(), (boolean)query.isFindChildren(), (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP), (Class)query.getReturnType(), (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP), (String)query.getEntityNameToMatch());
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(groupQuery);
            } else {
                results = this.searchGroupRelationshipsWithGroupTypeSpecified(query);
            }
        } else {
            throw new IllegalArgumentException("Cannot search for relationships between a USER and another USER");
        }
        return ImmutableList.copyOf(results);
    }

    protected abstract <T> Iterable<T> searchGroupRelationshipsWithGroupTypeSpecified(MembershipQuery<T> var1) throws OperationFailedException;

    protected abstract LDAPCredentialEncoder getCredentialEncoder();

    public boolean supportsSettingEncryptedCredential() {
        return this.getCredentialEncoder().supportsSettingEncryptedPasswords();
    }

    public boolean supportsPasswordExpiration() {
        return false;
    }

    public void expireAllPasswords() throws OperationFailedException {
        throw new OperationFailedException("Crowd does not support expiring passwords in LDAP directories");
    }

    public boolean supportsNestedGroups() {
        return !this.ldapPropertiesMapper.isNestedGroupsDisabled();
    }

    public boolean isRolesDisabled() {
        return true;
    }

    public void testConnection() throws OperationFailedException {
        ClassLoader existingTCCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(SpringLDAPConnector.class.getClassLoader());
            try (LdapContext ldapContext = (LdapContext)this.contextSource.get().getReadOnlyContext();){
                ldapContext.getConnectControls();
            }
        }
        catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }
        finally {
            Thread.currentThread().setContextClassLoader(existingTCCL);
        }
    }

    @VisibleForTesting
    final String getStandardisedDN(LdapName dn) throws OperationFailedException {
        return DNStandardiser.standardise(dn, !this.ldapPropertiesMapper.isRelaxedDnStandardisation());
    }

    final String standardiseDN(String dn) {
        return DNStandardiser.standardise(dn, !this.ldapPropertiesMapper.isRelaxedDnStandardisation());
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    protected <T extends LDAPDirectoryEntity> LdapName asLdapName(String dn, String entityName, Class<T> entityClass) throws UserNotFoundException, GroupNotFoundException {
        try {
            return new LdapName(dn);
        }
        catch (InvalidNameException e) {
            throw this.typedEntityNotFoundException(entityName, entityClass);
        }
    }

    protected LdapName asLdapGroupName(String dn, String groupName) throws GroupNotFoundException {
        try {
            return this.asLdapName(dn, groupName, LDAPGroupWithAttributes.class);
        }
        catch (UserNotFoundException e) {
            throw new AssertionError((Object)"Should not throw UserNotFoundException.");
        }
    }

    protected LdapName asLdapUserName(String dn, String userName) throws UserNotFoundException {
        try {
            return this.asLdapName(dn, userName, LDAPUserWithAttributes.class);
        }
        catch (GroupNotFoundException e) {
            throw new AssertionError((Object)"Should not throw GroupNotFoundException.");
        }
    }

    public boolean supportsInactiveAccounts() {
        return false;
    }

    public RemoteDirectory getAuthoritativeDirectory() {
        return this;
    }

    private boolean groupExists(Group group) throws OperationFailedException {
        try {
            this.findGroupByName(group.getName());
            return true;
        }
        catch (GroupNotFoundException e) {
            return false;
        }
    }

    protected ContextMapperWithRequiredAttributes<AvatarReference.BlobAvatar> avatarMapper() {
        return new JpegPhotoContextMapper();
    }

    public AvatarReference.BlobAvatar getUserAvatarByName(String username, int sizeHint) throws OperationFailedException {
        EntityQuery query = QueryBuilder.queryFor(com.atlassian.crowd.model.user.User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatching((Object)username)).returningAtMost(1);
        try {
            LDAPQuery ldapQuery = this.ldapQueryTranslater.asLDAPFilter(query, this.ldapPropertiesMapper);
            LdapName baseDN = this.searchDN.getUser();
            String filter = ldapQuery.encode();
            logger.debug("Performing user search: baseDN = " + baseDN + " - filter = " + filter + " in directory " + this.directoryId);
            return (AvatarReference.BlobAvatar)Iterables.getFirst(this.searchEntities(baseDN, filter, this.avatarMapper(), query.getStartIndex(), query.getMaxResults()), null);
        }
        catch (NullResultException e) {
            return null;
        }
    }
}

