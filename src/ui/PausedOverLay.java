package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.UI.PauseButton.*;
import static utilz.Constants.UI.URMButton.*;
import static utilz.Constants.UI.VolumeButtons.*;

public class PausedOverLay {
	
	private Playing playing;
	private BufferedImage backgroundImg;
	private int bgX,bgY,bgW,bgH;
	private SoundButton musicButton,sfxButton;
	private UrmButton menuB,replayB,unpauseB;
	private VolumeButton volumebutton;

	public PausedOverLay(Playing playing){
		
		this.playing=playing;
		loadBackground();
		createSoundButton();
		createUrmButton();
		createVolumeBytton();
	}
	
	private void createVolumeBytton() {
		int vX = (int) (309 * Game.SCALE);
		int vY = (int) (278 * Game.SCALE);
		volumebutton = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
	}

	private void createUrmButton() {
		int menuX =(int)(313 * Game.SCALE);
		int replayX = (int)(387 * Game.SCALE);
		int unpauseX =(int)(462 * Game.SCALE);
		int bY = (int)(325 * Game.SCALE);
		
		menuB = new UrmButton(menuX,bY,URM_SIZE,URM_SIZE,2);
		replayB = new UrmButton(replayX,bY,URM_SIZE,URM_SIZE,1);
		unpauseB = new UrmButton(unpauseX,bY,URM_SIZE,URM_SIZE,0);
		
		
	}

	private void createSoundButton() {
		
		int soundX = (int)(450*Game.SCALE);
		int musicY = (int)(140*Game.SCALE);
		int sfxY = (int)(186*Game.SCALE);
		musicButton=new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE);
		sfxButton=new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE);
		
	}

	private void loadBackground() {
		backgroundImg =LoadSave.GetSpriteAtlas(LoadSave.PAUSED_BACKGROUND);
		bgW = (int)(backgroundImg.getWidth() * Game.SCALE);
		bgH = (int)(backgroundImg.getHeight() * Game.SCALE);
		bgX = Game.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int)(25 * Game.SCALE);
		
	}

	public void update() {
		
		
		//Sound Button
		musicButton.update();
		sfxButton.update();
		
		//URM Button
		menuB.update();
		replayB.update();
		unpauseB.update();
		
		//Volume Button
		volumebutton.update();
	}
	
	public void draw(Graphics g) {
		//Backgrounds
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);
		
		//Sound Button
		musicButton.draw(g);
		sfxButton.draw(g);
		
		//URM Button
		menuB.draw(g);
		replayB.draw(g);
		unpauseB.draw(g);
		
		//Volume Button
		volumebutton.draw(g);
	}
	
	public void mouseDragged(MouseEvent e) {
		
		if (volumebutton.isMousePressed()) {
			volumebutton.changeX(e.getX());
		}
		
	}
	
	public void mousePressed(MouseEvent e) {
		if(isIn(e,musicButton))
			musicButton.setMousePressed(true);
		else if(isIn(e,sfxButton))
			sfxButton.setMousePressed(true);
		else if(isIn(e,menuB))
			menuB.setMousePressed(true);
		else if(isIn(e,replayB))
			replayB.setMousePressed(true);
		else if(isIn(e,unpauseB))
			unpauseB.setMousePressed(true);
		else if (isIn(e, volumebutton))
			volumebutton.setMousePressed(true);
		
	}

	public void mouseReleased(MouseEvent e) {
		if(isIn(e,musicButton)) {
			if(musicButton.isMousePressed()) {
				musicButton.setMuted(!musicButton.isMuted());
			}
		}else if(isIn(e,sfxButton)) {
			if(sfxButton.isMousePressed()) {
				sfxButton.setMuted(!sfxButton.isMuted());
			}
		}else if(isIn(e,menuB)) {
			if(menuB.isMousePressed()) {
				playing.setGamestate(Gamestate.MENU);
				playing.resetAll();
				playing.unpauseGame();
			}
		}else if(isIn(e,replayB)) {
			if(replayB.isMousePressed()) {
				playing.resetAll();
				playing.unpauseGame();
				playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLevelIndex());
			}
		}else if(isIn(e,unpauseB)) {
			if(unpauseB.isMousePressed()) {
				playing.unpauseGame();
			}
		}
		
		musicButton.resetBools();
		sfxButton.resetBools();
		menuB.resetBools();
		replayB.resetBools();
		unpauseB.resetBools();
		volumebutton.resetBools();

		
	}
	
	public void mouseMove(MouseEvent e) {
		musicButton.setMouseOver(false);
		sfxButton.setMouseOver(false);
		menuB.setMouseOver(false);
		replayB.setMouseOver(false);
		unpauseB.setMouseOver(false);
		volumebutton.setMouseOver(false);
		
		if(isIn(e,musicButton))
			musicButton.setMouseOver(true);
		else if(isIn(e,sfxButton))
			sfxButton.setMouseOver(true);
		else if(isIn(e,menuB))
			menuB.setMouseOver(true);
		else if(isIn(e,replayB))
			replayB.setMouseOver(true);
		else if(isIn(e,unpauseB))
			unpauseB.setMouseOver(true);
		else if (isIn(e, volumebutton))
			volumebutton.setMouseOver(true);
		
	}
	
	private boolean isIn(MouseEvent e,PausedButton b) {
		return (b.getBounds().contains(e.getX(), e.getY()));
	}

}
