/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;

public interface LobCreatorBuilder {
    public LobCreator buildLobCreator(LobCreationContext var1);
}

