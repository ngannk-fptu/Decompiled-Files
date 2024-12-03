/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.regexp.Jdk14RegexpRegexp;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;

public class RegexpFactory
extends RegexpMatcherFactory {
    public Regexp newRegexp() throws BuildException {
        return this.newRegexp(null);
    }

    public Regexp newRegexp(Project p) throws BuildException {
        String systemDefault = p == null ? System.getProperty("ant.regexp.regexpimpl") : p.getProperty("ant.regexp.regexpimpl");
        if (systemDefault != null) {
            return this.createRegexpInstance(systemDefault);
        }
        return new Jdk14RegexpRegexp();
    }

    protected Regexp createRegexpInstance(String classname) throws BuildException {
        return ClasspathUtils.newInstance(classname, RegexpFactory.class.getClassLoader(), Regexp.class);
    }
}

