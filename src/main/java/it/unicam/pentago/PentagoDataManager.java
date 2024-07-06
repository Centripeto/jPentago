package it.unicam.pentago;

import com.lostrucos.jabtbg.dataanalysis.*;
import it.unicam.pentago.dataanalysis.*;
import it.unicam.pentago.models.PentagoAction;
import it.unicam.pentago.models.PentagoGameState;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PentagoDataManager {
    private DataCollector<PentagoGameState, PentagoAction> dataCollector;
    private GameTreeVisualizer<PentagoGameState, PentagoAction> gameTreeVisualizer;
    private PerformanceAnalyzer<PentagoGameState, PentagoAction> performanceAnalyzer;

    public PentagoDataManager() {
        this.dataCollector = new PentagoDataCollector();
        this.gameTreeVisualizer = new PentagoGameTreeVisualizer();
        this.performanceAnalyzer = new PentagoPerformanceAnalyzer();
    }

    public void logAction(PentagoGameState state, PentagoAction action, int player, long decisionTime) {
        dataCollector.collectData(state, action, player, decisionTime);
    }

    public void saveLogToFile(String format) {
        Map<String, Object> collectedData = dataCollector.getCollectedData();
        if ("txt".equalsIgnoreCase(format)) {
            saveTxtLog(collectedData);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            saveExcelLog(collectedData);
        }
    }

    private void saveTxtLog(Map<String, Object> data) {
        // Implementa la logica per salvare i dati in un file di testo
    }

    private void saveExcelLog(Map<String, Object> data) {
        // Implementa la logica per salvare i dati in un file Excel
    }

    public String getGameTreeVisualization() {
        return gameTreeVisualizer.visualize(dataCollector.getCollectedData());
    }

    public Map<String, Object> getPerformanceAnalysis() {
        return performanceAnalyzer.compareAlgorithms(Collections.singletonList(dataCollector.getCollectedData()));
    }

    public void saveGameHistorySnapshot(WritableImage snapshot) {
        try {
            File file = new File("game_history_" + System.currentTimeMillis() + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}