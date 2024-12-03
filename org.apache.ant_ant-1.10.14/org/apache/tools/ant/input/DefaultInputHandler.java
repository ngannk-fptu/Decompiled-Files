/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.apache.tools.ant.input.MultipleChoiceInputRequest;
import org.apache.tools.ant.util.KeepAliveInputStream;

public class DefaultInputHandler
implements InputHandler {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleInput(InputRequest request) throws BuildException {
        String prompt = this.getPrompt(request);
        BufferedReader r = null;
        boolean success = false;
        try {
            r = new BufferedReader(new InputStreamReader(this.getInputStream()));
            do {
                System.err.println(prompt);
                System.err.flush();
                try {
                    String input = r.readLine();
                    if (input == null) {
                        throw new BuildException("unexpected end of stream while reading input");
                    }
                    request.setInput(input);
                }
                catch (IOException e) {
                    throw new BuildException("Failed to read input from Console.", e);
                }
            } while (!request.isInputValid());
            success = true;
        }
        finally {
            block13: {
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (IOException e) {
                        if (!success) break block13;
                        throw new BuildException("Failed to close input.", e);
                    }
                }
            }
        }
    }

    protected String getPrompt(InputRequest request) {
        String prompt = request.getPrompt();
        String def = request.getDefaultValue();
        if (request instanceof MultipleChoiceInputRequest) {
            StringBuilder sb = new StringBuilder(prompt).append(" (");
            boolean first = true;
            for (String next : ((MultipleChoiceInputRequest)request).getChoices()) {
                if (!first) {
                    sb.append(", ");
                }
                if (next.equals(def)) {
                    sb.append('[');
                }
                sb.append(next);
                if (next.equals(def)) {
                    sb.append(']');
                }
                first = false;
            }
            sb.append(")");
            return sb.toString();
        }
        if (def != null) {
            return prompt + " [" + def + "]";
        }
        return prompt;
    }

    protected InputStream getInputStream() {
        return KeepAliveInputStream.wrapSystemIn();
    }
}

