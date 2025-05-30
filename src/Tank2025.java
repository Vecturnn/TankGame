import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

public class Tank2025 extends Application {
    private AnimationTimer gameplay;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private VBox pauseMenu;

    private static final Image playerTank1Image = new Image("yellowTank1.png");
    private static final Image playerTank2Image = new Image("yellowTank2.png");
    private static final Image enemyTank1Image = new Image("whiteTank1.png");
    private static final Image enemyTank2Image = new Image("whiteTank2.png");
    private static final Image bulletImage = new Image("bullet.png");
    private static final Image smallExplosionImage = new Image("smallExplosion.png");
    private static final Image explosionImage = new Image("explosion.png");
    private static final Image wallImage = new Image("wall.png");

    public static Image getPlayerTank1Image() {
        return playerTank1Image;
    }

    public static Image getPlayerTank2Image() {
        return playerTank2Image;
    }

    public static Image getEnemyTank1Image() {
        return enemyTank1Image;
    }

    public static Image getEnemyTank2Image() {
        return enemyTank2Image;
    }

    public static Image getBulletImage() {
        return bulletImage;
    }

    public static Image getSmallExplosionImage() {
        return smallExplosionImage;
    }

    public static Image getExplosionImage() {
        return explosionImage;
    }

    public static Image getWallImage() {
        return wallImage;
    }

    Random rand = new Random( );

    static Group root;
    static Gamezone gamezone;

    public static double getFrameCounter() {
        return frameCounter;
    }

    private static double frameCounter = 0;
    private boolean UpToggle = false;
    private boolean DownToggle = false;
    private boolean LeftToggle = false;
    private boolean RightToggle = false;
    private boolean XToggle = false;
    private boolean RToggle = false;

    private static ArrayList<Bullet> bullets = new ArrayList<>( );

    public static ArrayList<Bullet> getBullets() {
        return bullets;
    }

    private static ArrayList<Enemy> enemies = new ArrayList<>( );
    private static int maxTanks;

    public static ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public static PlayerTank player;
    private static int score = 0;

    public static double getSpawnX() {
        return spawnX;
    }

    public static double getSpawnY() {
        return spawnY;
    }

    private static double spawnX;
    private static double spawnY;

    public static void main(String[] args) {
        launch(args);
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Tank2025.score = score;
    }

