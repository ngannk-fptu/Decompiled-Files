/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.net.URL;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;

public class CloseResources
extends Task {
    private Union resources = new Union();

    public void add(ResourceCollection rc) {
        this.resources.add(rc);
    }

    @Override
    public void execute() {
        for (Resource r : this.resources) {
            URLProvider up = r.as(URLProvider.class);
            if (up == null) continue;
            URL u = up.getURL();
            try {
                FileUtils.close(u.openConnection());
            }
            catch (IOException iOException) {}
        }
    }
}

