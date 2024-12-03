/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package org.apache.velocity.runtime;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.VelocimacroManager;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Macro;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.log.LogDisplayWrapper;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class VelocimacroFactory {
    private final RuntimeServices rsvc;
    private final LogDisplayWrapper log;
    private VelocimacroManager vmManager = null;
    private boolean replaceAllowed = false;
    private boolean addNewAllowed = true;
    private boolean templateLocal = false;
    private boolean autoReloadLibrary = false;
    private List macroLibVec = null;
    private Map libModMap;

    public VelocimacroFactory(RuntimeServices rsvc) {
        this.rsvc = rsvc;
        this.log = new LogDisplayWrapper(rsvc.getLog(), "Velocimacro : ", rsvc.getBoolean("velocimacro.messages.on", true));
        this.libModMap = new HashMap();
        this.vmManager = new VelocimacroManager(rsvc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initVelocimacro() {
        VelocimacroFactory velocimacroFactory = this;
        synchronized (velocimacroFactory) {
            this.log.trace("initialization starting.");
            this.setReplacementPermission(true);
            this.vmManager.setNamespaceUsage(false);
            Object libfiles = this.rsvc.getProperty("velocimacro.library");
            if (libfiles == null) {
                this.log.debug("\"velocimacro.library\" is not set.  Trying default library: VM_global_library.vm");
                if (this.rsvc.getLoaderNameForResource("VM_global_library.vm") != null) {
                    libfiles = "VM_global_library.vm";
                } else {
                    this.log.debug("Default library not found.");
                }
            }
            if (libfiles != null) {
                this.macroLibVec = new ArrayList();
                if (libfiles instanceof Vector) {
                    this.macroLibVec.addAll((Vector)libfiles);
                } else if (libfiles instanceof String) {
                    this.macroLibVec.add(libfiles);
                }
                int is = this.macroLibVec.size();
                for (int i = 0; i < is; ++i) {
                    String lib = (String)this.macroLibVec.get(i);
                    if (!StringUtils.isNotEmpty((String)lib)) continue;
                    this.vmManager.setRegisterFromLib(true);
                    this.log.debug("adding VMs from VM library : " + lib);
                    try {
                        Template template = this.rsvc.getTemplate(lib);
                        Twonk twonk = new Twonk();
                        twonk.template = template;
                        twonk.modificationTime = template.getLastModified();
                        this.libModMap.put(lib, twonk);
                    }
                    catch (Exception e) {
                        String msg = "Velocimacro : Error using VM library : " + lib;
                        this.log.error(true, msg, e);
                        throw new VelocityException(msg, e);
                    }
                    this.log.trace("VM library registration complete.");
                    this.vmManager.setRegisterFromLib(false);
                }
            }
            this.setAddMacroPermission(true);
            if (!this.rsvc.getBoolean("velocimacro.permissions.allow.inline", true)) {
                this.setAddMacroPermission(false);
                this.log.debug("allowInline = false : VMs can NOT be defined inline in templates");
            } else {
                this.log.debug("allowInline = true : VMs can be defined inline in templates");
            }
            this.setReplacementPermission(false);
            if (this.rsvc.getBoolean("velocimacro.permissions.allow.inline.to.replace.global", false)) {
                this.setReplacementPermission(true);
                this.log.debug("allowInlineToOverride = true : VMs defined inline may replace previous VM definitions");
            } else {
                this.log.debug("allowInlineToOverride = false : VMs defined inline may NOT replace previous VM definitions");
            }
            this.vmManager.setNamespaceUsage(true);
            this.setTemplateLocalInline(this.rsvc.getBoolean("velocimacro.permissions.allow.inline.local.scope", false));
            if (this.getTemplateLocalInline()) {
                this.log.debug("allowInlineLocal = true : VMs defined inline will be local to their defining template only.");
            } else {
                this.log.debug("allowInlineLocal = false : VMs defined inline will be global in scope if allowed.");
            }
            this.vmManager.setTemplateLocalInlineVM(this.getTemplateLocalInline());
            this.setAutoload(this.rsvc.getBoolean("velocimacro.library.autoreload", false));
            if (this.getAutoload()) {
                this.log.debug("autoload on : VM system will automatically reload global library macros");
            } else {
                this.log.debug("autoload off : VM system will not automatically reload global library macros");
            }
            this.log.trace("Velocimacro : initialization complete.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addVelocimacro(String name, String macroBody, String[] argArray, String sourceTemplate) {
        if (name == null || macroBody == null || argArray == null || sourceTemplate == null) {
            String msg = "VM '" + name + "' addition rejected : ";
            msg = name == null ? msg + "name" : (macroBody == null ? msg + "macroBody" : (argArray == null ? msg + "argArray" : msg + "sourceTemplate"));
            msg = msg + " argument was null";
            this.log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!this.canAddVelocimacro(name, sourceTemplate)) {
            return false;
        }
        Object msg = this;
        synchronized (msg) {
            try {
                SimpleNode macroRootNode = this.rsvc.parse(new StringReader(macroBody), sourceTemplate);
                this.vmManager.addVM(name, macroRootNode, argArray, sourceTemplate, this.replaceAllowed);
            }
            catch (ParseException ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        if (this.log.isDebugEnabled()) {
            msg = new StringBuffer("added ");
            Macro.macroToString((StringBuffer)msg, argArray);
            ((StringBuffer)msg).append(" : source = ").append(sourceTemplate);
            this.log.debug(((StringBuffer)msg).toString());
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addVelocimacro(String name, Node macroBody, String[] argArray, String sourceTemplate) {
        if (name == null || macroBody == null || argArray == null || sourceTemplate == null) {
            String msg = "VM '" + name + "' addition rejected : ";
            msg = name == null ? msg + "name" : (macroBody == null ? msg + "macroBody" : (argArray == null ? msg + "argArray" : msg + "sourceTemplate"));
            msg = msg + " argument was null";
            this.log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!this.canAddVelocimacro(name, sourceTemplate)) {
            return false;
        }
        VelocimacroFactory velocimacroFactory = this;
        synchronized (velocimacroFactory) {
            this.vmManager.addVM(name, macroBody, argArray, sourceTemplate, this.replaceAllowed);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("added VM " + name + ": source=" + sourceTemplate);
        }
        return true;
    }

    private synchronized boolean canAddVelocimacro(String name, String sourceTemplate) {
        if (this.autoReloadLibrary && this.macroLibVec != null && this.macroLibVec.contains(sourceTemplate)) {
            return true;
        }
        if (!this.addNewAllowed) {
            this.log.warn("VM addition rejected : " + name + " : inline VMs not allowed.");
            return false;
        }
        if (!this.templateLocal && !this.replaceAllowed && this.isVelocimacro(name, sourceTemplate)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VM addition rejected : " + name + " : inline not allowed to replace existing VM");
            }
            return false;
        }
        return true;
    }

    public boolean isVelocimacro(String vm, String sourceTemplate) {
        return this.vmManager.get(vm, sourceTemplate) != null;
    }

    public Directive getVelocimacro(String vmName, String sourceTemplate) {
        return this.getVelocimacro(vmName, sourceTemplate, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Directive getVelocimacro(String vmName, String sourceTemplate, String renderingTemplate) {
        VelocimacroProxy vp = null;
        vp = this.vmManager.get(vmName, sourceTemplate, renderingTemplate);
        if (vp != null && this.autoReloadLibrary) {
            VelocimacroFactory velocimacroFactory = this;
            synchronized (velocimacroFactory) {
                String lib = this.vmManager.getLibraryName(vmName, sourceTemplate);
                if (lib != null) {
                    try {
                        Twonk tw = (Twonk)this.libModMap.get(lib);
                        if (tw != null) {
                            Template template = tw.template;
                            long tt = tw.modificationTime;
                            long ft = template.getResourceLoader().getLastModified(template);
                            if (ft > tt) {
                                this.log.debug("auto-reloading VMs from VM library : " + lib);
                                tw.modificationTime = ft;
                                tw.template = template = this.rsvc.getTemplate(lib);
                                tw.modificationTime = template.getLastModified();
                            }
                        }
                    }
                    catch (Exception e) {
                        String msg = "Velocimacro : Error using VM library : " + lib;
                        this.log.error(true, msg, e);
                        throw new VelocityException(msg, e);
                    }
                    vp = this.vmManager.get(vmName, sourceTemplate, renderingTemplate);
                }
            }
        }
        return vp;
    }

    public boolean dumpVMNamespace(String namespace) {
        return this.vmManager.dumpNamespace(namespace);
    }

    private void setTemplateLocalInline(boolean b) {
        this.templateLocal = b;
    }

    private boolean getTemplateLocalInline() {
        return this.templateLocal;
    }

    private boolean setAddMacroPermission(boolean addNewAllowed) {
        boolean b = this.addNewAllowed;
        this.addNewAllowed = addNewAllowed;
        return b;
    }

    private boolean setReplacementPermission(boolean arg) {
        boolean b = this.replaceAllowed;
        this.replaceAllowed = arg;
        this.vmManager.setInlineReplacesGlobal(arg);
        return b;
    }

    private void setAutoload(boolean b) {
        this.autoReloadLibrary = b;
    }

    private boolean getAutoload() {
        return this.autoReloadLibrary;
    }

    private static class Twonk {
        public Template template;
        public long modificationTime;

        private Twonk() {
        }
    }
}

