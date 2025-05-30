import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Gamezone extends Pane {
    private final ArrayList<Node> walls = new ArrayList<>();

    public Gamezone(Stage stage) {
        for (int i = 0; i < stage.getHeight() - 14; i += 14) {createWall(0, i);}
        for (int i = 0; i < stage.getHeight() - 14; i += 14) {createWall(stage.getWidth() - 32, i);}
        for (int i = 0; i < stage.getWidth() - 16; i += 16) {createWall(i, stage.getHeight() - 58);}
        for (int i = 0; i < stage.getWidth() - 16; i += 16) {createWall(i, 0);}

        for (int i = 150; i < stage.getWidth() - 160; i += 16) {createWall(i, 100);}
        for (int i = 200; i < stage.getWidth() - 200; i += 16) {createWall(i, 200);}
        for (int i = 200; i < stage.getHeight() - 150; i += 14) {createWall(stage.getWidth() - 120, i);}
        for (int i = 200; i < stage.getHeight() - 150; i += 14) {createWall(100, i);}


    }

    private void createWall(double x, double y) {
        ImageView wallImageView = new ImageView(Tank2025.getWallImage());
        wallImageView.setX(x);
        wallImageView.setY(y);
        walls.add(wallImageView);
        getChildren().addAll(wallImageView);
    }

    public ArrayList<Node> getWalls() {
        return walls;
    }
}
