package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class FarmOnlyException extends Throwable {

    public FarmOnlyException() {

        super("Dieser Block lässt sich nur in Farmen nutzen!");
    }
}
