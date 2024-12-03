/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JAIRMIImageServer;
import com.sun.media.jai.rmi.SerializableRenderableImage;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;
import java.util.List;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.SerializableState;

public final class JAIRMIImageServer_Skel
implements Skeleton {
    private static final Operation[] operations = new Operation[]{new Operation("javax.media.jai.remote.SerializableState copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("java.awt.image.RenderedImage createDefaultRendering(java.lang.Long)"), new Operation("void createRenderableOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock)"), new Operation("void createRenderedOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createScaledRendering(java.lang.Long, int, int, javax.media.jai.remote.SerializableState)"), new Operation("void dispose(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getBounds2D(java.lang.Long, java.lang.String)"), new Operation("javax.media.jai.remote.SerializableState getColorModel(java.lang.Long)"), new Operation("byte getCompressedTile(java.lang.Long, int, int)[]"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getInvalidRegion(java.lang.Long, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("javax.media.jai.RenderedOp getNode(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.util.List getOperationDescriptors()"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.String getPropertyNames(java.lang.String)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("float getRenderableHeight(java.lang.Long)"), new Operation("float getRenderableMinX(java.lang.Long)"), new Operation("float getRenderableMinY(java.lang.Long)"), new Operation("float getRenderableWidth(java.lang.Long)"), new Operation("boolean getRendering(java.lang.Long)"), new Operation("java.lang.Long getRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("javax.media.jai.remote.SerializableState getSampleModel(java.lang.Long)"), new Operation("javax.media.jai.remote.NegotiableCapabilitySet getServerCapabilities()"), new Operation("java.lang.String getServerSupportedOperationNames()[]"), new Operation("javax.media.jai.remote.SerializableState getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("java.lang.Long handleEvent(java.lang.Long, int, javax.media.jai.remote.SerializableState, java.lang.Object)"), new Operation("java.lang.Long handleEvent(java.lang.Long, java.lang.String, java.lang.Object, java.lang.Object)"), new Operation("void incrementRefCount(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.String)"), new Operation("java.awt.Rectangle mapDestRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("javax.media.jai.remote.SerializableState mapRenderContext(int, java.lang.Long, java.lang.String, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.Rectangle mapSourceRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("void setRenderableRMIServerProxyAsSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, com.sun.media.jai.rmi.SerializableRenderableImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, javax.media.jai.RenderableOp, int)"), new Operation("void setRenderedSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderedSource(java.lang.Long, javax.media.jai.RenderedOp, int)"), new Operation("void setServerNegotiatedValues(java.lang.Long, javax.media.jai.remote.NegotiableCapabilitySet)")};
    private static final long interfaceHash = 6167769405001739342L;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void dispatch(Remote remote, RemoteCall remoteCall, int n, long l) throws Exception {
        if (n < 0) {
            if (l == -967509352521768614L) {
                n = 0;
            } else if (l == -8497891458627429487L) {
                n = 1;
            } else if (l == 7086259789809689998L) {
                n = 2;
            } else if (l == 5101379426256032149L) {
                n = 3;
            } else if (l == -5245001515136243438L) {
                n = 4;
            } else if (l == 2752392759141353347L) {
                n = 5;
            } else if (l == 6460799139781649959L) {
                n = 6;
            } else if (l == -7344372886056435090L) {
                n = 7;
            } else if (l == -1100163628488185119L) {
                n = 8;
            } else if (l == -1379943561537216322L) {
                n = 9;
            } else if (l == 6361054168006114985L) {
                n = 10;
            } else if (l == -3749893868609537021L) {
                n = 11;
            } else if (l == -7560603472052038977L) {
                n = 12;
            } else if (l == 2196538291040842281L) {
                n = 13;
            } else if (l == 5809966745410438246L) {
                n = 14;
            } else if (l == -9076617268613815876L) {
                n = 15;
            } else if (l == -5297535099750447733L) {
                n = 16;
            } else if (l == 7733459005376369327L) {
                n = 17;
            } else if (l == 9161432851012319050L) {
                n = 18;
            } else if (l == 3645100420184954761L) {
                n = 19;
            } else if (l == -1731091968647972742L) {
                n = 20;
            } else if (l == 3535648159716437706L) {
                n = 21;
            } else if (l == 216968610676295195L) {
                n = 22;
            } else if (l == 3931591828613160321L) {
                n = 23;
            } else if (l == 316409741847260476L) {
                n = 24;
            } else if (l == -232353888923603427L) {
                n = 25;
            } else if (l == 5608422195731594411L) {
                n = 26;
            } else if (l == 2691228702599857582L) {
                n = 27;
            } else if (l == 4212368935241858980L) {
                n = 28;
            } else if (l == 5338396004630022671L) {
                n = 29;
            } else if (l == -2265440493870323208L) {
                n = 30;
            } else if (l == -6125241444070859614L) {
                n = 31;
            } else if (l == -1813341280855901292L) {
                n = 32;
            } else if (l == -5684371542470892640L) {
                n = 33;
            } else if (l == -4886984326445878690L) {
                n = 34;
            } else if (l == 3187214795636220126L) {
                n = 35;
            } else if (l == -8218495432205133449L) {
                n = 36;
            } else if (l == -7482127068346373541L) {
                n = 37;
            } else if (l == 7785669351714030715L) {
                n = 38;
            } else if (l == 282122131312695349L) {
                n = 39;
            } else if (l == -8357318297729299690L) {
                n = 40;
            } else if (l == -2091789747834377998L) {
                n = 41;
            } else if (l == 6735595879989328767L) {
                n = 42;
            } else if (l == -3309069034569190342L) {
                n = 43;
            } else if (l == 9106025340051027274L) {
                n = 44;
            } else if (l == -6284830256520969130L) {
                n = 45;
            } else if (l == 2783117304536308041L) {
                n = 46;
            } else if (l == 3382362498715729166L) {
                n = 47;
            } else if (l == -5162241366759407841L) {
                n = 48;
            } else if (l == -1865549286439023174L) {
                n = 49;
            } else if (l == -2003236639401449658L) {
                n = 50;
            } else if (l == -8080617916453915737L) {
                n = 51;
            } else if (l == -7879955699630425072L) {
                n = 52;
            } else if (l == -5890575207352710342L) {
                n = 53;
            } else if (l == -8761942329287512340L) {
                n = 54;
            } else if (l == 2834995389306513647L) {
                n = 55;
            } else if (l == -6335170796820847995L) {
                n = 56;
            } else if (l == -1071494500456449009L) {
                n = 57;
            } else if (l == -7819102304157660296L) {
                n = 58;
            } else {
                if (l != -27037179580597379L) throw new UnmarshalException("invalid method hash");
                n = 59;
            }
        } else if (l != 6167769405001739342L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        JAIRMIImageServer jAIRMIImageServer = (JAIRMIImageServer)remote;
        switch (n) {
            case 0: {
                ObjectOutput objectOutput;
                Rectangle rectangle;
                Long l2;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l2 = (Long)objectInput.readObject();
                        rectangle = (Rectangle)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_222 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.copyData(l2, rectangle);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 1: {
                ObjectOutput objectOutput;
                Long l3;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l3 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_361 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RenderedImage renderedImage = jAIRMIImageServer.createDefaultRendering(l3);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(renderedImage);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 2: {
                ParameterBlock parameterBlock;
                String string;
                Long l4;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l4 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        parameterBlock = (ParameterBlock)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_9 = null;
                }
                catch (Throwable throwable) {
                    Object var11_10 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.createRenderableOp(l4, string, parameterBlock);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 3: {
                SerializableState serializableState;
                ParameterBlock parameterBlock;
                String string;
                Long l5;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l5 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        parameterBlock = (ParameterBlock)objectInput.readObject();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var12_478 = null;
                }
                catch (Throwable throwable) {
                    Object var12_479 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.createRenderedOp(l5, string, parameterBlock, serializableState);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 4: {
                ObjectOutput objectOutput;
                SerializableState serializableState;
                Long l6;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l6 = (Long)objectInput.readObject();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_231 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RenderedImage renderedImage = jAIRMIImageServer.createRendering(l6, serializableState);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(renderedImage);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 5: {
                ObjectOutput objectOutput;
                SerializableState serializableState;
                int n2;
                int n3;
                Long l7;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l7 = (Long)objectInput.readObject();
                        n3 = objectInput.readInt();
                        n2 = objectInput.readInt();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var12_481 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RenderedImage renderedImage = jAIRMIImageServer.createScaledRendering(l7, n3, n2, serializableState);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(renderedImage);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 6: {
                Long l8;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l8 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var9_368 = null;
                }
                catch (Throwable throwable) {
                    Object var9_369 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.dispose(l8);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 7: {
                ObjectOutput objectOutput;
                String string;
                Long l9;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l9 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_238 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getBounds2D(l9, string);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 8: {
                ObjectOutput objectOutput;
                Long l10;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l10 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_373 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getColorModel(l10);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 9: {
                ObjectOutput objectOutput;
                int n4;
                int n5;
                Long l11;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l11 = (Long)objectInput.readObject();
                        n5 = objectInput.readInt();
                        n4 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var11_22 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                byte[] byArray = jAIRMIImageServer.getCompressedTile(l11, n5, n4);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(byArray);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 10: {
                ObjectOutput objectOutput;
                Long l12;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l12 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_377 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getData(l12);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 11: {
                ObjectOutput objectOutput;
                Rectangle rectangle;
                Long l13;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l13 = (Long)objectInput.readObject();
                        rectangle = (Rectangle)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_249 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getData(l13, rectangle);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 12: {
                ObjectOutput objectOutput;
                Long l14;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l14 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_382 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n6 = jAIRMIImageServer.getHeight(l14);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n6);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 13: {
                ObjectOutput objectOutput;
                SerializableState serializableState;
                ParameterBlock parameterBlock;
                SerializableState serializableState2;
                ParameterBlock parameterBlock2;
                Long l15;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l15 = (Long)objectInput.readObject();
                        parameterBlock2 = (ParameterBlock)objectInput.readObject();
                        serializableState2 = (SerializableState)objectInput.readObject();
                        parameterBlock = (ParameterBlock)objectInput.readObject();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var13_540 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState3 = jAIRMIImageServer.getInvalidRegion(l15, parameterBlock2, serializableState2, parameterBlock, serializableState);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState3);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 14: {
                ObjectOutput objectOutput;
                Long l16;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l16 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_386 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n7 = jAIRMIImageServer.getMinTileX(l16);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n7);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 15: {
                ObjectOutput objectOutput;
                Long l17;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l17 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_389 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n8 = jAIRMIImageServer.getMinTileY(l17);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n8);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 16: {
                ObjectOutput objectOutput;
                Long l18;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l18 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_392 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n9 = jAIRMIImageServer.getMinX(l18);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n9);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 17: {
                ObjectOutput objectOutput;
                Long l19;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l19 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_395 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n10 = jAIRMIImageServer.getMinY(l19);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n10);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 18: {
                ObjectOutput objectOutput;
                Long l20;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l20 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_398 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RenderedOp renderedOp = jAIRMIImageServer.getNode(l20);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(renderedOp);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 19: {
                ObjectOutput objectOutput;
                Long l21;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l21 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_401 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n11 = jAIRMIImageServer.getNumXTiles(l21);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n11);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 20: {
                ObjectOutput objectOutput;
                Long l22;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l22 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_404 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n12 = jAIRMIImageServer.getNumYTiles(l22);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n12);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 21: {
                remoteCall.releaseInputStream();
                List list = jAIRMIImageServer.getOperationDescriptors();
                try {
                    ObjectOutput objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(list);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 22: {
                ObjectOutput objectOutput;
                String string;
                Long l23;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l23 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_277 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Object object = jAIRMIImageServer.getProperty(l23, string);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(object);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 23: {
                ObjectOutput objectOutput;
                Long l24;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l24 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_409 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                String[] stringArray = jAIRMIImageServer.getPropertyNames(l24);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(stringArray);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 24: {
                ObjectOutput objectOutput;
                String string;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        string = (String)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_412 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                String[] stringArray = jAIRMIImageServer.getPropertyNames(string);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(stringArray);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 25: {
                remoteCall.releaseInputStream();
                Long l25 = jAIRMIImageServer.getRemoteID();
                try {
                    ObjectOutput objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(l25);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 26: {
                ObjectOutput objectOutput;
                Long l26;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l26 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_415 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                float f = jAIRMIImageServer.getRenderableHeight(l26);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeFloat(f);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 27: {
                ObjectOutput objectOutput;
                Long l27;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l27 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_418 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                float f = jAIRMIImageServer.getRenderableMinX(l27);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeFloat(f);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 28: {
                ObjectOutput objectOutput;
                Long l28;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l28 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_421 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                float f = jAIRMIImageServer.getRenderableMinY(l28);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeFloat(f);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 29: {
                ObjectOutput objectOutput;
                Long l29;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l29 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_424 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                float f = jAIRMIImageServer.getRenderableWidth(l29);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeFloat(f);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 30: {
                ObjectOutput objectOutput;
                Long l30;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l30 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_427 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                boolean bl = jAIRMIImageServer.getRendering(l30);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeBoolean(bl);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 31: {
                ObjectOutput objectOutput;
                SerializableState serializableState;
                Long l31;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l31 = (Long)objectInput.readObject();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var10_301 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Long l32 = jAIRMIImageServer.getRendering(l31, serializableState);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(l32);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 32: {
                ObjectOutput objectOutput;
                Long l33;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l33 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_432 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getSampleModel(l33);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 33: {
                remoteCall.releaseInputStream();
                NegotiableCapabilitySet negotiableCapabilitySet = jAIRMIImageServer.getServerCapabilities();
                try {
                    ObjectOutput objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(negotiableCapabilitySet);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 34: {
                remoteCall.releaseInputStream();
                String[] stringArray = jAIRMIImageServer.getServerSupportedOperationNames();
                try {
                    ObjectOutput objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(stringArray);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 35: {
                ObjectOutput objectOutput;
                int n13;
                int n14;
                Long l34;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l34 = (Long)objectInput.readObject();
                        n14 = objectInput.readInt();
                        n13 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var11_35 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState = jAIRMIImageServer.getTile(l34, n14, n13);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 36: {
                ObjectOutput objectOutput;
                Long l35;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l35 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_436 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n15 = jAIRMIImageServer.getTileGridXOffset(l35);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n15);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 37: {
                ObjectOutput objectOutput;
                Long l36;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l36 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_439 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n16 = jAIRMIImageServer.getTileGridYOffset(l36);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n16);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 38: {
                ObjectOutput objectOutput;
                Long l37;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l37 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_442 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n17 = jAIRMIImageServer.getTileHeight(l37);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n17);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 39: {
                ObjectOutput objectOutput;
                Long l38;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l38 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_445 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n18 = jAIRMIImageServer.getTileWidth(l38);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n18);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 40: {
                ObjectOutput objectOutput;
                Long l39;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l39 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_448 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n19 = jAIRMIImageServer.getWidth(l39);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n19);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 41: {
                ObjectOutput objectOutput;
                Object object;
                SerializableState serializableState;
                int n20;
                Long l40;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l40 = (Long)objectInput.readObject();
                        n20 = objectInput.readInt();
                        serializableState = (SerializableState)objectInput.readObject();
                        object = objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var12_492 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Long l41 = jAIRMIImageServer.handleEvent(l40, n20, serializableState, object);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(l41);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 42: {
                ObjectOutput objectOutput;
                Object object;
                Object object2;
                String string;
                Long l42;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l42 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        object2 = objectInput.readObject();
                        object = objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var12_495 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Long l43 = jAIRMIImageServer.handleEvent(l42, string, object2, object);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(l43);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 43: {
                Long l44;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l44 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var9_452 = null;
                }
                catch (Throwable throwable) {
                    Object var9_453 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.incrementRefCount(l44);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 44: {
                ObjectOutput objectOutput;
                Long l45;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l45 = (Long)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_455 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                boolean bl = jAIRMIImageServer.isDynamic(l45);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeBoolean(bl);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 45: {
                ObjectOutput objectOutput;
                String string;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        string = (String)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_458 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                boolean bl = jAIRMIImageServer.isDynamic(string);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeBoolean(bl);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 46: {
                ObjectOutput objectOutput;
                int n21;
                Rectangle rectangle;
                Long l46;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l46 = (Long)objectInput.readObject();
                        rectangle = (Rectangle)objectInput.readObject();
                        n21 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var11_42 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Rectangle rectangle2 = jAIRMIImageServer.mapDestRect(l46, rectangle, n21);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rectangle2);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 47: {
                ObjectOutput objectOutput;
                SerializableState serializableState;
                String string;
                Long l47;
                int n22;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        n22 = objectInput.readInt();
                        l47 = (Long)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        serializableState = (SerializableState)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var12_501 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SerializableState serializableState4 = jAIRMIImageServer.mapRenderContext(n22, l47, string, serializableState);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(serializableState4);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 48: {
                ObjectOutput objectOutput;
                int n23;
                Rectangle rectangle;
                Long l48;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l48 = (Long)objectInput.readObject();
                        rectangle = (Rectangle)objectInput.readObject();
                        n23 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var11_47 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Rectangle rectangle3 = jAIRMIImageServer.mapSourceRect(l48, rectangle, n23);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rectangle3);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 49: {
                int n24;
                String string;
                String string2;
                Long l49;
                Long l50;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l50 = (Long)objectInput.readObject();
                        l49 = (Long)objectInput.readObject();
                        string2 = (String)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        n24 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var13_551 = null;
                }
                catch (Throwable throwable) {
                    Object var13_552 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableRMIServerProxyAsSource(l50, l49, string2, string, n24);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 50: {
                int n25;
                SerializableRenderableImage serializableRenderableImage;
                Long l51;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l51 = (Long)objectInput.readObject();
                        serializableRenderableImage = (SerializableRenderableImage)objectInput.readObject();
                        n25 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_50 = null;
                }
                catch (Throwable throwable) {
                    Object var11_51 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableSource(l51, serializableRenderableImage, n25);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 51: {
                int n26;
                RenderedImage renderedImage;
                Long l52;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l52 = (Long)objectInput.readObject();
                        renderedImage = (RenderedImage)objectInput.readObject();
                        n26 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_52 = null;
                }
                catch (Throwable throwable) {
                    Object var11_53 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableSource(l52, renderedImage, n26);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 52: {
                int n27;
                Long l53;
                Long l54;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l54 = (Long)objectInput.readObject();
                        l53 = (Long)objectInput.readObject();
                        n27 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_54 = null;
                }
                catch (Throwable throwable) {
                    Object var11_55 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableSource(l54, l53, n27);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 53: {
                int n28;
                String string;
                String string3;
                Long l55;
                Long l56;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l56 = (Long)objectInput.readObject();
                        l55 = (Long)objectInput.readObject();
                        string3 = (String)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        n28 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var13_553 = null;
                }
                catch (Throwable throwable) {
                    Object var13_554 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableSource(l56, l55, string3, string, n28);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 54: {
                int n29;
                RenderableOp renderableOp;
                Long l57;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l57 = (Long)objectInput.readObject();
                        renderableOp = (RenderableOp)objectInput.readObject();
                        n29 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_57 = null;
                }
                catch (Throwable throwable) {
                    Object var11_58 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderableSource(l57, renderableOp, n29);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 55: {
                int n30;
                RenderedImage renderedImage;
                Long l58;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l58 = (Long)objectInput.readObject();
                        renderedImage = (RenderedImage)objectInput.readObject();
                        n30 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_59 = null;
                }
                catch (Throwable throwable) {
                    Object var11_60 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderedSource(l58, renderedImage, n30);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 56: {
                int n31;
                Long l59;
                Long l60;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l60 = (Long)objectInput.readObject();
                        l59 = (Long)objectInput.readObject();
                        n31 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_61 = null;
                }
                catch (Throwable throwable) {
                    Object var11_62 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderedSource(l60, l59, n31);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 57: {
                int n32;
                String string;
                String string4;
                Long l61;
                Long l62;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l62 = (Long)objectInput.readObject();
                        l61 = (Long)objectInput.readObject();
                        string4 = (String)objectInput.readObject();
                        string = (String)objectInput.readObject();
                        n32 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var13_555 = null;
                }
                catch (Throwable throwable) {
                    Object var13_556 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderedSource(l62, l61, string4, string, n32);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 58: {
                int n33;
                RenderedOp renderedOp;
                Long l63;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l63 = (Long)objectInput.readObject();
                        renderedOp = (RenderedOp)objectInput.readObject();
                        n33 = objectInput.readInt();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_64 = null;
                }
                catch (Throwable throwable) {
                    Object var11_65 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setRenderedSource(l63, renderedOp, n33);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 59: {
                NegotiableCapabilitySet negotiableCapabilitySet;
                Long l64;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l64 = (Long)objectInput.readObject();
                        negotiableCapabilitySet = (NegotiableCapabilitySet)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var10_356 = null;
                }
                catch (Throwable throwable) {
                    Object var10_357 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                jAIRMIImageServer.setServerNegotiatedValues(l64, negotiableCapabilitySet);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            default: {
                throw new UnmarshalException("invalid method number");
            }
        }
    }

    public Operation[] getOperations() {
        return (Operation[])operations.clone();
    }
}

