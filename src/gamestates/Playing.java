package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverLay;
import ui.LevelCompletedOverlay;
import ui.PausedOverLay;
import utilz.LoadSave;

import static utilz.Constants.Enviroment.*;

public class Playing extends State implements Statemethods{
	
	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemymanager;
	private ObjectManager objectManager;
	private PausedOverLay pausedoverlay;
	private GameOverOverLay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused =false;
	
	
	private int xLvlOffset;
	private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);
	private int rightBorder = (int)(0.9 * Game.GAME_WIDTH);
	private int maxLvlOffsetX;
	
	private BufferedImage backgroundImg,bigCloud,smallCloud;
	private int[] smallCloudsPos;
	private Random rnd = new Random();
	
	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean playerDying;
	
	public Playing(Game game) {
		super(game);
		initClasses();
		
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PALYING_BG_IMG);
		bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
		smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
		smallCloudsPos = new int[8];
		for(int i=0;i<smallCloudsPos.length;i++)
			smallCloudsPos[i] = (int)(90 * Game.SCALE) + rnd.nextInt((int)(100 * Game.SCALE));
		
		calcLvlOffset();
		loadStartLevel();
	}
	
	public void loadNextLevel() {
		resetAll();
		levelManager.loadNextLevel();
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	}
	
	private void loadStartLevel() {
		enemymanager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObject(levelManager.getCurrentLevel());
	}


	private void calcLvlOffset() {
		maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
		
	}

	private void initClasses() {
		levelManager = new LevelManager(game);
		enemymanager = new EnemyManager(this);
		objectManager = new ObjectManager(this);
		
		player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE),this);
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		
		pausedoverlay = new PausedOverLay(this);
		gameOverOverlay = new GameOverOverLay(this);
		levelCompletedOverlay = new LevelCompletedOverlay(this);
	}
	
	@Override
	public void update() {
		
		if (paused) {
			pausedoverlay.update();
		} else if (lvlCompleted) {
			levelCompletedOverlay.update();
		} else if (gameOver) {
			gameOverOverlay.update();
		} else if (playerDying) {
			player.update();
		} else {
			levelManager.update();
			objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			player.update();
			enemymanager.update(levelManager.getCurrentLevel().getLevelData(), player);
			checkCloseToBorder();
		}
		
	}

	@Override
	public void draw(Graphics g) {
		
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		
		drawClouds(g);
		
		levelManager.draw(g, xLvlOffset);
		player.render(g,xLvlOffset);
		enemymanager.draw(g,xLvlOffset);
		objectManager.draw(g, xLvlOffset);
		
		if(paused) {
			g.setColor(new Color(0,0,0,150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pausedoverlay.draw(g);
		}else if(gameOver) {
			gameOverOverlay.draw(g);
		}else if(lvlCompleted) {
			levelCompletedOverlay.draw(g);
		}
	}
	
	private void checkCloseToBorder() {
		int playerX = (int)player.getHitbox().x;
		int diff =playerX - xLvlOffset;
		
		if(diff>rightBorder)
			xLvlOffset += diff - rightBorder;
		else if(diff < leftBorder)
			xLvlOffset += diff -leftBorder;
		if(xLvlOffset > maxLvlOffsetX)
			xLvlOffset = maxLvlOffsetX;
		else if(xLvlOffset < 0)
			xLvlOffset =0;
	}
	
	private void drawClouds(Graphics g) {
		
		for(int i=0;i<3;i++)
			g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int)(xLvlOffset * 0.3), (int)(204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
		
		for(int i=0;i<smallCloudsPos.length;i++)
			g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int)(xLvlOffset * 0.7),smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
		
	}
	
	public void resetAll() {
		gameOver = false;
		paused = false;
		lvlCompleted=false;
		playerDying =false;
		player.resetAll();
		enemymanager.resetAllEnemies();
		objectManager.resetAllObjects();
	}
	
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemymanager.checkEnemyHit(attackBox);
	}
	
	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		objectManager.checkObjectTouched(hitbox);
		
	}
	
	public void checkObjectHit(Rectangle2D.Float attackBox) {
		objectManager.checkobjecHit(attackBox);
	}
	
	public void checkSpikesTouched(Player p) {
		objectManager.checkSpikesTouched(p);
		
	}

	public void mouseDragged(MouseEvent e) {
		if(!gameOver)
			if(paused) {
				pausedoverlay.mouseDragged(e);
			}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!gameOver)
			if (e.getButton() == MouseEvent.BUTTON1)
				player.setAttacking(true);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pausedoverlay.mouseMove(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseMove(e);
		}else {
			gameOverOverlay.mouseMove(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pausedoverlay.mousePressed(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		}else {
			gameOverOverlay.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pausedoverlay.mouseReleased(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		}else {
			gameOverOverlay.mouseReleased(e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_ESCAPE:
				paused=!paused;
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(!gameOver)
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;		
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
		}
		
	}
	
	public void setLevelCompleted(boolean levelCompleted) {
		this.lvlCompleted = levelCompleted;
		if(levelCompleted)
			game.getAudioPlayer().lvlCompleted();
	}
	
	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}
	
	public void unpauseGame() {
		paused=false;
	}
	
	public void windowFocusLost() {
		player.resetDirBooleans();
	}

	public Player getPlayer() {
		return player;
	}
	
	public EnemyManager getEnemyManager() {
		return enemymanager;
	}
	
	public ObjectManager getObjectManager() {
		return objectManager;
	}
	
	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void setPlayerDying(boolean b) {
		this.playerDying=playerDying;	
	}

}
