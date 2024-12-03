/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class OrderedProperties
extends HashMap<String, String>
implements Iterable<String> {
    private static final long serialVersionUID = 2L;
    private final List<String> keyList = new LinkedList<String>();

    private OrderedProperties() {
    }

    static OrderedProperties load(String resource) {
        OrderedProperties p = new OrderedProperties();
        InputStream is = OrderedProperties.class.getResourceAsStream(resource);
        try {
            p.load(is);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException iOException) {}
        }
        return p;
    }

    private void load(InputStream inStream) throws IOException {
        this.load(new InputStreamReader(inStream));
    }

    private void load(Reader reader) throws IOException {
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        Pattern pattern = Pattern.compile("([^#].+)=([^#\\r\\n]+)");
        while ((line = bufferedReader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) continue;
            this.keyList.add(matcher.group(1).trim());
            this.put(matcher.group(1).trim(), matcher.group(2).trim());
        }
    }

    @Override
    public Iterator<String> iterator() {
        return this.keyList.iterator();
    }
}

