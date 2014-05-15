package be.nabu.libs.converter.api;

public interface Converter {
	public <T> T convert(Object instance, Class<T> targetClass);
	public boolean canConvert(Class<?> instanceClass, Class<?> targetClass);
}