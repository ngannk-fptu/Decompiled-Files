/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.MapFactory;

public class VelocimacroManager {
    private static String GLOBAL_NAMESPACE = "";
    private boolean registerFromLib = false;
    private final Map namespaceHash = MapFactory.create(17, 0.5f, 20, false);
    private final Map globalNamespace;
    private final Set libraries = Collections.synchronizedSet(new HashSet());
    private RuntimeServices rsvc = null;
    private boolean namespacesOn = true;
    private boolean inlineLocalMode = false;
    private boolean inlineReplacesGlobal = false;

    VelocimacroManager(RuntimeServices rsvc) {
        this.globalNamespace = this.addNamespace(GLOBAL_NAMESPACE);
        this.rsvc = rsvc;
    }

    public boolean addVM(String vmName, Node macroBody, String[] argArray, String namespace, boolean canReplaceGlobalMacro) {
        if (macroBody == null) {
            throw new VelocityException("Null AST for " + vmName + " in " + namespace);
        }
        MacroEntry me = new MacroEntry(vmName, macroBody, argArray, namespace, this.rsvc);
        me.setFromLibrary(this.registerFromLib);
        boolean isLib = true;
        MacroEntry exist = (MacroEntry)this.globalNamespace.get(vmName);
        if (this.registerFromLib) {
            this.libraries.add(namespace);
        } else {
            isLib = this.libraries.contains(namespace);
        }
        if (!isLib && this.usingNamespaces(namespace)) {
            Map local = this.getNamespace(namespace, true);
            local.put(vmName, me);
            return true;
        }
        if (exist != null) {
            me.setFromLibrary(exist.getFromLibrary());
        }
        this.globalNamespace.put(vmName, me);
        return true;
    }

    public VelocimacroProxy get(String vmName, String namespace) {
        return this.get(vmName, namespace, null);
    }

    public VelocimacroProxy get(String vmName, String namespace, String renderingTemplate) {
        MacroEntry me;
        Map local;
        if (this.inlineReplacesGlobal && renderingTemplate != null && (local = this.getNamespace(renderingTemplate, false)) != null && (me = (MacroEntry)local.get(vmName)) != null) {
            return me.getProxy(namespace);
        }
        if (this.usingNamespaces(namespace) && (local = this.getNamespace(namespace, false)) != null && (me = (MacroEntry)local.get(vmName)) != null) {
            return me.getProxy(namespace);
        }
        MacroEntry me2 = (MacroEntry)this.globalNamespace.get(vmName);
        if (me2 != null) {
            return me2.getProxy(namespace);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean dumpNamespace(String namespace) {
        if (this.usingNamespaces(namespace)) {
            VelocimacroManager velocimacroManager = this;
            synchronized (velocimacroManager) {
                Map h = (Map)this.namespaceHash.remove(namespace);
                if (h == null) {
                    return false;
                }
                h.clear();
                return true;
            }
        }
        return false;
    }

    public void setNamespaceUsage(boolean namespaceOn) {
        this.namespacesOn = namespaceOn;
    }

    public void setRegisterFromLib(boolean registerFromLib) {
        this.registerFromLib = registerFromLib;
    }

    public void setTemplateLocalInlineVM(boolean inlineLocalMode) {
        this.inlineLocalMode = inlineLocalMode;
    }

    private Map getNamespace(String namespace, boolean addIfNew) {
        Map h = (Map)this.namespaceHash.get(namespace);
        if (h == null && addIfNew) {
            h = this.addNamespace(namespace);
        }
        return h;
    }

    private Map addNamespace(String namespace) {
        Map h = MapFactory.create(17, 0.5f, 20, false);
        Map oh = this.namespaceHash.put(namespace, h);
        if (oh != null) {
            this.namespaceHash.put(namespace, oh);
            return null;
        }
        return h;
    }

    private boolean usingNamespaces(String namespace) {
        if (!this.namespacesOn) {
            return false;
        }
        return this.inlineLocalMode;
    }

    public String getLibraryName(String vmName, String namespace) {
        MacroEntry me;
        Map local;
        if (this.usingNamespaces(namespace) && (local = this.getNamespace(namespace, false)) != null && (me = (MacroEntry)local.get(vmName)) != null) {
            return null;
        }
        MacroEntry me2 = (MacroEntry)this.globalNamespace.get(vmName);
        if (me2 != null) {
            return me2.getSourceTemplate();
        }
        return null;
    }

    public void setInlineReplacesGlobal(boolean is) {
        this.inlineReplacesGlobal = is;
    }

    private static class MacroEntry {
        private final String vmName;
        private final String[] argArray;
        private final String sourceTemplate;
        private SimpleNode nodeTree = null;
        private boolean fromLibrary = false;
        private VelocimacroProxy vp;

        private MacroEntry(String vmName, Node macro, String[] argArray, String sourceTemplate, RuntimeServices rsvc) {
            this.vmName = vmName;
            this.argArray = argArray;
            this.nodeTree = (SimpleNode)macro;
            this.sourceTemplate = sourceTemplate;
            this.vp = new VelocimacroProxy();
            this.vp.setName(this.vmName);
            this.vp.setArgArray(this.argArray);
            this.vp.setNodeTree(this.nodeTree);
            this.vp.setLocation(macro.getLine(), macro.getColumn(), macro.getTemplateName());
            this.vp.init(rsvc);
        }

        public void setFromLibrary(boolean fromLibrary) {
            this.fromLibrary = fromLibrary;
        }

        public boolean getFromLibrary() {
            return this.fromLibrary;
        }

        public SimpleNode getNodeTree() {
            return this.nodeTree;
        }

        public String getSourceTemplate() {
            return this.sourceTemplate;
        }

        VelocimacroProxy getProxy(String namespace) {
            return this.vp;
        }
    }
}

