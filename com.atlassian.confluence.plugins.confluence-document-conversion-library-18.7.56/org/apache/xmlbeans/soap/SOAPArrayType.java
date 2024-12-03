/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.soap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

public final class SOAPArrayType {
    private QName _type;
    private int[] _ranks;
    private int[] _dimensions;
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    public boolean isSameRankAs(SOAPArrayType otherType) {
        return Arrays.equals(this._ranks, otherType._ranks) && this._dimensions.length == otherType._dimensions.length;
    }

    private static int[] internalParseCommaIntString(String csl) {
        ArrayList<String> dimStrings = new ArrayList<String>();
        int i = 0;
        while (true) {
            int j;
            if ((j = csl.indexOf(44, i)) < 0) break;
            dimStrings.add(csl.substring(i, j));
            i = j + 1;
        }
        dimStrings.add(csl.substring(i));
        return dimStrings.stream().mapToInt(SOAPArrayType::collapseDimString).toArray();
    }

    private static int collapseDimString(String dimString2) {
        String dimString = XmlWhitespace.collapse(dimString2, 3);
        try {
            return "*".equals(dimString) || dimString.isEmpty() ? -1 : Integer.parseInt(dimString);
        }
        catch (Exception e) {
            throw new XmlValueOutOfRangeException("Malformed integer in SOAP array index");
        }
    }

    public SOAPArrayType(String s, PrefixResolver m) {
        String uri;
        int firstbrace = s.indexOf(91);
        if (firstbrace < 0) {
            throw new XmlValueOutOfRangeException();
        }
        String firstpart = XmlWhitespace.collapse(s.substring(0, firstbrace), 3);
        int firstcolon = firstpart.indexOf(58);
        String prefix = "";
        if (firstcolon >= 0) {
            prefix = firstpart.substring(0, firstcolon);
        }
        if ((uri = m.getNamespaceForPrefix(prefix)) == null) {
            throw new XmlValueOutOfRangeException();
        }
        this._type = QNameHelper.forLNS(firstpart.substring(firstcolon + 1), uri);
        this.initDimensions(s, firstbrace);
    }

    public SOAPArrayType(QName name, String dimensions) {
        int firstbrace = dimensions.indexOf(91);
        if (firstbrace < 0) {
            this._type = name;
            this._ranks = EMPTY_INT_ARRAY;
            dimensions = XmlWhitespace.collapse(dimensions, 3);
            String[] dimStrings = dimensions.split(" ");
            for (int i = 0; i < dimStrings.length; ++i) {
                String dimString = dimStrings[i];
                if (dimString.equals("*")) {
                    this._dimensions[i] = -1;
                    continue;
                }
                try {
                    this._dimensions[i] = Integer.parseInt(dimStrings[i]);
                    continue;
                }
                catch (Exception e) {
                    throw new XmlValueOutOfRangeException();
                }
            }
        } else {
            this._type = name;
            this.initDimensions(dimensions, firstbrace);
        }
    }

    public SOAPArrayType(SOAPArrayType nested, int[] dimensions) {
        this._type = nested._type;
        this._ranks = new int[nested._ranks.length + 1];
        System.arraycopy(nested._ranks, 0, this._ranks, 0, nested._ranks.length);
        this._ranks[this._ranks.length - 1] = nested._dimensions.length;
        this._dimensions = new int[dimensions.length];
        System.arraycopy(dimensions, 0, this._dimensions, 0, dimensions.length);
    }

