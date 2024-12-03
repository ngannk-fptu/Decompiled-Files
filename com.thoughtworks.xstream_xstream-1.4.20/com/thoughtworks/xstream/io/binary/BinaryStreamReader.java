/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.binary;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.binary.ReaderDepthState;
import com.thoughtworks.xstream.io.binary.Token;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BinaryStreamReader
implements ExtendedHierarchicalStreamReader {
    private final DataInputStream in;
    private final ReaderDepthState depthState = new ReaderDepthState();
    private final IdRegistry idRegistry = new IdRegistry();
    private Token pushback;
    private final Token.Formatter tokenFormatter = new Token.Formatter();

    public BinaryStreamReader(InputStream inputStream) {
        this.in = new DataInputStream(inputStream);
        this.moveDown();
    }

    public boolean hasMoreChildren() {
        return this.depthState.hasMoreChildren();
    }

    public String getNodeName() {
        return this.depthState.getName();
    }

    public String getValue() {
        return this.depthState.getValue();
    }

    public String getAttribute(String name) {
        return this.depthState.getAttribute(name);
    }

    public String getAttribute(int index) {
        return this.depthState.getAttribute(index);
    }

    public int getAttributeCount() {
        return this.depthState.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return this.depthState.getAttributeName(index);
    }

    public Iterator getAttributeNames() {
        return this.depthState.getAttributeNames();
    }

    public void moveDown() {
        Token nextToken;
        this.depthState.push();
        Token firstToken = this.readToken();
        switch (firstToken.getType()) {
            case 3: {
                this.depthState.setName(this.idRegistry.get(firstToken.getId()));
                break;
            }
            default: {
                throw new StreamException("Expected StartNode");
            }
        }
        block9: while (true) {
            nextToken = this.readToken();
            switch (nextToken.getType()) {
                case 5: {
                    this.depthState.addAttribute(this.idRegistry.get(nextToken.getId()), nextToken.getValue());
                    continue block9;
                }
                case 6: {
                    this.depthState.setValue(nextToken.getValue());
                    continue block9;
                }
                case 4: {
                    this.depthState.setHasMoreChildren(false);
                    this.pushBack(nextToken);
                    return;
                }
                case 3: {
                    this.depthState.setHasMoreChildren(true);
                    this.pushBack(nextToken);
                    return;
                }
            }
            break;
        }
        throw new StreamException("Unexpected token " + nextToken);
    }

    /*
     * Enabled aggressive block sorting
     */
    public void moveUp() {
        Token nextToken;
        this.depthState.pop();
        int depth = 0;
        block8: while (true) {
            nextToken = this.readToken();
            switch (nextToken.getType()) {
                case 4: {
                    if (depth == 0) break block8;
                    --depth;
                    break;
                }
                case 3: {
                    ++depth;
                }
            }
        }
        nextToken = this.readToken();
        switch (nextToken.getType()) {
            case 4: {
                this.depthState.setHasMoreChildren(false);
                break;
            }
            case 3: {
                this.depthState.setHasMoreChildren(true);
                break;
            }
            default: {
                throw new StreamException("Unexpected token " + nextToken);
            }
        }
        this.pushBack(nextToken);
    }

    private Token readToken() {
        if (this.pushback == null) {
            try {
                Token token = this.tokenFormatter.read(this.in);
                switch (token.getType()) {
                    case 2: {
                        this.idRegistry.put(token.getId(), token.getValue());
                        return this.readToken();
                    }
                }
                return token;
            }
            catch (IOException e) {
                throw new StreamException(e);
            }
        }
        Token result = this.pushback;
        this.pushback = null;
        return result;
    }

    public void pushBack(Token token) {
        if (this.pushback != null) {
            throw new Error("Cannot push more than one token back");
        }
        this.pushback = token;
    }

    public void close() {
        try {
            this.in.close();
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public String peekNextChild() {
        if (this.depthState.hasMoreChildren()) {
            return this.idRegistry.get(this.pushback.getId());
        }
        return null;
    }

    public HierarchicalStreamReader underlyingReader() {
        return this;
    }

    public void appendErrors(ErrorWriter errorWriter) {
    }

    private static class IdRegistry {
        private Map map = new HashMap();

        private IdRegistry() {
        }

        public void put(long id, String value) {
            this.map.put(new Long(id), value);
        }

        public String get(long id) {
            String result = (String)this.map.get(new Long(id));
            if (result == null) {
                throw new StreamException("Unknown ID : " + id);
            }
            return result;
        }
    }
}

