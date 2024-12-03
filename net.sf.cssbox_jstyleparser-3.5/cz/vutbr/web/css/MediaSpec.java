/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.css.TermResolution;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MediaSpec {
    public static final float em = 16.0f;
    public static final float ex = 10.0f;
    public static final float dpi = 96.0f;
    protected static Map<String, Feature> featureMap = new HashMap<String, Feature>(13);
    protected String type;
    protected float width;
    protected float height;
    protected float deviceWidth;
    protected float deviceHeight;
    protected int color;
    protected int colorIndex;
    protected int monochrome;
    protected float resolution;
    protected boolean scanInterlace;
    protected int grid;

    public MediaSpec(String type) {
        this.loadDefaults();
        this.type = type.trim().toLowerCase(Locale.ENGLISH);
    }

    public String getType() {
        return this.type;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getDeviceWidth() {
        return this.deviceWidth;
    }

    public void setDeviceWidth(float deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    public float getDeviceHeight() {
        return this.deviceHeight;
    }

    public void setDeviceHeight(float deviceHeight) {
        this.deviceHeight = deviceHeight;
    }

    public void setDeviceDimensions(float deviceWidth, float deviceHeight) {
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public int getMonochrome() {
        return this.monochrome;
    }

    public void setMonochrome(int monochrome) {
        this.monochrome = monochrome;
    }

    public float getResolution() {
        return this.resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }

    public boolean isScanInterlace() {
        return this.scanInterlace;
    }

    public void setScanInterlace(boolean scanInterlace) {
        this.scanInterlace = scanInterlace;
    }

    public int getGrid() {
        return this.grid;
    }

    public void setGrid(int grid) {
        this.grid = grid;
    }

    public float getAspectRatio() {
        return this.width / this.height;
    }

    public float getDeviceAspectRation() {
        return this.deviceWidth / this.deviceHeight;
    }

    public boolean isPortrait() {
        return this.height >= this.width;
    }

    public boolean matches(MediaQuery q) {
        if (q.getType() != null && (q.getType().equals("all") ? q.isNegative() : q.getType().equals(this.getType()) == q.isNegative())) {
            return false;
        }
        for (MediaExpression e : q) {
            if (this.matches(e)) continue;
            return false;
        }
        return true;
    }

    public boolean matches(MediaExpression e) {
        String fs = e.getFeature();
        boolean isMin = false;
        boolean isMax = false;
        if (fs.startsWith("min-")) {
            isMin = true;
            fs = fs.substring(4);
        } else if (fs.startsWith("max-")) {
            isMax = true;
            fs = fs.substring(4);
        }
        Feature feature = this.getFeatureByName(fs);
        if (feature != null && (!isMin && !isMax || feature.isPrefixed())) {
            switch (feature) {
                case WIDTH: {
                    return this.valueMatches(this.getExpressionLengthPx(e), this.width, isMin, isMax);
                }
                case HEIGHT: {
                    return this.valueMatches(this.getExpressionLengthPx(e), this.height, isMin, isMax);
                }
                case DEVICE_WIDTH: {
                    return this.valueMatches(this.getExpressionLengthPx(e), this.deviceWidth, isMin, isMax);
                }
                case DEVICE_HEIGHT: {
                    return this.valueMatches(this.getExpressionLengthPx(e), this.deviceHeight, isMin, isMax);
                }
                case ORIENTATION: {
                    String oid = this.getExpressionIdentifier(e);
                    if (oid == null) {
                        return false;
                    }
                    if (oid.equals("portrait")) {
                        return this.isPortrait();
                    }
                    if (oid.equals("landscape")) {
                        return !this.isPortrait();
                    }
                    return false;
                }
                case ASPECT_RATIO: {
                    return this.valueMatches(this.getExpressionRatio(e), this.getAspectRatio(), isMin, isMax);
                }
                case DEVICE_ASPECT_RATIO: {
                    return this.valueMatches(this.getExpressionRatio(e), this.getDeviceAspectRation(), isMin, isMax);
                }
                case COLOR: {
                    return this.valueMatches(this.getExpressionInteger(e), this.color, isMin, isMax);
                }
                case COLOR_INDEX: {
                    return this.valueMatches(this.getExpressionInteger(e), this.colorIndex, isMin, isMax);
                }
                case MONOCHROME: {
                    return this.valueMatches(this.getExpressionInteger(e), this.monochrome, isMin, isMax);
                }
                case RESOLUTION: {
                    return this.valueMatches(this.getExpressionResolution(e), this.resolution, isMin, isMax);
                }
                case SCAN: {
                    String sid = this.getExpressionIdentifier(e);
                    if (sid == null) {
                        return false;
                    }
                    if (sid.equals("progressive")) {
                        return !this.scanInterlace;
                    }
                    if (sid.equals("interlace")) {
                        return this.scanInterlace;
                    }
                    return false;
                }
                case GRID: {
                    Integer gval = this.getExpressionInteger(e);
                    if (gval == null) {
                        return false;
                    }
                    if (gval == 0 || gval == 1) {
                        return gval == this.grid;
                    }
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    public boolean matchesOneOf(List<MediaQuery> queries) {
        for (MediaQuery q : queries) {
            if (!this.matches(q)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesEmpty() {
        return true;
    }

    protected Feature getFeatureByName(String name) {
        return featureMap.get(name);
    }

    protected boolean valueMatches(Float required, float current, boolean min, boolean max) {
        if (required != null) {
            if (min) {
                return current >= required.floatValue();
            }
            if (max) {
                return current <= required.floatValue();
            }
            return current == required.floatValue();
        }
        return false;
    }

    protected boolean valueMatches(Integer required, int current, boolean min, boolean max) {
        if (required != null) {
            if (min) {
                return current >= required;
            }
            if (max) {
                return current <= required;
            }
            return current == required;
        }
        return false;
    }

    protected Float getExpressionLengthPx(MediaExpression e) {
        if (e.size() == 1) {
            Term term = (Term)e.get(0);
            if (term instanceof TermLength) {
                return this.pxLength((TermLength)term);
            }
            return null;
        }
        return null;
    }

    protected Float getExpressionResolution(MediaExpression e) {
        if (e.size() == 1) {
            Term term = (Term)e.get(0);
            if (term instanceof TermResolution) {
                return this.dpiResolution((TermResolution)term);
            }
            return null;
        }
        return null;
    }

    protected Float getExpressionRatio(MediaExpression e) {
        if (e.size() == 2) {
            Term term1 = (Term)e.get(0);
            Term term2 = (Term)e.get(1);
            if (term1 instanceof TermInteger && term2 instanceof TermInteger && ((TermInteger)term2).getOperator() == Term.Operator.SLASH) {
                return Float.valueOf(((Float)((TermInteger)term1).getValue()).floatValue() / ((Float)((TermInteger)term2).getValue()).floatValue());
            }
            return null;
        }
        return null;
    }

    protected Integer getExpressionInteger(MediaExpression e) {
        if (e.size() == 1) {
            Term term = (Term)e.get(0);
            if (term instanceof TermInteger) {
                return ((TermInteger)term).getIntValue();
            }
            return null;
        }
        return null;
    }

    protected String getExpressionIdentifier(MediaExpression e) {
        if (e.size() == 1) {
            Term term = (Term)e.get(0);
            if (term instanceof TermIdent) {
                return ((String)((TermIdent)term).getValue()).trim().toLowerCase(Locale.ENGLISH);
            }
            return null;
        }
        return null;
    }

    protected Float pxLength(TermLength spec) {
        float nval = ((Float)spec.getValue()).floatValue();
        TermNumeric.Unit unit = spec.getUnit();
        switch (unit) {
            case pt: {
                return Float.valueOf(nval * 96.0f / 72.0f);
            }
            case in: {
                return Float.valueOf(nval * 96.0f);
            }
            case cm: {
                return Float.valueOf(nval * 96.0f / 2.54f);
            }
            case mm: {
                return Float.valueOf(nval * 96.0f / 25.4f);
            }
            case q: {
                return Float.valueOf(nval * 96.0f / 101.6f);
            }
            case pc: {
                return Float.valueOf(nval * 12.0f * 96.0f / 72.0f);
            }
            case px: {
                return Float.valueOf(nval);
            }
            case em: {
                return Float.valueOf(16.0f * nval);
            }
            case ex: {
                return Float.valueOf(10.0f * nval);
            }
        }
        return null;
    }

    protected Float dpiResolution(TermResolution spec) {
        float nval = ((Float)spec.getValue()).floatValue();
        TermNumeric.Unit unit = spec.getUnit();
        switch (unit) {
            case dpi: {
                return Float.valueOf(nval);
            }
            case dpcm: {
                return Float.valueOf(nval * 2.54f);
            }
            case dppx: {
                return Float.valueOf(nval * this.getResolution());
            }
        }
        return null;
    }

    protected void loadDefaults() {
        this.width = 1100.0f;
        this.height = 850.0f;
        this.deviceWidth = 1920.0f;
        this.deviceHeight = 1200.0f;
        this.color = 8;
        this.colorIndex = 0;
        this.monochrome = 0;
        this.resolution = 96.0f;
        this.scanInterlace = false;
        this.grid = 0;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(this.type).append('[');
        ret.append("width:").append(this.width).append("; ");
        ret.append("height:").append(this.height).append("; ");
        ret.append("device-width:").append(this.deviceWidth).append("; ");
        ret.append("device-height:").append(this.deviceHeight).append("; ");
        ret.append("color:").append(this.color).append("; ");
        ret.append("color-index:").append(this.colorIndex).append("; ");
        ret.append("monochrome:").append(this.monochrome).append("; ");
        ret.append("resolution:").append(this.resolution).append("; ");
        ret.append("scan:").append(this.scanInterlace ? "interlace" : "progressive").append("; ");
        ret.append("grid:").append(this.grid).append(";");
        ret.append(']');
        return ret.toString();
    }

    static {
        featureMap.put("width", Feature.WIDTH);
        featureMap.put("height", Feature.HEIGHT);
        featureMap.put("device-width", Feature.DEVICE_WIDTH);
        featureMap.put("device-height", Feature.DEVICE_HEIGHT);
        featureMap.put("orientation", Feature.ORIENTATION);
        featureMap.put("aspect-ratio", Feature.ASPECT_RATIO);
        featureMap.put("device-aspect-ratio", Feature.DEVICE_ASPECT_RATIO);
        featureMap.put("color", Feature.COLOR);
        featureMap.put("color-index", Feature.COLOR_INDEX);
        featureMap.put("monochrome", Feature.MONOCHROME);
        featureMap.put("resolution", Feature.RESOLUTION);
        featureMap.put("scan", Feature.SCAN);
        featureMap.put("grid", Feature.GRID);
    }

    public static enum Feature {
        WIDTH(true),
        HEIGHT(true),
        DEVICE_WIDTH(true),
        DEVICE_HEIGHT(true),
        ORIENTATION(false),
        ASPECT_RATIO(true),
        DEVICE_ASPECT_RATIO(true),
        COLOR(true),
        COLOR_INDEX(true),
        MONOCHROME(true),
        RESOLUTION(true),
        SCAN(false),
        GRID(false);

        private boolean prefixed;

        private Feature(boolean prefixed) {
            this.prefixed = prefixed;
        }

        public boolean isPrefixed() {
            return this.prefixed;
        }
    }
}

