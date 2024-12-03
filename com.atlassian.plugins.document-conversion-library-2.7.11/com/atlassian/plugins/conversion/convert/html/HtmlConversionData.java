/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.html;

import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.convert.html.Streamable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HtmlConversionData
implements Serializable,
HtmlConversionResult {
    private String _html;
    private Date _dateConverted;
    private Map<String, byte[]> _images = new HashMap<String, byte[]>();

    public HtmlConversionData() {
        this._dateConverted = new Date();
    }

    public void setHtml(String html) {
        this._html = html;
    }

    @Override
    public String getHtml() {
        return this._html;
    }

    @Override
    public Streamable getImage(String key) {
        final byte[] bytes = this._images.get(key);
        return new Streamable(){

            @Override
            public void streamTo(OutputStream outputStream) throws IOException {
                outputStream.write(bytes);
            }
        };
    }

    public void addImage(String key, byte[] val) {
        this._images.put(key, val);
    }

    public Date getDateConverted() {
        return this._dateConverted;
    }

    public Set<String> getImageKeys() {
        return Collections.unmodifiableSet(this._images.keySet());
    }
}

