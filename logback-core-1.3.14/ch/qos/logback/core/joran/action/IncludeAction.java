/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.IncludeModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.xml.sax.Attributes;

public class IncludeAction
extends Action {
    private static final String FILE_ATTR = "file";
    private static final String URL_ATTR = "url";
    private static final String RESOURCE_ATTR = "resource";
    private static final String OPTIONAL_ATTR = "optional";
    private String attributeInUse;
    private boolean optional;
    Model parentModel;
    IncludeModel includeModel;
    boolean inError = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void begin(SaxEventInterpretationContext ec, String name, Attributes attributes) throws ActionException {
        this.parentModel = null;
        this.includeModel = null;
        SaxEventRecorder recorder = new SaxEventRecorder(this.context);
        String optionalStr = attributes.getValue(OPTIONAL_ATTR);
        this.createModelForAlternateUse(ec, name, attributes, optionalStr);
        this.attributeInUse = null;
        this.optional = OptionHelper.toBoolean(optionalStr, false);
        if (!this.checkAttributes(attributes)) {
            this.inError = true;
            return;
        }
        InputStream in = this.getInputStream(ec, attributes);
        try {
            if (in != null) {
                this.parseAndRecord(in, recorder);
                this.trimHeadAndTail(recorder);
                ec.getSaxEventInterpreter().getEventPlayer().addEventsDynamically(recorder.getSaxEventList(), 2);
            }
        }
        catch (JoranException je) {
            this.addError("Error while parsing  " + this.attributeInUse, je);
        }
        finally {
            this.close(in);
        }
    }

    private void createModelForAlternateUse(SaxEventInterpretationContext seic, String name, Attributes attributes, String optionalStr) {
        this.includeModel = new IncludeModel();
        this.includeModel.setOptional(optionalStr);
        this.fillInIncludeModelAttributes(this.includeModel, name, attributes);
        if (!seic.isModelStackEmpty()) {
            this.parentModel = seic.peekModel();
        }
        int lineNumber = IncludeAction.getLineNumber(seic);
        this.includeModel.setLineNumber(lineNumber);
        seic.pushModel(this.includeModel);
    }

    private void fillInIncludeModelAttributes(IncludeModel includeModel, String name, Attributes attributes) {
        this.includeModel.setTag(name);
        String fileAttribute = attributes.getValue(FILE_ATTR);
        String urlAttribute = attributes.getValue(URL_ATTR);
        String resourceAttribute = attributes.getValue(RESOURCE_ATTR);
        this.includeModel.setFile(fileAttribute);
        this.includeModel.setUrl(urlAttribute);
        this.includeModel.setResource(resourceAttribute);
    }

    void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private boolean checkAttributes(Attributes attributes) {
        String fileAttribute = attributes.getValue(FILE_ATTR);
        String urlAttribute = attributes.getValue(URL_ATTR);
        String resourceAttribute = attributes.getValue(RESOURCE_ATTR);
        int count = 0;
        if (!OptionHelper.isNullOrEmpty(fileAttribute)) {
            ++count;
        }
        if (!OptionHelper.isNullOrEmpty(urlAttribute)) {
            ++count;
        }
        if (!OptionHelper.isNullOrEmpty(resourceAttribute)) {
            ++count;
        }
        if (count == 0) {
            this.addError("One of \"path\", \"resource\" or \"url\" attributes must be set.");
            return false;
        }
        if (count > 1) {
            this.addError("Only one of \"file\", \"url\" or \"resource\" attributes should be set.");
            return false;
        }
        if (count == 1) {
            return true;
        }
        throw new IllegalStateException("Count value [" + count + "] is not expected");
    }

    URL attributeToURL(String urlAttribute) {
        try {
            return new URL(urlAttribute);
        }
        catch (MalformedURLException mue) {
            String errMsg = "URL [" + urlAttribute + "] is not well formed.";
            this.addError(errMsg, mue);
            return null;
        }
    }

    InputStream openURL(URL url) {
        try {
            return url.openStream();
        }
        catch (IOException e) {
            this.optionalWarning("Failed to open [" + url.toString() + "]");
            return null;
        }
    }

    URL resourceAsURL(String resourceAttribute) {
        URL url = Loader.getResourceBySelfClassLoader(resourceAttribute);
        if (url == null) {
            this.optionalWarning("Could not find resource corresponding to [" + resourceAttribute + "]");
            return null;
        }
        return url;
    }

    private void optionalWarning(String msg) {
        if (!this.optional) {
            this.addWarn(msg);
        }
    }

    URL filePathAsURL(String path) {
        URI uri = new File(path).toURI();
        try {
            return uri.toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    URL getInputURL(SaxEventInterpretationContext ec, Attributes attributes) {
        String fileAttribute = attributes.getValue(FILE_ATTR);
        String urlAttribute = attributes.getValue(URL_ATTR);
        String resourceAttribute = attributes.getValue(RESOURCE_ATTR);
        if (!OptionHelper.isNullOrEmpty(fileAttribute)) {
            this.attributeInUse = ec.subst(fileAttribute);
            return this.filePathAsURL(this.attributeInUse);
        }
        if (!OptionHelper.isNullOrEmpty(urlAttribute)) {
            this.attributeInUse = ec.subst(urlAttribute);
            return this.attributeToURL(this.attributeInUse);
        }
        if (!OptionHelper.isNullOrEmpty(resourceAttribute)) {
            this.attributeInUse = ec.subst(resourceAttribute);
            return this.resourceAsURL(this.attributeInUse);
        }
        throw new IllegalStateException("A URL stream should have been returned");
    }

    InputStream getInputStream(SaxEventInterpretationContext ec, Attributes attributes) {
        URL inputURL = this.getInputURL(ec, attributes);
        if (inputURL == null) {
            return null;
        }
        ConfigurationWatchListUtil.addToWatchList(this.context, inputURL);
        return this.openURL(inputURL);
    }

    private void trimHeadAndTail(SaxEventRecorder recorder) {
        SaxEvent last;
        List<SaxEvent> saxEventList = recorder.getSaxEventList();
        if (saxEventList.size() == 0) {
            return;
        }
        SaxEvent first = saxEventList.get(0);
        if (first != null && first.qName.equalsIgnoreCase("included")) {
            saxEventList.remove(0);
        }
        if ((last = saxEventList.get(saxEventList.size() - 1)) != null && last.qName.equalsIgnoreCase("included")) {
            saxEventList.remove(saxEventList.size() - 1);
        }
    }

    private void parseAndRecord(InputStream inputSource, SaxEventRecorder recorder) throws JoranException {
        recorder.setContext(this.context);
        recorder.recordEvents(inputSource);
    }

    @Override
    public void end(SaxEventInterpretationContext seic, String name) throws ActionException {
        if (this.inError) {
            return;
        }
        Model m = seic.peekModel();
        if (m != this.includeModel) {
            this.addWarn("The object at the of the stack is not the model [" + this.includeModel.idString() + "] pushed earlier.");
            this.addWarn("This is wholly unexpected.");
        }
        if (this.parentModel != null) {
            this.parentModel.addSubModel(this.includeModel);
            seic.popModel();
        }
    }
}

