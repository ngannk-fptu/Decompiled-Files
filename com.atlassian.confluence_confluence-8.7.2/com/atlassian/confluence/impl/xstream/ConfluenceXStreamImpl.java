/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.XStreamException
 *  com.thoughtworks.xstream.converters.Converter
 */
package com.atlassian.confluence.impl.xstream;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.xstream.XStream111;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamInternal;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

@Internal
public class ConfluenceXStreamImpl
implements ConfluenceXStreamInternal {
    private final XStream xStreamPrimary;
    private final XStream xStreamSecondary;

    public ConfluenceXStreamImpl(XStream xStreamPrimary, XStream xStreamSecondary) {
        this.xStreamPrimary = xStreamPrimary;
        this.xStreamSecondary = xStreamSecondary;
    }

    @Override
    public String toXML(Object obj) {
        return this.xStreamPrimary.toXML(obj);
    }

    @Override
    public void toXML(Object obj, Writer writer) {
        this.xStreamPrimary.toXML(obj, writer);
    }

    @Override
    public Object fromXML(String xml) {
        try {
            return this.xStreamPrimary.fromXML(xml);
        }
        catch (XStreamException xe) {
            return this.xStreamSecondary.fromXML(xml);
        }
    }

    @Override
    public Object fromXML(Reader reader) {
        try {
            return this.xStreamPrimary.fromXML(reader);
        }
        catch (XStreamException xe) {
            try {
                reader.reset();
            }
            catch (IOException e) {
                throw new IllegalStateException("Error accessing reader in XStream", e);
            }
            return this.xStreamSecondary.fromXML(reader);
        }
    }

    @Override
    public XStream getXStream() {
        if (this.xStreamPrimary instanceof XStream111) {
            return this.xStreamPrimary;
        }
        return this.xStreamSecondary;
    }

    @Override
    public void registerConverter(Converter converter, Integer integer) {
        this.xStreamPrimary.registerConverter(converter, integer.intValue());
        this.xStreamSecondary.registerConverter(converter, integer.intValue());
    }

    @Override
    public void alias(String alias, Class aliasClass) {
        this.xStreamPrimary.alias(alias, aliasClass);
        this.xStreamSecondary.alias(alias, aliasClass);
    }

    @Override
    public void setUpDefaultSecurity() {
        XStream.setupDefaultSecurity((XStream)this.xStreamPrimary);
        XStream.setupDefaultSecurity((XStream)this.xStreamSecondary);
    }

    @Override
    public void allowTypes(String[] toArray) {
        this.xStreamPrimary.allowTypes(toArray);
        this.xStreamSecondary.allowTypes(toArray);
    }

    @Override
    public void allowTypesByWildcard(String[] toArray) {
        this.xStreamPrimary.allowTypesByWildcard(toArray);
        this.xStreamSecondary.allowTypesByWildcard(toArray);
    }

    @Override
    public void allowTypesByRegExp(String[] toArray) {
        this.xStreamPrimary.allowTypesByRegExp(toArray);
        this.xStreamSecondary.allowTypesByRegExp(toArray);
    }
}

