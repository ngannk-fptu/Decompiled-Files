/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.setup.velocity.DecoratorName;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.themes.persistence.PersistentDecoratorDao;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateResourceLoader
extends ResourceLoader {
    private static final Logger log = LoggerFactory.getLogger(HibernateResourceLoader.class);
    private SpaceManager spaceManager;
    private PersistentDecoratorDao persistentDecoratorDao;
    private UpgradeManager upgradeManager;

    public void init(ExtendedProperties configuration) {
        if (log.isDebugEnabled()) {
            log.debug("configuration = " + configuration);
        }
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        if (!this.isSetupComplete()) {
            log.debug("Resource [{}] skipped as server is not set up", (Object)source);
            return null;
        }
        if (!ContainerManager.isContainerSetup()) {
            log.debug("Resource [{}] skipped due to error in main spring context", (Object)source);
            return null;
        }
        if (!this.isUpgradeComplete()) {
            log.debug("Resource [{}] skipped as upgrades have not completed", (Object)source);
            return null;
        }
        PersistentDecorator persistentDecorator = this.getPersistentDecorator(source);
        if (persistentDecorator == null) {
            if (log.isDebugEnabled()) {
                log.debug("Template not found, returning null");
            }
            return null;
        }
        String templateBody = persistentDecorator.getBody();
        if (log.isDebugEnabled()) {
            log.debug("Template found. templateBody = " + templateBody);
        }
        String encoding = this.rsvc.getString("input.encoding", "ISO-8859-1");
        if (log.isDebugEnabled()) {
            log.debug("Converting to Velocity encoding: " + encoding);
        }
        try {
            return new ByteArrayInputStream(templateBody.getBytes(encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException("Could not convert template to encoding: " + encoding + ", exception: " + e.getMessage());
        }
    }

    private boolean spaceExists(String spaceKey) {
        if (this.getSpaceManager() == null) {
            return false;
        }
        return this.getSpaceManager().getSpace(spaceKey) != null;
    }

    private SpaceManager getSpaceManager() {
        try {
            if (this.spaceManager == null) {
                this.spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
            }
        }
        catch (ComponentNotFoundException e) {
            return null;
        }
        return this.spaceManager;
    }

    public boolean isSourceModified(Resource resource) {
        if (log.isDebugEnabled()) {
            log.debug("resource.getName() = " + resource.getName());
        }
        return resource.getLastModified() != this.getLastModified(resource);
    }

    public long getLastModified(Resource resource) {
        if (log.isDebugEnabled()) {
            log.debug("resource.getName() = " + resource.getName());
        }
        if (!this.isUpgradeComplete()) {
            return 0L;
        }
        PersistentDecorator persistentDecorator = this.getPersistentDecorator(resource.getName());
        if (persistentDecorator == null) {
            return 0L;
        }
        return persistentDecorator.getLastModificationDate().getTime();
    }

    public PersistentDecoratorDao getPersistentDecoratorDao() {
        if (this.persistentDecoratorDao == null) {
            this.persistentDecoratorDao = (PersistentDecoratorDao)ContainerManager.getComponent((String)"persistentDecoratorDao");
        }
        return this.persistentDecoratorDao;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPersistentDecoratorDao(PersistentDecoratorDao persistentDecoratorDao) {
        this.persistentDecoratorDao = persistentDecoratorDao;
    }

    private PersistentDecorator getPersistentDecorator(String source) {
        try {
            DecoratorName decorator;
            if (log.isDebugEnabled()) {
                log.debug("source = " + source);
            }
            if ((decorator = new DecoratorName(source)).isSpaceDecorator() && !this.spaceExists(decorator.getSpaceKey())) {
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug("Retrieving template for decorator: " + decorator);
            }
            return this.getPersistentDecoratorDao().get(decorator.getSpaceKey(), decorator.getTemplateName());
        }
        catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Error retrieving decorator '" + source + "' from database.", e);
            }
            return null;
        }
    }

    protected boolean isSetupComplete() {
        return GeneralUtil.isSetupComplete();
    }

    public UpgradeManager getUpgradeManager() {
        try {
            if (this.upgradeManager == null) {
                this.upgradeManager = (UpgradeManager)ContainerManager.getComponent((String)"upgradeManager");
            }
        }
        catch (ComponentNotFoundException e) {
            log.warn("Could not find a configured upgradeManager from the ContainerManager. ", (Throwable)e);
        }
        return this.upgradeManager;
    }

    void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    private boolean isUpgradeComplete() {
        return this.getUpgradeManager() == null || this.upgradeManager.isUpgraded();
    }
}

