package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.asu.emit.qyan.alg.model.Pair;

public class BandwidthTolerantPacketService extends PacketService implements Comparable<PacketService>{
	

	//if it is not a permanent service,these parameters must be set.
	public int total_data_size = 0;
	public int limited_time = 0;
	public int rest_data_to_be_transfered = 0;
	public int rest_time_long = 0; //the initial value is scheduled time long.
	public int max_extra_time_long = 0;//if needed, it can be reset after the packet service was generated.
//	public int max_transfer_rate = (int)(BW_10G * Service.TH_USP_LOW) - 1;//if the service type is TYPE_LIMITATION, this parameter may be reset.
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
			return (rest_data_to_be_transfered/rest_time_long)+1;
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
		if(current_rate > (int)(Service.MAX_ALLOWED_BW * TH_USP_LOW))
			System.out.println("Out of range there must be something wrong :"+current_rate);
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
	public int compareTo(PacketService o) {
		if(o instanceof BandwidthTolerantPacketService)
			return this.rest_time_long - ((BandwidthTolerantPacketService)o).rest_time_long;
		else
		{
			System.out.println("Should not be here");
			return super.compareTo(o);
		}
	}

	public int maxBWCanBeShrinked() {
		int tem =  current_rate - rest_data_to_be_transfered/rest_time_long - 1;
		if(tem >= Service.MIN_SHARED_BW_GRANULARITY)
			return tem;
		else if(tem < -1)
			System.out.println("BTPS MAX bw can be shrinked, wrong result"+tem);
		return 0;
	}
	

	public int maxBWCanBeExpended() {
		
		return (int)(Service.MAX_ALLOWED_BW * TH_USP_LOW) - current_rate;
	}

	public List<Pair<Service,Integer>> shrinkedWithBW(int o2) {
		if((this.current_rate - o2) < (rest_data_to_be_transfered/rest_time_long))
			System.out.println("Something wrong, must check");
		List<Pair<Service,Integer>> target_vtls = new ArrayList<Pair<Service,Integer>>();
		List<SubBTService> removed_list = new ArrayList<SubBTService>();
		
		int rest_request_bw = o2;
		Collections.reverse(this.sub_btpss);
		
		for(SubBTService ss : this.sub_btpss)
		{
			if(ss.static_bw <= rest_request_bw)
			{
				target_vtls.add(new Pair<Service,Integer>(ss.carriedVTL,ss.static_bw));
				rest_request_bw -= ss.static_bw;
				ss.shrinkItself(ss.static_bw);
				removed_list.add(ss);
			}
			else
			{
				target_vtls.add(new Pair<Service,Integer>(ss.carriedVTL,rest_request_bw));
				ss.shrinkItself(rest_request_bw);
				break;
			}
		}
		
		if(removed_list.size() > 0)
		{
			for(SubBTService removed_subbt : removed_list)
			{
				removed_subbt.carriedVTL.removeCarriedPacketService(removed_subbt);
				removed_subbt.father_btps.removeSubBTService(removed_subbt);
			}
		}
		if(target_vtls.size() > 0)
			return target_vtls;
		else
			return null;
	}

	public void removeSubBTService(SubBTService subBTService) {
		if(!sub_btpss.remove(subBTService))
			System.out.println("Something wrong");
		
	}

	public int checkStatue() {

		updateCurrent_rate();
		
		if(current_rate < rest_data_to_be_transfered/rest_time_long)
			System.out.println("There must be some bug here,current_rate:"+ current_rate +"calculate rest" + ( rest_data_to_be_transfered/rest_time_long));

		rest_data_to_be_transfered -= this.current_rate * Service.TIME_STEP;		
		rest_time_long -= Service.TIME_STEP;
		
		if(rest_data_to_be_transfered <= 0)
			return BTPS_NEED_TO_BE_REMOVED;
		else if(rest_time_long == 0)
		{
			System.out.println("rest_data:"+ rest_data_to_be_transfered);
		}
		
		return BTPS_STILL_RUNNING;
	}

	public String toString()
	{
		String describtion = new String();
		
		describtion +="BTPS, id:"+this.id+". Current bw :"+this.current_rate+"\n";
		
		if(this.sub_btpss != null && this.sub_btpss.size() > 0)
		{
			for(SubBTService subbt : this.sub_btpss)
				describtion += subbt.toString()+"\n";
		}
		
		return describtion;
		
	}

}
