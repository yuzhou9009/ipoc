package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.service.*;

public class VirtualTransLink extends Service implements Comparable<VirtualTransLink>{
	
	public static final int BUILD_REQUEST = 1;
	public static final int REMOVE_REQUEST = 2;
	public static final int EXTEND_REQUEST = 3;
	public static final int SHRINKED_REQUEST = 4;
	public static final int ADJUST_PSS_REQUEST = 5;
	//
	public static final int VTL_MAX_CARRIED_PS_HIGH = 10;
	public static final int VTL_MAX_CARRIED_PS_MID = 10;
	public static final int VTL_MAX_CARRIED_PS_LOW = 10;
	
	//test
	public static final int STATIC_AND_NOT_SHARED = 0;
	public static final int STATIC_BUT_SHARED = 1;
	public static final int DYNAMIC_AND_SHARED_BUT_CONFILICTING = 2;//traditional;vtl can be shared with other services, but once the bw is not enough, it will caused conflict, then the affiliated service need to move to other vtl or need a new vtl for being carried.
	public static final int VTL_BOD = 3;
		
	public static final double TH_USP_HIGH = 0.80;
	public static final double TH_USP_MID = 0.85;
	public static final double TH_USP_LOW = 0.95;
	
	//these four threshold values can be changed on-line. But these are used statically right now.
	public double th_usp_high = TH_USP_HIGH;
	public double th_usp_mid = TH_USP_MID;
	public double th_usp_low = TH_USP_LOW;	
	public double th_lsp_all = 0.4;
	public double th_step = 0.05;
	
	//
	public static final int BW_EMPTY = 0;
	
	public static final int NO_CORRECTION = 0;
	
	public int id;
	public int sourceVertex;
	public int destVertex;	
	
	public int vtl_priority = PRIORITY_LOW;
	
	public int max_carried_ps = VTL_MAX_CARRIED_PS_LOW;
	
	public int bw_capacity = BW_EMPTY;// the upper is 4Z bit, so int is enough right now.
	
	public int type  = STATIC_AND_NOT_SHARED;
	//public boolean bod_on = false;
	
	public int primary_service_bw_last = BW_EMPTY;
	public int primary_service_bw_current = BW_EMPTY;
	public int all_service_bw_current = BW_EMPTY;
	
	private List<PacketService> carriedPacketServices = null;
	
	public List<OTNService> relevantOTNServices = null;
	public List<OpticalService> relevantOpticalServices = null;
	
	public VirtualTransLink()
	{
		carriedPacketServices = new ArrayList<PacketService>();
		
		relevantOTNServices = new ArrayList<OTNService>();	
		relevantOpticalServices = new ArrayList<OpticalService>();		
	}
	
	public void setID(int _id)
	{
		this.id = _id;
	}
	
	public void setSourceAndDest(int _source, int _dest)
	{
		this.sourceVertex = _source;
		this.destVertex = _dest;
	}
	
	public void setPrioriy(int _priority)
	{
		//if(_priority < this.vtl_priority)//higher
		this.vtl_priority = _priority;
	}
	
	public void setType(int _type)
	{
		this.type = _type;
	}
	

	public void setTypeAccordingToConstraints(Map<Integer, Constraint> cons) {
		if(cons != null)
		{
			Constraint _con = cons.get(Constraint.VTL_CARRY_TYPE_C);
			if(_con != null)
			{
				setType(_con.value);
			}
		}
		else
		{
			System.out.println("The vtl carried type is not be defined.");
			setType(VirtualTransLink.STATIC_BUT_SHARED);
		}
	}
	
