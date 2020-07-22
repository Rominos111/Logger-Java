// SPDX-License-Identifier: GPL-3.0-or-later

package logger.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Couleurs ANSI pour le log via System.out et System.err
 *
 * @author Romain
 */
@SuppressWarnings("unused")
public enum LoggerColor {
    ANSI_RESET("\u001B[0m"),

    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),

    BLACK_BACKGROUND("\u001B[40m"),
    RED_BACKGROUND("\u001B[41m"),
    GREEN_BACKGROUND("\u001B[42m"),
    YELLOW_BACKGROUND("\u001B[43m"),
    BLUE_BACKGROUND("\u001B[44m"),
    PURPLE_BACKGROUND("\u001B[45m"),
    CYAN_BACKGROUND("\u001B[46m"),
    WHITE_BACKGROUND("\u001B[47m");

    /**
     * Code couleur associé
     */
    @NotNull
    private final String code;

    /**
     * Constructeur
     *
     * @param code Code couleur
     */
    LoggerColor(@NotNull String code) {
        this.code = code;
    }

    /**
     * To String
     *
     * @return Code couleur
     */
    @Override
    @NotNull
    public String toString() {
        return code;
    }

    /**
     * Récupère le code couleur
     *
     * @return Code couleur
     */
    @NotNull
    public String getCode() {
        return code;
    }
}
