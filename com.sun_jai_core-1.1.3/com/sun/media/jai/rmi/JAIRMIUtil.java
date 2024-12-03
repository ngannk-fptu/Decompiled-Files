/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.RMIServerProxy;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;

public final class JAIRMIUtil {
    public static Vector replaceIdWithSources(Vector srcs, Hashtable nodes, String opName, RenderingHints hints) {
        Vector<PlanarImage> replacedSrcs = new Vector<PlanarImage>();
        for (int i = 0; i < srcs.size(); ++i) {
            Object obj = srcs.elementAt(i);
            if (obj instanceof String) {
                boolean diffServer;
                String serverNodeDesc = (String)obj;
                int index = serverNodeDesc.indexOf("::");
                boolean bl = diffServer = index != -1;
                if (diffServer) {
                    replacedSrcs.add(new RMIServerProxy(serverNodeDesc, opName, hints));
                    continue;
                }
                replacedSrcs.add((PlanarImage)nodes.get(Long.valueOf(serverNodeDesc)));
                continue;
            }
            PlanarImage pi = PlanarImage.wrapRenderedImage((RenderedImage)obj);
            replacedSrcs.add(pi);
        }
        return replacedSrcs;
    }

    public static Vector replaceSourcesWithId(Vector srcs, String serverName) {
        Vector<Object> replacedSrcs = new Vector<Object>();
        for (int i = 0; i < srcs.size(); ++i) {
            Object obj = srcs.elementAt(i);
            if (obj instanceof RMIServerProxy) {
                RMIServerProxy rmisp = (RMIServerProxy)obj;
                if (rmisp.getServerName().equalsIgnoreCase(serverName)) {
                    replacedSrcs.add(rmisp.getRMIID().toString());
                    continue;
                }
                String str = new String(rmisp.getServerName() + "::" + rmisp.getRMIID());
                replacedSrcs.add(str);
                continue;
            }
            if (obj instanceof RemoteRenderedOp) {
                RemoteRenderedOp rrop = (RemoteRenderedOp)obj;
                PlanarImage ai = rrop.getRendering();
                if (ai instanceof RMIServerProxy) {
                    RMIServerProxy rmisp = (RMIServerProxy)ai;
                    if (rmisp.getServerName().equalsIgnoreCase(serverName)) {
                        replacedSrcs.add(rmisp.getRMIID().toString());
                        continue;
                    }
                    String str = new String(rmisp.getServerName() + "::" + rmisp.getRMIID());
                    replacedSrcs.add(str);
                    continue;
                }
                RenderedImage ri = ai;
                replacedSrcs.add(new SerializableRenderedImage(ri));
                continue;
            }
            if (obj instanceof RenderedOp) {
                RenderedOp rop = (RenderedOp)obj;
                replacedSrcs.add(new SerializableRenderedImage(rop.getRendering()));
                continue;
            }
            if (obj instanceof Serializable) {
                replacedSrcs.add(obj);
                continue;
            }
            if (!(obj instanceof RenderedImage)) continue;
            RenderedImage ri = (RenderedImage)obj;
            replacedSrcs.add(new SerializableRenderedImage(ri));
        }
        return replacedSrcs;
    }

    public static Object replaceImage(RenderedImage obj, String thisServerName) {
        if (obj instanceof RMIServerProxy) {
            RMIServerProxy rmisp = (RMIServerProxy)obj;
            if (rmisp.getServerName().equalsIgnoreCase(thisServerName)) {
                return "::" + rmisp.getRMIID();
            }
            return rmisp.getServerName() + "::" + rmisp.getRMIID() + ";;" + rmisp.getOperationName();
        }
        if (obj instanceof RenderedOp) {
            PlanarImage rendering = ((RenderedOp)obj).getRendering();
            return JAIRMIUtil.replaceImage(rendering, thisServerName);
        }
        if (obj instanceof RenderedImage) {
            if (obj instanceof Serializable) {
                return obj;
            }
            return new SerializableRenderedImage(obj);
        }
        return obj;
    }

    public static void checkClientParameters(ParameterBlock pb, String thisServerName) {
        if (pb == null) {
            return;
        }
        int numParams = pb.getNumParameters();
        Vector<Object> params = pb.getParameters();
        for (int i = 0; i < numParams; ++i) {
            Object obj = params.elementAt(i);
            if (obj == null || !(obj instanceof RenderedImage)) continue;
            pb.set(JAIRMIUtil.replaceImage((RenderedImage)obj, thisServerName), i);
        }
    }

    public static void checkClientParameters(Vector parameters, String thisServerName) {
        if (parameters == null) {
            return;
        }
        for (int i = 0; i < parameters.size(); ++i) {
            Object obj = parameters.elementAt(i);
            if (obj == null || !(obj instanceof RenderedImage)) continue;
            parameters.set(i, JAIRMIUtil.replaceImage((RenderedImage)obj, thisServerName));
        }
    }

    public static Object replaceStringWithImage(String s, Hashtable nodes) {
        int index1 = s.indexOf("::");
        int index2 = s.indexOf(";;");
        if (index1 == -1) {
            return s;
        }
        if (index2 == -1) {
            Long id = Long.valueOf(s.substring(index1 + 2));
            return nodes.get(id);
        }
        Long id = Long.valueOf(s.substring(index1 + 2, index2));
        String paramServerName = s.substring(0, index1);
        String opName = s.substring(index2 + 2);
        return new RMIServerProxy(paramServerName + "::" + id, opName, null);
    }

    public static void checkServerParameters(ParameterBlock pb, Hashtable nodes) {
        if (pb == null) {
            return;
        }
        int numParams = pb.getNumParameters();
        Vector<Object> params = pb.getParameters();
        for (int i = 0; i < numParams; ++i) {
            Object obj = params.elementAt(i);
            if (obj == null || !(obj instanceof String)) continue;
            pb.set(JAIRMIUtil.replaceStringWithImage((String)obj, nodes), i);
        }
    }

    public static void checkServerParameters(Vector parameters, Hashtable nodes) {
        if (parameters == null) {
            return;
        }
        for (int i = 0; i < parameters.size(); ++i) {
            Object obj = parameters.elementAt(i);
            if (obj == null || !(obj instanceof String)) continue;
            parameters.set(i, JAIRMIUtil.replaceStringWithImage((String)obj, nodes));
        }
    }
}

