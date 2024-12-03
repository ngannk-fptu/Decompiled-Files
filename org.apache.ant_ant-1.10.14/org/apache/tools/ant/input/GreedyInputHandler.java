/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.util.FileUtils;

public class GreedyInputHandler
extends DefaultInputHandler {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleInput(InputRequest request) throws BuildException {
        String prompt = this.getPrompt(request);
        InputStream in = null;
        try {
            in = this.getInputStream();
            System.err.println(prompt);
            System.err.flush();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamPumper p = new StreamPumper(in, baos);
            Thread t = new Thread(p);
            t.start();
            try {
                t.join();
            }
            catch (InterruptedException e) {
                try {
                    t.join();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            request.setInput(new String(baos.toByteArray()));
            if (!request.isInputValid()) {
                throw new BuildException("Received invalid console input");
            }
            if (p.getException() != null) {
                throw new BuildException("Failed to read input from console", p.getException());
            }
        }
        finally {
            FileUtils.close(in);
        }
    }
}

