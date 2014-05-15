package be.nabu.libs.converter;

import java.util.List;

import be.nabu.libs.converter.api.Converter;

public class MultipleConverter implements Converter {

	private List<Converter> converters;
	
	public MultipleConverter(List<Converter> converters) {
		this.converters = converters;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object instance, Class<T> targetClass) {
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
			else
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
