import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class Tank extends ImageView {
    private final double tankSpeed;
    private int explosionTimer;
    private final int explosionTick;

    public Tank(Image image) {
        super(image);
        tankSpeed = 1;
        explosionTimer = 0;
        explosionTick = 60;
    }

    public int getExplosionTick() {
        return explosionTick;
    }

    public int getExplosionTimer() {
        return explosionTimer;
    }

    public void setExplosionTimer(int explosionTimer) {
        this.explosionTimer = explosionTimer;
    }

    public void goRight() {
        this.setRotate(0);
        double oldX = this.getLayoutX();
        this.setLayoutX(this.getLayoutX() + tankSpeed);
        if (collisionChecker()) {
            setLayoutX(oldX);
        }
        if (Tank2025.getFrameCounter() % 6 == 0) {
            turnWheel();
        }
    }

    public void goLeft() {
        this.setRotate(-180);
        double oldX = this.getLayoutX();
        this.setLayoutX(this.getLayoutX() - tankSpeed);
        if (collisionChecker()) {
            setLayoutX(oldX);
        }
        if (Tank2025.getFrameCounter() % 6 == 0) {
            turnWheel();
        }
    }

    public void goUp() {
        this.setRotate(-90);
        double oldY = this.getLayoutY();
        this.setLayoutY(this.getLayoutY() - tankSpeed);
        if (collisionChecker()) {
            setLayoutY(oldY);
        }
        if (Tank2025.getFrameCounter() % 6 == 0) {
            turnWheel();
        }
    }

    public void goDown() {
        this.setRotate(90);
        double oldY = this.getLayoutY();
        this.setLayoutY(this.getLayoutY() + tankSpeed);
        if (collisionChecker()) {
            setLayoutY(oldY);
        }
        if (Tank2025.getFrameCounter() % 12 == 0) {
            turnWheel();
        }
    }

    public boolean collisionChecker() {
        ArrayList<Node> allWalls = Tank2025.gamezone.getWalls();
        for (Node wall : allWalls) {
            if (this.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                return true;
            }
        }
        for (Enemy tank : Tank2025.getEnemies()) {
            if (this.getBoundsInParent().intersects(tank.getBoundsInParent()) && !this.equals(tank)) {
                return true;
            }
        }
        if (getImage() != Tank2025.getExplosionImage()) {
            for (Bullet bullet : Tank2025.getBullets()) {
                if (this.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
                    explode();
                }
            }
        }

        return this.getBoundsInParent( ).intersects(Tank2025.player.getBoundsInParent( ));
    }

    public void turnWheel() {
        if (this.getImage() == Tank2025.getPlayerTank1Image()) {
            this.setImage(Tank2025.getPlayerTank2Image());
        } else {
            this.setImage(Tank2025.getPlayerTank1Image());
        }
    }

    public void shoot() {
        int tankRotation = (int) this.getRotate();
        double bulletX;
        double bulletY;

        switch (tankRotation) {
            case 90:
                bulletX = this.getLayoutX() + getImage( ).getHeight( )/2 - 6;
                bulletY = this.getLayoutY() + getImage().getHeight() + 6;
                break;
            case -180:
                bulletX = this.getLayoutX() - 10;
                bulletY = this.getLayoutY() + getImage().getHeight()/2 - 5;
                break;
            case -90:
                bulletX = this.getLayoutX() + getImage( ).getWidth( )/2 - 6;
                bulletY = this.getLayoutY() - 8;
                break;
            default:
                bulletX = this.getLayoutX() + getImage().getWidth() + 6;
                bulletY = this.getLayoutY() + getImage().getHeight()/2 - 5;
                break;
        }

        fire(tankRotation, bulletX, bulletY);
    }

    public void fire(double rotation, double x, double y) {
        Bullet bullet = new Bullet(Tank2025.getBulletImage(), rotation, x, y, false);
        Tank2025.getBullets().add(bullet);
        Tank2025.root.getChildren().add(bullet);
    }

    public void explode() {
        setImage(Tank2025.getExplosionImage());
        Tank2025.setScore(Tank2025.getScore() + 100);
    }
}

class PlayerTank extends Tank {
    private int shootingCooldown;
    private int health;

    public int getRespawnCountdown() {
        return respawnCountdown;
    }

