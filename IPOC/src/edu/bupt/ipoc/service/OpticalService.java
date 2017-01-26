package edu.bupt.ipoc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.Path;

public class OpticalService extends Service implements Comparable<OpticalService>{

	public static final int BUILD_REQUEST = 1;
	public static final int DELETE_RELEASE = 2;
	
	//rate默认为1，wdm为1，弹性光网络为其它 single_slot也表示是WDM
	public static final int SINGLE_SLOT = 1;

	//1G不可再分，10G 可以分成8个1G，(或4个2.5G)，
	//100G可分为8G个10G，或2个40G，
	//SINGLE表示不可再分的服务，即没有子opticalservice
	
	public static final boolean INDIVISIBLE = true;
	//public static final int CHANNEL_1G_SINGLE = 1;
	public static final int CHANNEL_10G_SINGLE = 2;
	public static final int CHANNEL_40G_SINGLE = 3;
	public static final int CHANNEL_100G_SINGLE = 4;
	public static final int CHANNEL_10G_WITH_EIGHT_1G = 5;
	public static final int CHANNEL_10G_FOT_OTN = 5;
	public static final int CHANNEL_100G_WITH_TWO_40G = 6;
	public static final int CHANNEL_100G_WITHWITH_EIGHT_10G = 7;

	public static final int SUB_1G_NUM = 8;
	public static final int SUB_OTN_NUM = 8;
	public static final int SUB_10G_NUM = 8;
	public static final int SUB_40G_NUM = 2;
		
	public static final int STATE_FREE = 0;
	public static final int STATE_USING_BUT_VACANT = 1;
	public static final int STATE_USING_NOT_VACANT = 2;
	public static final int ESTABLISHING = 3;

	
	
	public int id; 
	public int sourceVertex;
	public int sinkVertex;
	public Path path = null;
	
	//For elastic optical network, can be used as WDM
	public int start_slots;
	public int rate = SINGLE_SLOT;//used_slots_nums
	
	//set these two together
	public int type;//SINGLE_100G;
	public int rest_bw;
	public int used_bw = 0;
	public Map<Integer,OTNService> otn_children = null;;
	//public OpticalService os_father;

	//time_long为持续时间
	public static int INIT_TIME_LONG = 0;//100;if want to test static service, it should be set 0;
	public int timeLong;
	public long free_start_time = 0;

	public int state_flag = STATE_FREE;
	public int vacant_time_long = 0;
			
	public OpticalService(int _id, int _sourceVertex, int _sinkVertex, int _rate, int _timeLong)
	{
		//random
		this.id = _id;
		this.sourceVertex = _sourceVertex;
		this.sinkVertex = _sinkVertex;
		this.rate = _rate;
		this.timeLong = _timeLong;
		/*
		if(timeLong == 0)
		{
			ps_ids = new Vector<Integer>();
			ps_list = new Vector<PacketService>();
		}
 		*/
	}
	
	public void showMyself()
	{
		System.out.println("OS_id:"+this.id+"\t");
	}

	
	public int getBWCapacity()
	{
		return BW_10G;
	}

	public void setType(int _type)
	{
		if(_type == CHANNEL_10G_FOT_OTN)
		{
			type = _type;
			otn_children = new HashMap<Integer, OTNService>();
		}
		else if(_type == this.CHANNEL_10G_SINGLE)
		{
			type = _type;
		}
	}
	
	public boolean addSubOTN(OTNService _otns)
	{
		if(otn_children.size() < SUB_OTN_NUM)
		{
			otn_children.put(_otns.id, _otns);
			return true;
		}
		else
			System.out.println("Can't afford more OTNs, there must be something wrong, just check!");
		return false;
	}
	
	public boolean canOfferEnoughCapacity(int _requestBW)
	{
		if(this.type == CHANNEL_10G_FOT_OTN)
		{
			int _rest_capacity = (SUB_1G_NUM - otn_children.size()) * BW_1G;
			if(_rest_capacity >= _requestBW)
				return true;
			else
				return false;
		}
		else
			System.out.println("This optical service can not be used to offer sub otn, just check!");
		return false;
	}
	
	public int getNumberofFreeOTNs()
	{
		if(this.type == CHANNEL_10G_FOT_OTN)
		{
			return SUB_1G_NUM - otn_children.size();
		}
		else
			System.out.println("This optical service can not be used to offer sub otn, just check!!");
		return 0;
	}

	@Override
	public int compareTo(OpticalService o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
