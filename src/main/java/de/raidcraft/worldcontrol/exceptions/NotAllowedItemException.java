package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class NotAllowedItemException extends Throwable {

	public NotAllowedItemException() {
		super("Dieser Block ist hier verboten!");
	}
}