    public void setRespawnCountdown(int respawnCountdown) {
        this.respawnCountdown = respawnCountdown;
    }

    private int respawnCountdown;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setShootingCooldown(int shootingCooldown) {
        this.shootingCooldown = shootingCooldown;
    }

    public int getShootingCooldown() {
        return shootingCooldown;
    }

    PlayerTank(Image image) {
        super(image);
        shootingCooldown = 0;
        health = 3;
        respawnCountdown = 0;
    }

    @Override
    public boolean collisionChecker() {
        ArrayList<Node> allWalls = Tank2025.gamezone.getWalls();
        for (Node wall : allWalls) {
            if (this.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                return true;
            }
        }
        for (Enemy tank : Tank2025.getEnemies()) {
            if (this.getBoundsInParent().intersects(tank.getBoundsInParent()) && !(tank.getImage() == Tank2025.getExplosionImage())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void fire(double rotation, double x, double y) {
        Bullet bullet = new Bullet(Tank2025.getBulletImage(), rotation, x, y, true);
        Tank2025.getBullets().add(bullet);
        Tank2025.root.getChildren().add(bullet);
    }

    @Override
    public void explode() {
        setImage(Tank2025.getExplosionImage());
    }

    public void respawn() {
        setRespawnCountdown(0);
        setImage(Tank2025.getPlayerTank1Image());
        setLayoutX(Tank2025.getSpawnX());
        setLayoutY(Tank2025.getSpawnY());
    }
}

class Enemy extends Tank {
    private int cooldownTimer;
    Random rand;

    Enemy(Image image) {
        super(image);
        cooldownTimer = 0;
        rand = new Random();
    }

    @Override
    public void turnWheel() {
        if (this.getImage() == Tank2025.getEnemyTank1Image()) {
            this.setImage(Tank2025.getEnemyTank2Image());
        } else {
            this.setImage(Tank2025.getEnemyTank1Image());
        }
    }

    @Override
    public void shoot() {
        if (cooldownTimer <= 0) {
            super.shoot( );
            cooldownTimer = rand.nextInt(90) + 100;
        } else
            cooldownTimer--;
    }

    public void move() {
        int chanceFactor = rand.nextInt(10000);
        if (chanceFactor > 9950) {
            changeDirection();
        }
        switch ((int) getRotate()) {
            case 0:
                goRight();
                break;
            case -180:
                goLeft();
                break;
            case -90:
                goUp();
                break;
            case 90:
                goDown();
                break;
        }
    }

    public void changeDirection() {
        int newDirectionCode = rand.nextInt(3);
        switch ((int) getRotate()) {
            case 0:
                if (newDirectionCode == 0) {
                    setRotate(90);
                } else if (newDirectionCode == 1) {
                    setRotate(-90);
                } else {
                    setRotate(-180);
                } break;
            case 90:
                if (newDirectionCode == 0) {
                    setRotate(0);
                } else if (newDirectionCode == 1) {
                    setRotate(-90);
                } else {
                    setRotate(-180);
                } break;
            case -90:
                if (newDirectionCode == 0) {
                    setRotate(0);
                } else if (newDirectionCode == 1) {
                     setRotate(90);
                } else {
                     setRotate(-180);
                } break;
            default:
                if (newDirectionCode == 0) {
                    setRotate(0);
                } else if (newDirectionCode == 1) {
                    setRotate(90);
                } else {
                    setRotate(-90);
                } break;
        }
    }

    @Override
    public boolean collisionChecker() {
        ArrayList<Node> allWalls = Tank2025.gamezone.getWalls();
        for (Node wall : allWalls) {
            if (this.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                changeDirection();
                return true;
            }
        }
        for (Enemy tank : Tank2025.getEnemies()) {
            if (this.getBoundsInParent().intersects(tank.getBoundsInParent()) && !this.equals(tank) && !(tank.getImage() == Tank2025.getExplosionImage())) {
                changeDirection();
                return true;
            }
        }
        if (getImage() != Tank2025.getExplosionImage()) {
            for (Bullet bullet : Tank2025.getBullets()) {
                if (this.getBoundsInParent().intersects(bullet.getBoundsInParent()) && bullet.isFromPlayer()) {
                    explode();
                }
            }
        }

        return this.getBoundsInParent( ).intersects(Tank2025.player.getBoundsInParent( ));
    }
}
