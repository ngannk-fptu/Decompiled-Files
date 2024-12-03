/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface LoggableUserType {
    public String toLoggableString(Object var1, SessionFactoryImplementor var2);
}

