package com.gcstudios.entitys;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.gcstudio.world.AStar;
import com.gcstudio.world.Camera;
import com.gcstudio.world.Vector2i;
import com.gcstudio.world.World;
import com.gcstudios.main.Game;

public class Enemy extends Entity {
	
	private double speed = 1;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	
	private int life = 10;
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage [2];
		sprites [0] = Game.spritesheet.getSprite(112, 16,16,16);
		sprites [1] = Game.spritesheet.getSprite(112+16, 16,16,16);
	}

	public void tick () {
		depth = 0;
 		if (isColiddingWithPlayer() == false) {
 			if(path == null || path.size() == 0) {
 				Vector2i start = new Vector2i ((int)(x/16), (int)(y/16));
 				Vector2i end = new Vector2i ((int)(Game.player.x/16), (int)(Game.player.y/16));
 				path = AStar.findPath(Game.world, start, end);
 			}
	}
 		else {
 			if(new Random().nextInt(100) < 5) {
 				//Sound.hurtEffect.play();
 				Game.player.life-=Game.rand.nextInt(3);
 				Game.player.isDamaged = true;
 			}
 		}
        if(new Random().nextInt(100)<90)
		followPath(path);
        if(new Random().nextInt(100)<5) {
        	Vector2i start = new Vector2i ((int)(x/16), (int)(y/16));
				Vector2i end = new Vector2i ((int)(Game.player.x/16), (int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
        }
		frames ++;
		if (frames == maxFrames) {
			frames = 0;
			index ++;
			if (index > maxIndex) 
				index = 0;
			
		}
		collidingBullet();
		if (life <=0) {
			destroySelf();
			return;
		}
		if (isDamaged) {
			this.damageCurrent++;
			if (this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}

	}
	public void destroySelf () {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	public void collidingBullet () {
		for (int i =0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if (e instanceof BulletShoot) {
				if (Entity.isColidding(this, e)) {
					isDamaged = true;
					life --;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingWithPlayer () {
		Rectangle enemyCurrent = new Rectangle (this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle (Game.player.getX(), Game.player.getY(),16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	public void render (Graphics g) {
		if (! isDamaged)
		g.drawImage(sprites[index], this.getX() - Camera.x,this.getY() - Camera.y, null);
		else
		g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x,this.getY() - Camera.y, null);
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, mwidth, mheight);
		
	}
	
}