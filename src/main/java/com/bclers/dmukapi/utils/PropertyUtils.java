package com.bclers.dmukapi.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertyUtils
{
    @SneakyThrows
    public static Properties loadPropertiesFile(String fileName)
    {
        InputStream input = PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);

        Properties props = new Properties();
        props.load(input);

        return props;
    }
}
