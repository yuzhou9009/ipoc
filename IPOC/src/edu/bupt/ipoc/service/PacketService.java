package edu.bupt.ipoc.service;

import java.util.List;

public class PacketService extends Service{

	//End to end service request, to be carried or removed
	public static int CARRIED_REQUEST = 1;
	public static int REMOVED_REQUEST = 2;
	
	public static final int STATIC_CARRIED = 1;
	public static final int DYNAMICALLY_CARRIED_AND_INDIVISIBLE = 2;
	public static final int DYNAMICALLY_CARRIED_AND_DIVISIBLE = 3;
	//service type
	//public static int TYPE_BEST_EFFORT = 0;//Only for services with high or mid priority
	//public static int TYPE_LIMITATION =1;  //Only for services with low prioity
	
	//permanent means the service lasts forever, otherwise it will just last a while, and will be removed.
	public static final boolean PERMANENT = true;
	public static final boolean NOT_PERMANENT = false;
	public static final int RANDOM_BW = 0;
	
	//parameters
	public int id;
	public int sourceNode;
	public int destNode;
	public int priority = PRIORITY_HIGH;
	public int carried_type;
	public int[] real_time_bw_buckets = null;
	public int current_bucket_count = 0;
	public boolean isPermanent = PERMANENT;//if the scheduled_time_long = 0, it will be a permanent service, otherwise, it is not.
	
	//if it is a permanent service:
	public int peek_bw = 0;
	
	//if it is not a permanent service,these parameters must be set.
	public int rest_bw_to_be_transfered = 0;
	public int rest_time_long = 0; //the initial value is scheduled time long.
	public int max_extra_time_long = 0;//if needed, it can be reset after the packet service was generated.
	public int max_transfer_rate = BW_10G;//if the service type is TYPE_LIMITATION, this parameter may be reset.
	
	public VirtualTransLink carriedVTL;	
	public List<PacketServiceChild> sub_packet_services = null;
	
	public PacketService(int _id, int _sourceNode, int _destNode, int _priority, int _bw, int _scheduled_time_long)
	{
		this.id = _id;
		this.sourceNode = _sourceNode;
		this.destNode = _destNode;
		this.priority = _priority;
	
		//if _bw and _scheduled_time_long both are zero, it means it is a permanent service.
		//if _scheduled_time_long is zero, it means it is a permanent static service with _bw bandwidth request.
		//if _bw and _scheduled_time_long both are not zeor, it means it is not a permanent service.
		if(_bw == RANDOM_BW && _scheduled_time_long == 0)
		{
			isPermanent = PERMANENT;
			fillBwArrWithRandowBW();
		}
		else if(_scheduled_time_long == 0 && _bw != RANDOM_BW)
		{
			isPermanent = PERMANENT;
			fillBwArr(_bw);
		}
		else if(_scheduled_time_long != 0 && _bw != RANDOM_BW)
		{
			isPermanent = NOT_PERMANENT;
			rest_bw_to_be_transfered = _bw;
			rest_time_long = _scheduled_time_long;
			fillBwArr(0); 
			//may need add more actions.
		}
		else
			System.out.println("Please check the input of constructor function!");	
	}
	
	public void setServiceCarriedType(int _c_type) {
		this.carried_type = _c_type;		
	}
	
	private void fillBwArr(int _bw) 
	{
		real_time_bw_buckets = new int[TIME_BUCKET_NUM];
		for(int i = 0; i < TIME_BUCKET_NUM;i++)
			real_time_bw_buckets[i] = _bw;
		peek_bw = _bw;
	}
	
	private void fillBwArrWithRandowBW()
	{
		real_time_bw_buckets = ServiceGenerator.generate_bw_buckets(this.priority);//should be connected with priority
		
		peek_bw = 0;
		for(int _bw : real_time_bw_buckets)
		{
			if(_bw > peek_bw)
				peek_bw = _bw;
		}
	}
	
	public void addStepBucketCount()
	{
		current_bucket_count++;
		if(current_bucket_count == TIME_BUCKET_NUM)
			current_bucket_count = 0;
	}
	
	public int getCurrentOccupiedBw()
	{
		if(this.carried_type == STATIC_CARRIED)
			return getPeekBw();
		else
			return getCurrentBw();
	}
	
	private int getCurrentBw()
	{
		return real_time_bw_buckets[current_bucket_count];
	
	}
	
	public int getPeekBw()
	{
		return this.peek_bw;
	}
	
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
	
	public void cleanMyselfButKeepBWStatistics()
	{
		cleanMyselfWithoutBucketCount();
		current_bucket_count = 0;
		if(isPermanent == NOT_PERMANENT)
			fillBwArr(0);
	}
	
	public void cleanMyselfWithoutBucketCount()
	{
		carriedVTL = null;
		if(sub_packet_services != null)
			sub_packet_services.clear();
		sub_packet_services = null;
	}

}
