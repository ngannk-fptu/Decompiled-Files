/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MemberAccess
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ConfigParseUtil;
import com.opensymphony.xwork2.util.ProxyUtil;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ognl.MemberAccess;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ognl.ProviderAllowlist;

public class SecurityMemberAccess
implements MemberAccess {
    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccess.class);
    private static final Set<String> ALLOWLIST_REQUIRED_PACKAGES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("com.opensymphony.xwork2.validator.validators", "org.apache.struts2.components", "org.apache.struts2.views.jsp")));
    private static final Set<Class<?>> ALLOWLIST_REQUIRED_CLASSES = Collections.unmodifiableSet(new HashSet<Class>(Arrays.asList(Enum.class, String.class, Date.class, HashMap.class, Map.class, Map.Entry.class)));
    private final ProviderAllowlist providerAllowlist;
    private boolean allowStaticFieldAccess = true;
    private Set<Pattern> excludeProperties = Collections.emptySet();
    private Set<Pattern> acceptProperties = Collections.emptySet();
    private Set<String> excludedClasses = Collections.unmodifiableSet(new HashSet<String>(Collections.singletonList(Object.class.getName())));
    private Set<Pattern> excludedPackageNamePatterns = Collections.emptySet();
    private Set<String> excludedPackageNames = Collections.emptySet();
    private Set<String> excludedPackageExemptClasses = Collections.emptySet();
    private boolean enforceAllowlistEnabled = false;
    private Set<Class<?>> allowlistClasses = Collections.emptySet();
    private Set<String> allowlistPackageNames = Collections.emptySet();
    private boolean disallowProxyMemberAccess = false;
    private boolean disallowDefaultPackageAccess = false;

    @Inject
    public SecurityMemberAccess(@Inject ProviderAllowlist providerAllowlist) {
        this.providerAllowlist = providerAllowlist;
    }

    @Deprecated
    public SecurityMemberAccess(boolean allowStaticFieldAccess) {
        this(null);
        this.useAllowStaticFieldAccess(String.valueOf(allowStaticFieldAccess));
    }

    public Object setup(Map context, Object target, Member member, String propertyName) {
        AccessibleObject accessible;
        Boolean result = null;
        if (this.isAccessible(context, target, member, propertyName) && !(accessible = (AccessibleObject)((Object)member)).isAccessible()) {
            result = Boolean.FALSE;
            accessible.setAccessible(true);
        }
        return result;
    }

    public void restore(Map context, Object target, Member member, String propertyName, Object state) {
        if (state == null) {
            return;
        }
        if (((Boolean)state).booleanValue()) {
            throw new IllegalArgumentException(MessageFormat.format("Improper restore state [true] for target [{0}], member [{1}], propertyName [{2}]", target, member, propertyName));
        }
        ((AccessibleObject)((Object)member)).setAccessible(false);
    }

    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        LOG.debug("Checking access for [target: {}, member: {}, property: {}]", target, (Object)member, (Object)propertyName);
        if (target != null) {
            if (Class.class.equals(target.getClass()) && !Class.class.equals(target)) {
                if (!SecurityMemberAccess.isStatic(member)) {
                    throw new IllegalArgumentException("Member expected to be static!");
                }
                if (!member.getDeclaringClass().equals(target)) {
                    throw new IllegalArgumentException("Target class does not match static member!");
                }
                target = null;
            } else if (!member.getDeclaringClass().isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("Member does not exist on target!");
            }
        }
        if (!this.checkProxyMemberAccess(target, member)) {
            LOG.warn("Access to proxy is blocked! Member class [{}] of target [{}], member [{}]", member.getDeclaringClass(), target, (Object)member);
            return false;
        }
        if (!this.checkPublicMemberAccess(member)) {
            LOG.warn("Access to non-public [{}] is blocked!", (Object)member);
            return false;
        }
        if (!this.checkStaticFieldAccess(member)) {
            LOG.warn("Access to static field [{}] is blocked!", (Object)member);
            return false;
        }
        if (!this.checkStaticMethodAccess(member)) {
            LOG.warn("Access to static method [{}] is blocked!", (Object)member);
            return false;
        }
        if (!this.checkDefaultPackageAccess(target, member)) {
            return false;
        }
        if (!this.checkExclusionList(target, member)) {
            return false;
        }
        if (!this.checkAllowlist(target, member)) {
            return false;
        }
        return this.isAcceptableProperty(propertyName);
    }

    protected boolean checkAllowlist(Object target, Member member) {
        Class<?> memberClass = member.getDeclaringClass();
        if (!this.enforceAllowlistEnabled) {
            return true;
        }
        if (!this.isClassAllowlisted(memberClass)) {
            LOG.warn(MessageFormat.format("Declaring class [{0}] of member type [{1}] is not allowlisted!", memberClass, member));
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (!this.isClassAllowlisted(targetClass)) {
            LOG.warn(MessageFormat.format("Target class [{0}] of target [{1}] is not allowlisted!", targetClass, target));
            return false;
        }
        return true;
    }

    protected boolean isClassAllowlisted(Class<?> clazz) {
        return this.allowlistClasses.contains(clazz) || ALLOWLIST_REQUIRED_CLASSES.contains(clazz) || this.providerAllowlist != null && this.providerAllowlist.getProviderAllowlist().contains(clazz) || SecurityMemberAccess.isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES) || SecurityMemberAccess.isClassBelongsToPackages(clazz, this.allowlistPackageNames);
    }

    protected boolean checkExclusionList(Object target, Member member) {
        Class<?> memberClass = member.getDeclaringClass();
        if (this.isClassExcluded(memberClass)) {
            LOG.warn("Declaring class of member type [{}] is excluded!", memberClass);
            return false;
        }
        if (this.isPackageExcluded(memberClass)) {
            LOG.warn("Package [{}] of member class [{}] of member [{}] is excluded!", (Object)memberClass.getPackage(), memberClass, target);
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (this.isClassExcluded(targetClass)) {
            LOG.warn("Target class [{}] of target [{}] is excluded!", targetClass, target);
            return false;
        }
        if (this.isPackageExcluded(targetClass)) {
            LOG.warn("Package [{}] of target [{}] is excluded!", (Object)targetClass.getPackage(), (Object)member);
            return false;
        }
        return true;
    }

    protected boolean checkDefaultPackageAccess(Object target, Member member) {
        if (!this.disallowDefaultPackageAccess) {
            return true;
        }
        Class<?> memberClass = member.getDeclaringClass();
        if (memberClass.getPackage() == null || memberClass.getPackage().getName().isEmpty()) {
            LOG.warn("Class [{}] from the default package is excluded!", memberClass);
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (targetClass.getPackage() == null || targetClass.getPackage().getName().isEmpty()) {
            LOG.warn("Class [{}] from the default package is excluded!", targetClass);
            return false;
        }
        return true;
    }

    protected boolean checkProxyMemberAccess(Object target, Member member) {
        return !this.disallowProxyMemberAccess || !ProxyUtil.isProxyMember(member, target);
    }

    protected boolean checkStaticMethodAccess(Member member) {
        return member instanceof Field || !SecurityMemberAccess.isStatic(member);
    }

    private static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    protected boolean checkStaticFieldAccess(Member member) {
        if (this.allowStaticFieldAccess) {
            return true;
        }
        return !(member instanceof Field) || !SecurityMemberAccess.isStatic(member);
    }

    protected boolean checkPublicMemberAccess(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    protected boolean isPackageExcluded(Class<?> clazz) {
        return !this.excludedPackageExemptClasses.contains(clazz.getName()) && (this.isExcludedPackageNames(clazz) || this.isExcludedPackageNamePatterns(clazz));
    }

    public static String toPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName();
    }

    protected boolean isExcludedPackageNamePatterns(Class<?> clazz) {
        return this.excludedPackageNamePatterns.stream().anyMatch(pattern -> pattern.matcher(SecurityMemberAccess.toPackageName(clazz)).matches());
    }

    protected boolean isExcludedPackageNames(Class<?> clazz) {
        return SecurityMemberAccess.isClassBelongsToPackages(clazz, this.excludedPackageNames);
    }

    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        List<String> packageParts = Arrays.asList(SecurityMemberAccess.toPackageName(clazz).split("\\."));
        for (int i = 0; i < packageParts.size(); ++i) {
            String parentPackage = String.join((CharSequence)".", packageParts.subList(0, i + 1));
            if (!matchingPackages.contains(parentPackage)) continue;
            return true;
        }
        return false;
    }

    protected boolean isClassExcluded(Class<?> clazz) {
        return this.excludedClasses.contains(clazz.getName());
    }

    protected boolean isAcceptableProperty(String name) {
        return name == null || !this.isExcluded(name) && this.isAccepted(name);
    }

    protected boolean isAccepted(String paramName) {
        if (this.acceptProperties.isEmpty()) {
            return true;
        }
        return this.acceptProperties.stream().map(pattern -> pattern.matcher(paramName)).anyMatch(Matcher::matches);
    }

    protected boolean isExcluded(String paramName) {
        return this.excludeProperties.stream().map(pattern -> pattern.matcher(paramName)).anyMatch(Matcher::matches);
    }

    public void useExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public void useAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }

    @Inject(value="struts.ognl.allowStaticFieldAccess", required=false)
    public void useAllowStaticFieldAccess(String allowStaticFieldAccess) {
        this.allowStaticFieldAccess = BooleanUtils.toBoolean((String)allowStaticFieldAccess);
        if (!this.allowStaticFieldAccess) {
            this.useExcludedClasses(Class.class.getName());
        }
    }

    @Inject(value="struts.excludedClasses", required=false)
    public void useExcludedClasses(String commaDelimitedClasses) {
        this.excludedClasses = ConfigParseUtil.toNewClassesSet(this.excludedClasses, commaDelimitedClasses);
    }

    @Inject(value="struts.excludedPackageNamePatterns", required=false)
    public void useExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.excludedPackageNamePatterns = ConfigParseUtil.toNewPatternsSet(this.excludedPackageNamePatterns, commaDelimitedPackagePatterns);
    }

    @Inject(value="struts.excludedPackageNames", required=false)
    public void useExcludedPackageNames(String commaDelimitedPackageNames) {
        this.excludedPackageNames = ConfigParseUtil.toNewPackageNamesSet(this.excludedPackageNames, commaDelimitedPackageNames);
    }

    @Inject(value="struts.excludedPackageExemptClasses", required=false)
    public void useExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.excludedPackageExemptClasses = ConfigParseUtil.toClassesSet(commaDelimitedClasses);
    }

    @Inject(value="struts.allowlist.enable", required=false)
    public void useEnforceAllowlistEnabled(String enforceAllowlistEnabled) {
        this.enforceAllowlistEnabled = BooleanUtils.toBoolean((String)enforceAllowlistEnabled);
    }

    @Inject(value="struts.allowlist.classes", required=false)
    public void useAllowlistClasses(String commaDelimitedClasses) {
        this.allowlistClasses = ConfigParseUtil.toClassObjectsSet(commaDelimitedClasses);
    }

    @Inject(value="struts.allowlist.packageNames", required=false)
    public void useAllowlistPackageNames(String commaDelimitedPackageNames) {
        this.allowlistPackageNames = ConfigParseUtil.toPackageNamesSet(commaDelimitedPackageNames);
    }

    @Inject(value="struts.disallowProxyMemberAccess", required=false)
    public void useDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = BooleanUtils.toBoolean((String)disallowProxyMemberAccess);
    }

    @Inject(value="struts.disallowDefaultPackageAccess", required=false)
    public void useDisallowDefaultPackageAccess(String disallowDefaultPackageAccess) {
        this.disallowDefaultPackageAccess = BooleanUtils.toBoolean((String)disallowDefaultPackageAccess);
    }
}

