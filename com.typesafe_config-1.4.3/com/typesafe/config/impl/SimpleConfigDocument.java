/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigDocumentParser;
import com.typesafe.config.impl.ConfigNodeRoot;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokenizer;
import com.typesafe.config.parser.ConfigDocument;
import java.io.StringReader;
import java.util.Iterator;

final class SimpleConfigDocument
implements ConfigDocument {
    private ConfigNodeRoot configNodeTree;
    private ConfigParseOptions parseOptions;

    SimpleConfigDocument(ConfigNodeRoot parsedNode, ConfigParseOptions parseOptions) {
        this.configNodeTree = parsedNode;
        this.parseOptions = parseOptions;
    }

    @Override
    public ConfigDocument withValueText(String path, String newValue) {
        if (newValue == null) {
            throw new ConfigException.BugOrBroken("null value for " + path + " passed to withValueText");
        }
        SimpleConfigOrigin origin = SimpleConfigOrigin.newSimple("single value parsing");
        StringReader reader = new StringReader(newValue);
        Iterator<Token> tokens = Tokenizer.tokenize(origin, reader, this.parseOptions.getSyntax());
        AbstractConfigNodeValue parsedValue = ConfigDocumentParser.parseValue(tokens, origin, this.parseOptions);
        reader.close();
        return new SimpleConfigDocument(this.configNodeTree.setValue(path, parsedValue, this.parseOptions.getSyntax()), this.parseOptions);
    }

    @Override
    public ConfigDocument withValue(String path, ConfigValue newValue) {
        if (newValue == null) {
            throw new ConfigException.BugOrBroken("null value for " + path + " passed to withValue");
        }
        ConfigRenderOptions options = ConfigRenderOptions.defaults();
        options = options.setOriginComments(false);
        return this.withValueText(path, newValue.render(options).trim());
    }

    @Override
    public ConfigDocument withoutPath(String path) {
        return new SimpleConfigDocument(this.configNodeTree.setValue(path, null, this.parseOptions.getSyntax()), this.parseOptions);
    }

    @Override
    public boolean hasPath(String path) {
        return this.configNodeTree.hasValue(path);
    }

    @Override
    public String render() {
        return this.configNodeTree.render();
    }

    public boolean equals(Object other) {
        return other instanceof ConfigDocument && this.render().equals(((ConfigDocument)other).render());
    }

    public int hashCode() {
        return this.render().hashCode();
    }
}

