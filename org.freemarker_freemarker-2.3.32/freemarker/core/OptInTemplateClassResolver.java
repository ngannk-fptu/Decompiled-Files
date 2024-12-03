/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.TemplateClassResolver;
import freemarker.core._MiscTemplateException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptInTemplateClassResolver
implements TemplateClassResolver {
    private final Set allowedClasses;
    private final List trustedTemplatePrefixes;
    private final Set trustedTemplateNames;

    public OptInTemplateClassResolver(Set allowedClasses, List trustedTemplates) {
        Set set = this.allowedClasses = allowedClasses != null ? allowedClasses : Collections.EMPTY_SET;
        if (trustedTemplates != null) {
            this.trustedTemplateNames = new HashSet();
            this.trustedTemplatePrefixes = new ArrayList();
            for (String li : trustedTemplates) {
                if (li.startsWith("/")) {
                    li = li.substring(1);
                }
                if (li.endsWith("*")) {
                    this.trustedTemplatePrefixes.add(li.substring(0, li.length() - 1));
                    continue;
                }
                this.trustedTemplateNames.add(li);
            }
        } else {
            this.trustedTemplateNames = Collections.EMPTY_SET;
            this.trustedTemplatePrefixes = Collections.EMPTY_LIST;
        }
    }

    @Override
    public Class resolve(String className, Environment env, Template template) throws TemplateException {
        String templateName = this.safeGetTemplateName(template);
        if (templateName != null && (this.trustedTemplateNames.contains(templateName) || this.hasMatchingPrefix(templateName))) {
            return TemplateClassResolver.SAFER_RESOLVER.resolve(className, env, template);
        }
        if (!this.allowedClasses.contains(className)) {
            throw new _MiscTemplateException(env, "Instantiating ", className, " is not allowed in the template for security reasons. (If you run into this problem when using ?new in a template, you may want to check the \"", "new_builtin_class_resolver", "\" setting in the FreeMarker configuration.)");
        }
        try {
            return ClassUtil.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new _MiscTemplateException((Throwable)e, env);
        }
    }

    protected String safeGetTemplateName(Template template) {
        int dotDotIdx;
        if (template == null) {
            return null;
        }
        String name = template.getName();
        if (name == null) {
            return null;
        }
        String decodedName = name;
        if (decodedName.indexOf(37) != -1) {
            decodedName = StringUtil.replace(decodedName, "%2e", ".", false, false);
            decodedName = StringUtil.replace(decodedName, "%2E", ".", false, false);
            decodedName = StringUtil.replace(decodedName, "%2f", "/", false, false);
            decodedName = StringUtil.replace(decodedName, "%2F", "/", false, false);
            decodedName = StringUtil.replace(decodedName, "%5c", "\\", false, false);
            decodedName = StringUtil.replace(decodedName, "%5C", "\\", false, false);
        }
        if ((dotDotIdx = decodedName.indexOf("..")) != -1) {
            int after;
            int before = dotDotIdx - 1 >= 0 ? (int)decodedName.charAt(dotDotIdx - 1) : -1;
            int n = after = dotDotIdx + 2 < decodedName.length() ? (int)decodedName.charAt(dotDotIdx + 2) : -1;
            if (!(before != -1 && before != 47 && before != 92 || after != -1 && after != 47 && after != 92)) {
                return null;
            }
        }
        return name.startsWith("/") ? name.substring(1) : name;
    }

    private boolean hasMatchingPrefix(String name) {
        for (int i = 0; i < this.trustedTemplatePrefixes.size(); ++i) {
            String prefix = (String)this.trustedTemplatePrefixes.get(i);
            if (!name.startsWith(prefix)) continue;
            return true;
        }
        return false;
    }
}

