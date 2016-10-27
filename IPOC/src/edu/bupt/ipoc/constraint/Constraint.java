package edu.bupt.ipoc.constraint;

import java.util.HashMap;

public class Constraint {
	
	public static final int SOURCE_C = 1;//souce node ,range 0~xx
	public static final int DEST_C = 2;//dest node, range 1~xx
	public static final int PRIORITY_C = 3;//service priority , range 1~3
	
	public static final int INITBW_C = 4;//request inital bandwidth
	
	public static final int PACKET_SERVICE_CARRIED_TYPE_C = 5;
	public static final int VTL_CARRY_TYPE_C = 7;
	
	public static final int FENJI_C = 8;	
	
	public int type;
	public int value;
	public String description;
	
	public Constraint(int _type, int _value, String _description)
	{
		this.type = _type;
		this.value = _value;
		this.description = _description;
	}
	
	public String toString()
	{
		return this.description;
	}
	
	public static Constraint getConstraintByType(HashMap<Integer,Constraint> cons, int type)
	{
		return cons.get(type);
	}

}
