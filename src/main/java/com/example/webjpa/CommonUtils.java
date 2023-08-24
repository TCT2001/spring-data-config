package com.example.webjpa;

import com.example.webjpa.exceptions.NullPropertyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

public class CommonUtils {
    private CommonUtils() throws IllegalAccessException {
        throw new IllegalAccessException("CommonUtils");
    }

    public static String getNonNullPropertyOrThrow(Environment environment, String key) {
        var property = environment.getProperty(key);
        if (property == null) {
            throw new NullPropertyException("Property %s can't be null".formatted(key));
        }
        return property;
    }

    public static String getNullableProperty(Environment environment, String key) {
        return environment.getProperty(key);
    }

    public static String[] getNullPropertyNames(final Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Stream.of(src.getPropertyDescriptors())
                .parallel()
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> {
                    Object srcValue = src.getPropertyValue(propertyName);
                    return srcValue == null || (srcValue instanceof String && ((String) srcValue).isEmpty());
                }).toArray(String[]::new);
    }

    public static void copyProperties(@NonNull final Object source, @NonNull final Object target) throws BeansException {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }
}
