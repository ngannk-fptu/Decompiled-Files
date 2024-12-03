/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.util.List;

public class ObjectConstructor
implements TemplateMethodModelEx {
    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.isEmpty()) {
            throw new TemplateModelException("This method must have at least one argument, the name of the class to instantiate.");
        }
        String classname = args.get(0).toString();
        Class cl = null;
        try {
            cl = ClassUtil.forName(classname);
        }
        catch (Exception e) {
            throw new TemplateModelException(e.getMessage());
        }
        BeansWrapper bw = BeansWrapper.getDefaultInstance();
        Object obj = bw.newInstance(cl, args.subList(1, args.size()));
        return bw.wrap(obj);
    }
}

