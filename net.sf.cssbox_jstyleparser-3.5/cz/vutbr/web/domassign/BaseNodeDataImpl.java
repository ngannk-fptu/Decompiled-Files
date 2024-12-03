/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.SupportedCSS;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.csskit.DeclarationTransformer;

public abstract class BaseNodeDataImpl
implements NodeData {
    protected static DeclarationTransformer transformer = CSSFactory.getDeclarationTransformer();
    protected static SupportedCSS css = CSSFactory.getSupportedCSS();

    @Override
    public <T extends CSSProperty> T getSpecifiedProperty(String name) {
        Object prop = this.getProperty(name, true);
        if (prop == null) {
            CSSProperty def = css.getDefaultProperty(name);
            return (T)def;
        }
        return prop;
    }

    @Override
    public Term<?> getSpecifiedValue(String name) {
        TermColor ret = this.getValue(name, true);
        if (ret == null) {
            ret = css.getDefaultValue(name);
        }
        if (ret != null && ret instanceof TermColor && ((TermColor)ret).getKeyword() == TermColor.Keyword.CURRENT_COLOR) {
            TermFactory tf = CSSFactory.getTermFactory();
            ret = tf.createColor(tf.createIdent("currentColor"));
            TermColor cvalue = this.getValue(TermColor.class, "color", true);
            if (cvalue == null) {
                cvalue = (TermColor)css.getDefaultValue("color");
            }
            ret.setValue(cvalue.getValue());
        }
        return ret;
    }

    @Override
    public <T extends Term<?>> T getSpecifiedValue(Class<T> clazz, String name) {
        return (T)((Term)clazz.cast(this.getSpecifiedValue(name)));
    }
}

