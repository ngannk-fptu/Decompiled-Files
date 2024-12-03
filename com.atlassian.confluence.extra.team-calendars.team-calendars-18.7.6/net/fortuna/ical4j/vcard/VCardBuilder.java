/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.GroupRegistry;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;
import net.fortuna.ical4j.vcard.ParameterFactoryRegistry;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.PropertyFactoryRegistry;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.property.Xproperty;
import org.apache.commons.codec.DecoderException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class VCardBuilder {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Pattern VCARD_BEGIN = Pattern.compile("^BEGIN:VCARD$", 2);
    private static final Pattern RELAXED_VCARD_BEGIN = Pattern.compile("^BEGIN:VCARD\\s*$", 2);
    private static final Pattern VCARD_END = Pattern.compile("^END:VCARD$", 2);
    private static final Pattern RELAXED_VCARD_END = Pattern.compile("^END:VCARD\\s*$", 2);
    static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile("^(([a-zA-Z-\\d]+\\.)?[a-zA-Z]+(?=[;:]))|(([a-zA-Z-\\d]+\\.)?[Xx]-[a-zA-Z-]+(?=[;:]))");
    private static final Pattern PROPERTY_VALUE_PATTERN = Pattern.compile("(?<=[:]).*$");
    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("(?<=[;])[^:]*(?=[:])");
    private static final int BUFFER_SIZE = 1024;
    private final BufferedReader reader;
    private final GroupRegistry groupRegistry;
    private final PropertyFactoryRegistry propertyFactoryRegistry;
    private final ParameterFactoryRegistry parameterFactoryRegistry;
    private final boolean relaxedParsing;

    public VCardBuilder(InputStream in) {
        this(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    public VCardBuilder(Reader in) {
        this(in, new GroupRegistry(), new PropertyFactoryRegistry(), new ParameterFactoryRegistry());
    }

    public VCardBuilder(Reader in, GroupRegistry registry, PropertyFactoryRegistry propertyFactoryRegistry, ParameterFactoryRegistry parameterFactoryRegistry) {
        this.reader = new BufferedReader(new UnfoldingReader(in, 1024), 1024);
        this.groupRegistry = registry;
        this.propertyFactoryRegistry = propertyFactoryRegistry;
        this.parameterFactoryRegistry = parameterFactoryRegistry;
        this.relaxedParsing = CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed");
    }

    public VCard build() throws IOException, ParserException {
        return this.build(true);
    }

    public List<VCard> buildAll() throws IOException, ParserException {
        VCard card;
        ArrayList<VCard> cards = new ArrayList<VCard>();
        while ((card = this.build(false)) != null) {
            cards.add(card);
        }
        return Collections.unmodifiableList(cards);
    }

    private VCard build(boolean single) throws IOException, ParserException {
        VCard vcard = null;
        String line = null;
        String lastLine = null;
        int nonBlankLineNo = 0;
        int totalLineNo = 0;
        boolean end = false;
        Pattern beginPattern = null;
        Pattern endPattern = null;
        if (this.relaxedParsing) {
            beginPattern = RELAXED_VCARD_BEGIN;
            endPattern = RELAXED_VCARD_END;
        } else {
            beginPattern = VCARD_BEGIN;
            endPattern = VCARD_END;
        }
        while ((single || !end) && (line = this.reader.readLine()) != null) {
            ++totalLineNo;
            if (line.trim().length() == 0) continue;
            if (++nonBlankLineNo == 1) {
                if (!beginPattern.matcher(line).matches()) {
                    throw new ParserException(nonBlankLineNo);
                }
                vcard = new VCard();
            } else if (!endPattern.matcher(line).matches()) {
                Property property;
                try {
                    property = this.parseProperty(line);
                }
                catch (URISyntaxException e) {
                    throw new ParserException("Error parsing line", totalLineNo, e);
                }
                catch (ParseException e) {
                    throw new ParserException("Error parsing line", totalLineNo, e);
                }
                catch (DecoderException e) {
                    throw new ParserException("Error parsing line", totalLineNo, e);
                }
                if (property != null) {
                    vcard.getProperties().add(property);
                }
            } else if (endPattern.matcher(line).matches()) {
                end = true;
            }
            if (line.trim().length() <= 0) continue;
            lastLine = line;
        }
        if (single && (nonBlankLineNo <= 1 || !endPattern.matcher(lastLine).matches())) {
            throw new ParserException(totalLineNo);
        }
        return vcard;
    }

    private Property parseProperty(String line) throws URISyntaxException, ParseException, DecoderException {
        Matcher matcher = PROPERTY_NAME_PATTERN.matcher(line);
        if (matcher.find()) {
            PropertyFactory<? extends Property> factory = null;
            Group group = null;
            boolean xprop = false;
            String propertyName = matcher.group();
            if (propertyName.indexOf(46) >= 0) {
                String[] groupProperty = propertyName.split("\\.");
                group = this.groupRegistry.getGroup(groupProperty[0]);
                if (group == null) {
                    group = new Group(groupProperty[0]);
                }
                propertyName = groupProperty[1].toUpperCase();
                factory = this.propertyFactoryRegistry.getFactory(propertyName);
            } else {
                propertyName = propertyName.toUpperCase();
                factory = this.propertyFactoryRegistry.getFactory(propertyName);
            }
            if (factory == null) {
                factory = Xproperty.FACTORY;
                xprop = true;
            }
            if ((matcher = PROPERTY_VALUE_PATTERN.matcher(line)).find()) {
                String propertyValue = matcher.group(0);
                List<Parameter> params = this.parseParameters(line);
                if (xprop) {
                    Xproperty.ExtendedFactory xfactory = (Xproperty.ExtendedFactory)factory;
                    if (group != null) {
                        return xfactory.createProperty(group, propertyName, params, propertyValue);
                    }
                    return xfactory.createProperty(propertyName, params, propertyValue);
                }
                if (group != null) {
                    return factory.createProperty(group, params, propertyValue);
                }
                return factory.createProperty(params, propertyValue);
            }
        }
        return null;
    }

    private List<Parameter> parseParameters(String line) {
        ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        Matcher matcher = PARAMETERS_PATTERN.matcher(line);
        if (matcher.find()) {
            String[] params;
            for (String param : params = matcher.group().split(";")) {
                String[] vals = param.split("=");
                ParameterFactory<? extends Parameter> factory = this.parameterFactoryRegistry.getFactory(vals[0].toUpperCase());
                if (factory == null) continue;
                if (vals.length > 1) {
                    parameters.add(factory.createParameter(vals[1]));
                    continue;
                }
                parameters.add(factory.createParameter(null));
            }
        }
        return parameters;
    }
}

