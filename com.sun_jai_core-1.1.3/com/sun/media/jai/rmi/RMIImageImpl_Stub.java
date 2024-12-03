/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ColorModelProxy;
import com.sun.media.jai.rmi.RMIImage;
import com.sun.media.jai.rmi.RasterProxy;
import com.sun.media.jai.rmi.RenderContextProxy;
import com.sun.media.jai.rmi.SampleModelProxy;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public final class RMIImageImpl_Stub
extends RemoteStub
implements RMIImage {
    private static final Operation[] operations = new Operation[]{new Operation("com.sun.media.jai.rmi.RasterProxy copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("void dispose(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.ColorModelProxy getColorModel(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("com.sun.media.jai.rmi.SampleModelProxy getSampleModel(java.lang.Long)"), new Operation("java.util.Vector getSources(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("void setSource(java.lang.Long, java.awt.image.RenderedImage)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderableOp, com.sun.media.jai.rmi.RenderContextProxy)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderedOp)")};
    private static final long interfaceHash = -9186133247174212020L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_copyData_0;
    private static Method $method_dispose_1;
    private static Method $method_getColorModel_2;
    private static Method $method_getData_3;
    private static Method $method_getData_4;
    private static Method $method_getHeight_5;
    private static Method $method_getMinTileX_6;
    private static Method $method_getMinTileY_7;
    private static Method $method_getMinX_8;
    private static Method $method_getMinY_9;
    private static Method $method_getNumXTiles_10;
    private static Method $method_getNumYTiles_11;
    private static Method $method_getProperty_12;
    private static Method $method_getPropertyNames_13;
    private static Method $method_getRemoteID_14;
    private static Method $method_getSampleModel_15;
    private static Method $method_getSources_16;
    private static Method $method_getTile_17;
    private static Method $method_getTileGridXOffset_18;
    private static Method $method_getTileGridYOffset_19;
    private static Method $method_getTileHeight_20;
    private static Method $method_getTileWidth_21;
    private static Method $method_getWidth_22;
    private static Method $method_setSource_23;
    private static Method $method_setSource_24;
    private static Method $method_setSource_25;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RMIImage;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$awt$Rectangle;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RenderContextProxy;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;

    static {
        try {
            (class$java$rmi$server$RemoteRef != null ? class$java$rmi$server$RemoteRef : (class$java$rmi$server$RemoteRef = RMIImageImpl_Stub.class$("java.rmi.server.RemoteRef"))).getMethod("invoke", class$java$rmi$Remote != null ? class$java$rmi$Remote : (class$java$rmi$Remote = RMIImageImpl_Stub.class$("java.rmi.Remote")), class$java$lang$reflect$Method != null ? class$java$lang$reflect$Method : (class$java$lang$reflect$Method = RMIImageImpl_Stub.class$("java.lang.reflect.Method")), array$Ljava$lang$Object != null ? array$Ljava$lang$Object : (array$Ljava$lang$Object = RMIImageImpl_Stub.class$("[Ljava.lang.Object;")), Long.TYPE);
            useNewInvoke = true;
            $method_copyData_0 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("copyData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = RMIImageImpl_Stub.class$("java.awt.Rectangle")));
            $method_dispose_1 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("dispose", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getColorModel_2 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getColorModel", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getData_3 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getData_4 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = RMIImageImpl_Stub.class$("java.awt.Rectangle")));
            $method_getHeight_5 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getHeight", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getMinTileX_6 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getMinTileX", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getMinTileY_7 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getMinTileY", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getMinX_8 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getMinX", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getMinY_9 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getMinY", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getNumXTiles_10 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getNumXTiles", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getNumYTiles_11 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getNumYTiles", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getProperty_12 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getProperty", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = RMIImageImpl_Stub.class$("java.lang.String")));
            $method_getPropertyNames_13 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getPropertyNames", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getRemoteID_14 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getRemoteID", new Class[0]);
            $method_getSampleModel_15 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getSampleModel", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getSources_16 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getSources", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getTile_17 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getTile", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), Integer.TYPE, Integer.TYPE);
            $method_getTileGridXOffset_18 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getTileGridXOffset", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getTileGridYOffset_19 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getTileGridYOffset", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getTileHeight_20 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getTileHeight", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getTileWidth_21 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getTileWidth", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_getWidth_22 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("getWidth", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")));
            $method_setSource_23 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("setSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$java$awt$image$RenderedImage != null ? class$java$awt$image$RenderedImage : (class$java$awt$image$RenderedImage = RMIImageImpl_Stub.class$("java.awt.image.RenderedImage")));
            $method_setSource_24 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("setSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$javax$media$jai$RenderableOp != null ? class$javax$media$jai$RenderableOp : (class$javax$media$jai$RenderableOp = RMIImageImpl_Stub.class$("javax.media.jai.RenderableOp")), class$com$sun$media$jai$rmi$RenderContextProxy != null ? class$com$sun$media$jai$rmi$RenderContextProxy : (class$com$sun$media$jai$rmi$RenderContextProxy = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RenderContextProxy")));
            $method_setSource_25 = (class$com$sun$media$jai$rmi$RMIImage != null ? class$com$sun$media$jai$rmi$RMIImage : (class$com$sun$media$jai$rmi$RMIImage = RMIImageImpl_Stub.class$("com.sun.media.jai.rmi.RMIImage"))).getMethod("setSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = RMIImageImpl_Stub.class$("java.lang.Long")), class$javax$media$jai$RenderedOp != null ? class$javax$media$jai$RenderedOp : (class$javax$media$jai$RenderedOp = RMIImageImpl_Stub.class$("javax.media.jai.RenderedOp")));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            useNewInvoke = false;
        }
    }

    public RMIImageImpl_Stub() {
    }

    public RMIImageImpl_Stub(RemoteRef remoteRef) {
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

    public RasterProxy copyData(Long l, Rectangle rectangle) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_copyData_0, new Object[]{l, rectangle}, -4480130102587337594L);
                return (RasterProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 0, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(rectangle);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RasterProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var6_13 = null;
            }
            catch (Throwable throwable) {
                Object var6_14 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public void dispose(Long l) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_dispose_1, new Object[]{l}, 6460799139781649959L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 1, -9186133247174212020L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling arguments", iOException);
                }
                this.ref.invoke(remoteCall);
                this.ref.done(remoteCall);
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

    public ColorModelProxy getColorModel(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getColorModel_2, new Object[]{l}, 5862232465831048388L);
                return (ColorModelProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 2, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (ColorModelProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public RasterProxy getData(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getData_3, new Object[]{l}, 5982474592659170320L);
                return (RasterProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 3, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RasterProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public RasterProxy getData(Long l, Rectangle rectangle) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getData_4, new Object[]{l, rectangle}, -7782001095732779284L);
                return (RasterProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 4, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(rectangle);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RasterProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var6_13 = null;
            }
            catch (Throwable throwable) {
                Object var6_14 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public int getHeight(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getHeight_5, new Object[]{l}, -7560603472052038977L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 5, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getMinTileX(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getMinTileX_6, new Object[]{l}, 5809966745410438246L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 6, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getMinTileY(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getMinTileY_7, new Object[]{l}, -9076617268613815876L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 7, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getMinX(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getMinX_8, new Object[]{l}, -5297535099750447733L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 8, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getMinY(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getMinY_9, new Object[]{l}, 7733459005376369327L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 9, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getNumXTiles(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getNumXTiles_10, new Object[]{l}, 3645100420184954761L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 10, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getNumYTiles(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getNumYTiles_11, new Object[]{l}, -1731091968647972742L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 11, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public Object getProperty(Long l, String string) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getProperty_12, new Object[]{l, string}, 216968610676295195L);
                return object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 12, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(string);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var6_13 = null;
            }
            catch (Throwable throwable) {
                Object var6_14 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public String[] getPropertyNames(Long l) throws RemoteException {
        try {
            String[] stringArray;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getPropertyNames_13, new Object[]{l}, 3931591828613160321L);
                return (String[])object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 13, -9186133247174212020L);
            try {
                stringArray = remoteCall.getOutputStream();
                stringArray.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    stringArray = (String[])objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return stringArray;
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

    public Long getRemoteID() throws RemoteException {
        try {
            Long l;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRemoteID_14, null, -232353888923603427L);
                return (Long)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 14, -9186133247174212020L);
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    l = (Long)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var4_10 = null;
            }
            catch (Throwable throwable) {
                Object var4_11 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return l;
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

    public SampleModelProxy getSampleModel(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getSampleModel_15, new Object[]{l}, -8396533149827190655L);
                return (SampleModelProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 15, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (SampleModelProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public Vector getSources(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getSources_16, new Object[]{l}, -3713513808775692904L);
                return (Vector)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 16, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (Vector)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public RasterProxy getTile(Long l, int n, int n2) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getTile_17, new Object[]{l, new Integer(n), new Integer(n2)}, -1008030285235108860L);
                return (RasterProxy)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 17, -9186133247174212020L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeInt(n);
                object.writeInt(n2);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RasterProxy)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var7_14 = null;
            }
            catch (Throwable throwable) {
                Object var7_15 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object;
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

    public int getTileGridXOffset(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getTileGridXOffset_18, new Object[]{l}, -8218495432205133449L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 18, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getTileGridYOffset(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getTileGridYOffset_19, new Object[]{l}, -7482127068346373541L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 19, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getTileHeight(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getTileHeight_20, new Object[]{l}, 7785669351714030715L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 20, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getTileWidth(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getTileWidth_21, new Object[]{l}, 282122131312695349L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 21, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public int getWidth(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getWidth_22, new Object[]{l}, -8357318297729299690L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 22, -9186133247174212020L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(l);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    n = objectInput.readInt();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                Object var5_12 = null;
            }
            catch (Throwable throwable) {
                Object var5_13 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return n;
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

    public void setSource(Long l, RenderedImage renderedImage) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setSource_23, new Object[]{l, renderedImage}, 4248763766578677765L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 23, -9186133247174212020L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderedImage);
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling arguments", iOException);
                }
                this.ref.invoke(remoteCall);
                this.ref.done(remoteCall);
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

    public void setSource(Long l, RenderableOp renderableOp, RenderContextProxy renderContextProxy) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setSource_24, new Object[]{l, renderableOp, renderContextProxy}, 7010328997687947687L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 24, -9186133247174212020L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderableOp);
                    objectOutput.writeObject(renderContextProxy);
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling arguments", iOException);
                }
                this.ref.invoke(remoteCall);
                this.ref.done(remoteCall);
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

    public void setSource(Long l, RenderedOp renderedOp) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setSource_25, new Object[]{l, renderedOp}, -4039999355356694323L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 25, -9186133247174212020L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderedOp);
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling arguments", iOException);
                }
                this.ref.invoke(remoteCall);
                this.ref.done(remoteCall);
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
}

