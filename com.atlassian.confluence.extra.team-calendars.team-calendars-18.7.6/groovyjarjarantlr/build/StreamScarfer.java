/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.build;

import groovyjarjarantlr.build.Tool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamScarfer
extends Thread {
    InputStream is;
    String type;
    Tool tool;

    StreamScarfer(InputStream inputStream, String string, Tool tool) {
        this.is = inputStream;
        this.type = string;
        this.tool = tool;
    }

    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(this.is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String string = null;
            while ((string = bufferedReader.readLine()) != null) {
                if (this.type == null || this.type.equals("stdout")) {
                    this.tool.stdout(string);
                    continue;
                }
                this.tool.stderr(string);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
}