    private void initDimensions(String s, int firstbrace) {
        ArrayList<String> braces = new ArrayList<String>();
        int lastbrace = -1;
        int i = firstbrace;
        while (i >= 0) {
            lastbrace = s.indexOf(93, i);
            if (lastbrace < 0) {
                throw new XmlValueOutOfRangeException();
            }
            braces.add(s.substring(i + 1, lastbrace));
            i = s.indexOf(91, lastbrace);
        }
        String trailer = s.substring(lastbrace + 1);
        if (!XmlWhitespace.isAllSpace(trailer)) {
            throw new XmlValueOutOfRangeException();
        }
        this._ranks = new int[braces.size() - 1];
        for (int i2 = 0; i2 < this._ranks.length; ++i2) {
            String commas = (String)braces.get(i2);
            int commacount = 0;
            for (int j = 0; j < commas.length(); ++j) {
                char ch = commas.charAt(j);
                if (ch == ',') {
                    ++commacount;
                    continue;
                }
                if (XmlWhitespace.isSpace(ch)) continue;
                throw new XmlValueOutOfRangeException();
            }
            this._ranks[i2] = commacount + 1;
        }
        this._dimensions = SOAPArrayType.internalParseCommaIntString((String)braces.get(braces.size() - 1));
    }

    public QName getQName() {
        return this._type;
    }

    public int[] getRanks() {
        int[] result = new int[this._ranks.length];
        System.arraycopy(this._ranks, 0, result, 0, result.length);
        return result;
    }

    public int[] getDimensions() {
        int[] result = new int[this._dimensions.length];
        System.arraycopy(this._dimensions, 0, result, 0, result.length);
        return result;
    }

    public boolean containsNestedArrays() {
        return this._ranks.length > 0;
    }

    public String soap11DimensionString() {
        return this.soap11DimensionString(this._dimensions);
    }

    public String soap11DimensionString(int[] actualDimensions) {
        StringBuilder sb = new StringBuilder();
        for (int rank : this._ranks) {
            sb.append('[');
            for (int j = 1; j < rank; ++j) {
                sb.append(',');
            }
            sb.append(']');
        }
        sb.append(IntStream.of(actualDimensions).mapToObj(i -> i >= 0 ? Integer.toString(i) : "").collect(Collectors.joining(",", "[", "]")));
        return sb.toString();
    }

    private SOAPArrayType() {
    }

    public static SOAPArrayType newSoap12Array(QName itemType, String arraySize) {
        arraySize = XmlWhitespace.collapse(arraySize, 3);
        String[] dimStrings = arraySize.split(" ");
        int[] dimensions = new int[dimStrings.length];
        for (int i = 0; i < dimStrings.length; ++i) {
            String dimString = dimStrings[i];
            if (i == 0 && dimString.equals("*")) {
                dimensions[i] = -1;
                continue;
            }
            try {
                dimensions[i] = Integer.parseInt(dimStrings[i]);
                continue;
            }
            catch (Exception e) {
                throw new XmlValueOutOfRangeException();
            }
        }
        SOAPArrayType sot = new SOAPArrayType();
        sot._ranks = EMPTY_INT_ARRAY;
        sot._type = itemType;
        sot._dimensions = dimensions;
        return sot;
    }

    public String soap12DimensionString(int[] actualDimensions) {
        return IntStream.of(actualDimensions).mapToObj(i -> i >= 0 ? Integer.toString(i) : "").collect(Collectors.joining(" "));
    }

    public SOAPArrayType nestedArrayType() {
        if (!this.containsNestedArrays()) {
            throw new IllegalStateException();
        }
        SOAPArrayType result = new SOAPArrayType();
        result._type = this._type;
        result._ranks = new int[this._ranks.length - 1];
        System.arraycopy(this._ranks, 0, result._ranks, 0, result._ranks.length);
        result._dimensions = new int[this._ranks[this._ranks.length - 1]];
        Arrays.fill(result._dimensions, -1);
        return result;
    }

    public int hashCode() {
        return this._type.hashCode() + this._dimensions.length + this._ranks.length + (this._dimensions.length == 0 ? 0 : this._dimensions[0]);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SOAPArrayType)) {
            return false;
        }
        SOAPArrayType sat = (SOAPArrayType)obj;
        return this._type.equals(sat._type) && Arrays.equals(this._ranks, sat._ranks) && Arrays.equals(this._dimensions, sat._dimensions);
    }
}

