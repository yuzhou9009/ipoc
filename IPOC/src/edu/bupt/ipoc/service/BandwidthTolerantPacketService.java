package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.List;

import edu.asu.emit.qyan.alg.model.Pair;

public class BandwidthTolerantPacketService extends PacketService implements Comparable<BandwidthTolerantPacketService>{
	

	//if it is not a permanent service,these parameters must be set.
	public int total_data_size = 0;
	public int limited_time = 0;
	public int rest_data_to_be_transfered = 0;
	public int rest_time_long = 0; //the initial value is scheduled time long.
	public int max_extra_time_long = 0;//if needed, it can be reset after the packet service was generated.
	public int max_transfer_rate = BW_10G;//if the service type is TYPE_LIMITATION, this parameter may be reset.
	public int current_rate = 0;

//	public boolean has_child = false;
	public List<SubBTService> sub_btpss = null;
	
	public BandwidthTolerantPacketService(int _id, int _sourceNode, int _destNode, int _priority, int _total_data_size,
			int _limited_time) {
		super(_id, _sourceNode, _destNode, _priority);
		
		setPriority(PRIORITY_LOW);
		this.total_data_size = _total_data_size;
		this.rest_data_to_be_transfered = this.total_data_size;
		
		this.limited_time = _limited_time;
		this.rest_time_long = this.limited_time;
		
	}
	
	public int getCurrentOccupiedBw()
	{
		if(current_rate == 0)
			return (total_data_size/limited_time)+1;
		else
			return current_rate;
	}
	
	public int updateCurrent_rate()
	{
		current_rate = 0;
		for(SubBTService subbts : sub_btpss)
		{
			current_rate += subbts.static_bw;
		}
		return current_rate;
	}
	
	public void cleanMyselfButKeepBWStatistics()
	{
		cleanMyselfWithoutBucketCount();
		this.rest_data_to_be_transfered = this.total_data_size;
		this.rest_time_long = this.limited_time;
		if(sub_btpss != null)
			sub_btpss.clear();
		sub_btpss = null;

	}

	@Override
	public int compareTo(BandwidthTolerantPacketService o) {
		return this.rest_time_long - o.rest_time_long;				
	}

	public int maxBWCanBeShrinked() {
		// TODO Auto-generated method stub
		int tem =  rest_data_to_be_transfered/rest_time_long - current_rate;
		//TODO NEED TO BE TESTED
		if(tem >= Service.MIN_SHARED_BW_GRANULARITY)
			return tem;
		else
			return 0;
	}

	public List<Pair<Service,Integer>> shrinkedWithBW(int o2) {
		// TODO Auto-generated method stub
		if((this.current_rate - o2) < (rest_data_to_be_transfered/rest_time_long))
			System.out.println("Something wrong, must check");
		List<Service> target_vtls = new ArrayList<Service>();
		
		
		//TODO sort the children
		for(SubBTService sub_btps : this.sub_btpss)
		{
			//TODO
			/*
			for(Service ss : this.sub_packet_services)
			{
				if(ss < rest_bw)
					ps.removeMyself();
				else
					ps.shrinkBW();
			}*/
		}
		return null;
	}
	
	public boolean shrinkWithBw(int bw)
	{
		//TODO
		return true;
	}

	public void removeSubBTService(SubBTService subBTService) {
		if(!sub_btpss.remove(subBTService))
			System.out.println("Something wrong");
		
	}

}
