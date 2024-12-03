/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.search.LuceneIndependent;

@LuceneIndependent
public class ChangeDocumentIdBuilder {
    public String getChangeDocumentAndAuthorId(Searchable entityObject) {
        this.assertConfluenceEntityObject(entityObject);
        String handle = this.getGroupHandle(entityObject).toString();
        String lastModifierName = ((ConfluenceEntityObject)entityObject).getLastModifierName();
        return handle + "-" + lastModifierName;
    }

    public String getGroupId(Handle handle) {
        return handle.toString();
    }

    public String getGroupId(Searchable searchable) {
        return this.getGroupHandle(searchable).toString();
    }

    private void assertConfluenceEntityObject(Searchable entityObject) {
        if (!(entityObject instanceof ConfluenceEntityObject)) {
            throw new IllegalArgumentException("Can not generate a change document id for \"" + entityObject + "\". Class needs to extend " + ConfluenceEntityObject.class.getName() + ".");
        }
    }

    private HibernateHandle getGroupHandle(Searchable searchable) {
        HibernateHandle handle = new HibernateHandle(searchable);
        if (searchable instanceof Versioned) {
            handle = new HibernateHandle((Searchable)((Versioned)searchable).getLatestVersion());
        }
        return handle;
    }
}

