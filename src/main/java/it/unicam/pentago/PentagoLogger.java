package it.unicam.pentago;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TextArea;

public class PentagoLogger {
    private StringBuilder gameLog;
    private static final String LOG_FILE_PATH = "pentago_game_log.txt";
    private TextArea logArea;

    public PentagoLogger(TextArea logArea) {
        this.gameLog = new StringBuilder();
        this.logArea = logArea;
    }

    public void logAction(PentagoAction action, String player) {
        String logEntry = String.format("%s - %s ha eseguito l'azione: (%d, %d), rotazione quadrante %d %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                player, action.getRow(), action.getCol(), action.getQuadrant(),
                action.isClockwise() ? "in senso orario" : "in senso antiorario");
        gameLog.append(logEntry);
        updateLogArea(logEntry);
    }

    private void updateLogArea(String logEntry) {
        logArea.appendText(logEntry);
    }

    public void saveLogToFile() {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.write(gameLog.toString());
            writer.write("\n--- Fine della partita ---\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        gameLog = new StringBuilder();
        logArea.clear();
    }
}
