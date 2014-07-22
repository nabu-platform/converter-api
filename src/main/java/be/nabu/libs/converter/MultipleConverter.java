package be.nabu.libs.converter;

import java.util.List;

import be.nabu.libs.converter.api.Converter;

public class MultipleConverter implements Converter {

	private List<Converter> converters;
	private boolean throwException = true;
	
	public MultipleConverter(List<Converter> converters) {
		this.converters = converters;
		// set throwException of nested multiple converters to false because this converter will throw the exception if necessary
		for (Converter converter : converters) {
			if (converter instanceof MultipleConverter)
				((MultipleConverter) converter).throwException = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object instance, Class<T> targetClass) {
		if (instance == null) {
			return null;
		}
		else if (instance.getClass().equals(targetClass)) {
			return (T) instance;
		}
		T converted = null;
		for (Converter converter : converters) {
			converted = converter.convert(instance, targetClass);
			if (converted != null)
				break;
		}
		// if we did not find a specific converter but the instance is a superclass, just return it
		if (converted == null && instance != null) {
			if (targetClass.isAssignableFrom(instance.getClass()))
				return (T) instance;
			else if (throwException)
				throw new ClassCastException("Can not convert " + instance.getClass() + " to " + targetClass);
		}
		return converted;
	}

	@Override
	public boolean canConvert(Class<?> instanceClass, Class<?> targetClass) {
		for (Converter converter : converters) {
			if (converter.canConvert(instanceClass, targetClass))
				return true;
		}
		return targetClass.isAssignableFrom(instanceClass);
	}

}
