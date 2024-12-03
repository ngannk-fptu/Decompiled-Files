/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class InitialContextHelper {
    public static InitialContext getInitialContext() {
        try {
            return new InitialContext();
        }
        catch (NamingException ex) {
            return null;
        }
        catch (LinkageError ex) {
            return null;
        }
    }
}

