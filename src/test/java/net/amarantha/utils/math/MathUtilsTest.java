package net.amarantha.utils.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathUtilsTest {

    @Test
    public void testMaxMin() {

        Double[] testDoubles = new Double[] { 1.0, 5.6, 2.8, -1.8, 0.0 };
        assertEquals(5.6, MathUtils.max(testDoubles), 0.01);
        assertEquals(-1.8, MathUtils.min(testDoubles), 0.01);

        Integer[] testIntegers = new Integer[] { 1, 56, 28, -18, 0 };
        assertEquals((long)56, (long)MathUtils.max(testIntegers));
        assertEquals((long)-18, (long)MathUtils.min(testIntegers));

    }

    @Test
    public void testRoundBound() {

        assertEquals(7, MathUtils.bound(5, 10, 7));
        assertEquals(5, MathUtils.bound(5, 10, 2));
        assertEquals(10, MathUtils.bound(5, 10, 25));

        assertEquals(5, MathUtils.round(5.2));
        assertEquals(6, MathUtils.round(5.8));


    }

}
