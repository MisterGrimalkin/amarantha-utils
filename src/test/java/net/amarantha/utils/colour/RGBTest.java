package net.amarantha.utils.colour;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RGBTest {

    @Test
    public void testCreation() {
        RGB test = new RGB(50, 60, 70);
        assertEquals(50, test.getRed());
        assertEquals(60, test.getGreen());
        assertEquals(70, test.getBlue());
    }

    @Test
    public void testBounds() {
        RGB test = new RGB(-1, 10, 256);
        assertEquals(new RGB(0, 10, 255), test);
    }

    @Test
    public void testBrightness() {
        RGB test = new RGB(100,100,100);
        RGB half = test.withBrightness(0.5);
        assertEquals(new RGB(50,50,50), half);
    }

    @Test
    public void testHSLConversion() {

        // Tests using: http://www.rapidtables.com/convert/color/rgb-to-hsl.htm
        compareColours(0, 0, 0,        0, 0, 0);
        compareColours(255, 0, 0,      0, 100, 50);
        compareColours(66, 0, 0,       0, 100, 13);
        compareColours(53, 174, 0,     102, 100, 34);
        compareColours(106, 76, 196,   255, 50, 53);
        compareColours(83, 138, 70,    109, 33, 41);
        compareColours(100, 100, 100,  0, 0, 39);
        compareColours(100, 255, 100,  120, 100, 70);
        compareColours(255, 255, 255,  0, 0, 100);

    }

    private void compareColours(int red, int green, int blue, int hue, int saturation, int luminance) {
        RGB colour = new RGB(red, green, blue);
        assertEquals(hue, colour.getHue());
        assertEquals(saturation, colour.getSaturation());
        assertEquals(luminance, colour.getLuminance());
    }

}
