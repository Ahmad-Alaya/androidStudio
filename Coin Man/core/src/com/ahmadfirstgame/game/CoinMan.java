package com.ahmadfirstgame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	//Texture img;
	Texture background;
	Texture background2;
	Texture [] man;
	int manState=0;
	int pause=0;
	float gravity =0.2f;
	float velocity=0;
	int manY=0; // y position vom spieler
	int gamestate=0;
	BitmapFont font ; // text auf dem screen
	BitmapFont gameOver;
	int score=0;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;
	Random random;
	Rectangle manRectangle;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;
	Texture dizzy;
	float bgX=0;
	float backgroundVelocity=4;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");
		background = new Texture("bg.png");
		background2 = new Texture("bg.png");

		man =new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2 ;
		dizzy =new Texture("dizzy-1.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10); // size von text

		gameOver = new BitmapFont();
		gameOver.setColor(Color.RED);
		gameOver.getData().setScale(6); // size von text

	}
	public  void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());

	}
	public  void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,bgX,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.draw(background,bgX+Gdx.graphics.getWidth(),0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());



		if (gamestate==0){
			//warte auf spielstart
			if (Gdx.input.justTouched()){
				gamestate=1;
			}

		}else if(gamestate ==1){
			// das Spiel läuft
			bgX -=backgroundVelocity;
			if (bgX + Gdx.graphics.getWidth()==0){
				bgX = 0;
			}
			// coins alle 100 frames erstellen
			if (coinCount<100){
				coinCount ++;
			}else {
				coinCount=0;
				makeCoin();
			}
			// die coins sind am anfang ganz recht deshalb sind sie nicht sichtbar
			// deswegen lass die coins sich nach links bewegen
			coinRectangles.clear();
			for (int i=0 ; i<coinXs.size() ; i++){
				batch.draw(coin ,coinXs.get(i),coinYs.get(i)); // coin malen
				coinXs.set(i,coinXs.get(i)-4);  // coin um 4 pixel nach links setzen :)
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}


			// Bombs alle 250 frames erstellen
			if (bombCount <150){
				bombCount++;
			}else {
				bombCount =0;
				makeBomb();
			}
			// die Bomb sind am anfang ganz recht deshalb sind sie nicht sichtbar
			// deswegen lass die Bomb sich nach links bewegen
			bombRectangles.clear();
			for (int i = 0; i< bombXs.size() ; i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i)); // coin malen
				bombXs.set(i, bombXs.get(i)-8);  // coin um 4 pixel nach links setzen :)
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}


			// hochspringen
			if (Gdx.input.justTouched()){
				velocity =-10;
			}

			//Geschwindigkeit  verlangsamen
			if(pause<8){  pause++;  }
			else {
				pause=0;
				if (manState<3){
					manState++;
				}else {
					manState=0;
				}
			}

			velocity +=gravity;
			manY -=velocity;  // geschwindigkeit von spieler Y- position abziehen also fällt runter

			if (manY<=0|| manY > Gdx.graphics.getHeight() ){  manY=0; } //damit der spieler nicht von der screen verschwindet

			//batch.draw(man[manState],Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
			// ein physic für charackter erstellen in viereck form
			manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());
			for (int i =0; i<coinRectangles.size();i++){
				//aufpassen auf rectangel import . er sollte nicht von java
				if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
					//Gdx.app.log("Coin ","kollidiert");
					score ++;
					coinRectangles.remove(i);
					coinXs.remove(i);
					coinYs.remove(i);
					break;
				}

			}
			for (int i =0; i<bombRectangles.size();i++){
				if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
					//Gdx.app.log("bomb ","kollidiert");
					gamestate=2;
				}

			}

		}else if (gamestate==2){
			//das Spiel is beendet  game over
			if (Gdx.input.justTouched()){
				score =0;
				gamestate=1;
				velocity =0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount=0;
				manY=Gdx.graphics.getHeight()/2;

			}

		}

		if (gamestate==2){
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
			gameOver.draw(batch,"Game Over",500 ,1000);

		}else {
			batch.draw(man[manState],Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

		}

		font.draw(batch,String.valueOf(score),1200,Gdx.graphics.getHeight()-40);

		batch.end();


	}
	
	@Override
	public void dispose () {
		background.dispose();
		background2.dispose();
		batch.dispose();
	}
}
