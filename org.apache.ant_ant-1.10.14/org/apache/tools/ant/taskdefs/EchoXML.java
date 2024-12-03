/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.XMLFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EchoXML
extends XMLFragment {
    private File file;
    private boolean append;
    private NamespacePolicy namespacePolicy = NamespacePolicy.DEFAULT;
    private static final String ERROR_NO_XML = "No nested XML specified";

    public void setFile(File f) {
        this.file = f;
    }

    public void setNamespacePolicy(NamespacePolicy n) {
        this.namespacePolicy = n;
    }

    public void setAppend(boolean b) {
        this.append = b;
    }

    public void execute() {
        Node n = this.getFragment().getFirstChild();
        if (n == null) {
            throw new BuildException(ERROR_NO_XML);
        }
        DOMElementWriter writer = new DOMElementWriter(!this.append, this.namespacePolicy.getPolicy());
        try (OutputStream os = this.file == null ? new LogOutputStream(this, 2) : FileUtils.newOutputStream(this.file.toPath(), this.append);){
            writer.write((Element)n, os);
        }
        catch (BuildException be) {
            throw be;
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public static class NamespacePolicy
    extends EnumeratedAttribute {
        private static final String IGNORE = "ignore";
        private static final String ELEMENTS = "elementsOnly";
        private static final String ALL = "all";
        public static final NamespacePolicy DEFAULT = new NamespacePolicy("ignore");

        public NamespacePolicy() {
        }

        public NamespacePolicy(String s) {
            this.setValue(s);
        }

        @Override
        public String[] getValues() {
            return new String[]{IGNORE, ELEMENTS, ALL};
        }

        public DOMElementWriter.XmlNamespacePolicy getPolicy() {
            String s = this.getValue();
            if (IGNORE.equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.IGNORE;
            }
            if (ELEMENTS.equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.ONLY_QUALIFY_ELEMENTS;
            }
            if (ALL.equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.QUALIFY_ALL;
            }
            throw new BuildException("Invalid namespace policy: " + s);
        }
    }
}

