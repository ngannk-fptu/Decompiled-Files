/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;

public class ContainsSelector
extends BaseExtendSelector
implements ResourceSelector {
    public static final String EXPRESSION_KEY = "expression";
    public static final String CONTAINS_KEY = "text";
    public static final String CASE_KEY = "casesensitive";
    public static final String WHITESPACE_KEY = "ignorewhitespace";
    private String contains = null;
    private boolean casesensitive = true;
    private boolean ignorewhitespace = false;
    private String encoding = null;

    @Override
    public String toString() {
        return String.format("{containsselector text: \"%s\" casesensitive: %s ignorewhitespace: %s}", this.contains, this.casesensitive, this.ignorewhitespace);
    }

    public void setText(String contains) {
        this.contains = contains;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setCasesensitive(boolean casesensitive) {
        this.casesensitive = casesensitive;
    }

    public void setIgnorewhitespace(boolean ignorewhitespace) {
        this.ignorewhitespace = ignorewhitespace;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (CONTAINS_KEY.equalsIgnoreCase(paramname)) {
                    this.setText(parameter.getValue());
                    continue;
                }
                if (CASE_KEY.equalsIgnoreCase(paramname)) {
                    this.setCasesensitive(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (WHITESPACE_KEY.equalsIgnoreCase(paramname)) {
                    this.setIgnorewhitespace(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.contains == null) {
            this.setError("The text attribute is required");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        return this.isSelected(new FileResource(file));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean isSelected(Resource r) {
        this.validate();
        if (r.isDirectory()) return true;
        if (this.contains.isEmpty()) {
            return true;
        }
        String userstr = this.contains;
        if (!this.casesensitive) {
            userstr = this.contains.toLowerCase();
        }
        if (this.ignorewhitespace) {
            userstr = SelectorUtils.removeWhitespace(userstr);
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(r.getInputStream(), this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding)));
            try {
                String teststr = in.readLine();
                while (teststr != null) {
                    if (!this.casesensitive) {
                        teststr = teststr.toLowerCase();
                    }
                    if (this.ignorewhitespace) {
                        teststr = SelectorUtils.removeWhitespace(teststr);
                    }
                    if (teststr.contains(userstr)) {
                        boolean bl = true;
                        return bl;
                    }
                    teststr = in.readLine();
                }
                boolean bl = false;
                return bl;
            }
            catch (IOException ioe) {
                throw new BuildException("Could not read " + r.toLongString());
            }
            finally {
                try {
                    in.close();
                }
                catch (Throwable throwable) {
                    Throwable throwable2;
                    throwable2.addSuppressed(throwable);
                }
            }
        }
        catch (IOException e) {
            throw new BuildException("Could not get InputStream from " + r.toLongString(), e);
        }
    }
}

