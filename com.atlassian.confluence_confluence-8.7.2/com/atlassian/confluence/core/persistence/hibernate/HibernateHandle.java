/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.bonnie.Searchable
 *  org.hibernate.Hibernate
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.Searchable;
import java.text.ParseException;
import org.hibernate.Hibernate;

public class HibernateHandle
extends bucket.core.persistence.hibernate.HibernateHandle {
    public HibernateHandle(String handleString) throws ParseException {
        super(handleString);
    }

    public HibernateHandle(Searchable searchable) {
        this(Hibernate.getClass((Object)searchable).getName(), searchable.getId());
    }

    public HibernateHandle(String className, long id) {
        super(className, id);
    }
}

