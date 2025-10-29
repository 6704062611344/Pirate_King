package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Game;
import ui.MenuButton;
import utilz.LoadSave;

public class Menu extends State implements Statemethods{
	
	private MenuButton[] buttons = new MenuButton[3];
	private BufferedImage backgroundImg,backgroundImgLayer1,backgroundImgLayer2,backgroundImgLayer3,backgroundImgLayer4,backgroundImgLayer5;
	private int menuX,menuY,menuWidth,menuHeight;

	public Menu(Game game) {
		super(game);
		loadButton();
		loadBackground();
		backgroundImgLayer1 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG_LAYER1);
		backgroundImgLayer2 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG_LAYER2);
		backgroundImgLayer3 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG_LAYER3);
		backgroundImgLayer4 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG_LAYER4);
		backgroundImgLayer5 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG_LAYER5);
	}

	private void loadBackground() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
		menuWidth=(int)(backgroundImg.getWidth() * Game.SCALE);
		menuHeight=(int)(backgroundImg.getHeight() * Game.SCALE);
		menuX = Game.GAME_WIDTH/2 - menuWidth/2;
		menuY = (int)(45*Game.SCALE);
		
	}

	private void loadButton() {
		buttons[0]=new MenuButton(Game.GAME_WIDTH/2 , (int)(150*Game.SCALE),0,Gamestate.PLAYING);
		buttons[1]=new MenuButton(Game.GAME_WIDTH/2 , (int)(220*Game.SCALE),1,Gamestate.OPTIONS);
		buttons[2]=new MenuButton(Game.GAME_WIDTH/2 , (int)(290*Game.SCALE),2,Gamestate.QUIT);
	}

	@Override
	public void update() {
		for(MenuButton mb:buttons)
			mb.update();
	}

	@Override
	public void draw(Graphics g) {
		
		g.drawImage(backgroundImgLayer1, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(backgroundImgLayer2, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(backgroundImgLayer3, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(backgroundImgLayer4, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(backgroundImgLayer5, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		
		g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
		
		for(MenuButton mb:buttons)
			mb.draw(g);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMove(MouseEvent e) {
		for(MenuButton mb:buttons) 
			mb.setMouseOver(false);
		
		for(MenuButton mb:buttons)
			if(isIn(e,mb)) {
				mb.setMouseOver(true);
				break;
			}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(MenuButton mb:buttons)
			if(isIn(e,mb)){
				mb.setMousePressed(true);
				break;
			}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(MenuButton mb:buttons) {
			if(isIn(e,mb)){
				if(mb.isMousePressed())
					mb.applyGamestate();
				if(mb.getState() == Gamestate.PLAYING)
					game.getAudioPlayer().setLevelSong(game.getplaying().getLevelManager().getLevelIndex());
				break;
			}
		}
		resetButton();
	}

	private void resetButton() {
		for(MenuButton mb:buttons) {
			mb.resetBools();
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
			Gamestate.state = Gamestate.PLAYING;
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
