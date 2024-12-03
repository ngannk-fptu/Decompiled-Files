/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.util.List;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextCallback;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.NameClassPairMapper;
import org.springframework.ldap.core.SearchExecutor;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;

public interface LdapOperations {
    public void search(SearchExecutor var1, NameClassPairCallbackHandler var2, DirContextProcessor var3) throws NamingException;

    public void search(SearchExecutor var1, NameClassPairCallbackHandler var2) throws NamingException;

    public <T> T executeReadOnly(ContextExecutor<T> var1) throws NamingException;

    public <T> T executeReadWrite(ContextExecutor<T> var1) throws NamingException;

    public void search(Name var1, String var2, SearchControls var3, NameClassPairCallbackHandler var4) throws NamingException;

    public void search(String var1, String var2, SearchControls var3, NameClassPairCallbackHandler var4) throws NamingException;

    public void search(Name var1, String var2, SearchControls var3, NameClassPairCallbackHandler var4, DirContextProcessor var5) throws NamingException;

    public <T> List<T> search(String var1, String var2, SearchControls var3, AttributesMapper<T> var4, DirContextProcessor var5) throws NamingException;

    public <T> List<T> search(Name var1, String var2, SearchControls var3, AttributesMapper<T> var4, DirContextProcessor var5) throws NamingException;

    public <T> List<T> search(String var1, String var2, SearchControls var3, ContextMapper<T> var4, DirContextProcessor var5) throws NamingException;

    public <T> List<T> search(Name var1, String var2, SearchControls var3, ContextMapper<T> var4, DirContextProcessor var5) throws NamingException;

    public void search(String var1, String var2, SearchControls var3, NameClassPairCallbackHandler var4, DirContextProcessor var5) throws NamingException;

    public void search(Name var1, String var2, int var3, boolean var4, NameClassPairCallbackHandler var5) throws NamingException;

    public void search(String var1, String var2, int var3, boolean var4, NameClassPairCallbackHandler var5) throws NamingException;

    public void search(Name var1, String var2, NameClassPairCallbackHandler var3) throws NamingException;

    public void search(String var1, String var2, NameClassPairCallbackHandler var3) throws NamingException;

    public <T> List<T> search(Name var1, String var2, int var3, String[] var4, AttributesMapper<T> var5) throws NamingException;

    public <T> List<T> search(String var1, String var2, int var3, String[] var4, AttributesMapper<T> var5) throws NamingException;

    public <T> List<T> search(Name var1, String var2, int var3, AttributesMapper<T> var4) throws NamingException;

    public <T> List<T> search(String var1, String var2, int var3, AttributesMapper<T> var4) throws NamingException;

    public <T> List<T> search(Name var1, String var2, AttributesMapper<T> var3) throws NamingException;

    public <T> List<T> search(String var1, String var2, AttributesMapper<T> var3) throws NamingException;

    public <T> List<T> search(Name var1, String var2, int var3, String[] var4, ContextMapper<T> var5) throws NamingException;

    public <T> List<T> search(String var1, String var2, int var3, String[] var4, ContextMapper<T> var5) throws NamingException;

    public <T> List<T> search(Name var1, String var2, int var3, ContextMapper<T> var4) throws NamingException;

    public <T> List<T> search(String var1, String var2, int var3, ContextMapper<T> var4) throws NamingException;

    public <T> List<T> search(Name var1, String var2, ContextMapper<T> var3) throws NamingException;

    public <T> List<T> search(String var1, String var2, ContextMapper<T> var3) throws NamingException;

    public <T> List<T> search(String var1, String var2, SearchControls var3, ContextMapper<T> var4) throws NamingException;

    public <T> List<T> search(Name var1, String var2, SearchControls var3, ContextMapper<T> var4) throws NamingException;

    public <T> List<T> search(String var1, String var2, SearchControls var3, AttributesMapper<T> var4) throws NamingException;

    public <T> List<T> search(Name var1, String var2, SearchControls var3, AttributesMapper<T> var4) throws NamingException;

    public void list(String var1, NameClassPairCallbackHandler var2) throws NamingException;

    public void list(Name var1, NameClassPairCallbackHandler var2) throws NamingException;

    public <T> List<T> list(String var1, NameClassPairMapper<T> var2) throws NamingException;

    public <T> List<T> list(Name var1, NameClassPairMapper<T> var2) throws NamingException;

    public List<String> list(String var1) throws NamingException;

    public List<String> list(Name var1) throws NamingException;

    public void listBindings(String var1, NameClassPairCallbackHandler var2) throws NamingException;

    public void listBindings(Name var1, NameClassPairCallbackHandler var2) throws NamingException;

    public <T> List<T> listBindings(String var1, NameClassPairMapper<T> var2) throws NamingException;

