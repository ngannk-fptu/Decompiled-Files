/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ColorModelProxy;
import com.sun.media.jai.rmi.RMIImageImpl;
import com.sun.media.jai.rmi.RasterProxy;
import com.sun.media.jai.rmi.RenderContextProxy;
import com.sun.media.jai.rmi.SampleModelProxy;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
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
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public final class RMIImageImpl_Skel
implements Skeleton {
    private static final Operation[] operations = new Operation[]{new Operation("com.sun.media.jai.rmi.RasterProxy copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("void dispose(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.ColorModelProxy getColorModel(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("com.sun.media.jai.rmi.SampleModelProxy getSampleModel(java.lang.Long)"), new Operation("java.util.Vector getSources(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("void setSource(java.lang.Long, java.awt.image.RenderedImage)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderableOp, com.sun.media.jai.rmi.RenderContextProxy)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderedOp)")};
    private static final long interfaceHash = -9186133247174212020L;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void dispatch(Remote remote, RemoteCall remoteCall, int n, long l) throws Exception {
        if (n < 0) {
            if (l == -4480130102587337594L) {
                n = 0;
            } else if (l == 6460799139781649959L) {
                n = 1;
            } else if (l == 5862232465831048388L) {
                n = 2;
            } else if (l == 5982474592659170320L) {
                n = 3;
            } else if (l == -7782001095732779284L) {
                n = 4;
            } else if (l == -7560603472052038977L) {
                n = 5;
            } else if (l == 5809966745410438246L) {
                n = 6;
            } else if (l == -9076617268613815876L) {
                n = 7;
            } else if (l == -5297535099750447733L) {
                n = 8;
            } else if (l == 7733459005376369327L) {
                n = 9;
            } else if (l == 3645100420184954761L) {
                n = 10;
            } else if (l == -1731091968647972742L) {
                n = 11;
            } else if (l == 216968610676295195L) {
                n = 12;
            } else if (l == 3931591828613160321L) {
                n = 13;
            } else if (l == -232353888923603427L) {
                n = 14;
            } else if (l == -8396533149827190655L) {
                n = 15;
            } else if (l == -3713513808775692904L) {
                n = 16;
            } else if (l == -1008030285235108860L) {
                n = 17;
            } else if (l == -8218495432205133449L) {
                n = 18;
            } else if (l == -7482127068346373541L) {
                n = 19;
            } else if (l == 7785669351714030715L) {
                n = 20;
            } else if (l == 282122131312695349L) {
                n = 21;
            } else if (l == -8357318297729299690L) {
                n = 22;
            } else if (l == 4248763766578677765L) {
                n = 23;
            } else if (l == 7010328997687947687L) {
                n = 24;
            } else {
                if (l != -4039999355356694323L) throw new UnmarshalException("invalid method hash");
                n = 25;
            }
        } else if (l != -9186133247174212020L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        RMIImageImpl rMIImageImpl = (RMIImageImpl)remote;
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
                    Object var10_98 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RasterProxy rasterProxy = rMIImageImpl.copyData(l2, rectangle);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rasterProxy);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 1: {
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
                    Object var9_170 = null;
                }
                catch (Throwable throwable) {
                    Object var9_171 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                rMIImageImpl.dispose(l3);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 2: {
                ObjectOutput objectOutput;
                Long l4;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l4 = (Long)objectInput.readObject();
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
                    Object var9_173 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                ColorModelProxy colorModelProxy = rMIImageImpl.getColorModel(l4);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(colorModelProxy);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 3: {
                ObjectOutput objectOutput;
                Long l5;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l5 = (Long)objectInput.readObject();
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
                    Object var9_176 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RasterProxy rasterProxy = rMIImageImpl.getData(l5);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rasterProxy);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 4: {
                ObjectOutput objectOutput;
                Rectangle rectangle;
                Long l6;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l6 = (Long)objectInput.readObject();
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
                    Object var10_110 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RasterProxy rasterProxy = rMIImageImpl.getData(l6, rectangle);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rasterProxy);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 5: {
                ObjectOutput objectOutput;
                Long l7;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l7 = (Long)objectInput.readObject();
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
                    Object var9_181 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n2 = rMIImageImpl.getHeight(l7);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n2);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 6: {
                ObjectOutput objectOutput;
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
                    objectOutput = null;
                }
                catch (Throwable throwable) {
                    Object var9_184 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n3 = rMIImageImpl.getMinTileX(l8);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n3);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 7: {
                ObjectOutput objectOutput;
                Long l9;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l9 = (Long)objectInput.readObject();
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
                    Object var9_187 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n4 = rMIImageImpl.getMinTileY(l9);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n4);
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
                    Object var9_190 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n5 = rMIImageImpl.getMinX(l10);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n5);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 9: {
                ObjectOutput objectOutput;
                Long l11;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l11 = (Long)objectInput.readObject();
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
                    Object var9_193 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n6 = rMIImageImpl.getMinY(l11);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n6);
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
                    Object var9_196 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n7 = rMIImageImpl.getNumXTiles(l12);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n7);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 11: {
                ObjectOutput objectOutput;
                Long l13;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l13 = (Long)objectInput.readObject();
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
                    Object var9_199 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n8 = rMIImageImpl.getNumYTiles(l13);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n8);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 12: {
                ObjectOutput objectOutput;
                String string;
                Long l14;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l14 = (Long)objectInput.readObject();
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
                    Object var10_134 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Object object = rMIImageImpl.getProperty(l14, string);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(object);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 13: {
                ObjectOutput objectOutput;
                Long l15;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l15 = (Long)objectInput.readObject();
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
                    Object var9_204 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                String[] stringArray = rMIImageImpl.getPropertyNames(l15);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(stringArray);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 14: {
                remoteCall.releaseInputStream();
                Long l16 = rMIImageImpl.getRemoteID();
                try {
                    ObjectOutput objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(l16);
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
                    Object var9_207 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                SampleModelProxy sampleModelProxy = rMIImageImpl.getSampleModel(l17);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(sampleModelProxy);
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
                    Object var9_210 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                Vector vector = rMIImageImpl.getSources(l18);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(vector);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 17: {
                ObjectOutput objectOutput;
                int n9;
                int n10;
                Long l19;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l19 = (Long)objectInput.readObject();
                        n10 = objectInput.readInt();
                        n9 = objectInput.readInt();
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
                    Object var11_16 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                RasterProxy rasterProxy = rMIImageImpl.getTile(l19, n10, n9);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeObject(rasterProxy);
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
                    Object var9_214 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n11 = rMIImageImpl.getTileGridXOffset(l20);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n11);
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
                    Object var9_217 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n12 = rMIImageImpl.getTileGridYOffset(l21);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n12);
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
                    Object var9_220 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n13 = rMIImageImpl.getTileHeight(l22);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n13);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 21: {
                ObjectOutput objectOutput;
                Long l23;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l23 = (Long)objectInput.readObject();
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
                    Object var9_223 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n14 = rMIImageImpl.getTileWidth(l23);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n14);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 22: {
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
                    Object var9_226 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                int n15 = rMIImageImpl.getWidth(l24);
                try {
                    objectOutput = remoteCall.getResultStream(true);
                    objectOutput.writeInt(n15);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 23: {
                RenderedImage renderedImage;
                Long l25;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l25 = (Long)objectInput.readObject();
                        renderedImage = (RenderedImage)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var10_162 = null;
                }
                catch (Throwable throwable) {
                    Object var10_163 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                rMIImageImpl.setSource(l25, renderedImage);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 24: {
                RenderContextProxy renderContextProxy;
                RenderableOp renderableOp;
                Long l26;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l26 = (Long)objectInput.readObject();
                        renderableOp = (RenderableOp)objectInput.readObject();
                        renderContextProxy = (RenderContextProxy)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var11_21 = null;
                }
                catch (Throwable throwable) {
                    Object var11_22 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                rMIImageImpl.setSource(l26, renderableOp, renderContextProxy);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (IOException iOException) {
                    throw new MarshalException("error marshalling return", iOException);
                }
            }
            case 25: {
                RenderedOp renderedOp;
                Long l27;
                try {
                    try {
                        ObjectInput objectInput = remoteCall.getInputStream();
                        l27 = (Long)objectInput.readObject();
                        renderedOp = (RenderedOp)objectInput.readObject();
                    }
                    catch (IOException iOException) {
                        throw new UnmarshalException("error unmarshalling arguments", iOException);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
                    }
                    Object var10_166 = null;
                }
                catch (Throwable throwable) {
                    Object var10_167 = null;
                    remoteCall.releaseInputStream();
                    throw throwable;
                }
                remoteCall.releaseInputStream();
                rMIImageImpl.setSource(l27, renderedOp);
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

