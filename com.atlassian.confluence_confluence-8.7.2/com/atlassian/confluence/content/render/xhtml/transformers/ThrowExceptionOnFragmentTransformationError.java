/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import javax.xml.stream.XMLEventReader;

public class ThrowExceptionOnFragmentTransformationError
implements FragmentTransformationErrorHandler {
    @Override
    public String handle(XMLEventReader erroneousFragment, Exception transformationException) {
        if (transformationException instanceof RuntimeException) {
            throw (RuntimeException)transformationException;
        }
        throw new RuntimeException(transformationException);
    }
}

