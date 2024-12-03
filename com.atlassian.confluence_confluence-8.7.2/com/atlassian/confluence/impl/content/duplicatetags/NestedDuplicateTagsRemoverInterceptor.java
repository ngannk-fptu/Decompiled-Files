/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.CallbackException
 *  org.hibernate.EmptyInterceptor
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.content.duplicatetags;

import com.atlassian.confluence.content.ContentCleaner;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.impl.content.duplicatetags.DuplicateNestedTagsRemover;
import java.io.Serializable;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class NestedDuplicateTagsRemoverInterceptor
extends EmptyInterceptor {
    private static final long serialVersionUID = -4925052371681904141L;
    private final transient ContentCleaner contentCleaner = duplicateNestedTagsRemover::cleanQuietly;

    public NestedDuplicateTagsRemoverInterceptor(DuplicateNestedTagsRemover duplicateNestedTagsRemover) {
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        return this.cleanBodyContents(entity, propertyNames, currentState);
    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return this.cleanBodyContents(entity, propertyNames, state);
    }

    private boolean cleanBodyContents(Object entity, String[] propertyNames, Object[] state) {
        if (entity instanceof BodyContent) {
            String body;
            BodyContent bodyContent = (BodyContent)entity;
            int bodyPropertyIndex = this.getPropertyIndexByName("body", propertyNames);
            if (bodyPropertyIndex >= 0 && (body = (String)state[bodyPropertyIndex]) != null) {
                state[bodyPropertyIndex] = bodyContent.cleanBody(this.contentCleaner);
                return true;
            }
        }
        return false;
    }

    private int getPropertyIndexByName(String name, String[] propertyNames) {
        for (int i = 0; i < propertyNames.length; ++i) {
            if (!propertyNames[i].equals(name)) continue;
            return i;
        }
        return -1;
    }
}

