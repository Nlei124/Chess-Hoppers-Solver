package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class ChessGUI extends Application implements Observer<ChessModel, String> {
    private ChessModel model;

    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    private Stage stage;
    private BorderPane borderPane;
    private Button loadButton;
    private Button hintButton;
    private Button resetButton;
    private Text statusText;
    private FileChooser fileChooser;


    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    /** Hashmap for the characters to Images*/
    private HashMap<Character, Image> characterImageHashMap;
    /**the bishop image*/
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    /**the pawn image*/
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    /**the king image*/
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    /**the queen image*/
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    /**the rook image*/
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));
    /**the knight image*/
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));

    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));


    @Override
    public void init() throws IOException {
        // get the file name from the command line
        String filename = getParameters().getRaw().get(0);
        this.model = new ChessModel(filename);
        this.model.addObserver(this);

        //Make the hashmap from characters to images
        characterImageHashMap = new HashMap<>();
        characterImageHashMap.put('B', bishop);
        characterImageHashMap.put('P', pawn);
        characterImageHashMap.put('K', king);
        characterImageHashMap.put('N', knight);
        characterImageHashMap.put('Q', queen);
        characterImageHashMap.put('R', rook);

        //Set up the file path for the file chooser
        fileChooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        currentPath += File.separator + "data" + File.separator + "chess";  // or "hoppers"
        fileChooser.setInitialDirectory(new File(currentPath));
    }

    /**Calls load with the file from the file chooser*/
    private void loadFromFileChooser()
    {
        try
        {
            model.load(fileChooser.showOpenDialog(stage).getAbsolutePath());
        }
        //Ignore null pointer when nothing is choosen
        catch(NullPointerException ignored)
        {
        }
        catch (Exception e)
        {
            update(model, "Invalid File Choosen");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Chess GUI");

        //Create the status label on top
        statusText = new Text("stat");
        statusText.setStyle("-fx-font-size: " + FONT_SIZE);

        //The three buttons at the bottom just call the model's respective methods
        loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadFromFileChooser());
        resetButton = new Button("Reset");
        resetButton.setOnAction(event -> model.reset());
        hintButton = new Button("Hint");
        hintButton.setOnAction(event -> model.hint());

        //Set everything in the border pane
        borderPane = new BorderPane();
        //Top with the status text
        BorderPane.setAlignment(statusText, Pos.CENTER);
        borderPane.setTop(statusText);

        //bottom with a flowpane with the three buttons
        GridPane bot = new GridPane();
        bot.add(loadButton, 0, 0);
        bot.add(resetButton, 1, 0);
        bot.add(hintButton, 2, 0);
        bot.setAlignment(Pos.CENTER);
        borderPane.setBottom(bot);

        //loading the file and thus calling update
        model.load(getParameters().getRaw().get(0));

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        this.stage.sizeToScene();
        stage.show();
    }

    @Override
    public void update(ChessModel chessModel, String msg) {
        //Update the status
        statusText.setText(msg);
        //Create and update the grid for the board of buttons
        GridPane gridPane = new GridPane();
        for (int i = 0; i < model.getRows(); i++) {
            for (int j = 0; j < model.getCols(); j++) {
                Button temp = new Button();
                //For alternating colors
                //If the row and columns are both even or odd then they are light
                if(i % 2  == j % 2){
                    temp.setBackground(LIGHT);
                }
                else {
                    temp.setBackground(DARK);
                }
                //Assigning the button the corresponding image based on the character
                char piece = model.getVal(i, j);
                if(piece != '.') // . is empty
                {
                    //Using hashmap to get the image
                    temp.setGraphic(new ImageView(characterImageHashMap.get(piece)));
                }
                temp.setMinSize(ICON_SIZE, ICON_SIZE);
                temp.setMaxSize(ICON_SIZE, ICON_SIZE);
                int finalI = i;
                int finalJ = j;
                //the buttons just call select with the given row and column
                temp.setOnAction(event -> model.select(finalI, finalJ));
                gridPane.add(temp, j, i);
            }
        }
        //replacing the grid pane
        gridPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(gridPane);
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
