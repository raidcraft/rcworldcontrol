package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class LocalPlaceLimitReachedException extends Throwable {

    public LocalPlaceLimitReachedException() {

        super("Dieser Block wurde hier in der Gegend schon zu oft gesetzt!");
    }
}
