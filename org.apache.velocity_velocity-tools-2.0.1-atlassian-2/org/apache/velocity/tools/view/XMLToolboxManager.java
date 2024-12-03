/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.RuleSet
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.velocity.tools.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.view.ToolInfo;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.ToolboxRuleSet;

@Deprecated
public class XMLToolboxManager
implements ToolboxManager {
    protected static final Log LOG = LogFactory.getLog(XMLToolboxManager.class);
    private List toolinfo = new ArrayList();
    private Map data = new HashMap();
    private static RuleSet ruleSet = new ToolboxRuleSet();

    public XMLToolboxManager() {
        LOG.warn((Object)"XMLToolboxManager has been deprecated. Please use org.apache.velocity.tools.ToolboxFactory instead.");
    }

    @Override
    public void addTool(ToolInfo info) {
        if (this.validateToolInfo(info)) {
            this.toolinfo.add(info);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Added " + info.getClassname() + " to the toolbox as " + info.getKey()));
            }
        }
    }

    @Override
    public void addData(ToolInfo info) {
        if (this.validateToolInfo(info)) {
            this.data.put(info.getKey(), info.getInstance(null));
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Added '" + info.getInstance(null) + "' to the toolbox as " + info.getKey()));
            }
        }
    }

    protected boolean validateToolInfo(ToolInfo info) {
        if (info == null) {
            LOG.error((Object)"ToolInfo is null!");
            return false;
        }
        if (info.getKey() == null || info.getKey().length() == 0) {
            LOG.error((Object)"Tool has no key defined!");
            return false;
        }
        if (info.getClassname() == null) {
            LOG.error((Object)("Tool " + info.getKey() + " has no Class definition!"));
            return false;
        }
        return true;
    }

    @Override
    public Map getToolbox(Object initData) {
        HashMap<String, Object> toolbox = new HashMap<String, Object>(this.data);
        for (ToolInfo info : this.toolinfo) {
            toolbox.put(info.getKey(), info.getInstance(initData));
        }
        return toolbox;
    }

    public void load(String path) throws Exception {
        if (path == null) {
            throw new IllegalArgumentException("Path value cannot be null");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Could not find toolbox config file at: " + path);
        }
        this.load(new FileInputStream(file));
    }

    public void load(InputStream input) throws Exception {
        LOG.trace((Object)"Loading toolbox...");
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setUseContextClassLoader(true);
        digester.push((Object)this);
        digester.addRuleSet(this.getRuleSet());
        digester.parse(input);
        LOG.trace((Object)"Toolbox loaded.");
    }

    protected RuleSet getRuleSet() {
        return ruleSet;
    }
}

