/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="i18n", tldTagClass="org.apache.struts2.views.jsp.I18nTag", description="Get a resource bundle and place it on the value stack")
public class I18n
extends Component {
    private static final Logger LOG = LogManager.getLogger(I18n.class);
    protected boolean pushed;
    protected String name;
    private LocalizedTextProvider localizedTextProvider;
    private TextProvider textProvider;
    private TextProvider defaultTextProvider;
    private LocaleProviderFactory localeProviderFactory;
    private TextProviderFactory textProviderFactory;

    public I18n(ValueStack stack) {
        super(stack);
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Inject(value="system")
    public void setTextProvider(TextProvider textProvider) {
        this.defaultTextProvider = textProvider;
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        try {
            String name = this.findString(this.name, "name", "Resource bundle name is required. Example: foo or foo_en");
            ResourceBundle bundle = this.defaultTextProvider.getTexts(name);
            if (bundle == null) {
                LocaleProvider localeProvider = this.localeProviderFactory.createLocaleProvider();
                bundle = this.localizedTextProvider.findResourceBundle(name, localeProvider.getLocale());
            }
            if (bundle != null) {
                this.textProvider = this.textProviderFactory.createInstance(bundle);
                this.getStack().push(this.textProvider);
                this.pushed = true;
            }
        }
        catch (Exception e) {
            throw new StrutsException("Could not find the bundle " + this.name, e);
        }
        return result;
    }

    @Override
    public boolean end(Writer writer, String body) throws StrutsException {
        Object o;
        if (this.pushed && ((o = this.getStack().pop()) == null || !o.equals(this.textProvider))) {
            LOG.error("A closing i18n tag attempted to pop its own TextProvider from the top of the ValueStack but popped an unexpected object (" + (o != null ? o.getClass() : "null") + "). Refactor the page within the i18n tags to ensure no objects are pushed onto the ValueStack without popping them prior to the closing tag. If you see this message it's likely that the i18n's TextProvider is still on the stack and will continue to provide message resources after the closing tag.");
            throw new StrutsException("A closing i18n tag attempted to pop its TextProvider from the top of the ValueStack but popped an unexpected object (" + (o != null ? o.getClass() : "null") + ")");
        }
        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="Name of resource bundle to use (eg foo/bar/customBundle)", required=true, defaultValue="String")
    public void setName(String name) {
        this.name = name;
    }
}

