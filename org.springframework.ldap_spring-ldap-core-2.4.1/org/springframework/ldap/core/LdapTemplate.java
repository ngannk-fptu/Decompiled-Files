/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.core;

import java.util.List;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.PartialResultException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AttributesMapperCallbackHandler;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextCallback;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.CollectingAuthenticationErrorCallback;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextMapperCallbackHandler;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DefaultNameClassPairMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.core.LdapEntryIdentificationContextMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.NameClassPairMapper;
import org.springframework.ldap.core.SearchExecutor;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.odm.core.OdmException;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;

public class LdapTemplate
implements LdapOperations,
InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(LdapTemplate.class);
    private static final boolean DONT_RETURN_OBJ_FLAG = false;
    private static final boolean RETURN_OBJ_FLAG = true;
    private static final String[] ALL_ATTRIBUTES = null;
    private ContextSource contextSource;
    private boolean ignorePartialResultException = false;
    private boolean ignoreNameNotFoundException = false;
    private boolean ignoreSizeLimitExceededException = true;
    private int defaultSearchScope = 2;
    private int defaultTimeLimit = 0;
    private int defaultCountLimit = 0;
    private ObjectDirectoryMapper odm = new DefaultObjectDirectoryMapper();

    public LdapTemplate() {
    }

    public LdapTemplate(ContextSource contextSource) {
        this.contextSource = contextSource;
    }

    public void setContextSource(ContextSource contextSource) {
        this.contextSource = contextSource;
    }

    @Override
    public ObjectDirectoryMapper getObjectDirectoryMapper() {
        return this.odm;
    }

    public void setObjectDirectoryMapper(ObjectDirectoryMapper odm) {
        this.odm = odm;
    }

    public ContextSource getContextSource() {
        return this.contextSource;
    }

    public void setIgnoreNameNotFoundException(boolean ignore) {
        this.ignoreNameNotFoundException = ignore;
    }

    public void setIgnorePartialResultException(boolean ignore) {
        this.ignorePartialResultException = ignore;
    }

    public void setIgnoreSizeLimitExceededException(boolean ignore) {
        this.ignoreSizeLimitExceededException = ignore;
    }

    public void setDefaultSearchScope(int defaultSearchScope) {
        this.defaultSearchScope = defaultSearchScope;
    }

    public void setDefaultTimeLimit(int defaultTimeLimit) {
        this.defaultTimeLimit = defaultTimeLimit;
    }

    public void setDefaultCountLimit(int defaultCountLimit) {
        this.defaultCountLimit = defaultCountLimit;
    }

    @Override
    public void search(Name base, String filter, int searchScope, boolean returningObjFlag, NameClassPairCallbackHandler handler) {
        this.search(base, filter, this.getDefaultSearchControls(searchScope, returningObjFlag, ALL_ATTRIBUTES), handler);
    }

    @Override
    public void search(String base, String filter, int searchScope, boolean returningObjFlag, NameClassPairCallbackHandler handler) {
        this.search(base, filter, this.getDefaultSearchControls(searchScope, returningObjFlag, ALL_ATTRIBUTES), handler);
    }

    @Override
    public void search(final Name base, final String filter, final SearchControls controls, NameClassPairCallbackHandler handler) {
        SearchExecutor se = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.search(base, filter, controls);
            }
        };
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(se, handler);
    }

    @Override
    public void search(final String base, final String filter, final SearchControls controls, NameClassPairCallbackHandler handler) {
        SearchExecutor se = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.search(base, filter, controls);
            }
        };
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(se, handler);
    }

    @Override
    public void search(final Name base, final String filter, final SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
        SearchExecutor se = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.search(base, filter, controls);
            }
        };
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(se, handler, processor);
    }

    @Override
    public void search(final String base, final String filter, final SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
        SearchExecutor se = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.search(base, filter, controls);
            }
        };
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(se, handler, processor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void search(SearchExecutor se, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
        block28: {
            DirContext ctx = this.contextSource.getReadOnlyContext();
            NamingEnumeration results = null;
            NamingException ex = null;
            try {
                processor.preProcess(ctx);
                results = se.executeSearch(ctx);
                while (results.hasMore()) {
                    NameClassPair result = (NameClassPair)results.next();
                    handler.handleNameClassPair(result);
                }
            }
            catch (NameNotFoundException e) {
                if (this.ignoreNameNotFoundException) {
                    LOG.warn("Base context not found, ignoring: " + e.getMessage());
                }
                ex = LdapUtils.convertLdapException(e);
            }
            catch (PartialResultException e) {
                if (this.ignorePartialResultException) {
                    LOG.debug("PartialResultException encountered and ignored", (Throwable)e);
                }
                ex = LdapUtils.convertLdapException(e);
            }
            catch (SizeLimitExceededException e) {
                if (this.ignoreSizeLimitExceededException) {
                    LOG.debug("SizeLimitExceededException encountered and ignored", (Throwable)e);
                }
                ex = LdapUtils.convertLdapException(e);
            }
            catch (javax.naming.NamingException e) {
                ex = LdapUtils.convertLdapException(e);
            }
            finally {
                try {
                    processor.postProcess(ctx);
                }
                catch (javax.naming.NamingException e) {
                    if (ex == null) {
                        ex = LdapUtils.convertLdapException(e);
                    }
                    LOG.debug("Ignoring Exception from postProcess, main exception thrown instead", (Throwable)e);
                }
                this.closeContextAndNamingEnumeration(ctx, results);
                if (ex == null) break block28;
                throw ex;
            }
        }
    }

    @Override
    public void search(SearchExecutor se, NameClassPairCallbackHandler handler) {
        this.search(se, handler, new NullDirContextProcessor());
    }

    @Override
    public void search(Name base, String filter, NameClassPairCallbackHandler handler) {
        SearchControls controls = this.getDefaultSearchControls(this.defaultSearchScope, false, ALL_ATTRIBUTES);
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(base, filter, controls, handler);
    }

    @Override
    public void search(String base, String filter, NameClassPairCallbackHandler handler) {
        SearchControls controls = this.getDefaultSearchControls(this.defaultSearchScope, false, ALL_ATTRIBUTES);
        if (handler instanceof ContextMapperCallbackHandler) {
            this.assureReturnObjFlagSet(controls);
        }
        this.search(base, filter, controls, handler);
    }

    @Override
    public <T> List<T> search(Name base, String filter, int searchScope, String[] attrs, AttributesMapper<T> mapper) {
        return this.search(base, filter, this.getDefaultSearchControls(searchScope, false, attrs), mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, int searchScope, String[] attrs, AttributesMapper<T> mapper) {
        return this.search(base, filter, this.getDefaultSearchControls(searchScope, false, attrs), mapper);
    }

    @Override
    public <T> List<T> search(Name base, String filter, int searchScope, AttributesMapper<T> mapper) {
        return this.search(base, filter, searchScope, ALL_ATTRIBUTES, mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, int searchScope, AttributesMapper<T> mapper) {
        return this.search(base, filter, searchScope, ALL_ATTRIBUTES, mapper);
    }

    @Override
    public <T> List<T> search(Name base, String filter, AttributesMapper<T> mapper) {
        return this.search(base, filter, this.defaultSearchScope, mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, AttributesMapper<T> mapper) {
        return this.search(base, filter, this.defaultSearchScope, mapper);
    }

    @Override
    public <T> List<T> search(Name base, String filter, int searchScope, String[] attrs, ContextMapper<T> mapper) {
        return this.search(base, filter, this.getDefaultSearchControls(searchScope, true, attrs), mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, int searchScope, String[] attrs, ContextMapper<T> mapper) {
        return this.search(base, filter, this.getDefaultSearchControls(searchScope, true, attrs), mapper);
    }

    @Override
    public <T> List<T> search(Name base, String filter, int searchScope, ContextMapper<T> mapper) {
        return this.search(base, filter, searchScope, ALL_ATTRIBUTES, mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, int searchScope, ContextMapper<T> mapper) {
        return this.search(base, filter, searchScope, ALL_ATTRIBUTES, mapper);
    }

    @Override
    public <T> List<T> search(Name base, String filter, ContextMapper<T> mapper) {
        return this.search(base, filter, this.defaultSearchScope, mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, ContextMapper<T> mapper) {
        return this.search(base, filter, this.defaultSearchScope, mapper);
    }

    @Override
    public <T> List<T> search(String base, String filter, SearchControls controls, ContextMapper<T> mapper) {
        return this.search(base, filter, controls, mapper, (DirContextProcessor)new NullDirContextProcessor());
    }

    @Override
    public <T> List<T> search(Name base, String filter, SearchControls controls, ContextMapper<T> mapper) {
        return this.search(base, filter, controls, mapper, (DirContextProcessor)new NullDirContextProcessor());
    }

    @Override
    public <T> List<T> search(Name base, String filter, SearchControls controls, AttributesMapper<T> mapper) {
        return this.search(base, filter, controls, mapper, (DirContextProcessor)new NullDirContextProcessor());
    }

    @Override
    public <T> List<T> search(String base, String filter, SearchControls controls, AttributesMapper<T> mapper) {
        return this.search(base, filter, controls, mapper, (DirContextProcessor)new NullDirContextProcessor());
    }

    @Override
    public <T> List<T> search(String base, String filter, SearchControls controls, AttributesMapper<T> mapper, DirContextProcessor processor) {
        AttributesMapperCallbackHandler<T> handler = new AttributesMapperCallbackHandler<T>(mapper);
        this.search(base, filter, controls, handler, processor);
        return handler.getList();
    }

    @Override
    public <T> List<T> search(Name base, String filter, SearchControls controls, AttributesMapper<T> mapper, DirContextProcessor processor) {
        AttributesMapperCallbackHandler<T> handler = new AttributesMapperCallbackHandler<T>(mapper);
        this.search(base, filter, controls, handler, processor);
        return handler.getList();
    }

    @Override
    public <T> List<T> search(String base, String filter, SearchControls controls, ContextMapper<T> mapper, DirContextProcessor processor) {
        this.assureReturnObjFlagSet(controls);
        ContextMapperCallbackHandler<T> handler = new ContextMapperCallbackHandler<T>(mapper);
        this.search(base, filter, controls, handler, processor);
        return handler.getList();
    }

    @Override
    public <T> List<T> search(Name base, String filter, SearchControls controls, ContextMapper<T> mapper, DirContextProcessor processor) {
        this.assureReturnObjFlagSet(controls);
        ContextMapperCallbackHandler<T> handler = new ContextMapperCallbackHandler<T>(mapper);
        this.search(base, filter, controls, handler, processor);
        return handler.getList();
    }

    @Override
    public void list(final String base, NameClassPairCallbackHandler handler) {
        SearchExecutor searchExecutor = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.list(base);
            }
        };
        this.search(searchExecutor, handler);
    }

    @Override
    public void list(final Name base, NameClassPairCallbackHandler handler) {
        SearchExecutor searchExecutor = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.list(base);
            }
        };
        this.search(searchExecutor, handler);
    }

    @Override
    public <T> List<T> list(String base, NameClassPairMapper<T> mapper) {
        MappingCollectingNameClassPairCallbackHandler<T> handler = new MappingCollectingNameClassPairCallbackHandler<T>(mapper);
        this.list(base, handler);
        return handler.getList();
    }

    @Override
    public <T> List<T> list(Name base, NameClassPairMapper<T> mapper) {
        MappingCollectingNameClassPairCallbackHandler<T> handler = new MappingCollectingNameClassPairCallbackHandler<T>(mapper);
        this.list(base, handler);
        return handler.getList();
    }

    @Override
    public List<String> list(Name base) {
        return this.list(base, new DefaultNameClassPairMapper());
    }

    @Override
    public List<String> list(String base) {
        return this.list(base, new DefaultNameClassPairMapper());
    }

    @Override
    public void listBindings(final String base, NameClassPairCallbackHandler handler) {
        SearchExecutor searchExecutor = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.listBindings(base);
            }
        };
        this.search(searchExecutor, handler);
    }

    @Override
    public void listBindings(final Name base, NameClassPairCallbackHandler handler) {
        SearchExecutor searchExecutor = new SearchExecutor(){

            @Override
            public NamingEnumeration executeSearch(DirContext ctx) throws javax.naming.NamingException {
                return ctx.listBindings(base);
            }
        };
        this.search(searchExecutor, handler);
    }

    @Override
    public <T> List<T> listBindings(String base, NameClassPairMapper<T> mapper) {
        MappingCollectingNameClassPairCallbackHandler<T> handler = new MappingCollectingNameClassPairCallbackHandler<T>(mapper);
        this.listBindings(base, handler);
        return handler.getList();
    }

    @Override
    public <T> List<T> listBindings(Name base, NameClassPairMapper<T> mapper) {
        MappingCollectingNameClassPairCallbackHandler<T> handler = new MappingCollectingNameClassPairCallbackHandler<T>(mapper);
        this.listBindings(base, handler);
        return handler.getList();
    }

    @Override
    public List<String> listBindings(String base) {
        return this.listBindings(base, new DefaultNameClassPairMapper());
    }

    @Override
    public List<String> listBindings(Name base) {
        return this.listBindings(base, new DefaultNameClassPairMapper());
    }

    @Override
    public <T> List<T> listBindings(String base, ContextMapper<T> mapper) {
        ContextMapperCallbackHandler<T> handler = new ContextMapperCallbackHandler<T>(mapper);
        this.listBindings(base, handler);
        return handler.getList();
    }

    @Override
    public <T> List<T> listBindings(Name base, ContextMapper<T> mapper) {
        ContextMapperCallbackHandler<T> handler = new ContextMapperCallbackHandler<T>(mapper);
        this.listBindings(base, handler);
        return handler.getList();
    }

    @Override
    public <T> T executeReadOnly(ContextExecutor<T> ce) {
        DirContext ctx = this.contextSource.getReadOnlyContext();
        return this.executeWithContext(ce, ctx);
    }

    @Override
    public <T> T executeReadWrite(ContextExecutor<T> ce) {
        DirContext ctx = this.contextSource.getReadWriteContext();
        return this.executeWithContext(ce, ctx);
    }

    private <T> T executeWithContext(ContextExecutor<T> ce, DirContext ctx) {
        try {
            T t = ce.executeWithContext(ctx);
            return t;
        }
        catch (javax.naming.NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        finally {
            this.closeContext(ctx);
        }
    }

    @Override
    public Object lookup(final Name dn) {
        return this.executeReadOnly(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                return ctx.lookup(dn);
            }
        });
    }

    @Override
    public Object lookup(final String dn) {
        return this.executeReadOnly(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                return ctx.lookup(dn);
            }
        });
    }

    @Override
    public <T> T lookup(final Name dn, final AttributesMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes attributes = ctx.getAttributes(dn);
                return mapper.mapFromAttributes(attributes);
            }
        });
    }

    @Override
    public <T> T lookup(final String dn, final AttributesMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes attributes = ctx.getAttributes(dn);
                return mapper.mapFromAttributes(attributes);
            }
        });
    }

    @Override
    public <T> T lookup(final Name dn, final ContextMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Object object = ctx.lookup(dn);
                return mapper.mapFromContext(object);
            }
        });
    }

    @Override
    public <T> T lookup(final String dn, final ContextMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Object object = ctx.lookup(dn);
                return mapper.mapFromContext(object);
            }
        });
    }

    @Override
    public <T> T lookup(final Name dn, final String[] attributes, final AttributesMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes filteredAttributes = ctx.getAttributes(dn, attributes);
                return mapper.mapFromAttributes(filteredAttributes);
            }
        });
    }

    @Override
    public <T> T lookup(final String dn, final String[] attributes, final AttributesMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes filteredAttributes = ctx.getAttributes(dn, attributes);
                return mapper.mapFromAttributes(filteredAttributes);
            }
        });
    }

    @Override
    public <T> T lookup(final Name dn, final String[] attributes, final ContextMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes filteredAttributes = ctx.getAttributes(dn, attributes);
                DirContextAdapter contextAdapter = new DirContextAdapter(filteredAttributes, dn);
                return mapper.mapFromContext(contextAdapter);
            }
        });
    }

    @Override
    public <T> T lookup(final String dn, final String[] attributes, final ContextMapper<T> mapper) {
        return this.executeReadOnly(new ContextExecutor<T>(){

            @Override
            public T executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                Attributes filteredAttributes = ctx.getAttributes(dn, attributes);
                LdapName name = LdapUtils.newLdapName(dn);
                DirContextAdapter contextAdapter = new DirContextAdapter(filteredAttributes, name);
                return mapper.mapFromContext(contextAdapter);
            }
        });
    }

    @Override
    public void modifyAttributes(final Name dn, final ModificationItem[] mods) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.modifyAttributes(dn, mods);
                return null;
            }
        });
    }

    @Override
    public void modifyAttributes(final String dn, final ModificationItem[] mods) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.modifyAttributes(dn, mods);
                return null;
            }
        });
    }

    @Override
    public void bind(final Name dn, final Object obj, final Attributes attributes) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.bind(dn, obj, attributes);
                return null;
            }
        });
    }

    @Override
    public void bind(final String dn, final Object obj, final Attributes attributes) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.bind(dn, obj, attributes);
                return null;
            }
        });
    }

    @Override
    public void unbind(Name dn) {
        this.doUnbind(dn);
    }

    @Override
    public void unbind(String dn) {
        this.doUnbind(dn);
    }

    @Override
    public void unbind(Name dn, boolean recursive) {
        if (!recursive) {
            this.doUnbind(dn);
        } else {
            this.doUnbindRecursively(dn);
        }
    }

    @Override
    public void unbind(String dn, boolean recursive) {
        if (!recursive) {
            this.doUnbind(dn);
        } else {
            this.doUnbindRecursively(dn);
        }
    }

    private void doUnbind(final Name dn) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.unbind(dn);
                return null;
            }
        });
    }

    private void doUnbind(final String dn) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.unbind(dn);
                return null;
            }
        });
    }

    private void doUnbindRecursively(final Name dn) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) {
                LdapTemplate.this.deleteRecursively(ctx, LdapUtils.newLdapName(dn));
                return null;
            }
        });
    }

    private void doUnbindRecursively(final String dn) {
        this.executeReadWrite(new ContextExecutor<Object>(){

            @Override
            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                LdapTemplate.this.deleteRecursively(ctx, LdapUtils.newLdapName(dn));
                return null;
            }
        });
    }

    protected void deleteRecursively(DirContext ctx, Name name) {
        NamingEnumeration<Binding> enumeration = null;
        try {
            enumeration = ctx.listBindings(name);
            while (enumeration.hasMore()) {
                Binding binding = enumeration.next();
                LdapName childName = LdapUtils.newLdapName(binding.getName());
                childName.addAll(0, name);
                this.deleteRecursively(ctx, childName);
            }
            ctx.unbind(name);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Entry " + name + " deleted");
            }
        }
        catch (javax.naming.NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        finally {
            try {
                enumeration.close();
            }
            catch (Exception exception) {}
        }
    }

    @Override
    public void rebind(final Name dn, final Object obj, final Attributes attributes) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.rebind(dn, obj, attributes);
                return null;
            }
        });
    }

    @Override
    public void rebind(final String dn, final Object obj, final Attributes attributes) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.rebind(dn, obj, attributes);
                return null;
            }
        });
    }

    @Override
    public void rename(final Name oldDn, final Name newDn) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.rename(oldDn, newDn);
                return null;
            }
        });
    }

    @Override
    public void rename(final String oldDn, final String newDn) {
        this.executeReadWrite(new ContextExecutor(){

            public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                ctx.rename(oldDn, newDn);
                return null;
            }
        });
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.contextSource, (String)"Property 'contextSource' must be set.");
    }

    private void closeContextAndNamingEnumeration(DirContext ctx, NamingEnumeration results) {
        this.closeNamingEnumeration(results);
        this.closeContext(ctx);
    }

    private void closeContext(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void closeNamingEnumeration(NamingEnumeration results) {
        if (results != null) {
            try {
                results.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private SearchControls getDefaultSearchControls(int searchScope, boolean returningObjFlag, String[] attrs) {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(searchScope);
        controls.setTimeLimit(this.defaultTimeLimit);
        controls.setCountLimit(this.defaultCountLimit);
        controls.setReturningObjFlag(returningObjFlag);
        controls.setReturningAttributes(attrs);
        return controls;
    }

    private void assureReturnObjFlagSet(SearchControls controls) {
        Assert.notNull((Object)controls, (String)"controls must not be null");
        if (!controls.getReturningObjFlag()) {
            LOG.debug("The returnObjFlag of supplied SearchControls is not set but a ContextMapper is used - setting flag to true");
            controls.setReturningObjFlag(true);
        }
    }

    @Override
    public DirContextOperations lookupContext(Name dn) {
        return (DirContextOperations)this.lookup(dn);
    }

    @Override
    public DirContextOperations lookupContext(String dn) {
        return (DirContextOperations)this.lookup(dn);
    }

    @Override
    public void modifyAttributes(DirContextOperations ctx) {
        Name dn = ctx.getDn();
        if (dn == null || !ctx.isUpdateMode()) {
            throw new IllegalStateException("The DirContextOperations instance needs to be properly initialized.");
        }
        this.modifyAttributes(dn, ctx.getModificationItems());
    }

    @Override
    public void bind(DirContextOperations ctx) {
        Name dn = ctx.getDn();
        if (dn == null || ctx.isUpdateMode()) {
            throw new IllegalStateException("The DirContextOperations instance needs to be properly initialized.");
        }
        this.bind(dn, (Object)ctx, null);
    }

    @Override
    public void rebind(DirContextOperations ctx) {
        Name dn = ctx.getDn();
        if (dn == null || ctx.isUpdateMode()) {
            throw new IllegalStateException("The DirContextOperations instance needs to be properly initialized.");
        }
        this.rebind(dn, (Object)ctx, null);
    }

    @Override
    public boolean authenticate(Name base, String filter, String password) {
        return this.authenticate(base, filter, password, (AuthenticatedLdapEntryContextCallback)new NullAuthenticatedLdapEntryContextCallback(), (AuthenticationErrorCallback)new NullAuthenticationErrorCallback());
    }

    @Override
    public boolean authenticate(String base, String filter, String password) {
        return this.authenticate(LdapUtils.newLdapName(base), filter, password, (AuthenticatedLdapEntryContextCallback)new NullAuthenticatedLdapEntryContextCallback(), (AuthenticationErrorCallback)new NullAuthenticationErrorCallback());
    }

    @Override
    public boolean authenticate(String base, String filter, String password, AuthenticatedLdapEntryContextCallback callback) {
        return this.authenticate(LdapUtils.newLdapName(base), filter, password, callback, (AuthenticationErrorCallback)new NullAuthenticationErrorCallback());
    }

    @Override
    public boolean authenticate(Name base, String filter, String password, AuthenticatedLdapEntryContextCallback callback) {
        return this.authenticate(base, filter, password, callback, (AuthenticationErrorCallback)new NullAuthenticationErrorCallback());
    }

    @Override
    public boolean authenticate(String base, String filter, String password, AuthenticationErrorCallback errorCallback) {
        return this.authenticate(LdapUtils.newLdapName(base), filter, password, (AuthenticatedLdapEntryContextCallback)new NullAuthenticatedLdapEntryContextCallback(), errorCallback);
    }

    @Override
    public boolean authenticate(Name base, String filter, String password, AuthenticationErrorCallback errorCallback) {
        return this.authenticate(base, filter, password, (AuthenticatedLdapEntryContextCallback)new NullAuthenticatedLdapEntryContextCallback(), errorCallback);
    }

    @Override
    public boolean authenticate(String base, String filter, String password, AuthenticatedLdapEntryContextCallback callback, AuthenticationErrorCallback errorCallback) {
        return this.authenticate(LdapUtils.newLdapName(base), filter, password, callback, errorCallback);
    }

    @Override
    public boolean authenticate(Name base, String filter, String password, AuthenticatedLdapEntryContextCallback callback, AuthenticationErrorCallback errorCallback) {
        return this.authenticate(base, filter, password, this.getDefaultSearchControls(this.defaultSearchScope, true, null), callback, errorCallback).isSuccess();
    }

    private AuthenticationStatus authenticate(Name base, String filter, String password, SearchControls searchControls, final AuthenticatedLdapEntryContextCallback callback, AuthenticationErrorCallback errorCallback) {
        List<LdapEntryIdentification> result = this.search(base, filter, searchControls, new LdapEntryIdentificationContextMapper());
        if (result.size() == 0) {
            String msg = "No results found for search, base: '" + base + "'; filter: '" + filter + "'.";
            LOG.info(msg);
            return AuthenticationStatus.EMPTYRESULT;
        }
        if (result.size() > 1) {
            String msg = "base: '" + base + "'; filter: '" + filter + "'.";
            throw new IncorrectResultSizeDataAccessException(msg, 1, result.size());
        }
        final LdapEntryIdentification entryIdentification = result.get(0);
        try {
            DirContext ctx = this.contextSource.getContext(entryIdentification.getAbsoluteName().toString(), password);
            this.executeWithContext(new ContextExecutor<Object>(){

                @Override
                public Object executeWithContext(DirContext ctx) throws javax.naming.NamingException {
                    callback.executeWithContext(ctx, entryIdentification);
                    return null;
                }
            }, ctx);
            return AuthenticationStatus.SUCCESS;
        }
        catch (Exception e) {
            LOG.debug("Authentication failed for entry with DN '" + entryIdentification.getAbsoluteName() + "'", (Throwable)e);
            errorCallback.execute(e);
            return AuthenticationStatus.UNDEFINED_FAILURE;
        }
    }

    @Override
    public <T> T authenticate(LdapQuery query, String password, AuthenticatedLdapEntryContextMapper<T> mapper) {
        SearchControls searchControls = this.searchControlsForQuery(query, true);
        ReturningAuthenticatedLdapEntryContext mapperCallback = new ReturningAuthenticatedLdapEntryContext(mapper);
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AuthenticationStatus authenticationStatus = this.authenticate(query.base(), query.filter().encode(), password, searchControls, mapperCallback, errorCallback);
        if (errorCallback.hasError()) {
            Exception error = errorCallback.getError();
            if (error instanceof NamingException) {
                throw (NamingException)((Object)error);
            }
            throw new UncategorizedLdapException(error);
        }
        if (AuthenticationStatus.EMPTYRESULT == authenticationStatus) {
            throw new EmptyResultDataAccessException(1);
        }
        if (!authenticationStatus.isSuccess()) {
            throw new AuthenticationException();
        }
        return (T)mapperCallback.collectedObject;
    }

    @Override
    public void authenticate(LdapQuery query, String password) {
        this.authenticate(query, password, new NullAuthenticatedLdapEntryContextCallback());
    }

    @Override
    public <T> T searchForObject(Name base, String filter, ContextMapper<T> mapper) {
        return this.searchForObject(base, filter, this.getDefaultSearchControls(this.defaultSearchScope, true, ALL_ATTRIBUTES), mapper);
    }

    @Override
    public <T> T searchForObject(String base, String filter, ContextMapper<T> mapper) {
        return this.searchForObject(LdapUtils.newLdapName(base), filter, mapper);
    }

    @Override
    public <T> T searchForObject(Name base, String filter, SearchControls searchControls, ContextMapper<T> mapper) {
        List<T> result = this.search(base, filter, searchControls, mapper);
        if (result.size() == 0) {
            throw new EmptyResultDataAccessException(1);
        }
        if (result.size() != 1) {
            throw new IncorrectResultSizeDataAccessException(1, result.size());
        }
        return result.get(0);
    }

    @Override
    public <T> T searchForObject(String base, String filter, SearchControls searchControls, ContextMapper<T> mapper) {
        return this.searchForObject(LdapUtils.newLdapName(base), filter, searchControls, mapper);
    }

    @Override
    public void search(LdapQuery query, NameClassPairCallbackHandler callbackHandler) {
        SearchControls searchControls = this.searchControlsForQuery(query, false);
        this.search(query.base(), query.filter().encode(), searchControls, callbackHandler);
    }

    @Override
    public <T> List<T> search(LdapQuery query, ContextMapper<T> mapper) {
        SearchControls searchControls = this.searchControlsForQuery(query, true);
        return this.search(query.base(), query.filter().encode(), searchControls, mapper);
    }

    private SearchControls searchControlsForQuery(LdapQuery query, boolean returnObjFlag) {
        SearchControls searchControls = this.getDefaultSearchControls(this.defaultSearchScope, returnObjFlag, query.attributes());
        if (query.searchScope() != null) {
            searchControls.setSearchScope(query.searchScope().getId());
        }
        if (query.countLimit() != null) {
            searchControls.setCountLimit(query.countLimit().intValue());
        }
        if (query.timeLimit() != null) {
            searchControls.setTimeLimit(query.timeLimit());
        }
        return searchControls;
    }

    @Override
    public <T> List<T> search(LdapQuery query, AttributesMapper<T> mapper) {
        SearchControls searchControls = this.searchControlsForQuery(query, false);
        return this.search(query.base(), query.filter().encode(), searchControls, mapper);
    }

    @Override
    public DirContextOperations searchForContext(LdapQuery query) {
        return this.searchForObject(query, new ContextMapper<DirContextOperations>(){

            @Override
            public DirContextOperations mapFromContext(Object ctx) throws javax.naming.NamingException {
                return (DirContextOperations)ctx;
            }
        });
    }

    @Override
    public <T> T searchForObject(LdapQuery query, ContextMapper<T> mapper) {
        SearchControls searchControls = this.searchControlsForQuery(query, false);
        return this.searchForObject(query.base(), query.filter().encode(), searchControls, mapper);
    }

    @Override
    public <T> T findByDn(Name dn, final Class<T> clazz) {
        String[] attributes;
        T result;
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Reading Entry at - %1$s", dn));
        }
        if ((result = this.lookup(dn, attributes = this.odm.manageClass(clazz), new ContextMapper<T>(){

            @Override
            public T mapFromContext(Object ctx) throws javax.naming.NamingException {
                return LdapTemplate.this.odm.mapFromLdapDataEntry((DirContextOperations)ctx, clazz);
            }
        })) == null) {
            throw new OdmException(String.format("Entry %1$s does not have the required objectclasses ", dn));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Found entry - %1$s", result));
        }
        return result;
    }

    @Override
    public void create(Object entry) {
        Name id;
        Assert.notNull((Object)entry, (String)"Entry must not be null");
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Creating entry - %1$s", entry));
        }
        if ((id = this.odm.getId(entry)) == null) {
            id = this.odm.getCalculatedId(entry);
            this.odm.setId(entry, id);
        }
        Assert.notNull((Object)id, (String)String.format("Unable to determine id for entry %s", entry.toString()));
        DirContextAdapter context = new DirContextAdapter(id);
        this.odm.mapToLdapDataEntry(entry, context);
        this.bind(context);
    }

    @Override
    public void update(Object entry) {
        Assert.notNull((Object)entry, (String)"Entry must not be null");
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Updating entry - %1$s", entry));
        }
        Name originalId = this.odm.getId(entry);
        Name calculatedId = this.odm.getCalculatedId(entry);
        if (originalId != null && calculatedId != null && !originalId.equals(calculatedId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Calculated DN of %s; of entry %s differs from explicitly specified one; %s - moving", calculatedId, entry, originalId));
            }
            this.unbind(originalId);
            DirContextAdapter context = new DirContextAdapter(calculatedId);
            this.odm.mapToLdapDataEntry(entry, context);
            this.bind(context);
            this.odm.setId(entry, calculatedId);
        } else {
            Name id = originalId;
            if (id == null) {
                id = calculatedId;
                this.odm.setId(entry, calculatedId);
            }
            Assert.notNull((Object)id, (String)String.format("Unable to determine id for entry %s", entry.toString()));
            DirContextOperations context = this.lookupContext(id);
            this.odm.mapToLdapDataEntry(entry, context);
            this.modifyAttributes(context);
        }
    }

    @Override
    public void delete(Object entry) {
        Name id;
        Assert.notNull((Object)entry, (String)"Entry must not be null");
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Deleting %1$s", entry));
        }
        if ((id = this.odm.getId(entry)) == null) {
            id = this.odm.getCalculatedId(entry);
        }
        Assert.notNull((Object)id, (String)String.format("Unable to determine id for entry %s", entry.toString()));
        this.unbind(id);
    }

    @Override
    public <T> List<T> findAll(Name base, SearchControls searchControls, Class<T> clazz) {
        return this.find(base, null, searchControls, clazz);
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        return this.findAll(LdapUtils.emptyLdapName(), this.getDefaultSearchControls(this.defaultSearchScope, true, ALL_ATTRIBUTES), clazz);
    }

    @Override
    public <T> List<T> find(Name base, Filter filter, SearchControls searchControls, final Class<T> clazz) {
        Filter finalFilter = this.odm.filterFor(clazz, filter);
        Name localBase = base;
        if (base == null || base.size() == 0) {
            localBase = LdapUtils.emptyLdapName();
        }
        if (searchControls.getReturningAttributes() == null) {
            String[] attributes = this.odm.manageClass(clazz);
            searchControls.setReturningAttributes(attributes);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Searching - base=%1$s, finalFilter=%2$s, scope=%3$s", base, finalFilter, searchControls));
        }
        List<T> result = this.search(localBase, finalFilter.encode(), searchControls, new ContextMapper<T>(){

            @Override
            public T mapFromContext(Object ctx) throws javax.naming.NamingException {
                return LdapTemplate.this.odm.mapFromLdapDataEntry((DirContextOperations)ctx, clazz);
            }
        });
        result.remove(null);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Found %1$s Entries - %2$s", result.size(), result));
        }
        return result;
    }

    @Override
    public <T> List<T> find(LdapQuery query, Class<T> clazz) {
        SearchControls searchControls = this.searchControlsForQuery(query, true);
        return this.find(query.base(), query.filter(), searchControls, clazz);
    }

    @Override
    public <T> T findOne(LdapQuery query, Class<T> clazz) {
        List<T> result = this.find(query, clazz);
        if (result.size() == 0) {
            throw new EmptyResultDataAccessException(1);
        }
        if (result.size() != 1) {
            throw new IncorrectResultSizeDataAccessException(1, result.size());
        }
        return result.get(0);
    }

    private static enum AuthenticationStatus {
        SUCCESS(true),
        EMPTYRESULT(false),
        UNDEFINED_FAILURE(false);

        private boolean success;

        private AuthenticationStatus(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return this.success;
        }
    }

    private static final class ReturningAuthenticatedLdapEntryContext<T>
    implements AuthenticatedLdapEntryContextCallback {
        private final AuthenticatedLdapEntryContextMapper<T> mapper;
        private T collectedObject;

        private ReturningAuthenticatedLdapEntryContext(AuthenticatedLdapEntryContextMapper<T> mapper) {
            this.mapper = mapper;
        }

        @Override
        public void executeWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
            this.collectedObject = this.mapper.mapWithContext(ctx, ldapEntryIdentification);
        }
    }

    private static final class NullAuthenticationErrorCallback
    implements AuthenticationErrorCallback {
        private NullAuthenticationErrorCallback() {
        }

        @Override
        public void execute(Exception e) {
        }
    }

    private static final class NullAuthenticatedLdapEntryContextCallback
    implements AuthenticatedLdapEntryContextCallback,
    AuthenticatedLdapEntryContextMapper<Object> {
        private NullAuthenticatedLdapEntryContextCallback() {
        }

        @Override
        public void executeWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
        }

        @Override
        public Object mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
            return null;
        }
    }

    public static final class MappingCollectingNameClassPairCallbackHandler<T>
    extends CollectingNameClassPairCallbackHandler<T> {
        private NameClassPairMapper<T> mapper;

        public MappingCollectingNameClassPairCallbackHandler(NameClassPairMapper<T> mapper) {
            this.mapper = mapper;
        }

        @Override
        public T getObjectFromNameClassPair(NameClassPair nameClassPair) {
            try {
                return this.mapper.mapFromNameClassPair(nameClassPair);
            }
            catch (javax.naming.NamingException e) {
                throw LdapUtils.convertLdapException(e);
            }
        }
    }

    public static final class NullDirContextProcessor
    implements DirContextProcessor {
        @Override
        public void postProcess(DirContext ctx) {
        }

        @Override
        public void preProcess(DirContext ctx) {
        }
    }
}

