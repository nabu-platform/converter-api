# Description

The converter library allows you to plug in converters to perform auto-conversion between types in java.
The API package itself holds no actual converters which means by default you can't convert anything.
You can plug in "converter-base" which provides a number of default converters or you can write your own.

# Examples

These examples come from the testcases of converter-base:

```java
// the factory instance can be configured using OSGI, if unavailable it uses SPI or manual configuration
Converter converter = ConverterFactory.getInstance().getConverter();

assertTrue(converter.canConvert(
	Boolean.class, 
	String.class)
);

assertEquals(
	1.0, 
	converter.convert("1", Double.class)
);

assertTrue(
	converter.convert("true", Boolean.class)
);
```

It will not automatically perform conversions that can not be guaranteed to be accurate, for example if you have a date, you can't convert it to string or the other way around.
Such logic would need additional information like timezone, format, locale,...

# Design decisions

The factory pattern used by the converter api is a pattern I use in many of my other frameworks. Initially it supported SPI and OSGi.
However in some cases both don't work so I added a third custom service loading mechanism.

This service loading mechanism was initially located by using the thread context classloader. However this is not always correct, for example I have a maven classloader that can **not** set the thread context classloader (because it is not valid for the entire thread) but that **must** be used to resolve the classes.

To this end I have reverted the context classloader into a search using the classloader that loaded the factory class. This seems to work in both environments I was targetting (JBoss AS and my own server).