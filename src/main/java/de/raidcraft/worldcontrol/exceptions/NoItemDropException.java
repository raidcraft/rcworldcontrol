package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class NoItemDropException extends Throwable {

	public NoItemDropException() {
		super("Dieser Block dropt hier kein Item!");
	}
}
