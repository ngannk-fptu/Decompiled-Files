/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import org.apache.jackrabbit.server.remoting.davex.DiffException;

interface DiffHandler {
    public void addNode(String var1, String var2) throws DiffException;

    public void setProperty(String var1, String var2) throws DiffException;

    public void remove(String var1, String var2) throws DiffException;

    public void move(String var1, String var2) throws DiffException;
}

