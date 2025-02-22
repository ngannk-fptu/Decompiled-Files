/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug;

import freemarker.template.TemplateModelException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface DebugModel
extends Remote {
    public static final int TYPE_SCALAR = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_DATE = 4;
    public static final int TYPE_BOOLEAN = 8;
    public static final int TYPE_SEQUENCE = 16;
    public static final int TYPE_COLLECTION = 32;
    public static final int TYPE_HASH = 64;
    public static final int TYPE_HASH_EX = 128;
    public static final int TYPE_METHOD = 256;
    public static final int TYPE_METHOD_EX = 512;
    public static final int TYPE_TRANSFORM = 1024;
    public static final int TYPE_ENVIRONMENT = 2048;
    public static final int TYPE_TEMPLATE = 4096;
    public static final int TYPE_CONFIGURATION = 8192;

    public String getAsString() throws TemplateModelException, RemoteException;

    public Number getAsNumber() throws TemplateModelException, RemoteException;

    public boolean getAsBoolean() throws TemplateModelException, RemoteException;

    public Date getAsDate() throws TemplateModelException, RemoteException;

    public int getDateType() throws TemplateModelException, RemoteException;

    public int size() throws TemplateModelException, RemoteException;

    public DebugModel get(int var1) throws TemplateModelException, RemoteException;

    public DebugModel[] get(int var1, int var2) throws TemplateModelException, RemoteException;

    public DebugModel get(String var1) throws TemplateModelException, RemoteException;

    public DebugModel[] get(String[] var1) throws TemplateModelException, RemoteException;

    public DebugModel[] getCollection() throws TemplateModelException, RemoteException;

    public String[] keys() throws TemplateModelException, RemoteException;

    public int getModelTypes() throws RemoteException;
}

