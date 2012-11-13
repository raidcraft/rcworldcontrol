package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class UnknownAllowedItemException extends Throwable {

	public UnknownAllowedItemException() {
		super("Dieser Block ist hier verboten!");
	}
}
