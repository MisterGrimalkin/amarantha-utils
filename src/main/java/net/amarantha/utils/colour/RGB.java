package net.amarantha.utils.colour;

import static net.amarantha.utils.math.MathUtils.*;

/**
 * Immutable Representation of a simple RGB colour
 */
public class RGB {

    protected final int red;
    protected final int green;
    protected final int blue;

    public RGB(int red, int green, int blue) {
        this.red =   bound(0, MAX, red);
        this.green = bound(0, MAX, green);
        this.blue =  bound(0, MAX, blue);
    }

    ///////////////////////////
    // Brightness Adjustment //
    ///////////////////////////

    /**
     * Create a new colour with this colour's values adjusted
     * @param brightness Adjustment, where 1=unchanged
     * @return A new RGB colour
     */
    public RGB withBrightness(double brightness) {
        int r = round(red*brightness);
        int g = round(green*brightness);
        int b = round(blue*brightness);
        return new RGB(r, g, b);
    }

    ///////////////////
    // RGB Accessors //
    ///////////////////

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    ///////////////////
    // HSL Accessors //
    ///////////////////

    public int getHue() {
        double hue;
        Double[] norm = normalised();
        int rawMax = max(red, green, blue);
        if ( rawMax==red ) {
            hue = (norm[G]-norm[B])/(norm[MX]-norm[MN]);
        } else if ( rawMax==green ) {
            hue = 2 + (norm[B]-norm[R])/(norm[MX]-norm[MN]);
        } else {
            hue = 4 + (norm[R]-norm[G])/(norm[MX]-norm[MN]);
        }
        hue *= 60;
        if ( hue < 0 ) {
            hue += 360;
        }
        return round(hue);
    }

    public int getSaturation() {
        Double[] norm = normalised();
        if ( norm[MX].equals(norm[MN]) ) {
            return 0;
        }
        int luminance = getLuminance();
        if ( luminance < 50 ) {
            return round(100 * ((norm[MX]-norm[MN])/(norm[MX]+norm[MN])));
        } else {
            return round(100 * ((norm[MX]-norm[MN])/(2-norm[MX]-norm[MN])));
        }
    }

    public int getLuminance() {
        Double[] norm = normalised();
        return round(100*((norm[MN] + norm[MX]) / 2));
    }

    ///////////////////
    // Normalisation //
    ///////////////////

    public static final int R = 0;
    public static final int G = 1;
    public static final int B = 2;
    public static final int MX = 3;
    public static final int MN = 4;

    private Double[] normalisedValues;

    /**
     * Converts RGB values in range 0..255 to normalised values 0..1
     * and includes the max and min normalised values
     * @return Array of normalised values: { RED, GREEN, BLUE, MAX, MIN }
     */
    public Double[] normalised() {
        if ( normalisedValues==null ) {
            double normR = (double)red / MAX;
            double normG = (double)green / MAX;
            double normB = (double)blue / MAX;
            normalisedValues = new Double[5];
            normalisedValues[R] = normR;
            normalisedValues[G] = normG;
            normalisedValues[B] = normB;
            normalisedValues[MX] = max(normR, normG, normB);
            normalisedValues[MN] = min(normR, normG, normB);
        }
        return normalisedValues;
    }

    /////////////////////
    // Text Conversion //
    /////////////////////

    public static RGB parse(String input) {
        String[] rgb = input.split(",");
        if ( rgb.length==3 ) {
            int r = Integer.parseInt(rgb[0].trim());
            int g = Integer.parseInt(rgb[1].trim());
            int b = Integer.parseInt(rgb[2].trim());
            return new RGB(r, g, b);
        } else throw new NumberFormatException("Bad RGB");
    }

    public String stateSymbol() {
        return (red==0&&green==0&&blue==0 ) ? "-" : "#" ;
    }

    public String rgbString() {
        return "RGB{"+red+","+green+","+blue+"}";
    }

    public String hslString() {
        return "HSL{"+getHue()+","+getSaturation()+","+getLuminance()+"}";
    }

    @Override
    public String toString() {
        return red+","+green+","+blue;
    }

    //////////////
    // Equality //
    //////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RGB rgb = (RGB) o;
        if (red != rgb.red) return false;
        if (green != rgb.green) return false;
        return blue == rgb.blue;
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + green;
        result = 31 * result + blue;
        return result;
    }

    ///////////////////
    // Basic Colours //
    ///////////////////

    protected static final int MAX = 255;

    public static final RGB BLACK =     new RGB(0, 0, 0);
    public static final RGB RED =       new RGB(MAX, 0, 0);
    public static final RGB GREEN =     new RGB(0, MAX, 0);
    public static final RGB BLUE =      new RGB(0, 0, MAX);
    public static final RGB CYAN =      new RGB(0, MAX, MAX);
    public static final RGB MAGENTA =   new RGB(MAX, 0, MAX);
    public static final RGB YELLOW =    new RGB(MAX, MAX, 0);
    public static final RGB WHITE =     new RGB(MAX, MAX, MAX);

}
