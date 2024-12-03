/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNullableByDefault
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.hibernate.CallbackException
 *  org.hibernate.EmptyInterceptor
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.annotations.nullability.ReturnValuesAreNullableByDefault;
import com.atlassian.confluence.content.ContentCleaner;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Supplier;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

@ReturnValuesAreNullableByDefault
@SuppressFBWarnings(value={"SE_TRANSIENT_FIELD_NOT_RESTORED"})
public class XhtmlCleaningInterceptor
extends EmptyInterceptor {
    private final transient ContentCleaner contentCleaner;
    private final transient Supplier<Boolean> siteReindexingChecker;
    private static final long serialVersionUID = 1L;

    public XhtmlCleaningInterceptor(StorageFormatCleaner storageFormatCleaner) {
        this(storageFormatCleaner, () -> false);
    }

    public XhtmlCleaningInterceptor(final StorageFormatCleaner storageFormatCleaner, Supplier<Boolean> siteReindexingChecker) {
        this.siteReindexingChecker = siteReindexingChecker;
        this.contentCleaner = new ContentCleaner(){
            private StorageFormatCleaner storageCleaner;
            {
                this.storageCleaner = storageFormatCleaner;
            }

            @Override
            public String clean(String content) {
                return this.storageCleaner.cleanQuietly(content);
            }
        };
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        return this.cleanBodyContents(entity, propertyNames, currentState);
    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return this.cleanBodyContents(entity, propertyNames, state);
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (this.siteReindexingChecker.get().booleanValue()) {
            return false;
        }
        if (!(entity instanceof BodyContent) || !this.isXhtmlContent(propertyNames, state)) {
            return false;
        }
        BodyContent bodyContent = (BodyContent)entity;
        int bodyPropertyIndex = this.getPropertyIndexByName("body", propertyNames);
        if (bodyPropertyIndex < 0) {
            return false;
        }
        String body = (String)state[bodyPropertyIndex];
        if (null == body) {
            return false;
        }
        if (!bodyContent.isBodyCleaned()) {
            state[bodyPropertyIndex] = this.contentCleaner.clean((String)state[bodyPropertyIndex]);
        }
        return true;
    }

    private boolean cleanBodyContents(Object entity, String[] propertyNames, Object[] state) {
        if (entity instanceof BodyContent && this.isXhtmlContent(propertyNames, state)) {
            String body;
            BodyContent bodyContent = (BodyContent)entity;
            int bodyPropertyIndex = this.getPropertyIndexByName("body", propertyNames);
            if (bodyPropertyIndex >= 0 && (body = (String)state[bodyPropertyIndex]) != null) {
                if (bodyContent.isBodyCleaned()) {
                    return true;
                }
                state[bodyPropertyIndex] = bodyContent.cleanBody(this.contentCleaner);
                return true;
            }
        }
        return false;
    }

    private boolean isXhtmlContent(String[] propertyNames, Object[] state) {
        int index = this.getPropertyIndexByName("bodyType", propertyNames);
        if (index >= 0) {
            BodyType bodyType = (BodyType)state[index];
            return bodyType == BodyType.XHTML;
        }
        return true;
    }

    private int getPropertyIndexByName(String name, String[] propertyNames) {
        for (int i = 0; i < propertyNames.length; ++i) {
            if (!propertyNames[i].equals(name)) continue;
            return i;
        }
        return -1;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new NotSerializableException();
    }
}

