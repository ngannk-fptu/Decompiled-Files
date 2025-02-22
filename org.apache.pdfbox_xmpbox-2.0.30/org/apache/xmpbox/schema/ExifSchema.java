/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="exif", namespace="http://ns.adobe.com/exif/1.0/")
public class ExifSchema
extends XMPSchema {
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String USER_COMMENT = "UserComment";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String EXIF_VERSION = "ExifVersion";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String FLASH_PIX_VERSION = "FlashpixVersion";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String COLOR_SPACE = "ColorSpace";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String COMPONENTS_CONFIGURATION = "ComponentsConfiguration";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String COMPRESSED_BPP = "CompressedBitsPerPixel";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String PIXEL_X_DIMENSION = "PixelXDimension";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String PIXEL_Y_DIMENSION = "PixelYDimension";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String RELATED_SOUND_FILE = "RelatedSoundFile";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String DATE_TIME_ORIGINAL = "DateTimeOriginal";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String DATE_TIME_DIGITIZED = "DateTimeDigitized";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String EXPOSURE_TIME = "ExposureTime";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String F_NUMBER = "FNumber";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String EXPOSURE_PROGRAM = "ExposureProgram";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String SPECTRAL_SENSITIVITY = "SpectralSensitivity";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String ISO_SPEED_RATINGS = "ISOSpeedRatings";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String APERTURE_VALUE = "ApertureValue";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String BRIGHTNESS_VALUE = "BrightnessValue";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String EXPOSURE_BIAS_VALUE = "ExposureBiasValue";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String MAX_APERTURE_VALUE = "MaxApertureValue";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String SUBJECT_DISTANCE = "SubjectDistance";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String METERING_MODE = "MeteringMode";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String LIGHT_SOURCE = "LightSource";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String FLASH_ENERGY = "FlashEnergy";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String FOCAL_LENGTH = "FocalLength";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String FOCAL_PLANE_XRESOLUTION = "FocalPlaneXResolution";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String FOCAL_PLANE_YRESOLUTION = "FocalPlaneYResolution";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String SUBJECT_AREA = "SubjectArea";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String SUBJECT_LOCATION = "SubjectLocation";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String EXPOSURE_INDEX = "ExposureIndex";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SENSING_METHOD = "SensingMethod";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String FILE_SOURCE = "FileSource";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SCENE_TYPE = "SceneType";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String CUSTOM_RENDERED = "CustomRendered";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String WHITE_BALANCE = "WhiteBalance";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String EXPOSURE_MODE = "ExposureMode";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String DIGITAL_ZOOM_RATIO = "DigitalZoomRatio";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String FOCAL_LENGTH_IN_3_5MM_FILM = "FocalLengthIn35mmFilm";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SCENE_CAPTURE_TYPE = "SceneCaptureType";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String GAIN_CONTROL = "GainControl";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String CONTRAST = "Contrast";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SATURATION = "Saturation";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SHARPNESS = "Sharpness";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String IMAGE_UNIQUE_ID = "ImageUniqueID";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPSVERSION_ID = "GPSVersionID";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_SATELLITES = "GPSSatellites";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_STATUS = "GPSStatus";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_MEASURE_MODE = "GPSMeasureMode";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_MAP_DATUM = "GPSMapDatum";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_SPEED_REF = "GPSSpeedRef";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_TRACK_REF = "GPSTrackRef";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_DEST_BEARING_REF = "GPSDestBearingRef";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_PROCESSING_METHOD = "GPSProcessingMethod";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String GPS_AREA_INFORMATION = "GPSAreaInformation";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_ALTITUDE = "GPSAltitude";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_DOP = "GPSDOP";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_SPEED = "GPSSpeed";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_TRACK = "GPSTrack";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_IMG_DIRECTION = "GPSImgDirection";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_DEST_BEARING = "GPSDestBearing";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String GPS_DEST_DISTANCE = "GPSDestDistance";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String GPS_ALTITUDE_REF = "GPSAltitudeRef";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String GPS_DIFFERENTIAL = "GPSDifferential";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String GPS_TIME_STAMP = "GPSTimeStamp";
    @PropertyType(type=Types.OECF)
    public static final String OECF = "OECF";
    @PropertyType(type=Types.OECF)
    public static final String SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse";
    @PropertyType(type=Types.GPSCoordinate)
    public static final String GPS_LATITUDE = "GPSLatitude";
    @PropertyType(type=Types.GPSCoordinate)
    public static final String GPS_LONGITUDE = "GPSLongitude";
    @PropertyType(type=Types.GPSCoordinate)
    public static final String GPS_DEST_LATITUDE = "GPSDestLatitude";
    @PropertyType(type=Types.GPSCoordinate)
    public static final String GPS_DEST_LONGITUDE = "GPSDestLongitude";
    @PropertyType(type=Types.CFAPattern)
    public static final String CFA_PATTERN = "CFAPattern";
    @PropertyType(type=Types.Flash)
    public static final String FLASH = "Flash";
    @PropertyType(type=Types.CFAPattern)
    public static final String CFA_PATTERN_TYPE = "CFAPatternType";
    @PropertyType(type=Types.DeviceSettings)
    public static final String DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription";

    public ExifSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public ExifSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public ArrayProperty getUserCommentProperty() {
        return (ArrayProperty)this.getProperty(USER_COMMENT);
    }

    public List<String> getUserCommentLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(USER_COMMENT);
    }

    public String getUserComment(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(USER_COMMENT, lang);
    }

    public String getUserComment() {
        return this.getUserComment(null);
    }
}

