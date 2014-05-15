package be.nabu.libs.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.converter.api.Converter;

public class ConverterFactory {

	private static ConverterFactory instance;
	
	public static ConverterFactory getInstance() {
		if (instance == null)
			instance = new ConverterFactory();
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
	
	public Converter getConverter() {
		if (converter == null) {
			if (converters.isEmpty()) {
				ServiceLoader<Converter> serviceLoader = ServiceLoader.load(Converter.class);
				for (Converter converter : serviceLoader)
					converters.add(converter);
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
