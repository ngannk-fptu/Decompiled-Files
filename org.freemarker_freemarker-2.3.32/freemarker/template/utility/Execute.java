/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Execute
implements TemplateMethodModel {
    private static final int OUTPUT_BUFFER_SIZE = 1024;

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        StringBuilder aOutputBuffer = new StringBuilder();
        if (arguments.size() < 1) {
            throw new TemplateModelException("Need an argument to execute");
        }
        String aExecute = (String)arguments.get(0);
        try {
            Process exec = Runtime.getRuntime().exec(aExecute);
            try (InputStream execOut = exec.getInputStream();){
                InputStreamReader execReader = new InputStreamReader(execOut);
                char[] buffer = new char[1024];
                int bytes_read = execReader.read(buffer);
                while (bytes_read > 0) {
                    aOutputBuffer.append(buffer, 0, bytes_read);
                    bytes_read = execReader.read(buffer);
                }
            }
        }
        catch (IOException ioe) {
            throw new TemplateModelException(ioe.getMessage());
        }
        return aOutputBuffer.toString();
    }
}