    @Override
    public void start(Stage stage) {
        root = new Group( );
        Scene scene = new Scene(root, Color.BLACK);
        Label stats = new Label( );
        stats.setTextFill(Color.WHITE);
        stats.setLayoutX(25);
        stats.setLayoutY(20);
        root.getChildren( ).add(stats);

        stage.setTitle("Tank 2025");
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.setResizable(false);

        pauseMenu = new VBox(20);
        pauseMenu.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        pauseMenu.setPrefSize(stage.getWidth( ), stage.getHeight( ));
        pauseMenu.setAlignment(Pos.CENTER);
        Text pauseText = new Text("GAME PAUSED!");
        pauseText.fontProperty( ).set(new Font(50));
        pauseText.setFill(Color.WHITE);
        Button resumeButton = new Button("Resume");
        Button quitButton = new Button("Quit");
        pauseMenu.getChildren( ).addAll(pauseText, resumeButton, quitButton);
        pauseMenu.setVisible(false);
        resumeButton.setOnAction(e -> resumeGame( ));
        quitButton.setOnAction(e -> System.exit(0));

        gamezone = new Gamezone(stage);
        root.getChildren( ).addAll(gamezone.getWalls( ));

        player = new PlayerTank(playerTank1Image);
        spawnX = stage.getWidth() / 2;
        spawnY = stage.getHeight() / 2 + 60;
        player.setLayoutX(spawnX);
        player.setLayoutY(spawnY);
        root.getChildren( ).add(player);
        maxTanks = 20;

        scene.setOnKeyPressed(event -> {
            switch (event.getCode( )) {
                case P:
                    if (!isGameOver) {
                        try {
                            pauseGame( );
                            root.getChildren( ).add(pauseMenu);
                            pauseMenu.setVisible(true);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case UP:
                    UpToggle = true;
                    break;
                case DOWN:
                    DownToggle = true;
                    break;
                case LEFT:
                    LeftToggle = true;
                    break;
                case RIGHT:
                    RightToggle = true;
                    break;
                case X:
                    XToggle = true;
                    break;
                case R:
                    RToggle = true;
                    break;
                default:
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode( )) {
                case UP:
                    UpToggle = false;
                    break;
                case DOWN:
                    DownToggle = false;
                    break;
                case LEFT:
                    LeftToggle = false;
                    break;
                case RIGHT:
                    RightToggle = false;
                    break;
                case X:
                    XToggle = false;
                    break;
                case R:
                    RToggle = false;
                    break;
                default:
                    break;
            }
        });

        gameplay = new AnimationTimer( ) {

            @Override
            public void handle(long now) {
                if (!isPaused) {
                    if (player.getHealth( ) <= 0) {
                        stop( );
                        endGame(stage);
                    }

                    stats.setText(String.format("Score: %d\nlives: %d", getScore( ), player.getHealth( )));

                    if (player.getImage( ) != getExplosionImage( )) {
                        if (UpToggle) {
                            player.goUp( );
                        } else if (DownToggle) {
                            player.goDown( );
                        } else if (LeftToggle) {
                            player.goLeft( );
                        } else if (RightToggle) {
                            player.goRight( );
                        }
                        if (XToggle) {
                            if (player.getShootingCooldown( ) == 0) {
                                player.shoot( );
                                player.setShootingCooldown(60);
                            }
                        }
                    }

                    Iterator<Bullet> bulletIterator = bullets.iterator( );
                    while (bulletIterator.hasNext( )) {
                        Bullet bullet = bulletIterator.next( );

                        if (bullet.getImage( ).equals(bulletImage)) {
                            bullet.move( );
                        }
                        if (bullet.collisionCheckerForWalls( ) && bullet.getImage( ) == bulletImage) {
                            bullet.explode( );
                        }
                        if (bullet.collisionCheckerForPlayer( ) && bullet.getImage( ) == bulletImage && player.getImage( ) != getExplosionImage( )) {
                            bullet.explode( );
                            player.setHealth(Tank2025.player.getHealth( ) - 1);
                            player.explode( );
                        }
                        if (bullet.getImage( ) == smallExplosionImage) {
                            bullet.setExplosionTimer(bullet.getExplosionTimer( ) + 1);
                            if (bullet.getExplosionTimer( ) >= bullet.getExplosionTick( )) {
                                root.getChildren( ).remove(bullet);
                                bulletIterator.remove( );
                            }
                        }
                    }

                    if (enemies.size( ) < maxTanks) {
                        boolean create = true;
                        Enemy enemyDummy = new Enemy(enemyTank1Image);
                        int dummyX = rand.nextInt((int) stage.getHeight( ) - 120) + 60;
                        int dummyY = rand.nextInt((int) stage.getHeight( ) - 120) + 60;
                        enemyDummy.setLayoutX(dummyX);
                        enemyDummy.setLayoutY(dummyY);

                        if (enemyDummy.getBoundsInParent( ).intersects(player.getBoundsInParent( ))) {
                            create = false;
                        }

                        double distanceX = enemyDummy.getLayoutX() - player.getLayoutX();
                        double distanceY = enemyDummy.getLayoutY() - player.getLayoutY();

                        if (Math.pow(distanceX, 2) + Math.pow(distanceY, 2) < Math.pow(70,2)) {
                            create = false;
                        }

                        if (create) {
                            for (Node wall : gamezone.getWalls( )) {
                                if (enemyDummy.getBoundsInParent( ).intersects(wall.getBoundsInParent( ))) {
                                    create = false;
                                    break;
                                }
                            }
                        }

                        if (create) {
                            for (Enemy enemyTank : enemies) {
                                if (enemyDummy.getBoundsInParent( ).intersects(enemyTank.getBoundsInParent( ))) {
                                    create = false;
                                    break;
                                }
                            }
                        }

                        if (create) {
                            Enemy enemy = new Enemy(enemyTank1Image);
                            enemy.setLayoutX(dummyX);
                            enemy.setLayoutY(dummyY);
                            enemies.add(enemy);
                            root.getChildren( ).add(enemy);
                        }
                    }

                    Iterator<Enemy> enemyIterator = enemies.iterator( );
                    while (enemyIterator.hasNext( )) {
                        Enemy enemy = enemyIterator.next( );
                        if (enemy.getImage( ) != explosionImage) {
                            enemy.move( );
                            enemy.shoot( );
                        } else {
                            enemy.setExplosionTimer(enemy.getExplosionTimer( ) + 1);
                            if (enemy.getExplosionTimer( ) >= enemy.getExplosionTick( )) {
                                root.getChildren( ).remove(enemy);
                                enemyIterator.remove( );
                            }
                        }
                    }

                    if (player.getImage( ) == getExplosionImage( )) {
                        if (player.getRespawnCountdown( ) <= 90) {
                            player.setRespawnCountdown(player.getRespawnCountdown( ) + 1);
                        } else {
                            player.respawn( );
                        }
                    }

                    frameCounter++;
                    if (frameCounter == 60) {
                        frameCounter = 0;
                    }
                    if (player.getShootingCooldown( ) > 0) {
                        player.setShootingCooldown(player.getShootingCooldown( ) - 1);
                    } else if (player.getShootingCooldown( ) < 0) {
                        player.setShootingCooldown(0);
                    }
                }
            }
        };
        gameplay.start( );

        stage.setScene(scene);
        stage.show( );
        pauseMenu.toFront( );
    }

    private void endGame(Stage stage) {
        Label gameOverLabel = new Label("GAME OVER!");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(Font.font(48));

        Label scoreLabel = new Label("Score: " + getScore( ));
        scoreLabel.setTextFill(Color.RED);
        scoreLabel.setFont(Font.font(40));

        Label restartLabel = new Label("Press R to restart!");
        restartLabel.setTextFill(Color.RED);
        restartLabel.setFont(Font.font(40));

        VBox gameoverPanel = new VBox(20, gameOverLabel, scoreLabel, restartLabel);
        gameoverPanel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.9), CornerRadii.EMPTY, Insets.EMPTY)));
        gameoverPanel.setAlignment(Pos.CENTER);

        StackPane endPane = new StackPane(gameoverPanel);

        endPane.prefWidthProperty( ).bind(stage.widthProperty( ));
        endPane.prefHeightProperty( ).bind(stage.heightProperty( ));

        root.getChildren( ).add(endPane);

        stage.getScene( ).setOnKeyPressed(evt -> {
            if (evt.getCode( ) == KeyCode.R) {
                root.getChildren( ).remove(endPane);
                root.getChildren( ).removeAll(enemies);
                root.getChildren( ).removeAll(bullets);
                root.getChildren( ).remove(player);
                enemies.clear( );
                bullets.clear( );
                player = new PlayerTank(playerTank1Image);
                player.setLayoutX(spawnX);
                player.setLayoutY(spawnY);
                root.getChildren( ).add(player);
                setScore(0);
                player.setHealth(3);
                frameCounter = 0;
                player.setShootingCooldown(0);
                isGameOver = false;
                gamezone = new Gamezone(stage);
                player = new PlayerTank(playerTank1Image);
                start(stage);
                gameplay.start( );
            }
        });
    }

    private void pauseGame() {
        if (!pauseMenu.isVisible( )) {
            gameplay.stop( );
            pauseMenu.setVisible(true);
        }
    }

    private void resumeGame() {
        if (pauseMenu.isVisible( )) {
            pauseMenu.setVisible(false);
            root.getChildren( ).remove(pauseMenu);
            gameplay.start( );
        }
    }
}