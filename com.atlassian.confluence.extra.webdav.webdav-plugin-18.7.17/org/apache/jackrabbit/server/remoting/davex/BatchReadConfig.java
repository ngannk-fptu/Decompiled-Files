/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BatchReadConfig {
    private static Logger log = LoggerFactory.getLogger(BatchReadConfig.class);
    private static final String NAME_DEFAULT = "default";
    public static final int DEPTH_DEFAULT = 0;
    public static final int DEPTH_INFINITE = -1;
    private int defaultDepth = 0;
    private final Map<String, Integer> depthMap = new HashMap<String, Integer>();

    BatchReadConfig() {
    }

    public void load(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        this.add(props);
    }

    public void add(Properties props) {
        Enumeration<?> en = props.propertyNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement().toString();
            String depthStr = props.getProperty(name);
            try {
                int depth = Integer.parseInt(depthStr);
                if (depth < -1) {
                    log.warn("invalid depth " + depthStr + " -> ignoring.");
                    continue;
                }
                if (NAME_DEFAULT.equals(name)) {
                    this.setDefaultDepth(depth);
                    continue;
                }
                this.setDepth(name, depth);
            }
            catch (NumberFormatException e) {
                log.warn("Invalid depth value for name " + name + ". " + depthStr + " cannot be parsed into an integer.");
            }
        }
    }

    public int getDepth(String ntName) {
        if (this.depthMap.containsKey(ntName)) {
            return this.depthMap.get(ntName);
        }
        return this.defaultDepth;
    }

    public int getDepth(Node node) {
        int depth = this.defaultDepth;
        try {
            String ntName = node.getPrimaryNodeType().getName();
            if (this.depthMap.containsKey(ntName)) {
                depth = this.depthMap.get(ntName);
            }
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
        return depth;
    }

    public void setDepth(String ntName, int depth) {
        if (ntName == null || depth < -1) {
            throw new IllegalArgumentException();
        }
        this.depthMap.put(ntName, depth);
    }

    public int getDefaultDepth() {
        return this.defaultDepth;
    }

    public void setDefaultDepth(int depth) {
        if (depth < -1) {
            throw new IllegalArgumentException();
        }
        this.defaultDepth = depth;
    }
}

