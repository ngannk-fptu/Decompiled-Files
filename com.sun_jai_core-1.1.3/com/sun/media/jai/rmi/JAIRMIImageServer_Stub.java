/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.SerializableRenderableImage;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.List;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.SerializableState;

public final class JAIRMIImageServer_Stub
extends RemoteStub
implements ImageServer,
Remote {
    private static final Operation[] operations = new Operation[]{new Operation("javax.media.jai.remote.SerializableState copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("java.awt.image.RenderedImage createDefaultRendering(java.lang.Long)"), new Operation("void createRenderableOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock)"), new Operation("void createRenderedOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createScaledRendering(java.lang.Long, int, int, javax.media.jai.remote.SerializableState)"), new Operation("void dispose(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getBounds2D(java.lang.Long, java.lang.String)"), new Operation("javax.media.jai.remote.SerializableState getColorModel(java.lang.Long)"), new Operation("byte getCompressedTile(java.lang.Long, int, int)[]"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getInvalidRegion(java.lang.Long, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("javax.media.jai.RenderedOp getNode(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.util.List getOperationDescriptors()"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.String getPropertyNames(java.lang.String)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("float getRenderableHeight(java.lang.Long)"), new Operation("float getRenderableMinX(java.lang.Long)"), new Operation("float getRenderableMinY(java.lang.Long)"), new Operation("float getRenderableWidth(java.lang.Long)"), new Operation("boolean getRendering(java.lang.Long)"), new Operation("java.lang.Long getRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("javax.media.jai.remote.SerializableState getSampleModel(java.lang.Long)"), new Operation("javax.media.jai.remote.NegotiableCapabilitySet getServerCapabilities()"), new Operation("java.lang.String getServerSupportedOperationNames()[]"), new Operation("javax.media.jai.remote.SerializableState getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("java.lang.Long handleEvent(java.lang.Long, int, javax.media.jai.remote.SerializableState, java.lang.Object)"), new Operation("java.lang.Long handleEvent(java.lang.Long, java.lang.String, java.lang.Object, java.lang.Object)"), new Operation("void incrementRefCount(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.String)"), new Operation("java.awt.Rectangle mapDestRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("javax.media.jai.remote.SerializableState mapRenderContext(int, java.lang.Long, java.lang.String, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.Rectangle mapSourceRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("void setRenderableRMIServerProxyAsSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, com.sun.media.jai.rmi.SerializableRenderableImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, javax.media.jai.RenderableOp, int)"), new Operation("void setRenderedSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderedSource(java.lang.Long, javax.media.jai.RenderedOp, int)"), new Operation("void setServerNegotiatedValues(java.lang.Long, javax.media.jai.remote.NegotiableCapabilitySet)")};
    private static final long interfaceHash = 6167769405001739342L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_copyData_0;
    private static Method $method_createDefaultRendering_1;
    private static Method $method_createRenderableOp_2;
    private static Method $method_createRenderedOp_3;
    private static Method $method_createRendering_4;
    private static Method $method_createScaledRendering_5;
    private static Method $method_dispose_6;
    private static Method $method_getBounds2D_7;
    private static Method $method_getColorModel_8;
    private static Method $method_getCompressedTile_9;
    private static Method $method_getData_10;
    private static Method $method_getData_11;
    private static Method $method_getHeight_12;
    private static Method $method_getInvalidRegion_13;
    private static Method $method_getMinTileX_14;
    private static Method $method_getMinTileY_15;
    private static Method $method_getMinX_16;
    private static Method $method_getMinY_17;
    private static Method $method_getNode_18;
    private static Method $method_getNumXTiles_19;
    private static Method $method_getNumYTiles_20;
    private static Method $method_getOperationDescriptors_21;
    private static Method $method_getProperty_22;
    private static Method $method_getPropertyNames_23;
    private static Method $method_getPropertyNames_24;
    private static Method $method_getRemoteID_25;
    private static Method $method_getRenderableHeight_26;
    private static Method $method_getRenderableMinX_27;
    private static Method $method_getRenderableMinY_28;
    private static Method $method_getRenderableWidth_29;
    private static Method $method_getRendering_30;
    private static Method $method_getRendering_31;
    private static Method $method_getSampleModel_32;
    private static Method $method_getServerCapabilities_33;
    private static Method $method_getServerSupportedOperationNames_34;
    private static Method $method_getTile_35;
    private static Method $method_getTileGridXOffset_36;
    private static Method $method_getTileGridYOffset_37;
    private static Method $method_getTileHeight_38;
    private static Method $method_getTileWidth_39;
    private static Method $method_getWidth_40;
    private static Method $method_handleEvent_41;
    private static Method $method_handleEvent_42;
    private static Method $method_incrementRefCount_43;
    private static Method $method_isDynamic_44;
    private static Method $method_isDynamic_45;
    private static Method $method_mapDestRect_46;
    private static Method $method_mapRenderContext_47;
    private static Method $method_mapSourceRect_48;
    private static Method $method_setRenderableRMIServerProxyAsSource_49;
    private static Method $method_setRenderableSource_50;
    private static Method $method_setRenderableSource_51;
    private static Method $method_setRenderableSource_52;
    private static Method $method_setRenderableSource_53;
    private static Method $method_setRenderableSource_54;
    private static Method $method_setRenderedSource_55;
    private static Method $method_setRenderedSource_56;
    private static Method $method_setRenderedSource_57;
    private static Method $method_setRenderedSource_58;
    private static Method $method_setServerNegotiatedValues_59;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$ImageServer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$awt$Rectangle;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$javax$media$jai$remote$SerializableState;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$SerializableRenderableImage;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableCapabilitySet;

    static {
        try {
            (class$java$rmi$server$RemoteRef != null ? class$java$rmi$server$RemoteRef : (class$java$rmi$server$RemoteRef = JAIRMIImageServer_Stub.class$("java.rmi.server.RemoteRef"))).getMethod("invoke", class$java$rmi$Remote != null ? class$java$rmi$Remote : (class$java$rmi$Remote = JAIRMIImageServer_Stub.class$("java.rmi.Remote")), class$java$lang$reflect$Method != null ? class$java$lang$reflect$Method : (class$java$lang$reflect$Method = JAIRMIImageServer_Stub.class$("java.lang.reflect.Method")), array$Ljava$lang$Object != null ? array$Ljava$lang$Object : (array$Ljava$lang$Object = JAIRMIImageServer_Stub.class$("[Ljava.lang.Object;")), Long.TYPE);
            useNewInvoke = true;
            $method_copyData_0 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("copyData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = JAIRMIImageServer_Stub.class$("java.awt.Rectangle")));
            $method_createDefaultRendering_1 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("createDefaultRendering", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_createRenderableOp_2 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("createRenderableOp", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$awt$image$renderable$ParameterBlock != null ? class$java$awt$image$renderable$ParameterBlock : (class$java$awt$image$renderable$ParameterBlock = JAIRMIImageServer_Stub.class$("java.awt.image.renderable.ParameterBlock")));
            $method_createRenderedOp_3 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("createRenderedOp", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$awt$image$renderable$ParameterBlock != null ? class$java$awt$image$renderable$ParameterBlock : (class$java$awt$image$renderable$ParameterBlock = JAIRMIImageServer_Stub.class$("java.awt.image.renderable.ParameterBlock")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_createRendering_4 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("createRendering", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_createScaledRendering_5 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("createScaledRendering", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE, Integer.TYPE, class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_dispose_6 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("dispose", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getBounds2D_7 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getBounds2D", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")));
            $method_getColorModel_8 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getColorModel", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getCompressedTile_9 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getCompressedTile", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE, Integer.TYPE);
            $method_getData_10 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getData_11 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getData", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = JAIRMIImageServer_Stub.class$("java.awt.Rectangle")));
            $method_getHeight_12 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getHeight", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getInvalidRegion_13 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getInvalidRegion", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$image$renderable$ParameterBlock != null ? class$java$awt$image$renderable$ParameterBlock : (class$java$awt$image$renderable$ParameterBlock = JAIRMIImageServer_Stub.class$("java.awt.image.renderable.ParameterBlock")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")), class$java$awt$image$renderable$ParameterBlock != null ? class$java$awt$image$renderable$ParameterBlock : (class$java$awt$image$renderable$ParameterBlock = JAIRMIImageServer_Stub.class$("java.awt.image.renderable.ParameterBlock")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_getMinTileX_14 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getMinTileX", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getMinTileY_15 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getMinTileY", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getMinX_16 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getMinX", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getMinY_17 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getMinY", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getNode_18 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getNode", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getNumXTiles_19 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getNumXTiles", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getNumYTiles_20 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getNumYTiles", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getOperationDescriptors_21 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getOperationDescriptors", new Class[0]);
            $method_getProperty_22 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getProperty", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")));
            $method_getPropertyNames_23 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getPropertyNames", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getPropertyNames_24 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getPropertyNames", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")));
            $method_getRemoteID_25 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRemoteID", new Class[0]);
            $method_getRenderableHeight_26 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRenderableHeight", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getRenderableMinX_27 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRenderableMinX", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getRenderableMinY_28 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRenderableMinY", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getRenderableWidth_29 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRenderableWidth", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getRendering_30 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRendering", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getRendering_31 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getRendering", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_getSampleModel_32 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getSampleModel", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getServerCapabilities_33 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getServerCapabilities", new Class[0]);
            $method_getServerSupportedOperationNames_34 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getServerSupportedOperationNames", new Class[0]);
            $method_getTile_35 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getTile", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE, Integer.TYPE);
            $method_getTileGridXOffset_36 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getTileGridXOffset", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getTileGridYOffset_37 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getTileGridYOffset", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getTileHeight_38 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getTileHeight", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getTileWidth_39 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getTileWidth", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_getWidth_40 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("getWidth", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_handleEvent_41 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("handleEvent", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE, class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")), class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = JAIRMIImageServer_Stub.class$("java.lang.Object")));
            $method_handleEvent_42 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("handleEvent", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = JAIRMIImageServer_Stub.class$("java.lang.Object")), class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = JAIRMIImageServer_Stub.class$("java.lang.Object")));
            $method_incrementRefCount_43 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("incrementRefCount", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_isDynamic_44 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("isDynamic", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")));
            $method_isDynamic_45 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("isDynamic", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")));
            $method_mapDestRect_46 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("mapDestRect", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = JAIRMIImageServer_Stub.class$("java.awt.Rectangle")), Integer.TYPE);
            $method_mapRenderContext_47 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("mapRenderContext", Integer.TYPE, class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$javax$media$jai$remote$SerializableState != null ? class$javax$media$jai$remote$SerializableState : (class$javax$media$jai$remote$SerializableState = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.SerializableState")));
            $method_mapSourceRect_48 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("mapSourceRect", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$Rectangle != null ? class$java$awt$Rectangle : (class$java$awt$Rectangle = JAIRMIImageServer_Stub.class$("java.awt.Rectangle")), Integer.TYPE);
            $method_setRenderableRMIServerProxyAsSource_49 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableRMIServerProxyAsSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), Integer.TYPE);
            $method_setRenderableSource_50 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$com$sun$media$jai$rmi$SerializableRenderableImage != null ? class$com$sun$media$jai$rmi$SerializableRenderableImage : (class$com$sun$media$jai$rmi$SerializableRenderableImage = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.SerializableRenderableImage")), Integer.TYPE);
            $method_setRenderableSource_51 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$image$RenderedImage != null ? class$java$awt$image$RenderedImage : (class$java$awt$image$RenderedImage = JAIRMIImageServer_Stub.class$("java.awt.image.RenderedImage")), Integer.TYPE);
            $method_setRenderableSource_52 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE);
            $method_setRenderableSource_53 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), Integer.TYPE);
            $method_setRenderableSource_54 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderableSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$javax$media$jai$RenderableOp != null ? class$javax$media$jai$RenderableOp : (class$javax$media$jai$RenderableOp = JAIRMIImageServer_Stub.class$("javax.media.jai.RenderableOp")), Integer.TYPE);
            $method_setRenderedSource_55 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderedSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$awt$image$RenderedImage != null ? class$java$awt$image$RenderedImage : (class$java$awt$image$RenderedImage = JAIRMIImageServer_Stub.class$("java.awt.image.RenderedImage")), Integer.TYPE);
            $method_setRenderedSource_56 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderedSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), Integer.TYPE);
            $method_setRenderedSource_57 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderedSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = JAIRMIImageServer_Stub.class$("java.lang.String")), Integer.TYPE);
            $method_setRenderedSource_58 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setRenderedSource", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$javax$media$jai$RenderedOp != null ? class$javax$media$jai$RenderedOp : (class$javax$media$jai$RenderedOp = JAIRMIImageServer_Stub.class$("javax.media.jai.RenderedOp")), Integer.TYPE);
            $method_setServerNegotiatedValues_59 = (class$com$sun$media$jai$rmi$ImageServer != null ? class$com$sun$media$jai$rmi$ImageServer : (class$com$sun$media$jai$rmi$ImageServer = JAIRMIImageServer_Stub.class$("com.sun.media.jai.rmi.ImageServer"))).getMethod("setServerNegotiatedValues", class$java$lang$Long != null ? class$java$lang$Long : (class$java$lang$Long = JAIRMIImageServer_Stub.class$("java.lang.Long")), class$javax$media$jai$remote$NegotiableCapabilitySet != null ? class$javax$media$jai$remote$NegotiableCapabilitySet : (class$javax$media$jai$remote$NegotiableCapabilitySet = JAIRMIImageServer_Stub.class$("javax.media.jai.remote.NegotiableCapabilitySet")));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            useNewInvoke = false;
        }
    }

    public JAIRMIImageServer_Stub() {
    }

    public JAIRMIImageServer_Stub(RemoteRef remoteRef) {
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

    public SerializableState copyData(Long l, Rectangle rectangle) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_copyData_0, new Object[]{l, rectangle}, -967509352521768614L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 0, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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

    public RenderedImage createDefaultRendering(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_createDefaultRendering_1, new Object[]{l}, -8497891458627429487L);
                return (RenderedImage)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 1, 6167769405001739342L);
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
                    object = (RenderedImage)objectInput.readObject();
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

    public void createRenderableOp(Long l, String string, ParameterBlock parameterBlock) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_createRenderableOp_2, new Object[]{l, string, parameterBlock}, 7086259789809689998L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 2, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(string);
                    objectOutput.writeObject(parameterBlock);
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

    public void createRenderedOp(Long l, String string, ParameterBlock parameterBlock, SerializableState serializableState) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_createRenderedOp_3, new Object[]{l, string, parameterBlock, serializableState}, 5101379426256032149L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 3, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(string);
                    objectOutput.writeObject(parameterBlock);
                    objectOutput.writeObject(serializableState);
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

    public RenderedImage createRendering(Long l, SerializableState serializableState) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_createRendering_4, new Object[]{l, serializableState}, -5245001515136243438L);
                return (RenderedImage)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 4, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(serializableState);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RenderedImage)objectInput.readObject();
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

    public RenderedImage createScaledRendering(Long l, int n, int n2, SerializableState serializableState) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_createScaledRendering_5, new Object[]{l, new Integer(n), new Integer(n2), serializableState}, 2752392759141353347L);
                return (RenderedImage)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 5, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeInt(n);
                object.writeInt(n2);
                object.writeObject(serializableState);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (RenderedImage)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var8_15 = null;
            }
            catch (Throwable throwable) {
                Object var8_16 = null;
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
                    this.ref.invoke(this, $method_dispose_6, new Object[]{l}, 6460799139781649959L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 6, 6167769405001739342L);
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

    public SerializableState getBounds2D(Long l, String string) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getBounds2D_7, new Object[]{l, string}, -7344372886056435090L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 7, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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

    public SerializableState getColorModel(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getColorModel_8, new Object[]{l}, -1100163628488185119L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 8, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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

    public byte[] getCompressedTile(Long l, int n, int n2) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getCompressedTile_9, new Object[]{l, new Integer(n), new Integer(n2)}, -1379943561537216322L);
                return (byte[])object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 9, 6167769405001739342L);
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
                    object = (byte[])objectInput.readObject();
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

    public SerializableState getData(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getData_10, new Object[]{l}, 6361054168006114985L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 10, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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

    public SerializableState getData(Long l, Rectangle rectangle) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getData_11, new Object[]{l, rectangle}, -3749893868609537021L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 11, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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
                Object object = this.ref.invoke(this, $method_getHeight_12, new Object[]{l}, -7560603472052038977L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 12, 6167769405001739342L);
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

    public SerializableState getInvalidRegion(Long l, ParameterBlock parameterBlock, SerializableState serializableState, ParameterBlock parameterBlock2, SerializableState serializableState2) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getInvalidRegion_13, new Object[]{l, parameterBlock, serializableState, parameterBlock2, serializableState2}, 2196538291040842281L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 13, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(parameterBlock);
                object.writeObject(serializableState);
                object.writeObject(parameterBlock2);
                object.writeObject(serializableState2);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (SerializableState)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var9_16 = null;
            }
            catch (Throwable throwable) {
                Object var9_17 = null;
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

    public int getMinTileX(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getMinTileX_14, new Object[]{l}, 5809966745410438246L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 14, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getMinTileY_15, new Object[]{l}, -9076617268613815876L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 15, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getMinX_16, new Object[]{l}, -5297535099750447733L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 16, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getMinY_17, new Object[]{l}, 7733459005376369327L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 17, 6167769405001739342L);
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

    public RenderedOp getNode(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getNode_18, new Object[]{l}, 9161432851012319050L);
                return (RenderedOp)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 18, 6167769405001739342L);
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
                    object = (RenderedOp)objectInput.readObject();
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

    public int getNumXTiles(Long l) throws RemoteException {
        try {
            int n;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getNumXTiles_19, new Object[]{l}, 3645100420184954761L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 19, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getNumYTiles_20, new Object[]{l}, -1731091968647972742L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 20, 6167769405001739342L);
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

    public List getOperationDescriptors() throws RemoteException {
        try {
            List list;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getOperationDescriptors_21, null, 3535648159716437706L);
                return (List)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 21, 6167769405001739342L);
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    list = (List)objectInput.readObject();
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
            return list;
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
                Object object2 = this.ref.invoke(this, $method_getProperty_22, new Object[]{l, string}, 216968610676295195L);
                return object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 22, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getPropertyNames_23, new Object[]{l}, 3931591828613160321L);
                return (String[])object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 23, 6167769405001739342L);
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

    public String[] getPropertyNames(String string) throws RemoteException {
        try {
            String[] stringArray;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getPropertyNames_24, new Object[]{string}, 316409741847260476L);
                return (String[])object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 24, 6167769405001739342L);
            try {
                stringArray = remoteCall.getOutputStream();
                stringArray.writeObject(string);
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
                Object object = this.ref.invoke(this, $method_getRemoteID_25, null, -232353888923603427L);
                return (Long)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 25, 6167769405001739342L);
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

    public float getRenderableHeight(Long l) throws RemoteException {
        try {
            float f;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRenderableHeight_26, new Object[]{l}, 5608422195731594411L);
                return ((Float)object).floatValue();
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 26, 6167769405001739342L);
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
                    f = objectInput.readFloat();
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
            return f;
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

    public float getRenderableMinX(Long l) throws RemoteException {
        try {
            float f;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRenderableMinX_27, new Object[]{l}, 2691228702599857582L);
                return ((Float)object).floatValue();
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 27, 6167769405001739342L);
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
                    f = objectInput.readFloat();
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
            return f;
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

    public float getRenderableMinY(Long l) throws RemoteException {
        try {
            float f;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRenderableMinY_28, new Object[]{l}, 4212368935241858980L);
                return ((Float)object).floatValue();
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 28, 6167769405001739342L);
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
                    f = objectInput.readFloat();
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
            return f;
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

    public float getRenderableWidth(Long l) throws RemoteException {
        try {
            float f;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRenderableWidth_29, new Object[]{l}, 5338396004630022671L);
                return ((Float)object).floatValue();
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 29, 6167769405001739342L);
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
                    f = objectInput.readFloat();
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
            return f;
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

    public boolean getRendering(Long l) throws RemoteException {
        try {
            boolean bl;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getRendering_30, new Object[]{l}, -2265440493870323208L);
                return (Boolean)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 30, 6167769405001739342L);
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
                    bl = objectInput.readBoolean();
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
            return bl;
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

    public Long getRendering(Long l, SerializableState serializableState) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getRendering_31, new Object[]{l, serializableState}, -6125241444070859614L);
                return (Long)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 31, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(serializableState);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (Long)objectInput.readObject();
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

    public SerializableState getSampleModel(Long l) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getSampleModel_32, new Object[]{l}, -1813341280855901292L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 32, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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

    public NegotiableCapabilitySet getServerCapabilities() throws RemoteException {
        try {
            NegotiableCapabilitySet negotiableCapabilitySet;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getServerCapabilities_33, null, -5684371542470892640L);
                return (NegotiableCapabilitySet)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 33, 6167769405001739342L);
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    negotiableCapabilitySet = (NegotiableCapabilitySet)objectInput.readObject();
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
            return negotiableCapabilitySet;
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

    public String[] getServerSupportedOperationNames() throws RemoteException {
        try {
            String[] stringArray;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_getServerSupportedOperationNames_34, null, -4886984326445878690L);
                return (String[])object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 34, 6167769405001739342L);
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
                Object var4_10 = null;
            }
            catch (Throwable throwable) {
                Object var4_11 = null;
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

    public SerializableState getTile(Long l, int n, int n2) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_getTile_35, new Object[]{l, new Integer(n), new Integer(n2)}, 3187214795636220126L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 35, 6167769405001739342L);
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
                    object = (SerializableState)objectInput.readObject();
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
                Object object = this.ref.invoke(this, $method_getTileGridXOffset_36, new Object[]{l}, -8218495432205133449L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 36, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getTileGridYOffset_37, new Object[]{l}, -7482127068346373541L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 37, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getTileHeight_38, new Object[]{l}, 7785669351714030715L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 38, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getTileWidth_39, new Object[]{l}, 282122131312695349L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 39, 6167769405001739342L);
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
                Object object = this.ref.invoke(this, $method_getWidth_40, new Object[]{l}, -8357318297729299690L);
                return (Integer)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 40, 6167769405001739342L);
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

    public Long handleEvent(Long l, int n, SerializableState serializableState, Object object) throws RemoteException {
        try {
            Object object2;
            if (useNewInvoke) {
                Object object3 = this.ref.invoke(this, $method_handleEvent_41, new Object[]{l, new Integer(n), serializableState, object}, -2091789747834377998L);
                return (Long)object3;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 41, 6167769405001739342L);
            try {
                object2 = remoteCall.getOutputStream();
                object2.writeObject(l);
                object2.writeInt(n);
                object2.writeObject(serializableState);
                object2.writeObject(object);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object2 = (Long)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var8_15 = null;
            }
            catch (Throwable throwable) {
                Object var8_16 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object2;
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

    public Long handleEvent(Long l, String string, Object object, Object object2) throws RemoteException {
        try {
            Object object3;
            if (useNewInvoke) {
                Object object4 = this.ref.invoke(this, $method_handleEvent_42, new Object[]{l, string, object, object2}, 6735595879989328767L);
                return (Long)object4;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 42, 6167769405001739342L);
            try {
                object3 = remoteCall.getOutputStream();
                object3.writeObject(l);
                object3.writeObject(string);
                object3.writeObject(object);
                object3.writeObject(object2);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object3 = (Long)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var8_15 = null;
            }
            catch (Throwable throwable) {
                Object var8_16 = null;
                this.ref.done(remoteCall);
                throw throwable;
            }
            this.ref.done(remoteCall);
            return object3;
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

    public void incrementRefCount(Long l) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_incrementRefCount_43, new Object[]{l}, -3309069034569190342L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 43, 6167769405001739342L);
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

    public boolean isDynamic(Long l) throws RemoteException {
        try {
            boolean bl;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_isDynamic_44, new Object[]{l}, 9106025340051027274L);
                return (Boolean)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 44, 6167769405001739342L);
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
                    bl = objectInput.readBoolean();
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
            return bl;
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

    public boolean isDynamic(String string) throws RemoteException {
        try {
            boolean bl;
            if (useNewInvoke) {
                Object object = this.ref.invoke(this, $method_isDynamic_45, new Object[]{string}, -6284830256520969130L);
                return (Boolean)object;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 45, 6167769405001739342L);
            try {
                ObjectOutput objectOutput = remoteCall.getOutputStream();
                objectOutput.writeObject(string);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    bl = objectInput.readBoolean();
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
            return bl;
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

    public Rectangle mapDestRect(Long l, Rectangle rectangle, int n) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_mapDestRect_46, new Object[]{l, rectangle, new Integer(n)}, 2783117304536308041L);
                return (Rectangle)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 46, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(rectangle);
                object.writeInt(n);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (Rectangle)objectInput.readObject();
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

    public SerializableState mapRenderContext(int n, Long l, String string, SerializableState serializableState) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_mapRenderContext_47, new Object[]{new Integer(n), l, string, serializableState}, 3382362498715729166L);
                return (SerializableState)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 47, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeInt(n);
                object.writeObject(l);
                object.writeObject(string);
                object.writeObject(serializableState);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (SerializableState)objectInput.readObject();
                }
                catch (IOException iOException) {
                    throw new UnmarshalException("error unmarshalling return", iOException);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new UnmarshalException("error unmarshalling return", classNotFoundException);
                }
                Object var8_15 = null;
            }
            catch (Throwable throwable) {
                Object var8_16 = null;
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

    public Rectangle mapSourceRect(Long l, Rectangle rectangle, int n) throws RemoteException {
        try {
            Object object;
            if (useNewInvoke) {
                Object object2 = this.ref.invoke(this, $method_mapSourceRect_48, new Object[]{l, rectangle, new Integer(n)}, -5162241366759407841L);
                return (Rectangle)object2;
            }
            RemoteCall remoteCall = this.ref.newCall(this, operations, 48, 6167769405001739342L);
            try {
                object = remoteCall.getOutputStream();
                object.writeObject(l);
                object.writeObject(rectangle);
                object.writeInt(n);
            }
            catch (IOException iOException) {
                throw new MarshalException("error marshalling arguments", iOException);
            }
            this.ref.invoke(remoteCall);
            try {
                try {
                    ObjectInput objectInput = remoteCall.getInputStream();
                    object = (Rectangle)objectInput.readObject();
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

    public void setRenderableRMIServerProxyAsSource(Long l, Long l2, String string, String string2, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableRMIServerProxyAsSource_49, new Object[]{l, l2, string, string2, new Integer(n)}, -1865549286439023174L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 49, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(l2);
                    objectOutput.writeObject(string);
                    objectOutput.writeObject(string2);
                    objectOutput.writeInt(n);
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

    public void setRenderableSource(Long l, SerializableRenderableImage serializableRenderableImage, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableSource_50, new Object[]{l, serializableRenderableImage, new Integer(n)}, -2003236639401449658L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 50, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(serializableRenderableImage);
                    objectOutput.writeInt(n);
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

    public void setRenderableSource(Long l, RenderedImage renderedImage, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableSource_51, new Object[]{l, renderedImage, new Integer(n)}, -8080617916453915737L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 51, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderedImage);
                    objectOutput.writeInt(n);
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

    public void setRenderableSource(Long l, Long l2, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableSource_52, new Object[]{l, l2, new Integer(n)}, -7879955699630425072L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 52, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(l2);
                    objectOutput.writeInt(n);
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

    public void setRenderableSource(Long l, Long l2, String string, String string2, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableSource_53, new Object[]{l, l2, string, string2, new Integer(n)}, -5890575207352710342L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 53, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(l2);
                    objectOutput.writeObject(string);
                    objectOutput.writeObject(string2);
                    objectOutput.writeInt(n);
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

    public void setRenderableSource(Long l, RenderableOp renderableOp, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderableSource_54, new Object[]{l, renderableOp, new Integer(n)}, -8761942329287512340L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 54, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderableOp);
                    objectOutput.writeInt(n);
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

    public void setRenderedSource(Long l, RenderedImage renderedImage, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderedSource_55, new Object[]{l, renderedImage, new Integer(n)}, 2834995389306513647L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 55, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderedImage);
                    objectOutput.writeInt(n);
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

    public void setRenderedSource(Long l, Long l2, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderedSource_56, new Object[]{l, l2, new Integer(n)}, -6335170796820847995L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 56, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(l2);
                    objectOutput.writeInt(n);
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

    public void setRenderedSource(Long l, Long l2, String string, String string2, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderedSource_57, new Object[]{l, l2, string, string2, new Integer(n)}, -1071494500456449009L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 57, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(l2);
                    objectOutput.writeObject(string);
                    objectOutput.writeObject(string2);
                    objectOutput.writeInt(n);
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

    public void setRenderedSource(Long l, RenderedOp renderedOp, int n) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setRenderedSource_58, new Object[]{l, renderedOp, new Integer(n)}, -7819102304157660296L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 58, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(renderedOp);
                    objectOutput.writeInt(n);
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

    public void setServerNegotiatedValues(Long l, NegotiableCapabilitySet negotiableCapabilitySet) throws RemoteException {
        block7: {
            try {
                if (useNewInvoke) {
                    this.ref.invoke(this, $method_setServerNegotiatedValues_59, new Object[]{l, negotiableCapabilitySet}, -27037179580597379L);
                    break block7;
                }
                RemoteCall remoteCall = this.ref.newCall(this, operations, 59, 6167769405001739342L);
                try {
                    ObjectOutput objectOutput = remoteCall.getOutputStream();
                    objectOutput.writeObject(l);
                    objectOutput.writeObject(negotiableCapabilitySet);
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

