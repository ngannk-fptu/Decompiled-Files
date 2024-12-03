/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;

public class DepthSelector
extends BaseExtendSelector {
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    public int min = -1;
    public int max = -1;

    @Override
    public String toString() {
        return "{depthselector min: " + this.min + " max: " + this.max + "}";
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (MIN_KEY.equalsIgnoreCase(paramname)) {
                    try {
                        this.setMin(Integer.parseInt(parameter.getValue()));
                    }
                    catch (NumberFormatException nfe1) {
                        this.setError("Invalid minimum value " + parameter.getValue());
                    }
                    continue;
                }
                if (MAX_KEY.equalsIgnoreCase(paramname)) {
                    try {
                        this.setMax(Integer.parseInt(parameter.getValue()));
                    }
                    catch (NumberFormatException nfe1) {
                        this.setError("Invalid maximum value " + parameter.getValue());
                    }
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.min < 0 && this.max < 0) {
            this.setError("You must set at least one of the min or the max levels.");
        }
        if (this.max < this.min && this.max > -1) {
            this.setError("The maximum depth is lower than the minimum.");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        int depth = -1;
        String absBase = basedir.getAbsolutePath();
        String absFile = file.getAbsolutePath();
        StringTokenizer tokBase = new StringTokenizer(absBase, File.separator);
        StringTokenizer tokFile = new StringTokenizer(absFile, File.separator);
        while (tokFile.hasMoreTokens()) {
            String filetoken = tokFile.nextToken();
            if (tokBase.hasMoreTokens()) {
                String basetoken = tokBase.nextToken();
                if (basetoken.equals(filetoken)) continue;
                throw new BuildException("File %s does not appear within %s directory", filename, absBase);
            }
            if (this.max <= -1 || ++depth <= this.max) continue;
            return false;
        }
        if (tokBase.hasMoreTokens()) {
            throw new BuildException("File %s is outside of %s directory tree", filename, absBase);
        }
        return this.min <= -1 || depth >= this.min;
    }
}

