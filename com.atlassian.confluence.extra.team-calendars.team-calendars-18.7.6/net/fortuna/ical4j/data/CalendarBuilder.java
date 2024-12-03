/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.DefaultComponentFactorySupplier;
import net.fortuna.ical4j.data.DefaultContentHandler;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.ParameterFactoryRegistry;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.PropertyFactoryRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

public class CalendarBuilder
implements Consumer<Calendar> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final CalendarParser parser;
    private final ContentHandler contentHandler;
    private final TimeZoneRegistry tzRegistry;
    private Calendar calendar;

    public CalendarBuilder() {
        this.parser = CalendarParserFactory.getInstance().get();
        this.tzRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();
        this.contentHandler = new DefaultContentHandler(this, this.tzRegistry);
    }

    public CalendarBuilder(CalendarParser parser) {
        this.parser = parser;
        this.tzRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();
        this.contentHandler = new DefaultContentHandler(this, this.tzRegistry);
    }

    public CalendarBuilder(TimeZoneRegistry tzRegistry) {
        this.parser = CalendarParserFactory.getInstance().get();
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    public CalendarBuilder(CalendarParser parser, TimeZoneRegistry tzRegistry) {
        this.parser = parser;
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    @Deprecated
    public CalendarBuilder(CalendarParser parser, PropertyFactoryRegistry propertyFactoryRegistry, ParameterFactoryRegistry parameterFactoryRegistry, TimeZoneRegistry tzRegistry) {
        this(parser, parameterFactoryRegistry, propertyFactoryRegistry, new DefaultComponentFactorySupplier(), tzRegistry);
    }

    public CalendarBuilder(CalendarParser parser, Supplier<List<ParameterFactory<?>>> parameterFactorySupplier, Supplier<List<PropertyFactory<?>>> propertyFactorySupplier, Supplier<List<ComponentFactory<?>>> componentFactorySupplier, TimeZoneRegistry tzRegistry) {
        this.parser = parser;
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry, parameterFactorySupplier, propertyFactorySupplier, componentFactorySupplier);
    }

    @Override
    public void accept(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar build(InputStream in) throws IOException, ParserException {
        return this.build(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    public Calendar build(Reader in) throws IOException, ParserException {
        return this.build(new UnfoldingReader(in));
    }

    public Calendar build(UnfoldingReader uin) throws IOException, ParserException {
        this.parser.parse(uin, this.contentHandler);
        return this.calendar;
    }

    public final TimeZoneRegistry getRegistry() {
        return this.tzRegistry;
    }
}

