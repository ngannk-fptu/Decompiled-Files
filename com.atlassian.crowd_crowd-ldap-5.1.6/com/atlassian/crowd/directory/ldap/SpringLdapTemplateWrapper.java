/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.DurationSystemProperty
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.AttributesMapper
 *  org.springframework.ldap.core.ContextMapper
 *  org.springframework.ldap.core.DirContextProcessor
 *  org.springframework.ldap.core.LdapTemplate
 *  org.springframework.ldap.core.LdapTemplate$NullDirContextProcessor
 *  org.springframework.ldap.core.NameClassPairCallbackHandler
 *  org.springframework.ldap.core.SearchExecutor
 */
package com.atlassian.crowd.directory.ldap;

import com.atlassian.crowd.common.properties.DurationSystemProperty;
import com.atlassian.crowd.directory.LimitedNamingEnumeration;
import com.atlassian.crowd.directory.ldap.mapper.AttributeToContextCallbackHandler;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.LookupCallbackHandler;
import com.atlassian.crowd.directory.ldap.monitoring.ExecutionInfoNameClassPairCallbackHandler;
import com.atlassian.crowd.directory.ldap.monitoring.TimedSupplier;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.SearchExecutor;

public class SpringLdapTemplateWrapper {
    private static final DurationSystemProperty TIMED_LOG_THRESHOLD = new DurationSystemProperty("com.atlassian.crowd.ldap.log.wait.threshold", (TemporalUnit)ChronoUnit.MILLIS, 1000L);
    private static final Logger logger = LoggerFactory.getLogger(SpringLdapTemplateWrapper.class);
    private final LdapTemplate template;
    private final long logThreshold;

    public SpringLdapTemplateWrapper(LdapTemplate template) {
        this.template = template;
        this.logThreshold = ((Duration)TIMED_LOG_THRESHOLD.getValue()).toMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static <T> T invokeWithContextClassLoader(Supplier<T> supplier) {
        Thread current = Thread.currentThread();
        ClassLoader orig = current.getContextClassLoader();
        try {
            ClassLoader classLoaderForThisClass = SpringLdapTemplateWrapper.class.getClassLoader();
            current.setContextClassLoader(classLoaderForThisClass);
            T t = supplier.get();
            return t;
        }
        finally {
            current.setContextClassLoader(orig);
        }
    }

    public List search(final Name base, final String filter, final SearchControls controls, final ContextMapper mapper) {
        Preconditions.checkArgument((!controls.getReturningObjFlag() ? 1 : 0) != 0);
        String operationDescription = "search on " + base + " with filter: " + filter;
        return SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<List>(operationDescription, this.logThreshold){

            @Override
            public List timedGet() {
                SearchExecutor se = ctx -> ctx.search(base, filter, controls);
                AttributeToContextCallbackHandler handler = new AttributeToContextCallbackHandler(mapper);
                ExecutionInfoNameClassPairCallbackHandler wrappedHandler = SpringLdapTemplateWrapper.this.wrapHandler(handler);
                SpringLdapTemplateWrapper.this.template.search(se, (NameClassPairCallbackHandler)wrappedHandler, (DirContextProcessor)new LdapTemplate.NullDirContextProcessor());
                wrappedHandler.logResultCount();
                return handler.getList();
            }
        });
    }

    public List search(Name base, String filter, SearchControls controls, ContextMapper mapper, DirContextProcessor processor) {
        Preconditions.checkArgument((!controls.getReturningObjFlag() ? 1 : 0) != 0);
        AttributeToContextCallbackHandler handler = new AttributeToContextCallbackHandler(mapper);
        this.search(base, filter, controls, handler, processor);
        return handler.getList();
    }

