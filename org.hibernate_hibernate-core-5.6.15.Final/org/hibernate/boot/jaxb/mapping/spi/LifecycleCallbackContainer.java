/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.mapping.spi;

import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;

public interface LifecycleCallbackContainer {
    public String getDescription();

    public void setDescription(String var1);

    public JaxbPrePersist getPrePersist();

    public void setPrePersist(JaxbPrePersist var1);

    public JaxbPostPersist getPostPersist();

    public void setPostPersist(JaxbPostPersist var1);

    public JaxbPreRemove getPreRemove();

    public void setPreRemove(JaxbPreRemove var1);

    public JaxbPostRemove getPostRemove();

    public void setPostRemove(JaxbPostRemove var1);

    public JaxbPreUpdate getPreUpdate();

    public void setPreUpdate(JaxbPreUpdate var1);

    public JaxbPostUpdate getPostUpdate();

    public void setPostUpdate(JaxbPostUpdate var1);

    public JaxbPostLoad getPostLoad();

    public void setPostLoad(JaxbPostLoad var1);

    public String getClazz();

    public void setClazz(String var1);
}

