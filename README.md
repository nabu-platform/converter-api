# Description

The converter library allows you to plug in converters to perform auto-conversion between types in java.
The API package itself holds no actual converters which means by default you can't convert anything.
You can plug in "converter-base" which provides a number of default converters or you can write your own.

# Examples

These examples come from the testcases of converter-base:

```java
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