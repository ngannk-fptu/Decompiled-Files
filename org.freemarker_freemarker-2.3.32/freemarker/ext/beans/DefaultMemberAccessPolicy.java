/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BlacklistMemberAccessPolicy;
import freemarker.ext.beans.ClassMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import freemarker.ext.beans.WhitelistMemberAccessPolicy;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DefaultMemberAccessPolicy
implements MemberAccessPolicy {
    private static final DefaultMemberAccessPolicy INSTANCE = new DefaultMemberAccessPolicy();
    private final Set<Class<?>> whitelistRuleFinalClasses;
    private final Set<Class<?>> whitelistRuleNonFinalClasses;
    private final WhitelistMemberAccessPolicy whitelistMemberAccessPolicy;
    private final BlacklistMemberAccessPolicy blacklistMemberAccessPolicy;
    private final boolean toStringAlwaysExposed;

    public static DefaultMemberAccessPolicy getInstance(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private DefaultMemberAccessPolicy() {
        try {
            ClassLoader classLoader = DefaultMemberAccessPolicy.class.getClassLoader();
            this.whitelistRuleFinalClasses = new HashSet();
            this.whitelistRuleNonFinalClasses = new HashSet();
            HashSet<Class> typesWithBlacklistUnlistedRule = new HashSet<Class>();
            ArrayList<void> whitelistMemberSelectors = new ArrayList<void>();
            for (String line : DefaultMemberAccessPolicy.loadMemberSelectorFileLines()) {
                Class<?> upperBoundType;
                void var6_11;
                if (MemberSelectorListMemberAccessPolicy.MemberSelector.isIgnoredLine(line = line.trim())) continue;
                if (line.startsWith("@")) {
                    Class upperBoundType2;
                    String[] stringArray = line.split("\\s+");
                    if (stringArray.length != 2) {
                        throw new IllegalStateException("Malformed @ line: " + line);
                    }
                    String typeName = stringArray[1];
                    try {
                        Class<?> upperBoundType22 = classLoader.loadClass(typeName);
                    }
                    catch (ClassNotFoundException e) {
                        upperBoundType2 = null;
                    }
                    String rule = stringArray[0].substring(1);
                    if (rule.equals("whitelistPolicyIfAssignable")) {
                        if (upperBoundType2 == null) continue;
                        Set<Class<?>> targetSet = (upperBoundType2.getModifiers() & 0x10) != 0 ? this.whitelistRuleFinalClasses : this.whitelistRuleNonFinalClasses;
                        targetSet.add(upperBoundType2);
                        continue;
                    }
                    if (rule.equals("blacklistUnlistedMembers")) {
                        if (upperBoundType2 == null) continue;
                        typesWithBlacklistUnlistedRule.add(upperBoundType2);
                        continue;
                    }
                    throw new IllegalStateException("Unhandled rule: " + rule);
                }
                try {
                    MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector = MemberSelectorListMemberAccessPolicy.MemberSelector.parse(line, classLoader);
                }
                catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
                    Object var6_10 = null;
                }
                if (var6_11 == null || (upperBoundType = var6_11.getUpperBoundType()) == null) continue;
                if (!(this.whitelistRuleFinalClasses.contains(upperBoundType) || this.whitelistRuleNonFinalClasses.contains(upperBoundType) || typesWithBlacklistUnlistedRule.contains(upperBoundType))) {
                    throw new IllegalStateException("Type without rule: " + upperBoundType.getName());
                }
                whitelistMemberSelectors.add(var6_11);
            }
            this.whitelistMemberAccessPolicy = new WhitelistMemberAccessPolicy(whitelistMemberSelectors);
            ArrayList<MemberSelectorListMemberAccessPolicy.MemberSelector> blacklistMemberSelectors = new ArrayList<MemberSelectorListMemberAccessPolicy.MemberSelector>();
            for (Class clazz : typesWithBlacklistUnlistedRule) {
                ClassMemberAccessPolicy classPolicy = this.whitelistMemberAccessPolicy.forClass(clazz);
                for (Method method : clazz.getMethods()) {
                    if (classPolicy.isMethodExposed(method)) continue;
                    blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(clazz, method));
                }
                for (Executable executable : clazz.getConstructors()) {
                    if (classPolicy.isConstructorExposed((Constructor<?>)executable)) continue;
                    blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector((Class<?>)clazz, (Constructor<?>)executable));
                }
                for (AccessibleObject accessibleObject : clazz.getFields()) {
                    if (classPolicy.isFieldExposed((Field)accessibleObject)) continue;
                    blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(clazz, (Field)accessibleObject));
                }
            }
            this.blacklistMemberAccessPolicy = new BlacklistMemberAccessPolicy(blacklistMemberSelectors);
            this.toStringAlwaysExposed = this.whitelistMemberAccessPolicy.isToStringAlwaysExposed() && this.blacklistMemberAccessPolicy.isToStringAlwaysExposed();
        }
        catch (Exception e) {
            throw new IllegalStateException("Couldn't init " + this.getClass().getName() + " instance", e);
        }
    }

    private static List<String> loadMemberSelectorFileLines() throws IOException {
        ArrayList<String> whitelist = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(DefaultMemberAccessPolicy.class.getResourceAsStream("DefaultMemberAccessPolicy-rules"), "UTF-8"));){
            String line;
            while ((line = reader.readLine()) != null) {
                whitelist.add(line);
            }
        }
        return whitelist;
    }

    @Override
    public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
        if (this.isTypeWithWhitelistRule(contextClass)) {
            return this.whitelistMemberAccessPolicy.forClass(contextClass);
        }
        return this.blacklistMemberAccessPolicy.forClass(contextClass);
    }

    @Override
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }

    private boolean isTypeWithWhitelistRule(Class<?> contextClass) {
        if (this.whitelistRuleFinalClasses.contains(contextClass)) {
            return true;
        }
        for (Class<?> nonFinalClass : this.whitelistRuleNonFinalClasses) {
            if (!nonFinalClass.isAssignableFrom(contextClass)) continue;
            return true;
        }
        return false;
    }
}

