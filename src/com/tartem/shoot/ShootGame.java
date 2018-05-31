package com.tartem.shoot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;

/** 主程序类 */
public class ShootGame extends JPanel {
	public static final int WIDTH = 400;  //窗口宽
	public static final int HEIGHT = 654; //窗口高
	
	public static BufferedImage background; //背景图
	public static BufferedImage start; //启动图
	public static BufferedImage pause; //暂停图
	public static BufferedImage gameover; //游戏结束图
	public static BufferedImage airplane; //敌机
	public static BufferedImage bee; //小蜜蜂
	public static BufferedImage bullet; //子弹
	public static BufferedImage hero0; //英雄机0
	public static BufferedImage hero1; //英雄机1
	
	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = START;
	
	private Hero hero = new Hero();
	private FlyingObject[] flyings = {};
	private Bullet[] bullets = {};
	
	static{ //加载静态资源(初始化静态图片)
		try{
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**随机生成敌人（敌人和小蜜蜂）对象*/
	public FlyingObject nextOne(){
		Random rand = new Random(); //随机数对象
		int type = rand.nextInt(20); //生成0到19的随机数
		if(type<5){ //为0时返回小蜜蜂
			return new Bee();
		}else{ //为1到19时返回敌机
			return new Airplane();
		}
	}
	
	int flyEnteredIndex = 0; //敌人入场计数
	/** 敌人入场 */
	public void enterAction(){ //10毫秒走一次
		flyEnteredIndex++; //每10毫秒增1
		if(flyEnteredIndex%40==0){ //每400毫秒走一次
			FlyingObject obj = nextOne(); //获取敌人对象
			flyings = Arrays.copyOf(flyings,flyings.length+1); //扩容
			flyings[flyings.length-1] = obj; //将敌人赋值给flyings的最后一个元素
		}
	}
	
	/** 飞行物走一步 */
	public void stepAction(){ //10毫秒走一次
		hero.step(); //英雄机走一步
		for(int i=0;i<flyings.length;i++){ //遍历敌人数组
			FlyingObject f = flyings[i]; //获取每一个敌人
			f.step(); //敌人走一步
		}
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			Bullet b = bullets[i]; //获取每一个子弹
			b.step(); //走弹走一步
		}
	}
	int shootIndex = 0; //子弹入场计数
	/** 子弹入场(英雄机发射子弹) */
	public void shootAction(){ //10毫秒走一次
		shootIndex++; //每10毫秒增1
		if(shootIndex%30==0){ //300(10*30)毫秒走一次
			Bullet[] bs = hero.shoot(); //获取子弹数组
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //扩容(bs有几个元素则扩大几个容量)
			System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //数组的追加
		}
	}
	
	/** 删除越界的敌人和子弹 */
	public void outOfBoundsAction(){
		int index = 0; //1.不越界敌人数组下标   2.不越界敌人个数
		FlyingObject[] flyingLives = new FlyingObject[flyings.length]; //不越界敌人数组
		for(int i=0;i<flyings.length;i++){ //遍历敌人数组
			FlyingObject f = flyings[i]; //获取每一个敌人
			if(!f.outOfBounds()){ //不越界
				flyingLives[index] = f; //将不越界敌人对象添加到不越界敌人数组中
				index++; //1.不越界敌人数组下标增一  2.不越界敌人个数增一
			}
		}
flyings = Arrays.copyOf(flyingLives,index); //将不越界敌人数组复制到flyings中，flyings的长度为index(即不越界敌人个数)
		
		index = 0; //归零 1.不越界子弹数组下标  2.不越界子弹个数
		Bullet[] bulletLives = new Bullet[bullets.length]; //不越界子弹数组
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			Bullet b = bullets[i]; //获取每一个子弹
			if(!b.outOfBounds()){ //不越界
				bulletLives[index] = b; //将不越界子弹对象添加到不越界子弹数组中
				index++; //1.不越界子弹数组下标增一  2.不越界子弹个数增一
			}
		}
       bullets = Arrays.copyOf(bulletLives,index); //将不越界子弹数组复制到bullets中，bullets的长度为index(即不越界子弹个数)
		
	}
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){
			bang(bullets[i]);
		}
	}
	int score=0;
	public void bang(Bullet b){
	    int index =-1;
	    for(int i=0;i<flyings.length;i++){
	    	FlyingObject f=flyings[i];
	    	if(f.shootBy(b)){
	    		index =i;
	    		break;
	    	}
	    }
		if(index!=-1){
			FlyingObject one=flyings[index];
			if(one instanceof Enemy){
				Enemy e=(Enemy)one;
				score+=e.getScore();
			}
			if(one instanceof Award){
				Award a=(Award)one;
				int type=a.getType();
				switch(type){
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
			FlyingObject t=flyings[index];
			flyings[index]=flyings[flyings.length-1];
			flyings[flyings.length-1]=t;
			flyings=Arrays.copyOf(flyings, flyings.length-1);
		}
	}
	
	public void checkGameOverAction(){
		if(isGameOver()){
			state = GAME_OVER;
		}
	}
	
	public boolean isGameOver(){
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(hero.hit(f)){
				hero.subtractLife();
				hero.clearDoubleFire();
				
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = t;
				flyings = Arrays.copyOf(flyings, flyings.length-1);
			}
		}
		return hero.getLife()<=0;
	}
	/** 启动程序的执行 */
	public void action(){
		MouseAdapter l = new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				if(state==RUNNING){
				   int x=e.getX();
				   int y=e.getY();
				   hero.moveTo(x,y);
				}
			}
			public void mouseClicked(MouseEvent e){
				switch(state){
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					state = START;
					score = 0;
					
					hero = new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					break;
				}
			}
			
			public void mouseExited(MouseEvent e){
				if(state==RUNNING){
					state=PAUSE;
				}
			}
			public void mouseEntered(MouseEvent e){
				if(state==PAUSE){
					state=RUNNING;
				}
			}
				
		};
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
		
		Timer timer = new Timer(); //定时器对象
		int intervel = 10; //定时间隔(以毫秒为单位)
		timer.schedule(new TimerTask(){
			public void run(){ //10毫秒-定时干的那个事
				if(state==RUNNING){
				enterAction(); //敌人入场
				stepAction();
				shootAction();
				outOfBoundsAction();
				bangAction();
				checkGameOverAction();
				}
				repaint(); //重画(调用paint())
				}
		},intervel,intervel);
	}
	
	/** 重写paint() g:画笔 */
	public void paint(Graphics g){
		g.drawImage(background,0,0,null); //画背景图
		paintHero(g); //画英雄机对象
		paintFlyingObjects(g); //画敌人(敌机+小蜜蜂)对象
		paintBullets(g); //画子弹对象
		paintScoreAndLife(g); //画分和画命
		paintState(g);
	}
	
	public void paintScoreAndLife(Graphics g){
		g.setColor(new Color(0xFF0000)); //设置颜色(纯红)
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));
		g.drawString("SCORE: "+score, 20, 20);
		g.drawString("LIFE: "+hero.getLife(), 20, 50);
	}
	
	
	
	/** 画英雄机对象 */
	public void paintHero(Graphics g){
		g.drawImage(hero.image,hero.x,hero.y,null); //画英雄机
	}
	/** 画敌人(敌机+小蜜蜂)对象 */
	public void paintFlyingObjects(Graphics g){
		for(int i=0;i<flyings.length;i++){ //遍历敌人数组
			FlyingObject f = flyings[i]; //获取每一个敌人
			g.drawImage(f.image,f.x,f.y,null); //画敌人
		}
	}
	/**画子弹对象*/
	public void paintBullets(Graphics g){
		for(int i=0;i<bullets.length;i++){ //遍历子弹数组
			Bullet b = bullets[i]; //获取每一个子弹
			g.drawImage(b.image,b.x,b.y,null); //画子弹
		}
	}
	public void paintState(Graphics g){
		switch(state){
		case START:
			g.drawImage(start,0,0,null);
			break;
		case PAUSE:
			g.drawImage(pause,0,0,null);
			break;
		case GAME_OVER:
			g.drawImage(gameover,0,0,null);
			break;
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("fly"); //窗口对象
		ShootGame game = new ShootGame(); //面板对象
		frame.add(game); //将面板添加到窗口上
		
		frame.setSize(WIDTH, HEIGHT); //设置窗口大小
		frame.setAlwaysOnTop(true); //设置一直居上
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置默认关闭操作(窗口关闭则退出)
		frame.setLocationRelativeTo(null); //设置窗口初始位置(居中显示)
		frame.setVisible(true); //1.设置窗口可见  2.尽快调用paint()
		
		game.action(); //启动程序的执行
	}
}
