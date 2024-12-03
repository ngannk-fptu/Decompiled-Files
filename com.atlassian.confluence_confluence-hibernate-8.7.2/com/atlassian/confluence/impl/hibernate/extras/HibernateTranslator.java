/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.extras;

import com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle;

@Deprecated
public interface HibernateTranslator {
    public ExportHibernateHandle objectOrHandleToHandle(Object var1);

    public Object objectOrHandleToObject(Object var1);

    public ExportHibernateHandle objectToHandle(Object var1);

    public Object handleToObject(ExportHibernateHandle var1);
}

