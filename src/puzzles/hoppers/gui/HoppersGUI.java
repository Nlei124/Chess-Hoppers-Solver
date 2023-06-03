package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private Image redFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"red_frog.png"));
    private Image greenFrog = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green_frog.png"));
    private Image lilyPad = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"lily_pad.png"));

    // set images to values
    private HashMap<Character, Image> IMAGES;

    private Stage stage;
    private HoppersModel model;
    private BorderPane mainPane;
    private Button loadButton;
    private Button hintButton;
    private Button resetButton;
    private Label statusLabel;

    private boolean initialized;

    // This map simply corresponds each char with an image for the board GridPane
    private void createImageMap() {
        IMAGES = new HashMap<>();
        // Water Sprite
        IMAGES.put('*', new Image(getClass().getResourceAsStream(RESOURCES_DIR+"water.png")));
        // Lily Pad
        IMAGES.put('.', new Image(getClass().getResourceAsStream(RESOURCES_DIR+"lily_pad.png")));
        // Green
        IMAGES.put('G', new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green_frog.png")));
        // Red
        IMAGES.put('R', new Image(getClass().getResourceAsStream(RESOURCES_DIR+"red_frog.png")));
    }
  
    // Create a filechooser
    private void loadButtonClicked() {
        FileChooser chooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        currentPath += File.separator + "data" + File.separator + "hoppers";
        chooser.setInitialDirectory(new File(currentPath));

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            // File was chosen, load based on file
            model.load(file.getAbsolutePath());
        }
    }

    public void init() throws IOException {
        this.initialized = false;
        createImageMap(); // Corresponds each char with an image
        String filename = getParameters().getRaw().get(0);
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // TOP has status
        // CENTER has board
        // BOTTOM has 3 buttons
        mainPane = new BorderPane();

        // Create status label
        statusLabel = new Label("Initial Text");
        statusLabel.setStyle( "-fx-font-size: " + FONT_SIZE );
        FlowPane top = new FlowPane(statusLabel);
        top.setAlignment(Pos.CENTER);
        mainPane.setTop(top);

        // Create bottom
        loadButton = new Button("Load"); // Load
        loadButton.setOnAction(e -> this.loadButtonClicked());
        // Reset
        resetButton = new Button("Reset");
        resetButton.setOnAction(e -> this.model.reset());
        // Hint
        hintButton = new Button("Hint");
        hintButton.setOnAction(e -> this.model.hint());
        FlowPane bottom = new FlowPane(loadButton, resetButton, hintButton);
        bottom.setAlignment(Pos.CENTER);
        mainPane.setBottom(bottom);

        // Create center
        mainPane.setCenter(new GridPane());

        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hoppers GUI");
        stage.show();

        initialized = true;
        update(this.model, "Loaded: hoppers-4.txt");
    }

    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        if (!initialized) return;

        // Update status message
        statusLabel.setText(msg);

        // Update frog board

        // grab board, width, and height
        int height = hoppersModel.getHeight();
        int width = hoppersModel.getWidth();
        char[][] board = hoppersModel.getBoard();

        // iterate and make new GridPane from board
        GridPane buttonBoard = new GridPane();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button button = new Button();
                button.setGraphic(new ImageView(IMAGES.get(board[i][j])));
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);

                // Onclick, call model's "select" method with my coords
                final int myX = j;
                final int myY = i;
                button.setOnAction(e -> this.model.select(myY, myX));

                buttonBoard.add(button, j, i);
            }
        }
        buttonBoard.setAlignment( Pos.CENTER );
        this.mainPane.setCenter(buttonBoard);

        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {

            Application.launch(args);
        }
    }
}
