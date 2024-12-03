/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.init;

import javax.annotation.Nullable;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.repository.init.RepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;

public class RepositoriesPopulatedEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 7449982118828889097L;
    private final Repositories repositories;

    public RepositoriesPopulatedEvent(RepositoryPopulator populator, Repositories repositories) {
        super((Object)populator);
        Assert.notNull((Object)populator, (String)"Populator must not be null!");
        Assert.notNull((Object)repositories, (String)"Repositories must not be null!");
        this.repositories = repositories;
    }

    public RepositoryPopulator getSource() {
        return (RepositoryPopulator)super.getSource();
    }

    public Repositories getRepositories() {
        return this.repositories;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !((Object)((Object)this)).getClass().equals(obj.getClass())) {
            return false;
        }
        RepositoriesPopulatedEvent that = (RepositoriesPopulatedEvent)((Object)obj);
        return this.source.equals(that.source) && this.repositories.equals(that.repositories);
    }

    public int hashCode() {
        int result = 17;
        result += 31 * this.source.hashCode();
        return result += 31 * this.repositories.hashCode();
    }
}

