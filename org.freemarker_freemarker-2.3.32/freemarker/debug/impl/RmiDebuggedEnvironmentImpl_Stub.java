/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.debug.DebugModel;
import freemarker.debug.DebuggedEnvironment;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Date;

public final class RmiDebuggedEnvironmentImpl_Stub
extends RemoteStub
implements DebuggedEnvironment,
DebugModel,
Remote {
    private static final long serialVersionUID = 2L;
    private static Method $method_get_0;
    private static Method $method_get_1;
    private static Method $method_get_2;
    private static Method $method_get_3;
    private static Method $method_getAsBoolean_4;
    private static Method $method_getAsDate_5;
    private static Method $method_getAsNumber_6;
    private static Method $method_getAsString_7;
    private static Method $method_getCollection_8;
    private static Method $method_getDateType_9;
    private static Method $method_getId_10;
    private static Method $method_getModelTypes_11;
    private static Method $method_keys_12;
    private static Method $method_resume_13;
    private static Method $method_size_14;
    private static Method $method_stop_15;
    static /* synthetic */ Class class$freemarker$debug$DebugModel;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class array$Ljava$lang$String;
    static /* synthetic */ Class class$freemarker$debug$DebuggedEnvironment;

    static {
        try {
            $method_get_0 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("get", Integer.TYPE);
            $method_get_1 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("get", Integer.TYPE, Integer.TYPE);
            $method_get_2 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("get", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = RmiDebuggedEnvironmentImpl_Stub.class$("java.lang.String")));
            $method_get_3 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("get", array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = RmiDebuggedEnvironmentImpl_Stub.class$("[Ljava.lang.String;")));
            $method_getAsBoolean_4 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getAsBoolean", new Class[0]);
            $method_getAsDate_5 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getAsDate", new Class[0]);
            $method_getAsNumber_6 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getAsNumber", new Class[0]);
            $method_getAsString_7 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getAsString", new Class[0]);
            $method_getCollection_8 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getCollection", new Class[0]);
            $method_getDateType_9 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getDateType", new Class[0]);
            $method_getId_10 = (class$freemarker$debug$DebuggedEnvironment != null ? class$freemarker$debug$DebuggedEnvironment : (class$freemarker$debug$DebuggedEnvironment = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebuggedEnvironment"))).getMethod("getId", new Class[0]);
            $method_getModelTypes_11 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("getModelTypes", new Class[0]);
            $method_keys_12 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("keys", new Class[0]);
            $method_resume_13 = (class$freemarker$debug$DebuggedEnvironment != null ? class$freemarker$debug$DebuggedEnvironment : (class$freemarker$debug$DebuggedEnvironment = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebuggedEnvironment"))).getMethod("resume", new Class[0]);
            $method_size_14 = (class$freemarker$debug$DebugModel != null ? class$freemarker$debug$DebugModel : (class$freemarker$debug$DebugModel = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebugModel"))).getMethod("size", new Class[0]);
            $method_stop_15 = (class$freemarker$debug$DebuggedEnvironment != null ? class$freemarker$debug$DebuggedEnvironment : (class$freemarker$debug$DebuggedEnvironment = RmiDebuggedEnvironmentImpl_Stub.class$("freemarker.debug.DebuggedEnvironment"))).getMethod("stop", new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }

    public RmiDebuggedEnvironmentImpl_Stub(RemoteRef remoteRef) {
        super(remoteRef);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public DebugModel get(int n) throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_get_0, new Object[]{new Integer(n)}, -8133058733457407300L);
            return (DebugModel)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public DebugModel[] get(int n, int n2) throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_get_1, new Object[]{new Integer(n), new Integer(n2)}, 2963274088089045739L);
            return (DebugModel[])object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public DebugModel get(String string) throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_get_2, new Object[]{string}, -724507235264020332L);
            return (DebugModel)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public DebugModel[] get(String[] stringArray) throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_get_3, new Object[]{stringArray}, -5400820492225333337L);
            return (DebugModel[])object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public boolean getAsBoolean() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getAsBoolean_4, null, 315270873791227726L);
            return (Boolean)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public Date getAsDate() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getAsDate_5, null, -6762406881188215033L);
            return (Date)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public Number getAsNumber() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getAsNumber_6, null, -6188010426576701549L);
            return (Number)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public String getAsString() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getAsString_7, null, -5749749291031241731L);
            return (String)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getCollection_8, null, -1992223977663617938L);
            return (DebugModel[])object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public int getDateType() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getDateType_9, null, -3242981404503740604L);
            return (Integer)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public long getId() throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getId_10, null, -6040770469254561000L);
            return (Long)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public int getModelTypes() throws RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_getModelTypes_11, null, -3673171458095957561L);
            return (Integer)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public String[] keys() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_keys_12, null, 563174456558742983L);
            return (String[])object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public void resume() throws RemoteException {
        try {
            this.ref.invoke(this, $method_resume_13, null, 4126995468303716546L);
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public int size() throws TemplateModelException, RemoteException {
        try {
            Object object = this.ref.invoke(this, $method_size_14, null, 4495240443643581991L);
            return (Integer)object;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (TemplateModelException templateModelException) {
            throw templateModelException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }

    public void stop() throws RemoteException {
        try {
            this.ref.invoke(this, $method_stop_15, null, -2856118408655404442L);
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (RemoteException remoteException) {
            throw remoteException;
        }
        catch (Exception exception) {
            throw new UnexpectedException("undeclared checked exception", exception);
        }
    }
}

