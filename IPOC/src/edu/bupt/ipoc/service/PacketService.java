package edu.bupt.ipoc.service;

import java.util.List;

public class PacketService extends Service implements Comparable<PacketService>{

	//End to end service request, to be carried or removed
	//public static int CARRIED_REQUEST = 1;
	
	public static final int STATIC_CARRIED = 1;
	public static final int DYNAMICALLY_CARRIED_AND_INDIVISIBLE = 2;
	public static final int DYNAMICALLY_CARRIED_AND_DIVISIBLE = 3;
	//service type
	public static int TYPE_BEST_EFFORT = 0;//Only for services with high or mid priority
	public static int TYPE_BW_TOLERANT =1;  //Only for services with low prioity
	
	//permanent means the service lasts forever, otherwise it will just last a while, and will be removed.
	public static final boolean PERMANENT = true;
	public static final boolean NOT_PERMANENT = false;
	
	//parameters
	public int id;
	public int sourceNode;
	public int destNode;
	public int priority = PRIORITY_HIGH;
	public int carried_type;

	public boolean isPermanent = PERMANENT;//if the scheduled_time_long = 0, it will be a permanent service, otherwise, it is not.

	public VirtualTransLink carriedVTL;

	
	public PacketService(int _id, int _sourceNode, int _destNode, int _priority)
	{
		this.id = _id;
		this.sourceNode = _sourceNode;
		this.destNode = _destNode;
		this.priority = _priority;
	}
	
	public void setServiceCarriedType(int _c_type) {
		this.carried_type = _c_type;		
	}
	
	public void setPriority(int _priority)
	{
		this.priority = _priority;
	}
	
	//needed to be rewrited by child Class
	public int getCurrentOccupiedBw()
	{
		return -1;
	}
	/*	

	
	public void showMyselfCurrentState()
	{
		System.out.print("Id:"+this.id+"\tSource:"+this.sourceNode+"\tDest:"+this.destNode+"\t");
		
		for(int i = 0; i < Service.TIME_BUCKET_NUM;i++)
			System.out.print(real_time_bw_buckets[i]+"\t");
		
		System.out.print("current_bw:\t"+getCurrentBw()+"\t");
		System.out.println();
	}
	
	public String toString()
	{
		String describtion = new String();
		describtion += "ps id:"+this.id+"\t the request bw is:"+this.getCurrentOccupiedBw();
		return describtion;
	}
*/	
	public void cleanMyselfButKeepBWStatistics()
	{
		cleanMyselfWithoutBucketCount();
//		current_bucket_count = 0;
//		if(isPermanent == NOT_PERMANENT)
//			fillBwArr(0);
	}
	
	public void cleanMyselfWithoutBucketCount()
	{
		carriedVTL = null;
		//TODO
	}

	@Override
	public int compareTo(PacketService o) {
		// TODO Auto-generated method stub
		return o.priority - this.priority;
	}

}
