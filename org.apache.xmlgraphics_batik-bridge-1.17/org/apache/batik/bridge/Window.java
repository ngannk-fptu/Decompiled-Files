/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.w3c.dom.Window
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.script.Interpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface Window
extends org.apache.batik.w3c.dom.Window {
    public Object setInterval(String var1, long var2);

    public Object setInterval(Runnable var1, long var2);

    public void clearInterval(Object var1);

    public Object setTimeout(String var1, long var2);

    public Object setTimeout(Runnable var1, long var2);

    public void clearTimeout(Object var1);

    public Node parseXML(String var1, Document var2);

    public String printNode(Node var1);

    public void getURL(String var1, URLResponseHandler var2);

    public void getURL(String var1, URLResponseHandler var2, String var3);

    public void postURL(String var1, String var2, URLResponseHandler var3);

    public void postURL(String var1, String var2, URLResponseHandler var3, String var4);

    public void postURL(String var1, String var2, URLResponseHandler var3, String var4, String var5);

    public void alert(String var1);

    public boolean confirm(String var1);

    public String prompt(String var1);

    public String prompt(String var1, String var2);

    public BridgeContext getBridgeContext();

    public Interpreter getInterpreter();

    public static interface URLResponseHandler {
        public void getURLDone(boolean var1, String var2, String var3);
    }
}

