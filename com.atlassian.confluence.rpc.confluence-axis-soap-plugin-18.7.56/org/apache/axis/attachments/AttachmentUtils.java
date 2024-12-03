/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axis.attachments;

import javax.activation.DataHandler;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.utils.Messages;

public class AttachmentUtils {
    static /* synthetic */ Class class$org$apache$axis$attachments$AttachmentPart;

    private AttachmentUtils() {
    }

    public static DataHandler getActivationDataHandler(Part part) throws AxisFault {
        if (null == part) {
            throw new AxisFault(Messages.getMessage("gotNullPart"));
        }
        if (!(part instanceof AttachmentPart)) {
            throw new AxisFault(Messages.getMessage("unsupportedAttach", part.getClass().getName(), (class$org$apache$axis$attachments$AttachmentPart == null ? (class$org$apache$axis$attachments$AttachmentPart = AttachmentUtils.class$("org.apache.axis.attachments.AttachmentPart")) : class$org$apache$axis$attachments$AttachmentPart).getName()));
        }
        return ((AttachmentPart)part).getActivationDataHandler();
    }

    public static boolean isAttachment(Object value) {
        if (null == value) {
            return false;
        }
        return value instanceof DataHandler;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

