/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user;

import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.Entity;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManagerImplementationException;
import com.opensymphony.user.authenticator.Authenticator;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.UserProvider;
import com.opensymphony.user.util.ConfigLoader;
import com.opensymphony.util.ClassLoaderUtil;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class UserManager
implements Serializable {
    private static UserManager instance;
    private static final Log logger;
    private static final int TYPE_USER = 0;
    private static final int TYPE_GROUP = 1;
    private Accessor accessor;
    private Authenticator authenticator = null;
    private List accessProviders = new ArrayList();
    private List credentialsProviders = new ArrayList();
    private List profileProviders = new ArrayList();
    static /* synthetic */ Class class$com$opensymphony$user$UserManager;
    static /* synthetic */ Class class$com$opensymphony$user$provider$CredentialsProvider;
    static /* synthetic */ Class class$com$opensymphony$user$provider$AccessProvider;

    public UserManager() {
        this("osuser.xml");
    }

    public UserManager(String filename) {
        this.accessor = new Accessor();
        ConfigLoader configLoader = new ConfigLoader();
        String loc = "/" + filename;
        InputStream in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        if (in == null) {
            loc = filename;
            in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        }
        if (in == null) {
            loc = "/META-INF/" + filename;
            in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        }
        if (in == null) {
            loc = "META-INF/" + filename;
            in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        }
        if (in == null) {
            loc = "/META-INF/osuser-default.xml";
            in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        }
        if (in == null) {
            loc = "META-INF/osuser-default.xml";
            in = ClassLoaderUtil.getResourceAsStream((String)loc, this.getClass());
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("loading using config : " + loc));
        }
        if (in == null) {
            throw new UserManagerImplementationException("The configuration file " + filename + " could not be found.");
        }
        configLoader.load(in, this);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("loaded using config : " + loc));
        }
    }

    public static void setInstance(UserManager userManager) {
        instance = userManager;
    }

    public static UserManager getInstance() {
        try {
            if (instance == null) {
                instance = new UserManager();
            }
        }
        catch (UserManagerImplementationException e) {
            logger.error((Object)"Unable to load configuration", (Throwable)e);
        }
        catch (RuntimeException e) {
            logger.error((Object)"unexpected runtime exception during initialization", (Throwable)e);
        }
        return instance;
    }

    public UserProvider getAccessProvider(String entityName) {
        return this.getProvider(entityName, this.accessProviders);
    }

    public Collection getAccessProviders() {
        return this.accessProviders;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public UserProvider getCredentialsProvider(String entityName) {
        return this.getProvider(entityName, this.credentialsProviders);
    }

    public Collection getCredentialsProviders() {
        return this.credentialsProviders;
    }

    public Group getGroup(String name) throws EntityNotFoundException {
        return (Group)this.getEntity(name, this.accessProviders, 1);
    }

    public List getGroups() {
        return this.getEntities(this.accessProviders, 1);
    }

    public UserProvider getProfileProvider(String entityName) {
        return this.getProvider(entityName, this.profileProviders);
    }

    public Collection getProfileProviders() {
        return this.profileProviders;
    }

    public User getUser(String name) throws EntityNotFoundException {
        return (User)this.getEntity(name, this.credentialsProviders, 0);
    }

    public List getUsers() {
        return this.getEntities(this.credentialsProviders, 0);
    }

    public void addProvider(UserProvider provider) {
        if (provider instanceof CredentialsProvider) {
            this.credentialsProviders.add(provider);
        }
        if (provider instanceof ProfileProvider) {
            this.profileProviders.add(provider);
        }
        if (provider instanceof AccessProvider) {
            this.accessProviders.add(provider);
        }
    }

    public Group createGroup(String name) throws DuplicateEntityException, ImmutableException {
        return (Group)this.createEntity(name, this.accessProviders, 1);
    }

    public User createUser(String name) throws DuplicateEntityException, ImmutableException {
        return (User)this.createEntity(name, this.credentialsProviders, 0);
    }

    public void flushCaches() {
        UserProvider userProvider;
        Iterator iterator = this.accessProviders.iterator();
        while (iterator.hasNext()) {
            userProvider = (UserProvider)iterator.next();
            userProvider.flushCaches();
        }
        iterator = this.credentialsProviders.iterator();
        while (iterator.hasNext()) {
            userProvider = (UserProvider)iterator.next();
            userProvider.flushCaches();
        }
        iterator = this.profileProviders.iterator();
        while (iterator.hasNext()) {
            userProvider = (UserProvider)iterator.next();
            userProvider.flushCaches();
        }
    }

    protected UserProvider getProvider(String name, List providers) {
        UserProvider provider;
        Iterator i = providers.iterator();
        while (i.hasNext()) {
            provider = (UserProvider)i.next();
            if (!provider.handles(name)) continue;
            return provider;
        }
        if (providers == this.profileProviders) {
            i = providers.iterator();
            while (i.hasNext()) {
                provider = (UserProvider)i.next();
                if (!provider.create(name)) continue;
                return provider;
            }
        }
        return null;
    }

    private List getEntities(List providers, int type) {
        ArrayList<Entity> result = new ArrayList<Entity>();
        List toCheck = this.credentialsProviders;
        if (type == 1) {
            toCheck = this.accessProviders;
        }
        Iterator i = toCheck.iterator();
        while (i.hasNext()) {
            UserProvider provider = (UserProvider)i.next();
            List entities = provider.list();
            if (entities == null) continue;
            Iterator j = entities.iterator();
            while (j.hasNext()) {
                String name = (String)j.next();
                Entity entity = this.buildEntity(name, provider, type);
                result.add(entity);
            }
        }
        return result;
    }

    private Entity getEntity(String name, List providers, int type) throws EntityNotFoundException {
        UserProvider provider = this.getProvider(name, type == 0 ? this.credentialsProviders : this.accessProviders);
        if (provider == null) {
            throw new EntityNotFoundException("No " + (type == 0 ? "user " : "group ") + name + " found");
        }
        return this.buildEntity(name, provider, type);
    }

    private Entity buildEntity(String name, UserProvider provider, int type) {
        switch (type) {
            case 0: {
                return new User(name, this.accessor);
            }
            case 1: {
                return new Group(name, this.accessor);
            }
        }
        return null;
    }

    private Entity createEntity(String name, List providers, int type) throws DuplicateEntityException, ImmutableException {
        List providerList = this.credentialsProviders;
        if (type == 1) {
            providerList = this.accessProviders;
        }
        if (this.getProvider(name, providerList) != null) {
            throw new DuplicateEntityException((type == 0 ? "user " : "group ") + name + " already exists");
        }
        Iterator i = providerList.iterator();
        while (i.hasNext()) {
            Class toCheck;
            UserProvider provider = (UserProvider)i.next();
            Class clazz = toCheck = class$com$opensymphony$user$provider$CredentialsProvider == null ? UserManager.class$("com.opensymphony.user.provider.CredentialsProvider") : class$com$opensymphony$user$provider$CredentialsProvider;
            if (type == 1) {
                Class clazz2 = toCheck = class$com$opensymphony$user$provider$AccessProvider == null ? UserManager.class$("com.opensymphony.user.provider.AccessProvider") : class$com$opensymphony$user$provider$AccessProvider;
            }
            if (!toCheck.isAssignableFrom(provider.getClass()) || !provider.create(name)) continue;
            return this.buildEntity(name, provider, type);
        }
        throw new ImmutableException("No provider successfully created entity " + name);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        logger = LogFactory.getLog((Class)(class$com$opensymphony$user$UserManager == null ? (class$com$opensymphony$user$UserManager = UserManager.class$("com.opensymphony.user.UserManager")) : class$com$opensymphony$user$UserManager));
    }

    public class Accessor
    implements ManagerAccessor {
        public AccessProvider getAccessProvider(String name) {
            return (AccessProvider)UserManager.this.getProvider(name, UserManager.this.accessProviders);
        }

        public CredentialsProvider getCredentialsProvider(String name) {
            return (CredentialsProvider)UserManager.this.getProvider(name, UserManager.this.credentialsProviders);
        }

        public ProfileProvider getProfileProvider(String name) {
            return (ProfileProvider)UserManager.this.getProvider(name, UserManager.this.profileProviders);
        }

        public UserManager getUserManager() {
            return UserManager.this;
        }
    }
}