    public Object lookup(final Name dn) {
        String operationDescription = "lookup on " + dn;
        return SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>(operationDescription, this.logThreshold){

            @Override
            public Object timedGet() {
                SearchExecutor se = ctx -> {
                    SearchControls searchControls = new SearchControls();
                    searchControls.setSearchScope(0);
                    searchControls.setReturningAttributes(null);
                    searchControls.setReturningObjFlag(false);
                    return ctx.search(dn, "(objectClass=*)", searchControls);
                };
                LookupCallbackHandler handler = new LookupCallbackHandler();
                ExecutionInfoNameClassPairCallbackHandler wrappedHandler = SpringLdapTemplateWrapper.this.wrapHandler(handler);
                SpringLdapTemplateWrapper.this.template.search(se, (NameClassPairCallbackHandler)wrappedHandler);
                wrappedHandler.logResultCount();
                return Iterables.getFirst((Iterable)handler.getList(), null);
            }
        });
    }

    public void search(final Name base, final String filter, final SearchControls controls, final AttributeToContextCallbackHandler handler, final DirContextProcessor processor) {
        Preconditions.checkArgument((!controls.getReturningObjFlag() ? 1 : 0) != 0);
        String operationDescription = "search with handler on baseDN: " + base + ", filter: " + filter;
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>(operationDescription, this.logThreshold){

            @Override
            public Void timedGet() {
                SearchExecutor se = ctx -> ctx.search(base, filter, controls);
                ExecutionInfoNameClassPairCallbackHandler wrappedHandler = SpringLdapTemplateWrapper.this.wrapHandler((NameClassPairCallbackHandler)handler);
                SpringLdapTemplateWrapper.this.template.search(se, (NameClassPairCallbackHandler)wrappedHandler, processor);
                wrappedHandler.logResultCount();
                return null;
            }
        });
    }

    public void unbind(final Name dn) {
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>("unbind on " + dn, this.logThreshold){

            @Override
            public Void timedGet() {
                SpringLdapTemplateWrapper.this.template.unbind(dn);
                return null;
            }
        });
    }

    public void bind(final Name dn, final Object obj, final Attributes attributes) {
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>("bind on " + dn, this.logThreshold){

            @Override
            public Void timedGet() {
                SpringLdapTemplateWrapper.this.template.bind(dn, obj, attributes);
                return null;
            }
        });
    }

    public void rename(final String oldDn, final String newDn) {
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>("rename " + oldDn + " -> " + newDn, this.logThreshold){

            @Override
            public Void timedGet() {
                SpringLdapTemplateWrapper.this.template.rename(oldDn, newDn);
                return null;
            }
        });
    }

    public void modifyAttributes(final Name dn, final ModificationItem[] mods) {
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>("modify attributes on " + dn, this.logThreshold){

            @Override
            public Void timedGet() {
                SpringLdapTemplateWrapper.this.template.modifyAttributes(dn, mods);
                return null;
            }
        });
    }

    public void lookup(final LdapName dn, final String[] attributes, final AttributesMapper mapper) {
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>("lookup on " + dn, this.logThreshold){

            @Override
            public Void timedGet() {
                Object result = SpringLdapTemplateWrapper.this.template.lookup((Name)dn, attributes, mapper);
                logger.trace("Lookup result: [{}]", result);
                return null;
            }
        });
    }

    public <T> T lookup(final LdapName dn, final ContextMapperWithRequiredAttributes<T> mapper) {
        Set<String> attrSet = mapper.getRequiredLdapAttributes();
        final String[] attributes = attrSet.toArray(new String[attrSet.size()]);
        return SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<T>("lookup with mapper on " + dn, this.logThreshold){

            @Override
            public T timedGet() {
                Object result = SpringLdapTemplateWrapper.this.template.lookup((Name)dn, attributes, (ContextMapper)mapper);
                logger.trace("Lookup result: [{}]", result);
                return result;
            }
        });
    }

    public void setIgnorePartialResultException(boolean ignore) {
        this.template.setIgnorePartialResultException(ignore);
    }

    private void search(final SearchExecutor se, final NameClassPairCallbackHandler handler, final DirContextProcessor processor) {
        String operationDescription = "search using searchexecutor " + se;
        SpringLdapTemplateWrapper.invokeWithContextClassLoader(new TimedSupplier<Object>(operationDescription, this.logThreshold){

            @Override
            public Void timedGet() {
                ExecutionInfoNameClassPairCallbackHandler wrappedHandler = SpringLdapTemplateWrapper.this.wrapHandler(handler);
                SpringLdapTemplateWrapper.this.template.search(se, (NameClassPairCallbackHandler)wrappedHandler, processor);
                wrappedHandler.logResultCount();
                return null;
            }
        });
    }

    private ExecutionInfoNameClassPairCallbackHandler wrapHandler(NameClassPairCallbackHandler handler) {
        return new ExecutionInfoNameClassPairCallbackHandler<NameClassPairCallbackHandler>(handler);
    }

    public List searchWithLimitedResults(final Name baseDN, final String filter, final SearchControls searchControls, ContextMapper contextMapper, DirContextProcessor processor, final int limit) {
        Preconditions.checkArgument((!searchControls.getReturningObjFlag() ? 1 : 0) != 0);
        SearchExecutor se = new SearchExecutor(){

            @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input, filter is encoded in all calls")
            public NamingEnumeration<SearchResult> executeSearch(DirContext ctx) throws NamingException {
                NamingEnumeration<SearchResult> ne = ctx.search(baseDN, filter, searchControls);
                if (limit != -1) {
                    return new LimitedNamingEnumeration<SearchResult>(ne, limit);
                }
                return ne;
            }

            public String toString() {
                return "baseDN: " + baseDN + ", filter: " + filter;
            }
        };
        AttributeToContextCallbackHandler handler = new AttributeToContextCallbackHandler(contextMapper);
        this.search(se, (NameClassPairCallbackHandler)handler, processor);
        return handler.getList();
    }
}

