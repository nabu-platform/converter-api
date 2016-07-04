package be.nabu.libs.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.converter.api.Converter;
import be.nabu.libs.converter.api.PrioritizedConverter;

public class ConverterFactory {

	private static ConverterFactory instance;
	
	public static ConverterFactory getInstance() {
		if (instance == null) {
			synchronized(ConverterFactory.class) {
				if (instance == null) {
					instance = new ConverterFactory();
				}
			}
		}
		return instance;
	}
	
	private List<Converter> converters = new ArrayList<Converter>();
	private Converter converter;
	
	public void addConverter(Converter converter) {
		this.converters.add(converter);
	}
	
	public void removeConverter(Converter converter) {
		this.converters.remove(converter);
	}
	
	@SuppressWarnings("unchecked")
	public Converter getConverter() {
		if (converter == null) {
			if (converters.isEmpty()) {
				try {
					// let's try this with custom service loading based on a configuration
					Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
					Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
					converters.addAll((List<Converter>) declaredMethod.invoke(null, Converter.class));
				}
				catch (ClassNotFoundException e) {
					// ignore, the framework is not present
				}
				catch (NoSuchMethodException e) {
					// corrupt framework?
					throw new RuntimeException(e);
				}
				catch (SecurityException e) {
					throw new RuntimeException(e);
				}
				catch (IllegalAccessException e) {
					// ignore
				}
				catch (InvocationTargetException e) {
					// ignore
				}
				// if there are still no instances, fall back to SPI
				// it is actually possible that you _are_ using the custom service loader, but 
				if (converters.isEmpty()) {
					ServiceLoader<Converter> serviceLoader = ServiceLoader.load(Converter.class);
					for (Converter converter : serviceLoader) {
						converters.add(converter);
					}
				}
				Collections.sort(converters, new Comparator<Converter>() {
					@Override
					public int compare(Converter o1, Converter o2) {
						int priority1 = o1 instanceof PrioritizedConverter ? ((PrioritizedConverter) o1).getPriority() : 0;
						int priority2 = o2 instanceof PrioritizedConverter ? ((PrioritizedConverter) o2).getPriority() : 0;
						return priority2 - priority1;
					}
				});
			}
			converter = new MultipleConverter(converters);
		}
		return converter;
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
	
}
