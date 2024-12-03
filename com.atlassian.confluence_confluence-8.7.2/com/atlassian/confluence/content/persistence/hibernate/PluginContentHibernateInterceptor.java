/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.CallbackException
 *  org.hibernate.EmptyInterceptor
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.content.persistence.hibernate;

import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.DefaultContentAdapter;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class PluginContentHibernateInterceptor
extends EmptyInterceptor {
    private final AtomicReference<ContentTypeManager> contentTypeManager = new AtomicReference();

    public PluginContentHibernateInterceptor(ContentTypeManager contentTypeManager) {
        this.contentTypeManager.set(contentTypeManager);
    }

    @Deprecated(forRemoval=true)
    public PluginContentHibernateInterceptor() {
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof CustomContentEntityObject) {
            CustomContentEntityObject customContent = (CustomContentEntityObject)entity;
            customContent.setAdapter(DefaultContentAdapter.INSTANCE);
            for (int i = 0; i < propertyNames.length; ++i) {
                String propertyName = propertyNames[i];
                if (!"pluginModuleKey".equals(propertyName)) continue;
                this.setPluginContentAdapter(customContent, (String)state[i]);
            }
        }
        return false;
    }

    private void setPluginContentAdapter(CustomContentEntityObject customContent, String pluginKeyName) {
        if (this.contentTypeManager.get() != null && StringUtils.isNotBlank((CharSequence)pluginKeyName)) {
            customContent.setAdapter(this.contentTypeManager.get().getContentType(pluginKeyName).getContentAdapter());
        }
    }

    @Deprecated(forRemoval=true)
    public void setContentTypeManager(ContentTypeManager contentTypeManager) {
        this.contentTypeManager.set(contentTypeManager);
    }
}

