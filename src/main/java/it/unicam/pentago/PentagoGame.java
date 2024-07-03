package it.unicam.pentago;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.effect.GaussianBlur;
import javafx.collections.FXCollections;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PentagoGame extends Application {

    private static final int BOARD_SIZE = 6;
    private static final int QUADRANT_SIZE = 3;
    private StackPane[][] cells = new StackPane[BOARD_SIZE][BOARD_SIZE];
    private GridPane[] quadrants = new GridPane[4];
    private Stage primaryStage;
    private Scene gameScene, menuScene, optionsScene;
    private VBox gameRoot;
    private Label instructionLabel;
    private TextArea gameLog;
    private ImageView[][] rotationArrows = new ImageView[4][2];
    private boolean areArrowsVisible = false;
    private int selectedRow = -1;
    private int selectedColumn = -1;

    private String player1Type;
    private String player2Type;
    private String difficulty1;
    private String difficulty2;
    private String startingPlayer;

    private PentagoController controller;

    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer effectSoundPlayer;
    private Slider musicVolumeSlider;
    private Slider effectsVolumeSlider;
    private ComboBox<String> resolutionComboBox;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.initializeAudio();
        this.createMenuScene();
        this.createGameScene();

        this.primaryStage.setScene(this.menuScene);
        this.primaryStage.setTitle("Pentago");
        this.primaryStage.show();

        this.backgroundMusicPlayer.play();
    }

    private void initializeAudio() {
        Media backgroundMusic = new Media(getClass().getResource("/audio/background_music.mp3").toExternalForm());
        backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        Media effectSound = new Media(getClass().getResource("/audio/piece_placement.mp3").toExternalForm());
        effectSoundPlayer = new MediaPlayer(effectSound);
    }

    private void createMenuScene() {
        VBox menuRoot = new VBox(10);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(20));
        menuRoot.setStyle("-fx-background-color: #333333;");

        Button newGameButton = new Button("Avvia nuova partita");
        Button resumeGameButton = new Button("Riprendi partita");
        Button optionsButton = new Button("Opzioni");
        Button rulesButton = new Button("Regolamento");
        Button creditsButton = new Button("Crediti");
        Button exitButton = new Button("Esci dal gioco");

        newGameButton.setOnAction(event -> this.showNewGameDialog());
        optionsButton.setOnAction(event -> this.showOptionsMenu());
        exitButton.setOnAction(event -> this.primaryStage.close());

        menuRoot.getChildren().addAll(newGameButton, resumeGameButton, optionsButton, rulesButton, creditsButton, exitButton);

        this.menuScene = new Scene(menuRoot, 800, 600);
    }

    private void showNewGameDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nuova Partita");
        dialog.setHeaderText("Seleziona le opzioni per la nuova partita");

        ButtonType playButtonType = new ButtonType("Gioca", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(playButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> player1TypeComboBox = new ComboBox<>();
        ComboBox<String> player2TypeComboBox = new ComboBox<>();
        player1TypeComboBox.getItems().addAll("Umano", "Monte Carlo Tree Search", "Minimax", "Minimax con Alfa-Beta pruning");
        player2TypeComboBox.getItems().addAll("Umano", "Monte Carlo Tree Search", "Minimax", "Minimax con Alfa-Beta pruning");
        player1TypeComboBox.setValue("Umano");
        player2TypeComboBox.setValue("Monte Carlo Tree Search");

        ComboBox<String> player1DifficultyComboBox = new ComboBox<>();
        ComboBox<String> player2DifficultyComboBox = new ComboBox<>();
        player1DifficultyComboBox.getItems().addAll("Facile", "Normale", "Difficile", "Competitiva");
        player2DifficultyComboBox.getItems().addAll("Facile", "Normale", "Difficile", "Competitiva");
        player1DifficultyComboBox.setValue("Normale");
        player2DifficultyComboBox.setValue("Normale");

        ComboBox<String> startingPlayerComboBox = new ComboBox<>();
        startingPlayerComboBox.getItems().addAll("Giocatore 1", "Giocatore 2", "Casuale");
        startingPlayerComboBox.setValue("Casuale");

        grid.add(new Label("Giocatore 1:"), 0, 0);
        grid.add(player1TypeComboBox, 1, 0);
        grid.add(new Label("Difficoltà:"), 0, 1);
        grid.add(player1DifficultyComboBox, 1, 1);
        grid.add(new Label("Giocatore 2:"), 0, 2);
        grid.add(player2TypeComboBox, 1, 2);
        grid.add(new Label("Difficoltà:"), 0, 3);
        grid.add(player2DifficultyComboBox, 1, 3);
        grid.add(new Label("Chi inizia:"), 0, 4);
        grid.add(startingPlayerComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == playButtonType) {
                this.player1Type = player1TypeComboBox.getValue();
                this.player2Type = player2TypeComboBox.getValue();
                this.difficulty1 = player1DifficultyComboBox.getValue();
                this.difficulty2 = player2DifficultyComboBox.getValue();
                this.startingPlayer = startingPlayerComboBox.getValue();
                this.controller = new PentagoController(this);
                this.controller.setupNewGame(this.player1Type, this.player2Type, this.difficulty1, this.difficulty2, this.startingPlayer);
                this.primaryStage.setScene(this.gameScene);
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void createGameScene() {
        this.gameRoot = new VBox(5);
        this.gameRoot.setPadding(new Insets(10, 10, 10, 10));
        this.gameRoot.setStyle("-fx-background-color: #f0f0f0;");

        HBox topBar = this.createTopBar();

        HBox mainContent = new HBox(10);
        mainContent.setAlignment(Pos.CENTER);

        StackPane boardContainer = new StackPane();

        StackPane boardGrid = this.createBoard();
        boardContainer.getChildren().add(boardGrid);

        VBox instructionArea = new VBox(10);
        instructionArea.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 3px;");
        instructionArea.setPadding(new Insets(10));
        instructionArea.setAlignment(Pos.CENTER);
        this.instructionLabel = new Label("");
        this.instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        instructionArea.getChildren().add(this.instructionLabel);

        VBox gameArea = new VBox(10);
        gameArea.getChildren().addAll(boardContainer, instructionArea);

        this.gameLog = new TextArea();
        this.gameLog.setEditable(false);
        this.gameLog.setPrefWidth(200);

        mainContent.getChildren().addAll(gameArea, this.gameLog);

        this.gameRoot.getChildren().addAll(topBar, mainContent);

        this.gameScene = new Scene(this.gameRoot, 1280, 720);

        boardGrid.prefWidthProperty().bind(this.gameScene.widthProperty().multiply(0.6));
        boardGrid.prefHeightProperty().bind(this.gameScene.heightProperty().multiply(0.7));
        this.gameLog.prefHeightProperty().bind(this.gameScene.heightProperty().multiply(0.8));
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView homeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/home-icon.png")));
        homeIcon.setFitHeight(40);
        homeIcon.setFitWidth(40);
        homeIcon.setOnMouseClicked(e -> this.showPauseMenu());

        ImageView treeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/tree-icon.png")));
        treeIcon.setFitHeight(40);
        treeIcon.setFitWidth(40);
        treeIcon.setOnMouseClicked(e -> this.showGameTree());

        topBar.getChildren().addAll(homeIcon, treeIcon);

        return topBar;
    }

    private StackPane createBoard() {
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(10);
        board.setVgap(10);
        board.setPadding(new Insets(20));

        StackPane boardContainer = new StackPane();
        boardContainer.setStyle("-fx-background-color: #1e555c; -fx-border-color: gray; -fx-border-width: 3px;");

        Rectangle woodenBackground = new Rectangle(390, 390);
        woodenBackground.setFill(Paint.valueOf("#4c2f27"));
        boardContainer.getChildren().add(woodenBackground);

        for (int q = 0; q < 4; q++) {
            this.quadrants[q] = new GridPane();
            this.quadrants[q].setHgap(5);
            this.quadrants[q].setVgap(5);
            this.quadrants[q].setStyle("-fx-background-color: #ba6a6a; -fx-padding: 5; -fx-background-radius: 5;");

            int startRow = (q / 2) * QUADRANT_SIZE;
            int startCol = (q % 2) * QUADRANT_SIZE;

            for (int i = 0; i < QUADRANT_SIZE; i++) {
                for (int j = 0; j < QUADRANT_SIZE; j++) {
                    int row = startRow + i;
                    int col = startCol + j;
                    StackPane cell = this.createCell(row, col);
                    this.quadrants[q].add(cell, j, i);
                }
            }

            board.add(this.quadrants[q], q % 2, q / 2);
        }

        boardContainer.getChildren().add(board);
        this.addRotationArrows(boardContainer);

        return boardContainer;
    }

    private StackPane createCell(int row, int col) {
        StackPane cell = new StackPane();
        Circle backgroundCircle = new Circle(25);
        backgroundCircle.setFill(Color.LIGHTGREEN);
        backgroundCircle.setStroke(Color.DARKGREEN);
        cell.getChildren().add(backgroundCircle);

        cell.setOnMouseEntered(event -> this.highlightCell(row, col, true));
        cell.setOnMouseExited(event -> this.highlightCell(row, col, false));
        cell.setOnMouseClicked(event -> {
            this.selectedRow = row;
            this.selectedColumn = col;
            this.controller.handleCellClick(row, col);
        });

        this.cells[row][col] = cell;
        return cell;
    }

    private void highlightCell(int row, int col, boolean highlight) {
        if (!this.areArrowsVisible) {
            StackPane cell = this.cells[row][col];
            Circle backgroundCircle = (Circle) cell.getChildren().get(0);

            if (highlight) {
                if (this.controller.getCurrentState().getBoard().getCell(row, col) == 0) {
                    backgroundCircle.setFill(Color.LIGHTGREEN);
                } else {
                    backgroundCircle.setFill(Color.RED);
                }
            } else {
                backgroundCircle.setFill(Color.YELLOW);
            }
        }
    }

    public void updateCellAppearance(int row, int col, int cellState) {
        StackPane cell = this.cells[row][col];
        Circle backgroundCircle = (Circle) cell.getChildren().get(0);

        if (cellState == 0) {
            backgroundCircle.setFill(Color.YELLOW);
            if (cell.getChildren().size() > 1) {
                cell.getChildren().remove(1);
            }
        } else {
            backgroundCircle.setFill(Color.YELLOW);
            Color pieceColor = cellState == 1 ? Color.BLACK : Color.RED;
            if (cell.getChildren().size() > 1) {
                ((Circle) cell.getChildren().get(1)).setFill(pieceColor);
            } else {
                Circle piece = new Circle(15);
                piece.setFill(pieceColor);
                cell.getChildren().add(piece);
            }
        }

        this.effectSoundPlayer.stop();
        this.effectSoundPlayer.play();
    }

    private void addRotationArrows(StackPane boardContainer) {
        double firstValue = -145;
        double secondValue = -210;
        double thirdValue = -210;
        double fourthValue = -140;
        double x = 0;
        double y = 0;

        String[] arrowPaths = {
                "/images/clockwise-arrow.png",
                "/images/counterclockwise-arrow.png"
        };

        for (int q = 0; q < 4; q++) {
            for (int d = 0; d < 2; d++) {
                ImageView arrow = new ImageView(new Image(getClass().getResourceAsStream(arrowPaths[d])));
                arrow.setFitHeight(30);
                arrow.setFitWidth(30);
                arrow.setVisible(false);
                final int quadrant = q;
                final boolean clockwise = (d == 0);
                arrow.setOnMouseClicked(e -> controller.handleRotation(quadrant, clockwise));

                rotationArrows[q][d] = arrow;

                // Posizionamento delle frecce agli angoli esterni dei quadranti
                switch (q) {
                    case 0:
                        x = (d == 0 ? firstValue : thirdValue);
                        y = (d == 0 ? secondValue : fourthValue);
                        if (d == 1) arrow.setRotate(270);
                        break;
                    case 1:
                        x = (d == 0 ? -secondValue : -fourthValue);
                        y = (d == 0 ? firstValue : thirdValue);
                        if (d == 0) arrow.setRotate(90);
                        break;
                    case 2:
                        x = (d == 0 ? secondValue : fourthValue);
                        y = (d == 0 ? -firstValue : -thirdValue);
                        if (d == 0) arrow.setRotate(270);
                        if (d == 1) arrow.setRotate(180);
                        break;
                    case 3:
                        x = (d == 0 ? -firstValue : -thirdValue);
                        y = (d == 0 ? -secondValue : -fourthValue);
                        if (d == 0) arrow.setRotate(180);
                        if (d == 1) arrow.setRotate(90);
                        break;
                }

                arrow.setTranslateX(x);
                arrow.setTranslateY(y);

                boardContainer.getChildren().add(arrow);
            }
        }
    }

    public void showRotationOptions() {
        for (ImageView[] quadrantArrows : rotationArrows) {
            for (ImageView arrow : quadrantArrows) {
                arrow.setVisible(true);
            }
        }
        areArrowsVisible = true;
    }

    public void hideRotationOptions() {
        for (ImageView[] quadrantArrows : rotationArrows) {
            for (ImageView arrow : quadrantArrows) {
                arrow.setVisible(false);
            }
        }
        areArrowsVisible = false;
    }

    public void rotateQuadrant(int quadrant, boolean clockwise) {
        RotateTransition rt = new RotateTransition(Duration.millis(500), quadrants[quadrant]);
        rt.setByAngle(clockwise ? 90 : -90);
        rt.setOnFinished(e -> {
            quadrants[quadrant].setRotate(clockwise ? -90 : 90);
        });
        rt.play();
    }

    private void showPauseMenu() {
        VBox pauseRoot = new VBox(10);
        pauseRoot.setAlignment(Pos.CENTER);
        pauseRoot.setPadding(new Insets(20));
        pauseRoot.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #333333; -fx-border-width: 2px; -fx-border-radius: 5px;");

        Button resumeButton = new Button("Riprendi");
        Button optionsButton = new Button("Opzioni");
        Button restartButton = new Button("Riavvia partita");
        Button saveAndQuitButton = new Button("Salva e vai al menù");
        Button exitButton = new Button("Esci dal gioco");

        resumeButton.setOnAction(event -> this.closePauseMenu());
        optionsButton.setOnAction(event -> this.showOptionsMenu());
        restartButton.setOnAction(event -> {
            this.closePauseMenu();
            this.controller.setupNewGame(this.player1Type, this.player2Type, this.difficulty1, this.difficulty2, this.startingPlayer);
        });
        saveAndQuitButton.setOnAction(event -> {
            // Implementa la logica di salvataggio qui
            this.primaryStage.setScene(this.menuScene);
            this.closePauseMenu();
        });
        exitButton.setOnAction(event -> this.primaryStage.close());

        pauseRoot.getChildren().addAll(resumeButton, optionsButton, restartButton, saveAndQuitButton, exitButton);

        Stage pauseStage = new Stage(StageStyle.UNDECORATED);
        Scene pauseScene = new Scene(pauseRoot, 300, 400);
        pauseStage.setScene(pauseScene);

        // Make pause menu draggable
        final Delta dragDelta = new Delta();
        pauseRoot.setOnMousePressed(mouseEvent -> {
            dragDelta.x = pauseStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = pauseStage.getY() - mouseEvent.getScreenY();
        });
        pauseRoot.setOnMouseDragged(mouseEvent -> {
            pauseStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            pauseStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });

        this.gameRoot.setEffect(new GaussianBlur(5));
        pauseStage.showAndWait();
    }

    private void closePauseMenu() {
        gameRoot.setEffect(null);
        Stage pauseStage = (Stage) gameRoot.getScene().getWindow();
        pauseStage.close();
    }

    private void showOptionsMenu() {
        VBox optionsRoot = new VBox(10);
        optionsRoot.setAlignment(Pos.CENTER);
        optionsRoot.setPadding(new Insets(20));
        optionsRoot.setStyle("-fx-background-color: #f0f0f0;");

        this.musicVolumeSlider = new Slider(0, 100, 50);
        this.effectsVolumeSlider = new Slider(0, 100, 50);
        CheckBox fullscreenCheckbox = new CheckBox("Schermo intero");

        this.musicVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.backgroundMusicPlayer.setVolume(newValue.doubleValue() / 100);
        });

        this.effectsVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.effectSoundPlayer.setVolume(newValue.doubleValue() / 100);
        });

        fullscreenCheckbox.setOnAction(event -> this.primaryStage.setFullScreen(fullscreenCheckbox.isSelected()));

        List<String> supportedResolutions = this.getSupportedResolutions();
        this.resolutionComboBox = new ComboBox<>(FXCollections.observableArrayList(supportedResolutions));
        this.resolutionComboBox.setValue(this.getCurrentResolution());
        this.resolutionComboBox.setOnAction(event -> this.changeResolution(this.resolutionComboBox.getValue()));

        Button backButton = new Button("Indietro");
        backButton.setOnAction(event -> this.closeOptionsMenu());

        optionsRoot.getChildren().addAll(
                new Label("Volume Musica"), this.musicVolumeSlider,
                new Label("Volume Effetti"), this.effectsVolumeSlider,
                fullscreenCheckbox,
                new Label("Risoluzione"), this.resolutionComboBox,
                backButton
        );

        Stage optionsStage = new Stage(StageStyle.UNDECORATED);
        Scene optionsScene = new Scene(optionsRoot, 300, 400);
        optionsStage.setScene(optionsScene);

        optionsStage.showAndWait();
    }

    private void closeOptionsMenu() {
        Stage optionsStage = (Stage) this.resolutionComboBox.getScene().getWindow();
        optionsStage.close();
    }

    private List<String> getSupportedResolutions() {
        List<String> resolutions = List.of("1280x720", "1920x1080", "2560x1440", "3840x2160");
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();

        return resolutions.stream()
                .filter(res -> {
                    String[] dims = res.split("x");
                    int width = Integer.parseInt(dims[0]);
                    int height = Integer.parseInt(dims[1]);
                    return width <= screenWidth && height <= screenHeight;
                })
                .collect(Collectors.toList());
    }

    private String getCurrentResolution() {
        return (int) this.primaryStage.getWidth() + "x" + (int) this.primaryStage.getHeight();
    }

    private void changeResolution(String resolution) {
        String[] dimensions = resolution.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        this.primaryStage.setWidth(width);
        this.primaryStage.setHeight(height);
        this.primaryStage.centerOnScreen();
    }

    /*private void showOptionsMenu() {
        Stage optionsStage = new Stage(StageStyle.UNDECORATED);
        optionsStage.setScene(optionsScene);

        Button backButton = new Button("Indietro");
        backButton.setOnAction(e -> optionsStage.close());

        ((VBox) optionsScene.getRoot()).getChildren().add(backButton);

        optionsStage.showAndWait();
    }*/

    private void showGameTree() {
        // Implement game tree visualization here
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Albero di Gioco");
        alert.setHeaderText(null);
        alert.setContentText("Visualizzazione dell'albero di gioco non ancora implementata.");
        alert.showAndWait();
    }

    public void updateBoard(PentagoBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                updateCellAppearance(i, j, board.getCell(i, j));
            }
        }
        this.hideRotationOptions();
        //this.setBlackTextColor();
    }

    /*public void setBlackTextColor() {
        instructionLabel.setTextFill(Color.BLACK);
    }

    public void setRedTextColor() {
        instructionLabel.setTextFill(Color.RED);
    }*/

    public void setInstructionText(String text) {
        instructionLabel.setText(text);
    }

    public void showGameEndDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fine della partita");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType newGameButton = new ButtonType("Nuova Partita");
        alert.getButtonTypes().setAll(newGameButton, ButtonType.OK);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == newGameButton) {
            this.showNewGameDialog();
        } else {
            this.primaryStage.setScene(this.menuScene);
        }
    }

    public void showErrorDialog(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void highlightWinningCells(List<int[]> winningCells) {
        for (int[] cell : winningCells) {
            StackPane cellPane = this.cells[cell[0]][cell[1]];
            Circle backgroundCircle = (Circle) cellPane.getChildren().get(0);
            backgroundCircle.setFill(Color.GREEN);
        }
    }

    // Getters
    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public TextArea getGameLog() {
        return gameLog;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Delta {
        double x, y;
    }
}
