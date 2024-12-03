/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyModelHandler
extends ModelHandlerBase {
    public static final String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";

    public PropertyModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new PropertyModelHandler(context);
    }

    protected Class<PropertyModel> getSupportedModelClass() {
        return PropertyModel.class;
    }

    @Override
    public void handle(ModelInterpretationContext interpretationContext, Model model) {
        PropertyModel propertyModel = (PropertyModel)model;
        ActionUtil.Scope scope = ActionUtil.stringToScope(propertyModel.getScopeStr());
        if (this.checkFileAttributeSanity(propertyModel)) {
            String file = propertyModel.getFile();
            file = interpretationContext.subst(file);
            try (FileInputStream istream = new FileInputStream(file);){
                this.loadAndSetProperties(interpretationContext, istream, scope);
            }
            catch (FileNotFoundException e) {
                this.addError("Could not find properties file [" + file + "].");
            }
            catch (IOException | IllegalArgumentException e1) {
                this.addError("Could not read properties file [" + file + "].", e1);
            }
        } else if (this.checkResourceAttributeSanity(propertyModel)) {
            String resource = propertyModel.getResource();
            URL resourceURL = Loader.getResourceBySelfClassLoader(resource = interpretationContext.subst(resource));
            if (resourceURL == null) {
                this.addError("Could not find resource [" + resource + "].");
            } else {
                try (InputStream istream = resourceURL.openStream();){
                    this.loadAndSetProperties(interpretationContext, istream, scope);
                }
                catch (IOException e) {
                    this.addError("Could not read resource file [" + resource + "].", e);
                }
            }
        } else if (this.checkValueNameAttributesSanity(propertyModel)) {
            String value = propertyModel.getValue();
            value = value.trim();
            value = interpretationContext.subst(value);
            ActionUtil.setProperty(interpretationContext, propertyModel.getName(), value, scope);
        } else {
            this.addError(INVALID_ATTRIBUTES);
        }
    }

    void loadAndSetProperties(ModelInterpretationContext mic, InputStream istream, ActionUtil.Scope scope) throws IOException {
        Properties props = new Properties();
        props.load(istream);
        ModelUtil.setProperties(mic, props, scope);
    }

    boolean checkFileAttributeSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(file) && OptionHelper.isNullOrEmpty(name) && OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(resource);
    }

    boolean checkResourceAttributeSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(resource) && OptionHelper.isNullOrEmpty(name) && OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(file);
    }

    boolean checkValueNameAttributesSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(name) && !OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(file) && OptionHelper.isNullOrEmpty(resource);
    }
}

