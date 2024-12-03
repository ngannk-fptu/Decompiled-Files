/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 */
package com.atlassian.crowd.dao.property;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.property.Property;
import java.util.List;

public interface PropertyDAO {
    public Property find(String var1, String var2) throws ObjectNotFoundException;

    public List<Property> findAll(String var1);

    public Property add(Property var1);

    public Property update(Property var1);

    public void remove(String var1, String var2);

    public List<Property> findAll();
}

