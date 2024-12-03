/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.PresetParser;
import org.apache.poi.util.XMLHelper;

public final class PresetGeometries {
    private final Map<String, CustomGeometry> map = new TreeMap<String, CustomGeometry>();

    public static PresetGeometries getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PresetGeometries() {
        try (InputStream is = PresetGeometries.class.getResourceAsStream("presetShapeDefinitions.xml");){
            XMLInputFactory staxFactory = XMLHelper.newXMLInputFactory();
            try (XMLStreamReader sr = staxFactory.createXMLStreamReader(new StreamSource(is));){
                PresetParser p = new PresetParser(PresetParser.Mode.FILE);
                p.parse(sr);
                this.map.putAll(p.getGeom());
            }
        }
        catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomGeometry get(String name) {
        return name == null ? null : this.map.get(name);
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    public int size() {
        return this.map.size();
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return Objects.hash(this.map);
    }

    private static class SingletonHelper {
        private static final PresetGeometries INSTANCE = new PresetGeometries();

        private SingletonHelper() {
        }
    }
}

