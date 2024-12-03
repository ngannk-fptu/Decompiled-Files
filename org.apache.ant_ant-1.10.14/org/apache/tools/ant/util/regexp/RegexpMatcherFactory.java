/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.regexp.Jdk14RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcher;

public class RegexpMatcherFactory {
    public RegexpMatcher newRegexpMatcher() throws BuildException {
        return this.newRegexpMatcher(null);
    }

    public RegexpMatcher newRegexpMatcher(Project p) throws BuildException {
        String systemDefault = p == null ? System.getProperty("ant.regexp.regexpimpl") : p.getProperty("ant.regexp.regexpimpl");
        if (systemDefault != null) {
            return this.createInstance(systemDefault);
        }
        return new Jdk14RegexpMatcher();
    }

    protected RegexpMatcher createInstance(String className) throws BuildException {
        return ClasspathUtils.newInstance(className, RegexpMatcherFactory.class.getClassLoader(), RegexpMatcher.class);
    }

    protected void testAvailability(String className) throws BuildException {
        try {
            Class.forName(className);
        }
        catch (Throwable t) {
            throw new BuildException(t);
        }
    }

    public static boolean regexpMatcherPresent(Project project) {
        try {
            new RegexpMatcherFactory().newRegexpMatcher(project);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}

