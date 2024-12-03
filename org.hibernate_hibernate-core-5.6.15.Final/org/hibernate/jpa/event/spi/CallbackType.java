/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PostLoad
 *  javax.persistence.PostPersist
 *  javax.persistence.PostRemove
 *  javax.persistence.PostUpdate
 *  javax.persistence.PrePersist
 *  javax.persistence.PreRemove
 *  javax.persistence.PreUpdate
 */
package org.hibernate.jpa.event.spi;

import java.lang.annotation.Annotation;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public enum CallbackType {
    PRE_UPDATE(PreUpdate.class),
    POST_UPDATE(PostUpdate.class),
    PRE_PERSIST(PrePersist.class),
    POST_PERSIST(PostPersist.class),
    PRE_REMOVE(PreRemove.class),
    POST_REMOVE(PostRemove.class),
    POST_LOAD(PostLoad.class);

    private Class<? extends Annotation> callbackAnnotation;

    private CallbackType(Class<? extends Annotation> callbackAnnotation) {
        this.callbackAnnotation = callbackAnnotation;
    }

    public Class<? extends Annotation> getCallbackAnnotation() {
        return this.callbackAnnotation;
    }
}

