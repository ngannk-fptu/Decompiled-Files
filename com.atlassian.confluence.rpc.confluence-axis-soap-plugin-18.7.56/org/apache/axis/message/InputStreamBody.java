/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.IOException;
import java.io.InputStream;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.IOUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class InputStreamBody
extends SOAPBodyElement {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$InputStreamBody == null ? (class$org$apache$axis$message$InputStreamBody = InputStreamBody.class$("org.apache.axis.message.InputStreamBody")) : class$org$apache$axis$message$InputStreamBody).getName());
    protected InputStream inputStream;
    static /* synthetic */ Class class$org$apache$axis$message$InputStreamBody;

    public InputStreamBody(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void outputImpl(SerializationContext context) throws IOException {
        try {
            byte[] buf = new byte[this.inputStream.available()];
            IOUtils.readFully(this.inputStream, buf);
            String contents = new String(buf);
            context.writeString(contents);
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
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

