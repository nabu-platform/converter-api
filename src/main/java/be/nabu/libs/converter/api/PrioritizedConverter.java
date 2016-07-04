package be.nabu.libs.converter.api;

public interface PrioritizedConverter extends Converter {
	/**
	 * Higher is better
	 */
	public int getPriority();
}
