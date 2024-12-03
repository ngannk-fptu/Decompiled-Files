/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.mapping.spi;

import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIdClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;

public interface EntityOrMappedSuperclass
extends ManagedType,
LifecycleCallbackContainer {
    public JaxbIdClass getIdClass();

    public void setIdClass(JaxbIdClass var1);

    public JaxbEmptyType getExcludeDefaultListeners();

    public void setExcludeDefaultListeners(JaxbEmptyType var1);

    public JaxbEmptyType getExcludeSuperclassListeners();

    public void setExcludeSuperclassListeners(JaxbEmptyType var1);

    public JaxbEntityListeners getEntityListeners();

    public void setEntityListeners(JaxbEntityListeners var1);

    @Override
    public JaxbPrePersist getPrePersist();

    @Override
    public void setPrePersist(JaxbPrePersist var1);

    @Override
    public JaxbPostPersist getPostPersist();

    @Override
    public void setPostPersist(JaxbPostPersist var1);

    @Override
    public JaxbPreRemove getPreRemove();

    @Override
    public void setPreRemove(JaxbPreRemove var1);

    @Override
    public JaxbPostRemove getPostRemove();

    @Override
    public void setPostRemove(JaxbPostRemove var1);

    @Override
    public JaxbPreUpdate getPreUpdate();

    @Override
    public void setPreUpdate(JaxbPreUpdate var1);

    @Override
    public JaxbPostUpdate getPostUpdate();

    @Override
    public void setPostUpdate(JaxbPostUpdate var1);

    @Override
    public JaxbPostLoad getPostLoad();

    @Override
    public void setPostLoad(JaxbPostLoad var1);

    @Override
    public JaxbAttributes getAttributes();

    public void setAttributes(JaxbAttributes var1);
}

