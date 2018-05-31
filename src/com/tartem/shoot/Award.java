package com.tartem.shoot;
/** 奖励 */
public interface Award {
	public int DOUBLE_FIRE=0;  //奖励火力值
	public int LIFE=1;          //奖励命
	/** 获取奖励的类型：0为火力值 1为命*/
	public int getType();

}
