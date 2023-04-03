package com.elcilc.clicle.utils;

public class ClassUtils {

    public static <T> T getSafeCastInstance(Object o, Class<T> clazz) {
         return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }

}
