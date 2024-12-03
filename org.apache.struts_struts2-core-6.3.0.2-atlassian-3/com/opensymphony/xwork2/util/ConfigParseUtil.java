/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ConfigParseUtil {
    private ConfigParseUtil() {
    }

    public static Set<String> toClassesSet(String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = TextParseUtil.commaDelimitedStringToSet(newDelimitedClasses);
        ConfigParseUtil.validateClasses(classNames, OgnlUtil.class.getClassLoader());
        return Collections.unmodifiableSet(classNames);
    }

    public static Set<Class<?>> toClassObjectsSet(String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = TextParseUtil.commaDelimitedStringToSet(newDelimitedClasses);
        return Collections.unmodifiableSet(ConfigParseUtil.validateClasses(classNames, OgnlUtil.class.getClassLoader()));
    }

    public static Set<String> toNewClassesSet(Set<String> oldClasses, String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = TextParseUtil.commaDelimitedStringToSet(newDelimitedClasses);
        ConfigParseUtil.validateClasses(classNames, OgnlUtil.class.getClassLoader());
        HashSet<String> excludedClasses = new HashSet<String>(oldClasses);
        excludedClasses.addAll(classNames);
        return Collections.unmodifiableSet(excludedClasses);
    }

    public static Set<Pattern> toNewPatternsSet(Set<Pattern> oldPatterns, String newDelimitedPatterns) throws ConfigurationException {
        Set<String> patterns = TextParseUtil.commaDelimitedStringToSet(newDelimitedPatterns);
        HashSet<Pattern> newPatterns = new HashSet<Pattern>(oldPatterns);
        for (String pattern : patterns) {
            try {
                newPatterns.add(Pattern.compile(pattern));
            }
            catch (PatternSyntaxException e) {
                throw new ConfigurationException("Excluded package name patterns could not be parsed due to invalid regex: " + pattern, e);
            }
        }
        return Collections.unmodifiableSet(newPatterns);
    }

    public static Set<Class<?>> validateClasses(Set<String> classNames, ClassLoader validatingClassLoader) throws ConfigurationException {
        HashSet classes = new HashSet();
        for (String className : classNames) {
            try {
                classes.add(validatingClassLoader.loadClass(className));
            }
            catch (ClassNotFoundException e) {
                throw new ConfigurationException("Cannot load class for exclusion/exemption configuration: " + className, e);
            }
        }
        return classes;
    }

    public static Set<String> toPackageNamesSet(String newDelimitedPackageNames) throws ConfigurationException {
        Set<String> packageNames = TextParseUtil.commaDelimitedStringToSet(newDelimitedPackageNames).stream().map(s -> StringUtils.strip((String)s, (String)".")).collect(Collectors.toSet());
        ConfigParseUtil.validatePackageNames(packageNames);
        return Collections.unmodifiableSet(packageNames);
    }

    public static Set<String> toNewPackageNamesSet(Collection<String> oldPackageNames, String newDelimitedPackageNames) throws ConfigurationException {
        Set<String> packageNames = TextParseUtil.commaDelimitedStringToSet(newDelimitedPackageNames).stream().map(s -> StringUtils.strip((String)s, (String)".")).collect(Collectors.toSet());
        ConfigParseUtil.validatePackageNames(packageNames);
        HashSet<String> newPackageNames = new HashSet<String>(oldPackageNames);
        newPackageNames.addAll(packageNames);
        return Collections.unmodifiableSet(newPackageNames);
    }

    public static void validatePackageNames(Collection<String> packageNames) {
        if (packageNames.stream().anyMatch(s -> Pattern.compile("\\s").matcher((CharSequence)s).find())) {
            throw new ConfigurationException("Excluded package names could not be parsed due to erroneous whitespace characters: " + packageNames);
        }
    }
}

