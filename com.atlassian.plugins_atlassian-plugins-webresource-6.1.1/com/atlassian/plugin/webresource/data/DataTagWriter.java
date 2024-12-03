/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.html.encode.JavascriptEncoder
 *  com.atlassian.html.encode.JavascriptEncodingWriter
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.data;

import com.atlassian.html.encode.JavascriptEncoder;
import com.atlassian.html.encode.JavascriptEncodingWriter;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.PluginDataResource;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTagWriter {
    private static final Logger log = LoggerFactory.getLogger(DataTagWriter.class);
    private static final String PRE = "<script>\nwindow.WRM=window.WRM||{};window.WRM._unparsedData=window.WRM._unparsedData||{};window.WRM._unparsedErrors=window.WRM._unparsedErrors||{};\n";
    private static final String POST = "if(window.WRM._dataArrived)window.WRM._dataArrived();</script>\n";

    public void write(Writer writer, Iterable<PluginDataResource> data) throws IOException {
        boolean scriptTagWritten = false;
        for (PluginDataResource datum : data) {
            if (!scriptTagWritten) {
                writer.write(PRE);
                scriptTagWritten = true;
            }
            this.write(writer, datum);
        }
        if (scriptTagWritten) {
            writer.write(POST);
        }
    }

    private void write(Writer writer, PluginDataResource data) throws IOException {
        try {
            Optional result = data.getData();
            if (result.isPresent()) {
                writer.write("WRM._unparsedData[\"");
                JavascriptEncoder.escape((Writer)writer, (String)data.getKey());
                writer.write("\"]=\"");
                ((Jsonable)result.get()).write((Writer)new JavascriptEncodingWriter(writer));
                writer.write("\";\n");
            } else {
                writer.write("WRM._unparsedErrors[\"");
                JavascriptEncoder.escape((Writer)writer, (String)data.getKey());
                writer.write("\"]=\"\";\n");
            }
        }
        catch (IOException ex) {
            log.error("IOException encountered rendering data resource '{}'", (Object)new String[]{data.getKey()}, (Object)ex);
        }
        catch (RuntimeException ex) {
            log.error("Exception encountered rendering data resource '{}'", (Object)new String[]{data.getKey()}, (Object)ex);
        }
    }
}

