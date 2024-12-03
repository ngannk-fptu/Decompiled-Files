/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.apache.catalina;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

public interface Valve {
    public Valve getNext();

    public void setNext(Valve var1);

    public void backgroundProcess();

    public void invoke(Request var1, Response var2) throws IOException, ServletException;

    public boolean isAsyncSupported();
}

