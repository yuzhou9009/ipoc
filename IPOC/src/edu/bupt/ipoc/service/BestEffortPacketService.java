package edu.bupt.ipoc.service;

public class BestEffortPacketService extends PacketService{

	public static final int RANDOM_BW = 0;
	
	public int[] real_time_bw_buckets = null;
	public int current_bucket_count = 0;
	
	//if it is a permanent service:
	public int peek_bw = 0;

	public BestEffortPacketService(int _id, int _sourceNode, int _destNode, int _priority, int _bw) {

		super(_id, _sourceNode, _destNode, _priority);

		
		setPriority(_priority);
		
		
		isPermanent = PERMANENT;
		
		if(_bw == RANDOM_BW)
		{
			fillBwArrWithRandowBW();
		}
		else if(_bw != RANDOM_BW)
		{
			fillBwArr(_bw);
		}
		
		
		// TODO Auto-generated constructor stub
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
	
	public int getActualBwItUsed(int _count) {
		return real_time_bw_buckets[_count] ;
	}

	public int getPeekBw()
	{
		return this.peek_bw;
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
	
	public int randomPriority()
	{
		int _tem = 0;
		
		_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(0,100);
		
		if(_tem<50)
			return PRIORITY_HIGH;
		else
			return PRIORITY_MID;
	}
	
	
	public void showMyselfCurrentState()
	{
		System.out.print("Id:"+this.id+"\tSource:"+this.sourceNode+"\tDest:"+this.destNode+"\t"+"priority"+this.priority+"\ta");
		
		for(int i = 0; i < Service.TIME_BUCKET_NUM;i++)
			System.out.print(real_time_bw_buckets[i]+"\t");
		
		System.out.print("current_bw:\t"+getCurrentBw()+"\t");
		System.out.println();
	}
	
	public void cleanMyselfButKeepBWStatistics()
	{
		super.cleanMyselfButKeepBWStatistics();
		current_bucket_count = 0;
//		if(isPermanent == NOT_PERMANENT)
//			fillBwArr(0);
	}
	
	public String toString()
	{
		String describtion = new String();
		
		describtion +="BEPS, id:"+this.id+". Current bw :"+this.getCurrentOccupiedBw()+".PRIORITY:"+this.priority+"\n";
		
		return describtion;
	}
	
	@Override
	public int compareTo(PacketService o) {
		if(o instanceof BestEffortPacketService)
			return this.priority - ((BestEffortPacketService)o).priority;
		else
		{
			System.out.println("Should not be here");
			return super.compareTo(o);
		}
	}

}
