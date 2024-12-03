/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.admin.actions.lookandfeel.DefaultDecorator;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.themes.CustomLayoutManager;
import com.atlassian.confluence.themes.events.DecoratorChangedEvent;
import com.atlassian.confluence.themes.persistence.PersistentDecoratorDao;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceCache;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.Collection;

public class DefaultCustomLayoutManager
implements CustomLayoutManager {
    private final PersistentDecoratorDao persistentDecoratorDao;
    private final EventPublisher eventPublisher;

    public DefaultCustomLayoutManager(PersistentDecoratorDao persistentDecoratorDao, EventPublisher eventPublisher) {
        this.persistentDecoratorDao = persistentDecoratorDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveOrUpdate(PersistentDecorator persistentDecorator) {
        PersistentDecorator oldDecorator = this.getPersistentDecorator(persistentDecorator.getSpaceKey(), persistentDecorator.getName());
        this.persistentDecoratorDao.saveOrUpdate(persistentDecorator);
        this.removeFromVelocityCache(persistentDecorator.getName());
        this.eventPublisher.publish((Object)new DecoratorChangedEvent(this, persistentDecorator.getSpaceKey(), oldDecorator, persistentDecorator));
    }

    @Override
    public void saveOrUpdate(String spaceKey, String decoratorName, String content) {
        PersistentDecorator decorator = new PersistentDecorator();
        decorator.setSpaceKey(spaceKey);
        decorator.setName(decoratorName);
        decorator.setBody(content);
        this.saveOrUpdate(decorator);
    }

    @Override
    public PersistentDecorator getPersistentDecorator(String spaceKey, String decoratorName) {
        return this.persistentDecoratorDao.get(spaceKey, decoratorName);
    }

    @Override
    public Collection<PersistentDecorator> getCustomSpaceDecorators(String spaceKey) {
        Collection<DefaultDecorator> defaultDecorators = this.getAllDefaultDecorators();
        ArrayList<PersistentDecorator> decorators = new ArrayList<PersistentDecorator>();
        for (DefaultDecorator decorator : defaultDecorators) {
            PersistentDecorator customDecorator = this.getPersistentDecorator(spaceKey, decorator.getFileName());
            if (customDecorator == null) continue;
            decorators.add(customDecorator);
        }
        return decorators;
    }

    @Override
    public Collection<PersistentDecorator> getApplicableCustomDecoratorsForSpace(String spaceKey) {
        Collection<DefaultDecorator> defaultDecorators = this.getAllDefaultDecorators();
        ArrayList<PersistentDecorator> decorators = new ArrayList<PersistentDecorator>();
        for (DefaultDecorator decorator : defaultDecorators) {
            PersistentDecorator customDecorator = this.getPersistentDecorator(spaceKey, decorator.getFileName());
            if (customDecorator == null) {
                customDecorator = this.getPersistentDecorator(null, decorator.getFileName());
            }
            if (customDecorator == null) continue;
            decorators.add(customDecorator);
        }
        return decorators;
    }

    @Override
    public Collection<PersistentDecorator> getCustomGlobalDecorators() {
        return this.getCustomSpaceDecorators(null);
    }

    @Override
    public boolean hasCustomSpaceDecorator(String spaceKey, String decoratorName) {
        return this.persistentDecoratorDao.get(spaceKey, decoratorName) != null;
    }

    @Override
    public boolean hasCustomGlobalDecorator(String decoratorName) {
        return this.persistentDecoratorDao.get(null, decoratorName) != null;
    }

    @Override
    public boolean hasCustomDecorator(String spaceKey, String decoratorName) {
        return this.hasCustomSpaceDecorator(spaceKey, decoratorName) || this.hasCustomGlobalDecorator(decoratorName);
    }

    @Override
    public boolean usesCustomLayout(String spaceKey) {
        return !this.getApplicableCustomDecoratorsForSpace(spaceKey).isEmpty();
    }

    @Override
    public void remove(String spaceKey, String decoratorName) {
        PersistentDecorator decorator = this.getPersistentDecorator(spaceKey, decoratorName);
        this.remove(decorator);
    }

    @Override
    public void remove(PersistentDecorator persistentDecorator) {
        if (persistentDecorator != null) {
            this.persistentDecoratorDao.remove(persistentDecorator);
            this.removeFromVelocityCache(persistentDecorator.getName());
            this.eventPublisher.publish((Object)new DecoratorChangedEvent(this, persistentDecorator.getSpaceKey(), persistentDecorator, null));
        }
    }

    @Override
    public void removeAllCustomSpaceDecorators(String spaceKey) {
        Collection<PersistentDecorator> customDecorators = this.getCustomSpaceDecorators(spaceKey);
        for (PersistentDecorator persistentDecorator : customDecorators) {
            this.remove(persistentDecorator);
        }
    }

    @Override
    public void removeAllCustomGlobalDecorators() {
        this.removeAllCustomSpaceDecorators(null);
    }

    @Override
    public DefaultDecorator getDefaultDecorator(String decoratorName) {
        return DefaultDecorator.getByFileName(decoratorName);
    }

    @Override
    public Collection<DefaultDecorator> getAllDefaultDecorators() {
        return DefaultDecorator.getDecorators();
    }

    private void removeFromVelocityCache(String decorator) {
        ConfluenceVelocityResourceCache.removeFromCaches(decorator);
    }
}

