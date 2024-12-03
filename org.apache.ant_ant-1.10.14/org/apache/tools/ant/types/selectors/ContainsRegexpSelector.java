/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class ContainsRegexpSelector
extends BaseExtendSelector
implements ResourceSelector {
    public static final String EXPRESSION_KEY = "expression";
    private static final String CS_KEY = "casesensitive";
    private static final String ML_KEY = "multiline";
    private static final String SL_KEY = "singleline";
    private String userProvidedExpression = null;
    private RegularExpression myRegExp = null;
    private Regexp myExpression = null;
    private boolean caseSensitive = true;
    private boolean multiLine = false;
    private boolean singleLine = false;

    @Override
    public String toString() {
        return String.format("{containsregexpselector expression: %s}", this.userProvidedExpression);
    }

    public void setExpression(String theexpression) {
        this.userProvidedExpression = theexpression;
    }

    public void setCaseSensitive(boolean b) {
        this.caseSensitive = b;
    }

    public void setMultiLine(boolean b) {
        this.multiLine = b;
    }

    public void setSingleLine(boolean b) {
        this.singleLine = b;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (EXPRESSION_KEY.equalsIgnoreCase(paramname)) {
                    this.setExpression(parameter.getValue());
                    continue;
                }
                if (CS_KEY.equalsIgnoreCase(paramname)) {
                    this.setCaseSensitive(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (ML_KEY.equalsIgnoreCase(paramname)) {
                    this.setMultiLine(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (SL_KEY.equalsIgnoreCase(paramname)) {
                    this.setSingleLine(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.userProvidedExpression == null) {
            this.setError("The expression attribute is required");
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
        if (r.isDirectory()) {
            return true;
        }
        if (this.myRegExp == null) {
            this.myRegExp = new RegularExpression();
            this.myRegExp.setPattern(this.userProvidedExpression);
            this.myExpression = this.myRegExp.getRegexp(this.getProject());
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(r.getInputStream()));
            try {
                String teststr = in.readLine();
                while (teststr != null) {
                    if (this.myExpression.matches(teststr, RegexpUtil.asOptions(this.caseSensitive, this.multiLine, this.singleLine))) {
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

