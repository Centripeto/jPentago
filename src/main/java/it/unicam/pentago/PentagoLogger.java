package it.unicam.pentago;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import it.unicam.pentago.models.PentagoAction;
import javafx.scene.control.TextArea;

public class PentagoLogger {
    private StringBuilder gameLog;
    private static final String LOG_FILE_PATH = "pentago_game_log.txt";
    private TextArea logArea;

    public PentagoLogger(TextArea logArea) {
        this.gameLog = new StringBuilder();
        this.logArea = logArea;
        this.gameLog.append("---Inizio della partita---\n\n");
        this.updateLogArea("---Inizio della partita---\n\n");
    }

    public void logAction(PentagoAction action, String player) {
        String logEntry = String.format("%s ha eseguito l'azione: (%d, %d) ruotando il quadrante %d %s\n",
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
