/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import net.sourceforge.jtds.util.Logger;

public class SSPIJNIClient {
    private static SSPIJNIClient thisInstance;
    private static boolean libraryLoaded;
    private boolean initialized;

    private native void initialize();

    private native void unInitialize();

    private native byte[] prepareSSORequest();

    private native byte[] prepareSSOSubmit(byte[] var1, long var2);

    private SSPIJNIClient() {
    }

    public static synchronized SSPIJNIClient getInstance() throws Exception {
        if (thisInstance == null) {
            if (!libraryLoaded) {
                throw new Exception("Native SSPI library not loaded. Check the java.library.path system property.");
            }
            thisInstance = new SSPIJNIClient();
            thisInstance.invokeInitialize();
        }
        return thisInstance;
    }

    public void invokeInitialize() {
        if (!this.initialized) {
            this.initialize();
            this.initialized = true;
        }
    }

    public void invokeUnInitialize() {
        if (this.initialized) {
            this.unInitialize();
            this.initialized = false;
        }
    }

    public byte[] invokePrepareSSORequest() throws Exception {
        if (!this.initialized) {
            throw new Exception("SSPI Not Initialized");
        }
        return this.prepareSSORequest();
    }

    public byte[] invokePrepareSSOSubmit(byte[] buf) throws Exception {
        if (!this.initialized) {
            throw new Exception("SSPI Not Initialized");
        }
        return this.prepareSSOSubmit(buf, buf.length);
    }

    static {
        try {
            System.loadLibrary("ntlmauth");
            libraryLoaded = true;
        }
        catch (UnsatisfiedLinkError err) {
            Logger.println("Unable to load library: " + err);
        }
    }
}

