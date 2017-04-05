package net.amarantha.utils.reflection;

import org.junit.Assert;
import org.junit.Test;

public class ReflectionTest {

    static class A {
        private int a = 96;
    }

    static class B extends A {
        private int b = 69;
    }

    @Test
    public void testReflectiveGet() {

        B object = new B();

        Assert.assertEquals(96, ((Integer)ReflectionUtils.reflectiveGet(object, "a")).intValue());
        Assert.assertEquals(69, ((Integer)ReflectionUtils.reflectiveGet(object, "b")).intValue());

    }
}
