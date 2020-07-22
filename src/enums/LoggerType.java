// SPDX-License-Identifier: GPL-3.0-or-later

package logger.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Types de log
 *
 * @author Romain
 */
public enum LoggerType {
    INFO("INFO", LoggerColor.BLUE),
    SUCCESS("SUCCESS", LoggerColor.GREEN),
    ERROR("ERROR", LoggerColor.RED),
    WARNING("WARNING", LoggerColor.YELLOW),
    DEBUG("DEBUG", LoggerColor.PURPLE);

    /**
     * Nom du type
     */
    @NotNull
    private final String msg;

    /**
     * Couleur associée au type
     */
    @NotNull
    private final LoggerColor textColor;

    /**
     * Constructeur
     *
     * @param msg Message
     * @param textColor Couleur
     */
    LoggerType(@NotNull String msg, @NotNull LoggerColor textColor) {
        this.msg = msg;
        this.textColor = textColor;
    }

    /**
     * To String
     *
     * @return Message associé
     */
    @Override
    public String toString() {
        return msg.toUpperCase();
    }

    /**
     * Récupère la couleur associée à une enum
     *
     * @return Couleur ANSI
     */
    @NotNull
    public LoggerColor getTextColor() {
        return textColor;
    }
}
