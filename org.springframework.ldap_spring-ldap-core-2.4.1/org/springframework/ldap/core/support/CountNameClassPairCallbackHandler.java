/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.NameClassPair;
import org.springframework.ldap.core.NameClassPairCallbackHandler;

public class CountNameClassPairCallbackHandler
implements NameClassPairCallbackHandler {
    private int noOfRows = 0;

    public int getNoOfRows() {
        return this.noOfRows;
    }

    @Override
    public void handleNameClassPair(NameClassPair nameClassPair) {
        ++this.noOfRows;
    }
}

