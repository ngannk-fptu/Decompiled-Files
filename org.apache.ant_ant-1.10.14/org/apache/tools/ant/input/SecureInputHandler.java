/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputRequest;

public class SecureInputHandler
extends DefaultInputHandler {
    @Override
    public void handleInput(InputRequest request) throws BuildException {
        String prompt = this.getPrompt(request);
        do {
            char[] input;
            if ((input = System.console().readPassword(prompt, new Object[0])) == null) {
                throw new BuildException("unexpected end of stream while reading input");
            }
            request.setInput(new String(input));
            Arrays.fill(input, ' ');
        } while (!request.isInputValid());
    }
}

