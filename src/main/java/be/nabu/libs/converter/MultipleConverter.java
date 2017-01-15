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
		targetClass = (Class<T>) box(targetClass);
		if (instance == null) {
			return null;
		}
		else if (instance.getClass().equals(targetClass)) {
			return (T) instance;
		}
		else if (!(instance instanceof Object[]) && targetClass.isArray() && targetClass.getComponentType().isAssignableFrom(instance.getClass())) {
			Object [] array = (Object[]) java.lang.reflect.Array.newInstance(targetClass.getComponentType(), 1);
			array[0] = instance;
			return (T) array;
		}
		T converted = null;
		for (Converter converter : converters) {
			converted = converter.convert(instance, targetClass);
			if (converted != null) {
				break;
			}
		}
		// if we did not find a specific converter but the instance is a superclass, just return it
		if (converted == null && instance != null) {
			if (targetClass.isAssignableFrom(instance.getClass())) {
				return (T) instance;
			}
			else if (throwException) {
				throw new ClassCastException("Can not convert " + instance.getClass() + " to " + targetClass + ": " + instance);
			}
		}
		return converted;
	}

	@Override
	public boolean canConvert(Class<?> instanceClass, Class<?> targetClass) {
		instanceClass = box(instanceClass);
		targetClass = box(targetClass);
		for (Converter converter : converters) {
			if (converter.canConvert(instanceClass, targetClass)) {
				return true;
			}
		}
		return targetClass.isAssignableFrom(instanceClass);
	}
	
	private static Class<?> box(Class<?> clazz) {
		if (int.class.equals(clazz)) {
			return Integer.class;
		}
		else if (long.class.equals(clazz)) {
			return Long.class;
		}
		else if (double.class.equals(clazz)) {
			return Double.class;
		}
		else if (float.class.equals(clazz)) {
			return Float.class;
		}
		else if (short.class.equals(clazz)) {
			return Short.class;
		}
		else if (byte.class.equals(clazz)) {
			return Byte.class;
		}
		else if (char.class.equals(clazz)) {
			return Character.class;
		}
		else if (void.class.equals(clazz)) {
			return Void.class;
		}
		else if (boolean.class.equals(clazz)) {
			return Boolean.class;
		}
		else {
			return clazz;
		}
	}

}
