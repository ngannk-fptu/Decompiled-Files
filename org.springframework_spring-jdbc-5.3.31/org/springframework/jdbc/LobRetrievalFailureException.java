/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataRetrievalFailureException
 */
package org.springframework.jdbc;

import java.io.IOException;
import org.springframework.dao.DataRetrievalFailureException;

public class LobRetrievalFailureException
extends DataRetrievalFailureException {
    public LobRetrievalFailureException(String msg) {
        super(msg);
    }

    public LobRetrievalFailureException(String msg, IOException ex) {
        super(msg, (Throwable)ex);
    }
}

