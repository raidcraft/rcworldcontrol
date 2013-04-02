package de.raidcraft.worldcontrol.exceptions;

/**
 * @author Silthus
 */
public class NotDeepEnoughException extends Throwable {

    public NotDeepEnoughException() {

        super("Dieser Block kann nur weiter unten gesetzt werden!");
    }
}
