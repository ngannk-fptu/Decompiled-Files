/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.Execute;
import freemarker.template.utility.ObjectConstructor;

public interface TemplateClassResolver {
    public static final TemplateClassResolver UNRESTRICTED_RESOLVER = new TemplateClassResolver(){

        @Override
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            try {
                return ClassUtil.forName(className);
            }
            catch (ClassNotFoundException e) {
                throw new _MiscTemplateException((Throwable)e, env);
            }
        }
    };
    public static final TemplateClassResolver SAFER_RESOLVER = new TemplateClassResolver(){

        @Override
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            if (className.equals(ObjectConstructor.class.getName()) || className.equals(Execute.class.getName()) || className.equals("freemarker.template.utility.JythonRuntime")) {
                throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
            }
            try {
                return ClassUtil.forName(className);
            }
            catch (ClassNotFoundException e) {
                throw new _MiscTemplateException((Throwable)e, env);
            }
        }
    };
    public static final TemplateClassResolver ALLOWS_NOTHING_RESOLVER = new TemplateClassResolver(){

        @Override
        public Class resolve(String className, Environment env, Template template) throws TemplateException {
            throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
        }
    };

    public Class resolve(String var1, Environment var2, Template var3) throws TemplateException;
}