	public void addRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.add(_os);		
		this.updateBwStatistics();
	}
	
	public void addRelevantOTNService(OTNService _otns)
	{
		this.relevantOTNServices.add(_otns);		
		this.updateBwStatistics();
	}
	
	public int updateCapacity()
	{
		int _bw_capacity = 0;
		if(relevantOTNServices.size()>0)
		{
			for(OTNService otns : relevantOTNServices)
			{
				_bw_capacity += otns.getBWCapacity();
			}
		}
		
		if(relevantOpticalServices.size() > 0)
		{
			
			for(OpticalService os : relevantOpticalServices)
			{
				_bw_capacity += os.getBWCapacity();
			}
		}
		
		bw_capacity = _bw_capacity;
		return bw_capacity;		
	}
	
	
	public void updateBwStatistics()
	{
		primary_service_bw_last = primary_service_bw_current;
		primary_service_bw_current = getUsedBWofVTLByPrimaryPacketServices();
		all_service_bw_current = getUsedBWofVTLByAllPacketServices();
		updateCapacity();
	}
	
	public boolean canCarryMorePS()
	{
		if(this.carriedPacketServices.size() < this.max_carried_ps)
			return true;
		return false;
	}
	
	public double getCurrentHighThresholdValue()
	{
		if(vtl_priority == PRIORITY_HIGH)
			return th_usp_high;
		else if(vtl_priority == PRIORITY_MID)
			return th_usp_mid;
		else if(vtl_priority == PRIORITY_LOW)
			return th_usp_low;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;
	}
	
	public static double getHighThresholdValue(int _priority)
	{
		if(_priority == PRIORITY_HIGH)
			return TH_USP_HIGH;
		else if(_priority == PRIORITY_MID)
			return TH_USP_MID;
		else if(_priority == PRIORITY_LOW)
			return TH_USP_LOW;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;
	}
	
	public double getCurrentLowThresholdValue()
	{
		return this.th_lsp_all;
		/*
		if(vtl_priority == VTL_P_HIGH)
			return ;
		else if(vtl_priority == VTL_P_MID)
			return ;
		else if(vtl_priority == VTL_P_LOW)
			return ;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;*/
	}
	
	public boolean canOfferMoreBW(int _bw)//just offer bw for the same priority
	{
		if(carriedPacketServices.size()>0)
		{
			if(this.type == VirtualTransLink.STATIC_AND_NOT_SHARED)
			{
				System.out.println("We may never have a chance to get here!");
				return false;
			}
			else if(this.type == VirtualTransLink.STATIC_BUT_SHARED)
			{
				if((getUsedBWofVTLByAllPacketServices() + _bw) <= getCapacity())
					return true;
			}
			else if(this.type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
			{
				if((getUsedBWofVTLByAllPacketServices() + _bw) * SURVIVABILITY_FACTOR <= getCapacity())
					return true;
			}
			else if(this.type == VirtualTransLink.VTL_BOD)
			{
				double _tem = 1.0/getCurrentHighThresholdValue();
				
				if(((getUsedBWofVTLByAllPacketServices() + _bw) / th_usp_low <= getCapacity()) 
						&& ((getUsedBWofVTLByPrimaryPacketServices()+ _bw) * _tem <= getCapacity()))
				{
					return true;
				}
			}
		}
		else
		{
			if(getCapacity()*getCurrentHighThresholdValue()>=_bw)
				return true;
		}
		return false;
	}
	
	public boolean canOfferMoreBwWithAdjustment(int _bw)
	{
		if((getUsedBWofVTLByPrimaryPacketServices() + _bw) / getCurrentHighThresholdValue() <= getCapacity())
		{
			return true;
		}
		return false;
	}
	
	public int getRestBW()//ID:00 need to be modified
	{
		if(this.type == VirtualTransLink.VTL_BOD)
		{
			//TODO ID:00 
			return smallerValue((int)(getCapacity() * getCurrentHighThresholdValue()) - getUsedBWofVTLByPrimaryPacketServices(),
					(int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices());
			//return (int)(getCapacity() * getCurrentHighThresholdValue()) - getUsedBWofVTLByAllPacketServices();
		}
		else if (this.type == VirtualTransLink.STATIC_AND_NOT_SHARED || this.type == VirtualTransLink.STATIC_BUT_SHARED || this.type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
			return (int)(getCapacity() * REGULAR_UPPER_THRESHOLD) - getUsedBWofVTLByAllPacketServices();
		else
			System.out.println("Never would be here!");
		return getCapacity() - getUsedBWofVTLByAllPacketServices();
	}
	
	public int getAcutallyRestBWforShare()
	{
		if(this.vtl_priority != Service.PRIORITY_LOW)
			return (((int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices())/MIN_SHARED_BW_GRANULARITY)*MIN_SHARED_BW_GRANULARITY;
		else
			return getAcutallyRestBWforShareNoLimit();
	}
	
	public int getAcutallyRestBWforShareNoLimit()
	{
		return ((int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices());
	}

	public int getCapacity()
	{
		updateCapacity();
		return this.bw_capacity;
	}
	
	public void removeRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.remove(_os);
		this.updateCapacity();
		//this.allBW += _os.get
	}
	
	public void removeRelevantOTNService(OTNService _otns)
	{
		this.relevantOTNServices.remove(_otns);
		this.updateCapacity();
		//this.allBW += _os.get
	}
	
	public void addPacketServiceToCarry(PacketService _ps)
	{
		this.carriedPacketServices.add(_ps);
		updateBwStatistics();
	}
	
	public void removeCarriedPacketService(PacketService _ps)
	{
		this.carriedPacketServices.remove(_ps);
		updateBwStatistics();
	}
	
	public int getCurrentPacketServiceNumber()
	{
		return this.carriedPacketServices.size();
	}
	
	public int getUsedBWofVTLByAllPacketServices()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	public int getAcutalUsedBWofVTLByAllPacketServices()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				usedBW += _ps.getActualBwItUsed();//.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	
	public int getUsedBWofVTLByPrimaryPacketServices()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				if(_ps.priority == this.vtl_priority)
					usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	public double getPathLong()
	{
		if(relevantOTNServices.size()>0)
		{
			return relevantOTNServices.get(0).osBelongTo.path.get_weight() + 5;
		}
		else if(relevantOpticalServices.size()>0)
		{
			return relevantOpticalServices.get(0).path.get_weight() + 5;
		}
		else
			return -1.0;
	}


	public int getCurrentStatue() 
	{
		if(this.type == STATIC_AND_NOT_SHARED || this.type == STATIC_BUT_SHARED)
			return VTL_NOT_NEED_ADJUSTED; 
		
		else if(this.type == VTL_BOD)
		{			
			if(isBeyondUpperThreshold(NO_CORRECTION))
			{
				return VTL_NEED_TO_BE_EXTENDED;
			}
			else if(isUnderLowerThreshold(NO_CORRECTION))
			{
				if(this.vtl_priority != Service.PRIORITY_LOW)
				{
					if(circuitNumber() == 1)
					{
						return VTL_NOT_NEED_ADJUSTED;
					}
					if(isNoServiceAnymore())
						System.out.println("This really happens");
				}
				else
				{
					if(isNoServiceAnymore())
					{
						//TODO System.out.println("This is a special conditions. May be extented!");
						//TODO Later If this is a service with low priority, we should do the adjustment.
						return VTL_NEED_TO_BE_REMOVED;
					}
				}								
				return VTL_NEED_TO_BE_SHRINKED;
			}
			else
			{
				if(isWholeBwBeyondUpperThreshold(NO_CORRECTION) || isStateChangingUpperThreshold(NO_CORRECTION))
				{
					return VTL_NEED_TO_BE_ADJUSTED;
				}
				
				if(this.vtl_priority == VirtualTransLink.PRIORITY_LOW)
				{
					if(this.relevantOTNServices != null && this.relevantOTNServices.size() > 0)
					{
						if((this.bw_capacity- (int)(this.getUsedBWofVTLByAllPacketServices()/this.th_usp_low))/Service.BW_1G > 0)
							return VTL_NEED_TO_BE_SHRINKED;
					}
					if(this.relevantOpticalServices.size() > 1)
					{
						if((this.bw_capacity- (int)(this.getUsedBWofVTLByAllPacketServices()/this.th_usp_low))/Service.BW_10G > 0)
							return VTL_NEED_TO_BE_SHRINKED;
					}
					
				}
				
			}	
		}
		else if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			//TODO Later
		}			
		return VTL_NOT_NEED_ADJUSTED;
	}

	private boolean isNoServiceAnymore() {
		if(this.getUsedBWofVTLByAllPacketServices() == 0)
			return true;
		return false;
	}

	private boolean isBeyondUpperThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == VTL_BOD)
		{
			if(((getCapacity() + _correction) * getCurrentHighThresholdValue() - primary_service_bw_current) < 0)
				return true;
		}
		else
			System.out.println("Bug");
		//else if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		//{
		//	if(getRestBW()<0)
		//		return true;
		//}
		return false;
	}

	private boolean isUnderLowerThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			if((((getCapacity()+_correction) * th_lsp_all) - all_service_bw_current) >= 0)
				return true;
		}
		else if(type == VTL_BOD)
		{
			if((((getCapacity()+_correction) * th_lsp_all) - primary_service_bw_current) >= 0)
				return true;
		}
		return false;
	}

	private boolean isWholeBwBeyondUpperThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			if(getRestBW()<0)
				return true;
		}
		else if(type == VTL_BOD)
		{
			if((((getCapacity()+_correction) * th_usp_low) - all_service_bw_current) < 0)
				return true;
		}
		return false;
	}
	
	private boolean isStateChangingUpperThreshold(int _correction)//only used after executing updateBwStatistics
	{
		if(type == VTL_BOD)
		{
			if((int)((1.0 * primary_service_bw_current/ bw_capacity)/th_step) != (int)((1.0 * primary_service_bw_last/ bw_capacity)/th_step))
				return true;
		}
		else
		{
			//TODO
			System.out.println("TODO");
		}
		return false;
	}
	
	public int howManyMoreBwNeeded() {
		return (int) (BW_1G * getCurrentHighThresholdValue())-1;
	}
	
	public int circuitNumber()
	{
		int number = 0;
		if(this.relevantOpticalServices!= null)
			number += this.relevantOpticalServices.size();
		if(this.relevantOTNServices!= null)
			number += this.relevantOTNServices.size();
		return number;
	}
	
	public List<Service> servicesNeededToRemove() 
	{
		List<Service> ls = new ArrayList<Service>();
		
		if(this.vtl_priority == Service.PRIORITY_LOW)
		{
			if(this.relevantOTNServices != null && this.relevantOTNServices.size()>0)
			{
				int max_num = (this.bw_capacity- (int)(getUsedBWofVTLByAllPacketServices()/th_usp_low))/Service.BW_1G;
				
				int tem = max_num < this.relevantOTNServices.size()?max_num:this.relevantOTNServices.size();
				Collections.sort(this.relevantOTNServices);
				for(int i = 0;i<tem;i++)
				{
					ls.add(this.relevantOTNServices.get(i));
				}
				if(max_num - this.relevantOTNServices.size() >= Service.BW_10G/Service.BW_1G)
				{
					if(this.relevantOpticalServices.size() > 1)
					{
						Collections.sort(this.relevantOpticalServices);
						ls.add(this.relevantOpticalServices.get(this.relevantOpticalServices.size() - 1));
					}						
				}
			}
			else
			{
				int max_num = (this.bw_capacity- (int)(getUsedBWofVTLByAllPacketServices()/th_usp_low))/Service.BW_10G;
				int list_size = this.relevantOpticalServices.size();
				if(list_size - max_num == 0)
					max_num --;
				for(int i = 0; i < max_num ; i++)
				{
					ls.add(this.relevantOpticalServices.get(list_size - 1 -i));
				}
				
			}	
		}
		else
		{
			//remove the last number of circuit, remove the circuit which are not in the same optical service.
			//TODO may be needed to remove more
			//sort
			if(this.relevantOTNServices.size()>1)
				ls.add(this.relevantOTNServices.get(this.relevantOTNServices.size()-1));
			else if(this.relevantOpticalServices.size()>1)
			{
				System.out.println("This should not happen Now! In servicesNeededToRemove");
			}
			
		}			
		return ls;
	}
	
	public void showMyself() 
	{
		System.out.println(this);	
	}
	
	public String toString()
	{
		String describtion = new String();
		describtion += "The vtl id:"+this.id;
		describtion += "\tthe source:"+this.sourceVertex+"\t the dest:"+this.destVertex;
		describtion += "\tthe priority:"+this.vtl_priority;
		describtion += "\n\tCarried ps num:"+carriedPacketServices.size();
		
		int request_bw = 0;
		for(PacketService _ps : carriedPacketServices)
		{
			describtion += "\n\t\t ps id:"+_ps.id+" ps priority:"+_ps.priority+"\t the request bw is:"+_ps.getCurrentOccupiedBw();
			//TODO
//			if(_ps.f_child == true)
//				describtion += "\t it is a ps child, his father id:"+_ps.father_ps.id;
			request_bw += _ps.getCurrentOccupiedBw();
		}
		describtion += "\n\t\t all ps request bw is :"+request_bw;
		if(this.relevantOpticalServices.size()>0)
		{
			describtion += "\n\tUsed os num:"+relevantOpticalServices.size();
			for(OpticalService _os : relevantOpticalServices)
			{
				describtion += "\n\t\t os id:"+_os.id;
			}
		}
		if(this.relevantOTNServices.size()>0)
		{
			describtion += "\n\tUsed otn num:"+relevantOTNServices.size();
			for(OTNService _otns : relevantOTNServices)
			{
				describtion += "\n\t\t otn id:"+_otns.id;
				describtion += "; carried os id:"+_otns.osBelongTo.id;
			}
		}
		
		return describtion;
		//System.out.println("vtl id:"+vtl.id+"\tthe priority:"+vtl.vtl_priority+"\tCarried packet services:"+vtl.carriedPacketServices.size());
		//System.out.println("")
	}
	
	public static int howManyOTNChannelsNeeded(int _priority, int _bw)
	{
		return (_bw / (int) (getHighThresholdValue(_priority) * Service.BW_1G + 1)) + 1;
	}
	
	public int smallerValue(int a, int b)
	{
		return a<b?a:b;
	}

	public List<PacketService> getServiceWithLowPriority() {
		
		if(this.carriedPacketServices != null && this.carriedPacketServices.size() > 0)
		{
			List<PacketService> target_sl = new ArrayList<PacketService>();
			for(PacketService ps : this.carriedPacketServices)
			{
				if(ps.priority == Service.PRIORITY_LOW)
					target_sl.add(ps);
			}
			if(target_sl.size()>0)
				return target_sl;
		}
		else
				System.out.println("Never be here!");
		return null;
	}

	@Override
	public int compareTo(VirtualTransLink o) {
		// TODO Auto-generated method stub
		//TODO
		return o.getRestBW() - this.getRestBW();
	}

	public List<Service> getAllResource() {
		List<Service> tem = new ArrayList<Service>();
		tem.addAll(this.relevantOpticalServices);
		tem.addAll(this.relevantOTNServices);
		return tem;
	}
}
