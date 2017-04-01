package net.amarantha.utils.colour;

import static net.amarantha.utils.math.MathUtils.bound;
import static net.amarantha.utils.math.MathUtils.round;

public class RGBW extends RGB {

    private final int white;

    public RGBW(int red, int green, int blue, int white) {
        super(red, green, blue);
        this.white = bound(0, MAX, white);
    }

    public RGBW withBrightness(double brightness) {
        int r = round(red*brightness);
        int g = round(green*brightness);
        int b = round(blue*brightness);
        int w = round(white*brightness);
        return new RGBW(r,g,b,w);
    }

    public int getWhite() {
        return white;
    }

    public static RGB[] convertToRGB(RGBW[] input) {
        RGB[] output = new RGB[((input.length/3)*4)];
        int j=0;
        for ( int i=0; i<input.length; i+=3 ) {
            RGBW c1 = input[i];
            RGBW c2 = i+1 < input.length ? input[i+1] : new RGBW(0,0,0,0);
            RGBW c3 = i+2 < input.length ? input[i+2] : new RGBW(0,0,0,0);
            if ( c1!=null && j<output.length ) output[j++] =
                    new RGB(c1.getGreen(),      c1.getRed(),  c1.getBlue());
            if ( c1!=null && c2!=null && j<output.length ) output[j++] =
                    new RGB(c1.getWhite(),    c2.getGreen(),    c2.getRed());
            if ( c2!=null && c3!=null && j<output.length ) output[j++] =
                    new RGB(c2.getBlue(),     c2.getWhite(),  c3.getGreen());
            if ( c3!=null && j<output.length ) output[j++] =
                    new RGB(c3.getRed(),    c3.getBlue(),   c3.getWhite());
        }
        for ( int k=j; k<output.length; k++ ) {
            output[k] = new RGB(0,0,0);
        }
        return output;
    }

    @Override
    public String toString() {
        return "RGBW{"+getRed()+","+getGreen()+","+getBlue()+","+white+"}";
    }

}
