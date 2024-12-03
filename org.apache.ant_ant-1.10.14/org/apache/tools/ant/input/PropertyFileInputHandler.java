/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;

public class PropertyFileInputHandler
implements InputHandler {
    private Properties props = null;
    public static final String FILE_NAME_KEY = "ant.input.properties";

    @Override
    public void handleInput(InputRequest request) throws BuildException {
        this.readProps();
        Object o = this.props.get(request.getPrompt());
        if (o == null) {
            throw new BuildException("Unable to find input for '" + request.getPrompt() + "'");
        }
        request.setInput(o.toString());
        if (!request.isInputValid()) {
            throw new BuildException("Found invalid input " + o + " for '" + request.getPrompt() + "'");
        }
    }

    private synchronized void readProps() throws BuildException {
        if (this.props == null) {
            String propsFile = System.getProperty(FILE_NAME_KEY);
            if (propsFile == null) {
                throw new BuildException("System property ant.input.properties for PropertyFileInputHandler not set");
            }
            this.props = new Properties();
            try {
                this.props.load(Files.newInputStream(Paths.get(propsFile, new String[0]), new OpenOption[0]));
            }
            catch (IOException e) {
                throw new BuildException("Couldn't load " + propsFile, e);
            }
        }
    }
}

