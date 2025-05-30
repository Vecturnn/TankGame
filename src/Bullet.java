import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Bullet extends ImageView {
    private final int bulletSpeed;
    private int explosionTimer = 0;
    private final int explosionTick;

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    private final boolean fromPlayer;

    public int getExplosionTick() {
        return explosionTick;
    }

    public int getExplosionTimer() {
        return explosionTimer;
    }

    public void setExplosionTimer(int explosionTimer) {
        this.explosionTimer = explosionTimer;
    }

    public Bullet(Image image, double rotation, double x, double y, boolean fromPlayer) {
        super(image);
        setLayoutX(x);
        setLayoutY(y);
        setRotate(rotation);
        bulletSpeed = 4;
        explosionTick = 40;
        this.fromPlayer = fromPlayer;
    }

    public void move() {
        switch ((int) getRotate()) {
            case 0:
                setLayoutX(getLayoutX() + bulletSpeed);
                break;
            case -180:
                setLayoutX(getLayoutX() - bulletSpeed);
                break;
            case 90:
                setLayoutY(getLayoutY() + bulletSpeed);
                break;
            case -90:
                setLayoutY(getLayoutY() - bulletSpeed);
                break;
        }
    }

    public boolean collisionCheckerForWalls() {
        ArrayList<Node> allChildren = Tank2025.gamezone.getWalls();
        for (Node child : allChildren) {
            if (this.getBoundsInParent().intersects(child.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public boolean collisionCheckerForPlayer() {
        if (this.getBoundsInParent().intersects(Tank2025.player.getBoundsInParent())) {
            if (this.getImage() == Tank2025.getBulletImage()) {
                return true;
            }
        } return false;
    }

    public boolean collisionCheckerForEnemies() {
        for (Enemy enemy : Tank2025.getEnemies()) {
            if (this.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                return true;
            }
        } return false;
    }

    public void explode() {
        setImage(Tank2025.getSmallExplosionImage());
    }
}
