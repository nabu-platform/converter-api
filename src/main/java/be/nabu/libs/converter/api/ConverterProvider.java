package be.nabu.libs.converter.api;

public interface ConverterProvider<S, T> {
	public T convert(S instance);
	public Class<S> getSourceClass();
	public Class<T> getTargetClass();
}
