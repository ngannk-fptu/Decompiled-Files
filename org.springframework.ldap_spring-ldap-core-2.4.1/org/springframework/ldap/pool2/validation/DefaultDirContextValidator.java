/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.pool2.validation;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.ldap.pool2.validation.DirContextValidator;
import org.springframework.util.Assert;

public class DefaultDirContextValidator
implements DirContextValidator {
    public static final String DEFAULT_FILTER = "objectclass=*";
    private static final int DEFAULT_TIME_LIMIT = 500;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String base;
    private String filter;
    private SearchControls searchControls = new SearchControls();

    public DefaultDirContextValidator() {
        this(0);
    }

    public DefaultDirContextValidator(int searchScope) {
        this.searchControls.setSearchScope(searchScope);
        this.searchControls.setCountLimit(1L);
        this.searchControls.setReturningAttributes(new String[]{"objectclass"});
        this.searchControls.setTimeLimit(500);
        this.base = "";
        this.filter = DEFAULT_FILTER;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getFilter() {
        return this.filter;
    }

    public void setFilter(String filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter may not be null");
        }
        this.filter = filter;
    }

    public SearchControls getSearchControls() {
        return this.searchControls;
    }

    public void setSearchControls(SearchControls searchControls) {
        if (searchControls == null) {
            throw new IllegalArgumentException("searchControls may not be null");
        }
        this.searchControls = searchControls;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean validateDirContext(DirContextType contextType, DirContext dirContext) {
        Assert.notNull((Object)contextType, (String)"contextType may not be null");
        Assert.notNull((Object)dirContext, (String)"dirContext may not be null");
        NamingEnumeration<SearchResult> searchResults = null;
        try {
            searchResults = dirContext.search(this.base, this.filter, this.searchControls);
            if (searchResults.hasMore()) {
                this.logger.debug("DirContext '{}' passed validation.", (Object)dirContext);
                boolean bl = true;
                return bl;
            }
        }
        catch (Exception e) {
            this.logger.debug("DirContext '{}' failed validation with an exception.", (Object)dirContext, (Object)e);
            boolean bl = false;
            return bl;
        }
        finally {
            if (searchResults != null) {
                try {
                    searchResults.close();
                }
                catch (NamingException namingException) {}
            }
        }
        this.logger.debug("DirContext '{}' failed validation.", (Object)dirContext);
        return false;
    }
}

