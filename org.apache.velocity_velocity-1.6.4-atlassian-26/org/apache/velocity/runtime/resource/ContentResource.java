/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.Resource;

public class ContentResource
extends Resource {
    public ContentResource() {
        this.setType(2);
    }

    @Override
    public boolean process() throws ResourceNotFoundException {
        BufferedReader reader = null;
        try {
            StringWriter sw = new StringWriter();
            reader = new BufferedReader(new InputStreamReader(this.resourceLoader.getResourceStream(this.name), this.encoding));
            char[] buf = new char[1024];
            int len = 0;
            while ((len = reader.read(buf, 0, 1024)) != -1) {
                sw.write(buf, 0, len);
            }
            this.setData(sw.toString());
            boolean bl = true;
            return bl;
        }
        catch (ResourceNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Cannot process content resource";
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception exception) {}
            }
        }
    }
}

