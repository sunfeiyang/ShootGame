package com.tartem.shoot;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Hero extends FlyingObject{
	private int life;//命
	private int doubleFire;//火力值
	private BufferedImage[]images;//图片数组（切换图片）
	private int index;//协助图片的切换
    /**构造方法*/
	public Hero(){
		image=ShootGame.hero0;//图片
		width = image.getWidth(); //宽
		height = image.getHeight(); //高
		x = 150; //x:固定的坐标
		y = 400; //y:固定的坐标
		life = 3; //初始命数为3
		doubleFire = 0; //火力值默认为0(单倍火力)
		images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1}; //初始图片数组hero0和hero1
		index = 0; //协助切换(初始为0)
		
	}
	
	public void step(){
		image=images[index++/10%images.length];
	}
	
	public Bullet[] shoot(){
		int xStep=this.width/4;
		int yStep=20;
		if(doubleFire>0){
			Bullet[]bs=new Bullet[2];
			bs[0]=new Bullet(this.x+1*xStep,this.y-yStep);
			bs[1]=new Bullet(this.x+3*xStep,this.y-yStep);
			doubleFire-=2;
			return bs;
		}else{
			Bullet[]bs=new Bullet[1];
			bs[0]=new Bullet(this.x+2*xStep,this.y-yStep);
			return bs;
		}
	}
	
	public void moveTo(int x,int y){
		this.x=x-this.width/2;
		this.y=y-this.height/2;
	}
	
	public boolean outOfBounds(){
		return false;
	} 

	public void addLife(){
		life++;
	}
	public int getLife(){
		return life;
	}
	
	public void subtractLife(){
		life--;
	}
	
	public void addDoubleFire(){
		doubleFire+=40;
	}
	
	public void clearDoubleFire(){
		doubleFire = 0;
	}
	
	/**英雄机撞击敌人，this：英雄机  other：敌人*/
	public boolean hit(FlyingObject other){
		/*int x1 = this.x-other.width;
		int x2 = this.x+this.width+other.width;
		int y1 = this.y-other.height;
		int y2 = this.y+this.height+other.height;
		int x = other.x-other.width;
		int y = other.y-other.height;*/
		
		int x1 = other.x-this.width/2;
		int x2 = other.x+other.width+this.width/2;
		int y1 = other.y-this.height/2;
		int y2 = other.y+other.height+this.height/2;
		int x = this.x+this.width/2;
		int y = this.y+this.height/2;
		
		return x>x1 && x<x2
		         &&
		         y>y1 && y<y2;
	}
	
	
	
}

















