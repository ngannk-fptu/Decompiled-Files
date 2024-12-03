/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import org.hibernate.usertype.UserType;

public interface EnhancedUserType
extends UserType {
    public String objectToSQLString(Object var1);

    @Deprecated
    public String toXMLString(Object var1);

    @Deprecated
    public Object fromXMLString(String var1);
}

