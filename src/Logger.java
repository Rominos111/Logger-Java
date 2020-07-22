// SPDX-License-Identifier: GPL-3.0-or-later

package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import logger.enums.LoggerColor;
import logger.enums.LoggerOption;
import logger.enums.LoggerType;

/**
 * Gestion des logs <br>
 * <br>
 * Avantages : couleurs, fichier, surcharge d'arguments (sans restriction de nombre ou de type), stacktrace <br>
 * Dossier des logs : ./logs <br>
 * Chaque fichier .log est nommé selon l'heure à laquelle il a été créé <br>
 * <br>
 * Exemples d'utilisation simple : <br><code>
 *      Logger.warning("Mauvais caractère"); <br>
 *      // Affiche un texte en jaune (car warning) et l'enregiste dans le fichier</code><br>
 * <br>
 * Exemple plus complexe : <br><code>
 *      Logger.debug("variable ", maVariable, " devait valoir", uneAutreVariable, LoggerOption.LOG_FILE_ONLY); <br>
 *      // N'affiche rien à l'écran mais sauvegarde dans le dossier deux lignes, une pour le message et une pour la stacktrace </code><br>
 *
 * @author Romain
 */
public abstract class Logger {
    /**
     * Chemin des logs
     */
    private final static String logPath = "./logs";

    /**
     * Nom du projet
     */
    private final static String projectName = "project";

    /**
     * Pour écrire dans le fichier
     */
    @Nullable
    private static PrintWriter printWriter = null;

    /**
     * Nombre de lignes écrites
     */
    private static int nbWrite = 0;

    /**
     * Ansi activé ou non. L'ANSI n'est pas compris par le terminal Windows par défaut
     */
    private static boolean ansiEnabled;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Méthodes de log

    /**
     * Info
     *
     * @param args Arguments (messages, options de log...)
     */
    @SuppressWarnings("unused")
    public static void info(Object... args) {
        genericLog(System.out, args, LoggerType.INFO);
    }

    /**
     * Succès
     *
     * @param args Arguments (messages, options de log...)
     */
    @SuppressWarnings("unused")
    public static void success(Object... args) {
        genericLog(System.out, args, LoggerType.SUCCESS);
    }

    /**
     * Erreur
     *
     * @param args Arguments (messages, options de log...)
     */
    @SuppressWarnings("unused")
    public static void error(Object... args) {
        genericLog(System.err, args, LoggerType.ERROR);
    }

    /**
     * Warning
     *
     * @param args Arguments (messages, options de log...)
     */
    @SuppressWarnings("unused")
    public static void warning(Object... args) {
        genericLog(System.err, args, LoggerType.WARNING);
    }

    /**
     * Debug
     *
     * @param args Arguments (messages, options de log...)
     */
    @SuppressWarnings("unused")
    public static void debug(Object... args) {
        genericLog(System.out, args, LoggerType.DEBUG);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonctions publiques

    /**
     * Initialisation
     */
    public static synchronized void init() {
        init(true);
    }

    /**
     * Initialisation
     *
     * @param enableAnsi Active l'ANSI ou non
     */
    public static synchronized void init(boolean enableAnsi) throws IllegalStateException {
        ansiEnabled = enableAnsi;

        if (isInitialized()) {
            throw new IllegalStateException("Logger déjà initialisé");
        }

        boolean ok = false;
        boolean dirCreated = false;
        BufferedWriter bufferedWriter = null;

        try {
            dirCreated = new File(logPath).mkdir();
            FileWriter fileWriter = new FileWriter(logPath + "/" + projectName + "_log_" + getDate() + ".log");
            bufferedWriter = new BufferedWriter(fileWriter);

            ok = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (ok) {
            printWriter = new PrintWriter(bufferedWriter);

            info("Début des logs", LoggerOption.LOG_FILE_ONLY);

            if (dirCreated) {
                warning(
                        "Le dossier contenant les logs n'existait pas, il a été créé. Path :",
                        new File(logPath).getAbsolutePath()
                );
            }
        }
        else {
            error("Erreur lors de la création de l'environnement de log");
        }
    }

    /**
     * Logger déjà initialisé ou non
     *
     * @return Initialisé
     */
    public static boolean isInitialized() {
        return printWriter != null;
    }

    /**
     * Pour quitter
     */
    public static synchronized void exit() {
        if (isInitialized()) {
            writeToFile("Fin des logs, fermeture", LoggerType.INFO);
            printWriter.close();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonctions privées

    /**
     * Log générique
     *
     * @param where System.out, System.err, ou même possiblement ailleurs
     * @param args Arguments (messages, options de log...)
     * @param type Type de log (debug, warning...)
     */
    private static synchronized void genericLog(PrintStream where, Object[] args, LoggerType type) {
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<LoggerOption> options = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof LoggerOption) {
                options.add((LoggerOption) arg);
            }
            else {
                messages.add(String.valueOf(arg));
            }
        }

        final String separator = " ";

        StringBuilder message = new StringBuilder();

        for (int i=0; i<messages.size(); i++) {
            message.append(messages.get(i));

            if (i != messages.size()-1) {
                message.append(separator);
            }
        }


        if (!options.contains(LoggerOption.LOG_FILE_ONLY)) {
            if (ansiEnabled) {
                where.println(type.getTextColor().getCode() + message.toString() + LoggerColor.ANSI_RESET.getCode());
            }
            else {
                where.println(message.toString());
            }
        }

        if (!options.contains(LoggerOption.LOG_CONSOLE_ONLY)) {
            writeToFile(message.toString(), type);
        }
    }

    /**
     * Récupère l'heure
     *
     * @return Heure sous forme "hh:mm:ss:mmm"
     */
    private static String getHour() {
        LocalTime now = LocalTime.now();

        return String.format("%02d", now.getHour())
                + ":" + String.format("%02d", now.getMinute())
                + ":" + String.format("%02d", now.getSecond())
                + ":" + String.format("%03d", now.getNano()/1000000);
    }

    /**
     * Récupère la date
     *
     * @return Date sous forme "yyyy-mm-dd@hh-mm-ss"
     */
    private static String getDate() {
        LocalDateTime now = LocalDateTime.now();

        return now.getYear()
                + "-" + String.format("%02d", now.getMonthValue())
                + "-" + String.format("%02d", now.getDayOfMonth())
                + "@" + String.format("%02d", now.getHour())
                + "-" + String.format("%02d", now.getMinute())
                + "-" + String.format("%02d", now.getSecond());
    }

    /**
     * Écrit dans le fichier
     *
     * @param msg Messsage à écrire
     * @param type Type de log
     */
    private static synchronized void writeToFile(String msg, LoggerType type) {
        if (!isInitialized()) {
            init();

            warning("Logger initialisé en interne");
        }

        if (printWriter != null) {
            String toPrint = "[";
            toPrint += nbWrite + "-";
            toPrint += getHour() + "-";
            // toPrint += String.format("%7s", type.toString()) + "] ";
            toPrint += type.toString() + "] ";
            toPrint += msg;

            printWriter.println(toPrint);

            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            StringBuilder builder = new StringBuilder();
            builder.append("@");

            for (StackTraceElement stackTraceElement : stackTrace) {
                if (!stackTraceElement.toString().contains("java.desktop")
                && !stackTraceElement.toString().contains("java.base")
                && !stackTraceElement.toString().contains("Logger.writeToFile")
                && !stackTraceElement.toString().contains("Logger.genericLog")) {

                    builder.append(" : ");
                    builder.append(stackTraceElement);
                }
            }

            printWriter.println(builder);

            nbWrite ++;

            printWriter.flush();
        }
    }
}