    public <T> List<T> listBindings(Name var1, NameClassPairMapper<T> var2) throws NamingException;

    public List<String> listBindings(String var1) throws NamingException;

    public List<String> listBindings(Name var1) throws NamingException;

    public <T> List<T> listBindings(String var1, ContextMapper<T> var2) throws NamingException;

    public <T> List<T> listBindings(Name var1, ContextMapper<T> var2) throws NamingException;

    public Object lookup(Name var1) throws NamingException;

    public Object lookup(String var1) throws NamingException;

    public <T> T lookup(Name var1, AttributesMapper<T> var2) throws NamingException;

    public <T> T lookup(String var1, AttributesMapper<T> var2) throws NamingException;

    public <T> T lookup(Name var1, ContextMapper<T> var2) throws NamingException;

    public <T> T lookup(String var1, ContextMapper<T> var2) throws NamingException;

    public <T> T lookup(Name var1, String[] var2, AttributesMapper<T> var3) throws NamingException;

    public <T> T lookup(String var1, String[] var2, AttributesMapper<T> var3) throws NamingException;

    public <T> T lookup(Name var1, String[] var2, ContextMapper<T> var3) throws NamingException;

    public <T> T lookup(String var1, String[] var2, ContextMapper<T> var3) throws NamingException;

    public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException;

    public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException;

    public void bind(Name var1, Object var2, Attributes var3) throws NamingException;

    public void bind(String var1, Object var2, Attributes var3) throws NamingException;

    public void unbind(Name var1) throws NamingException;

    public void unbind(String var1) throws NamingException;

    public void unbind(Name var1, boolean var2) throws NamingException;

    public void unbind(String var1, boolean var2) throws NamingException;

    public void rebind(Name var1, Object var2, Attributes var3) throws NamingException;

    public void rebind(String var1, Object var2, Attributes var3) throws NamingException;

    public void rename(Name var1, Name var2) throws NamingException;

    public void rename(String var1, String var2) throws NamingException;

    public DirContextOperations lookupContext(Name var1) throws NamingException, ClassCastException;

    public DirContextOperations lookupContext(String var1) throws NamingException, ClassCastException;

    public void modifyAttributes(DirContextOperations var1) throws IllegalStateException, NamingException;

    public void bind(DirContextOperations var1);

    public void rebind(DirContextOperations var1);

    public boolean authenticate(Name var1, String var2, String var3);

    public boolean authenticate(String var1, String var2, String var3);

    public boolean authenticate(Name var1, String var2, String var3, AuthenticatedLdapEntryContextCallback var4);

    public boolean authenticate(String var1, String var2, String var3, AuthenticatedLdapEntryContextCallback var4);

    public boolean authenticate(Name var1, String var2, String var3, AuthenticatedLdapEntryContextCallback var4, AuthenticationErrorCallback var5);

    public boolean authenticate(String var1, String var2, String var3, AuthenticatedLdapEntryContextCallback var4, AuthenticationErrorCallback var5);

    public boolean authenticate(Name var1, String var2, String var3, AuthenticationErrorCallback var4);

    public boolean authenticate(String var1, String var2, String var3, AuthenticationErrorCallback var4);

    public <T> T authenticate(LdapQuery var1, String var2, AuthenticatedLdapEntryContextMapper<T> var3);

    public void authenticate(LdapQuery var1, String var2);

    public <T> T searchForObject(Name var1, String var2, ContextMapper<T> var3);

    public <T> T searchForObject(Name var1, String var2, SearchControls var3, ContextMapper<T> var4);

    public <T> T searchForObject(String var1, String var2, SearchControls var3, ContextMapper<T> var4);

    public <T> T searchForObject(String var1, String var2, ContextMapper<T> var3);

    public void search(LdapQuery var1, NameClassPairCallbackHandler var2);

    public <T> List<T> search(LdapQuery var1, ContextMapper<T> var2);

    public <T> List<T> search(LdapQuery var1, AttributesMapper<T> var2);

    public DirContextOperations searchForContext(LdapQuery var1);

    public <T> T searchForObject(LdapQuery var1, ContextMapper<T> var2);

    public <T> T findByDn(Name var1, Class<T> var2);

    public void create(Object var1);

    public void update(Object var1);

    public void delete(Object var1);

    public <T> List<T> findAll(Class<T> var1);

    public <T> List<T> findAll(Name var1, SearchControls var2, Class<T> var3);

    public <T> List<T> find(Name var1, Filter var2, SearchControls var3, Class<T> var4);

    public <T> List<T> find(LdapQuery var1, Class<T> var2);

    public <T> T findOne(LdapQuery var1, Class<T> var2);

    public ObjectDirectoryMapper getObjectDirectoryMapper();
}

